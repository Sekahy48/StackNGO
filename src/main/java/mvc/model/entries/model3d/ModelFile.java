package mvc.model.entries.model3d;

import java.util.Objects;

/**
 * Un fichero de modelo 3D perteneciente a una etapa.
 *
 * <p>Distingue tres nombres porque son tres cosas distintas y confundirlas es justo lo que
 * hace fragil el manejo de ficheros:</p>
 *
 * <ul>
 *   <li><b>storedName</b>: con el que el fichero vive en la carpeta de la aplicacion. Lo
 *       genera {@link utilities.ModelFileStore} y no significa nada para nadie, precisamente
 *       para que dos modelos que el autor llamo igual no se pisen. Es lo unico que guarda la
 *       base de datos.</li>
 *   <li><b>originalName</b>: el que tenia el fichero cuando el autor lo eligio. Existe solo
 *       para que el editor muestre algo legible.</li>
 *   <li><b>sourcePath</b>: de donde se saco. Solo esta puesto mientras el fichero sigue
 *       pendiente de guardarse, y deja de tener sentido en cuanto se copia.</li>
 * </ul>
 *
 * <p>Un fichero recien elegido en el editor nace pendiente, y no toca el disco hasta que el
 * item se guarda. Asi una edicion abandonada no deja nada detras.</p>
 *
 * <p>El nombre con el que el fichero viaja en una exportacion no es ninguno de los tres: se
 * construye al exportar, cuando ya se conoce el item al que pertenece.</p>
 */
public class ModelFile {

    private final String storedName;
    private final String originalName;
    private final String sourcePath;

    private ModelFile(String storedName, String originalName, String sourcePath) {
        this.storedName = storedName;
        this.originalName = originalName;
        this.sourcePath = sourcePath;
    }

    /**
     * Fichero que ya vive en la carpeta de la aplicacion. Es lo que devuelve la lectura de
     * base de datos.
     */
    public static ModelFile stored(String storedName, String originalName) {
        return new ModelFile(storedName, originalName, null);
    }

    /**
     * Fichero elegido por el autor y todavia sin copiar. Se resuelve al guardar el item.
     *
     * @param sourcePath ruta de origen, tal como la entregue el selector de ficheros
     * @param originalName nombre con el que mostrarlo en el editor
     */
    public static ModelFile pending(String sourcePath, String originalName) {
        return new ModelFile(null, originalName, sourcePath);
    }

    public String getStoredName() {
        return storedName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    /** Si el fichero todavia no se ha copiado a la carpeta de la aplicacion. */
    public boolean isPending() {
        return storedName == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelFile other = (ModelFile) o;
        return isPending()
                ? Objects.equals(sourcePath, other.sourcePath) && other.isPending()
                : Objects.equals(storedName, other.storedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPending() ? sourcePath : storedName);
    }

    @Override
    public String toString() {
        if (originalName != null) return originalName;
        return storedName != null ? storedName : sourcePath;
    }
}
