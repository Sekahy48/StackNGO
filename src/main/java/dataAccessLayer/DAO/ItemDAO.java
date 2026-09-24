package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemWithCollectionDTO;
import mvc.model.entries.Item;
import mvc.model.entries.component.ItemComponentValue;

public class ItemDAO extends AbstractEntryDAO<ItemDTO, Item> {

    private final ItemComponentDAO itemComponentDAO = new ItemComponentDAO();
    private final ItemModelDAO itemModelDAO = new ItemModelDAO();

    @Override
    protected String getTableName() {
        return "items";
    }

    @Override
    protected ItemDTO buildDTO(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        return DTOFactory.item(
            rs.getString("name"),
            rs.getString("icon"),
            rs.getString("description"),
            id,
            itemComponentDAO.readByItem(id),
            rs.getString("model_driven_by"),
            itemModelDAO.readByItem(id)
        );
    }

    /**
     * Borra el item y, antes, sus modelos.
     *
     * <p>La cascada de la base de datos se lleva las filas de etapas y ficheros sola, pero
     * los .glb viven en la carpeta de la aplicacion y nadie mas los conoce. Si el borrado
     * del item fallara despues, lo perdido serian ficheros que ya no se veian en ningun
     * sitio, no filas.</p>
     */
    @Override
    public boolean delete(int id) {
        itemModelDAO.purgeForItem(id);
        return super.delete(id);
    }

    @Override
    protected List<ItemDTO> readAllInternal(int collectionId) throws SQLException {
        List<ItemDTO> out = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE collection_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(buildDTO(rs));
            }
        }
        return out;
    }
    

    public int isInRecipe(int itemId) {
        int recipeId;
        String sql = "SELECT recipes_id FROM recipe_inputs WHERE items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            recipeId = rs.next() ? rs.getInt("recipes_id") : -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return recipeId;

    }

    @Override
    public boolean create(Item entry, int[] foreignKeys) {
        String sql = "INSERT INTO items (id, name, icon, description, collection_id, model_driven_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getId().value());
            stmt.setString(2, entry.getName());
            stmt.setString(3, entry.getImagePath());
            stmt.setString(4, entry.getDescription());
            stmt.setInt(5, foreignKeys[0]); // collection_id
            stmt.setString(6, entry.getModelDrivenBy());
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                itemComponentDAO.updateForItem(entry.getId().value(), entry.getComponents());
                itemModelDAO.updateForItem(entry.getId().value(), entry.getModelStages());
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Item entry, int id) {
        String sql = "UPDATE items SET name = ?, icon = ?, description = ?, model_driven_by = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getImagePath());
            stmt.setString(3, entry.getDescription());
            stmt.setString(4, entry.getModelDrivenBy());
            stmt.setInt(5, id);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                itemComponentDAO.updateForItem(id, entry.getComponents());
                itemModelDAO.updateForItem(id, entry.getModelStages());
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ItemWithCollectionDTO> readAllWithCollection(int accountId) {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS collection_name FROM items i " +
                "JOIN collections c ON i.collection_id = c.id " +
                "WHERE c.account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemDTO item = buildDTO(rs);
                String collectionName = rs.getString("collection_name");
                out.add(new ItemWithCollectionDTO(item, collectionName));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    protected String getParentColumnName() {
        return "collection_id";
    }
}