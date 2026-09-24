package dataAccessLayer.DAO;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logger.Logger;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;
import utilities.ModelFileStore;

/**
 * Persistencia de las etapas de modelo de un item.
 *
 * <p>No es un DAO de primera clase y no esta en {@link DAOType}: las etapas no tienen vida
 * propia fuera del item que las contiene, igual que los valores de componente. Se usa desde
 * dentro de {@link ItemDAO}, que es quien decide cuando un item se lee, se crea o se
 * actualiza.</p>
 *
 * <p>Ademas de filas, aqui hay ficheros. Esa es la unica diferencia real con
 * {@link ItemComponentDAO}: borrar una fila de componente no deja nada detras, mientras que
 * borrar una etapa deja un .glb en la carpeta de la aplicacion que ya no referencia nadie.
 * Por eso cada escritura compara los nombres almacenados de antes con los de despues y
 * barre la diferencia.</p>
 */
public class ItemModelDAO {

    protected Connection connection = DBManager.getConnection();

    /**
     * Lee las etapas de un item, de mayor a menor umbral, con sus ficheros en el orden en
     * que los coloco el autor.
     *
     * @param itemId identificador del item
     * @return etapas del item, lista vacia si no tiene
     */
    public List<ItemModelStage> readByItem(int itemId) {
        String sql = "SELECT s.id AS stage_id, s.threshold, f.stored_name, f.original_name " +
                     "FROM item_model_stages s " +
                     "LEFT JOIN item_model_files f ON f.stage_id = s.id " +
                     "WHERE s.item_id = ? " +
                     "ORDER BY s.threshold DESC, f.sort_order ASC, f.id ASC";

        List<ItemModelStage> out = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            int currentStageId = -1;
            ItemModelStage current = null;

            while (rs.next()) {
                int stageId = rs.getInt("stage_id");

                if (stageId != currentStageId) {
                    current = new ItemModelStage(rs.getFloat("threshold"));
                    out.add(current);
                    currentStageId = stageId;
                }

                String storedName = rs.getString("stored_name");
                if (storedName != null) {
                    current.addFile(ModelFile.stored(storedName, rs.getString("original_name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    /**
     * Reemplaza las etapas de un item por las que se le pasan, y borra de disco los ficheros
     * que dejan de estar referenciados.
     *
     * <p>Sigue la forma de {@link ItemComponentDAO#updateForItem}: borrar e insertar, en vez
     * de comparar fila a fila. Es mas simple y no pierde nada, porque una etapa no tiene
     * identidad que conservar mas alla de su umbral.</p>
     *
     * <p>El barrido se calcula antes de tocar nada y se ejecuta despues de insertar. En ese
     * orden porque el conjunto de ficheros que sobrevive solo se conoce una vez escrito lo
     * nuevo, y borrar antes dejaria al item sin modelo si la insercion fallara.</p>
     *
     * @param itemId identificador del item
     * @param stages etapas que deben quedar
     */
    public void updateForItem(int itemId, List<ItemModelStage> stages) {
        Set<String> before = storedNamesOf(itemId);

        deleteForItem(itemId);
        insertForItem(itemId, stages);

        Set<String> after = new HashSet<>();
        if (stages != null) {
            for (ItemModelStage stage : stages) {
                for (ModelFile file : stage.getFiles()) after.add(file.getStoredName());
            }
        }

        before.removeAll(after);
        sweep(before);
    }

    /**
     * Borra las etapas de un item y, con ellas, los ficheros que ya no referencie nadie.
     *
     * <p>Lo llama el borrado del item. La cascada de la base de datos se lleva las filas
     * sola, pero no sabe nada de la carpeta de modelos.</p>
     *
     * @param itemId identificador del item
     */
    public void purgeForItem(int itemId) {
        Set<String> before = storedNamesOf(itemId);
        deleteForItem(itemId);
        sweep(before);
    }

    /**
     * Borra las filas de etapas de un item. Los ficheros de cada etapa caen por cascada.
     *
     * @param itemId identificador del item
     */
    public void deleteForItem(int itemId) {
        String sql = "DELETE FROM item_model_stages WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserta las etapas de un item con sus ficheros. Descarta las etapas vacias, que no
     * representan nada y solo ocupan un umbral que otra podria necesitar.
     */
    private void insertForItem(int itemId, List<ItemModelStage> stages) {
        if (stages == null || stages.isEmpty()) return;

        String stageSql = "INSERT INTO item_model_stages (item_id, threshold) VALUES (?, ?)";
        String fileSql = "INSERT INTO item_model_files (stage_id, stored_name, original_name, sort_order) VALUES (?, ?, ?, ?)";

        try {
            for (ItemModelStage stage : stages) {
                if (stage.isEmpty()) continue;

                int stageId;
                try (PreparedStatement stmt = connection.prepareStatement(stageSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, itemId);
                    stmt.setFloat(2, stage.getThreshold());
                    stmt.executeUpdate();

                    ResultSet keys = stmt.getGeneratedKeys();
                    if (!keys.next()) continue;
                    stageId = keys.getInt(1);
                }

                try (PreparedStatement stmt = connection.prepareStatement(fileSql)) {
                    int order = 0;
                    for (ModelFile file : stage.getFiles()) {
                        // Un fichero pendiente no tiene nombre almacenado que guardar. No
                        // deberia llegar aqui: el item normaliza al fijar las etapas. Si
                        // llega es que la copia fallo, y escribirlo dejaria una fila
                        // apuntando a nada.
                        if (file.isPending()) continue;

                        stmt.setInt(1, stageId);
                        stmt.setString(2, file.getStoredName());
                        stmt.setString(3, file.getOriginalName() != null ? file.getOriginalName() : file.getStoredName());
                        stmt.setInt(4, order++);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Borra de la carpeta de modelos todo fichero que ya no referencie ninguna fila.
     *
     * <p>Existe porque no todo borrado pasa por {@link ItemDAO#delete}: al borrar una
     * coleccion o una cuenta, la base de datos se lleva sus items en cascada sin que ningun
     * codigo Java vea pasar cada uno. La cascada resuelve las filas y deja los ficheros, asi
     * que despues de esas operaciones hace falta una escoba que mire el conjunto entero en
     * vez de un item concreto.</p>
     */
    public void purgeUnreferenced() {
        Set<String> referenced = allStoredNames();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(ModelFileStore.modelsDir())) {
            for (Path file : files) {
                String name = file.getFileName().toString();
                if (!referenced.contains(name)) ModelFileStore.delete(name);
            }
        } catch (IOException e) {
            Logger.getInstance().warning(this.getClass().toString(),
                    "No se pudo repasar la carpeta de modelos: " + e.getMessage());
        }
    }

    /**
     * Nombres almacenados que referencia cualquier item.
     */
    private Set<String> allStoredNames() {
        Set<String> out = new HashSet<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT stored_name FROM item_model_files")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) out.add(rs.getString("stored_name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    /**
     * Nombres almacenados que un item referencia ahora mismo.
     */
    private Set<String> storedNamesOf(int itemId) {
        String sql = "SELECT f.stored_name FROM item_model_files f " +
                     "JOIN item_model_stages s ON s.id = f.stage_id WHERE s.item_id = ?";

        Set<String> out = new HashSet<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) out.add(rs.getString("stored_name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    /**
     * Borra de disco los ficheros candidatos que ya no referencia ninguna fila.
     *
     * <p>La comprobacion es necesaria y no paranoia: el mismo fichero puede estar en dos
     * etapas del mismo item, o compartido por dos items si alguna vez se reaprovecha un
     * nombre almacenado. Quitarlo de una no autoriza a borrarlo de disco.</p>
     *
     * @param candidates nombres almacenados que el item ha dejado de usar
     */
    private void sweep(Set<String> candidates) {
        if (candidates.isEmpty()) return;

        String sql = "SELECT COUNT(*) FROM item_model_files WHERE stored_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String storedName : candidates) {
                stmt.setString(1, storedName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) ModelFileStore.delete(storedName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
