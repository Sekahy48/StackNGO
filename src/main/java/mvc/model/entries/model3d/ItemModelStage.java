package mvc.model.entries.model3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Una etapa de modelo: el umbral a partir del cual aplica y las variantes que la
 * representan.
 *
 * <p>El umbral se lee sobre la magnitud que el item declara en {@code modelDrivenBy}, y una
 * etapa aplica cuando el valor de esa magnitud es mayor o igual que su umbral. Por eso el
 * consumidor debe recorrerlas de mayor a menor: la primera que cumple es la que gana, y una
 * etapa de umbral 0 actua como caso por defecto.</p>
 *
 * <p>Las variantes de una misma etapa son intercambiables: representan lo mismo con aspecto
 * distinto y quien las consume elige una. Un item con un solo modelo es, simplemente, una
 * etapa de umbral 0 con un fichero.</p>
 */
public class ItemModelStage {

    private float threshold;
    private final List<ModelFile> files;

    public ItemModelStage(float threshold) {
        this(threshold, new ArrayList<>());
    }

    public ItemModelStage(float threshold, List<ModelFile> files) {
        this.threshold = threshold;
        this.files = files != null ? new ArrayList<>(files) : new ArrayList<>();
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public List<ModelFile> getFiles() {
        return files;
    }

    public void addFile(ModelFile file) {
        this.files.add(file);
    }

    public boolean removeFile(ModelFile file) {
        return this.files.remove(file);
    }

    public boolean isEmpty() {
        return this.files.isEmpty();
    }

    @Override
    public String toString() {
        return threshold + " -> " + files;
    }
}
