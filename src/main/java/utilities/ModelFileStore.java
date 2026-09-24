package utilities;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import logger.Logger;

/**
 * Carpeta propia de la aplicacion para los modelos 3D, y unica puerta de entrada y salida
 * de esos ficheros.
 *
 * <p>Un modelo elegido por el autor se copia aqui y a partir de ese momento la base de datos
 * solo guarda el nombre almacenado, nunca la ruta de origen. Esto separa la vida del fichero
 * de la del sitio donde el autor lo tuviera: mover o borrar la carpeta de trabajo deja de
 * romper el item, y una importacion desde otro equipo cae en el mismo sitio que una alta
 * manual.</p>
 *
 * <p>El nombre almacenado es un UUID. No es cosmetica: la carpeta es plana y compartida por
 * todas las colecciones, asi que dos ficheros que el autor llamo igual y venian de sitios
 * distintos se sobrescribirian el uno al otro si se conservara el nombre de origen. El
 * nombre legible se reconstruye al exportar, que es cuando existe el id del item y cuando
 * importa que se entienda.</p>
 */
public final class ModelFileStore {

    /** Extension unica admitida. glTF binario: un solo fichero con geometria y texturas. */
    public static final String EXTENSION = ".glb";

    private ModelFileStore() {
        // No se instancia. Esto no es un objeto, es una herramienta.
    }

    /**
     * Carpeta donde viven los modelos, creada si hace falta.
     *
     * @return ruta a la carpeta de modelos de la aplicacion
     */
    public static Path modelsDir() throws IOException {
        Path dir = Paths.get(System.getProperty("user.home"), "Stack&Go", "models");
        Files.createDirectories(dir);
        return dir;
    }

    /**
     * Copia un fichero elegido por el autor a la carpeta de la aplicacion.
     *
     * <p>Si el fichero ya esta dentro de la carpeta no se duplica: se devuelve su nombre tal
     * cual. Ese caso se da al reimportar o al volver a elegir un modelo que ya se habia dado
     * de alta, y copiarlo otra vez solo dejaria basura con otro UUID.</p>
     *
     * @param sourcePath ruta del fichero de origen, admitida tanto como ruta del sistema
     *                   como en forma de URI {@code file:/...}, que es lo que devuelve el
     *                   selector de ficheros
     * @return nombre con el que el fichero queda almacenado, o null si la copia fallo
     */
    public static String store(String sourcePath) {
        if (sourcePath == null || sourcePath.isBlank()) return null;

        try {
            Path source = toPath(sourcePath);
            Path dir = modelsDir();

            if (source.getParent() != null && Files.isSameFile(source.getParent(), dir)) {
                return source.getFileName().toString();
            }

            String storedName = UUID.randomUUID() + EXTENSION;
            Files.copy(source, dir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
            return storedName;

        } catch (IOException e) {
            Logger.getInstance().error(ModelFileStore.class.toString(),
                    "No se pudo guardar el modelo " + sourcePath + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda en la carpeta de la aplicacion un modelo que llega ya en bytes, sin pasar por
     * disco. Es el camino de la importacion, donde el fichero viene de dentro de un zip.
     *
     * @param bytes contenido del fichero
     * @return nombre con el que queda almacenado, o null si la escritura fallo
     */
    public static String storeBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;

        try {
            String storedName = UUID.randomUUID() + EXTENSION;
            Files.write(modelsDir().resolve(storedName), bytes);
            return storedName;

        } catch (IOException e) {
            Logger.getInstance().error(ModelFileStore.class.toString(),
                    "No se pudo guardar un modelo importado: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ruta absoluta de un fichero almacenado. No comprueba que exista.
     *
     * @param storedName nombre almacenado
     * @return ruta dentro de la carpeta de modelos, o null si no se pudo resolver
     */
    public static Path resolve(String storedName) {
        if (storedName == null || storedName.isBlank()) return null;

        try {
            return modelsDir().resolve(storedName);
        } catch (IOException e) {
            Logger.getInstance().error(ModelFileStore.class.toString(),
                    "No se pudo resolver el modelo " + storedName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Comprueba que un fichero almacenado sigue estando en disco. Lo usa la validacion
     * previa a exportar, que no puede fiarse de que la fila implique el fichero.
     *
     * @param storedName nombre almacenado
     * @return true si el fichero existe
     */
    public static boolean exists(String storedName) {
        Path path = resolve(storedName);
        return path != null && Files.isRegularFile(path);
    }

    /**
     * Borra un fichero almacenado.
     *
     * <p>Solo debe llamarse cuando ya se ha comprobado que ninguna fila lo referencia. La
     * comprobacion no se hace aqui porque esta clase no sabe de la base de datos, y meterle
     * ese conocimiento la convertiria en un DAO disfrazado.</p>
     *
     * @param storedName nombre almacenado
     */
    public static void delete(String storedName) {
        Path path = resolve(storedName);
        if (path == null) return;

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            Logger.getInstance().warning(ModelFileStore.class.toString(),
                    "No se pudo borrar el modelo huerfano " + storedName + ": " + e.getMessage());
        }
    }

    /**
     * Normaliza lo que entregue el selector de ficheros, que devuelve URI, frente a lo que
     * ya viene como ruta del sistema.
     */
    private static Path toPath(String raw) {
        return raw.startsWith("file:/") ? Paths.get(URI.create(raw)) : Paths.get(raw);
    }
}
