package mvc.model.entries;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.component.ItemComponentValue;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;
import utilities.ModelFileStore;

public class Item extends Entry {

    private List<ItemComponentValue> components = new ArrayList<>();
    private String modelDrivenBy;
    private List<ItemModelStage> modelStages = new ArrayList<>();

    public Item(String name, String description, String imagePath, int id) {
        super(name, description, imagePath, id);
    }
    
    public Item(String name, int id) {
        super(name, id);
    }

    public List<ItemComponentValue> getComponents() { return components; }
    public void setComponents(List<ItemComponentValue> components) { this.components = components; }
    public void addComponent(ItemComponentValue value) { this.components.add(value); }
    public void removeComponent(int componentDefId) {
        components.removeIf(c -> c.getComponentDefId() == componentDefId);
    }

    /**
     * Magnitud que decide que etapa de modelo se muestra, cualificada como
     * {@code Componente.campo}.
     *
     * <p>Es null cuando el item no tiene etapas o cuando tiene una sola: en ambos casos no
     * hay nada que decidir, y obligar a elegir una magnitud seria pedir un dato que no se
     * va a leer.</p>
     */
    public String getModelDrivenBy() { return modelDrivenBy; }
    public void setModelDrivenBy(String modelDrivenBy) { this.modelDrivenBy = modelDrivenBy; }

    /**
     * Etapas de modelo del item, de mayor a menor umbral.
     *
     * <p>El orden no es un adorno: quien resuelve que modelo mostrar se queda con la primera
     * etapa cuyo umbral no supere el valor de la magnitud, asi que recorrerlas al reves
     * daria siempre la etapa mas baja. La ordenacion la garantiza la capa que las lee.</p>
     */
    public List<ItemModelStage> getModelStages() { return modelStages; }

    /**
     * Fija las etapas del item y copia a la carpeta de la aplicacion los ficheros que
     * todavia estaban pendientes.
     *
     * <p>La copia vive aqui por la misma razon que la de los iconos vive en
     * {@link Entry#setImagePath}: el item se construye a partir de un DTO venga de donde
     * venga —alta, modificacion o importacion—, asi que este es el unico punto por el que
     * pasan todos los caminos. Hacerlo en cada controlador seria tener la misma regla
     * escrita tres veces, y bastaria olvidarla en uno para que la base de datos guardara una
     * ruta que solo existe en el equipo del autor.</p>
     *
     * <p>Se hace al guardar y no al elegir el fichero para que una edicion abandonada no
     * deje ficheros que ya no referencia nadie.</p>
     */
    public void setModelStages(List<ItemModelStage> modelStages) {
        this.modelStages = modelStages != null ? modelStages : new ArrayList<>();
        this.storePendingFiles();
    }

    /**
     * Sustituye cada fichero pendiente por su version ya almacenada. Los que no se pudieron
     * copiar se descartan, porque una fila que apunta a un fichero inexistente es peor que
     * no tener la fila.
     */
    private void storePendingFiles() {
        for (ItemModelStage stage : this.modelStages) {
            List<ModelFile> files = stage.getFiles();

            for (int i = files.size() - 1; i >= 0; i--) {
                ModelFile file = files.get(i);
                if (!file.isPending()) continue;

                String storedName = ModelFileStore.store(file.getSourcePath());

                if (storedName == null) files.remove(i);
                else files.set(i, ModelFile.stored(storedName, file.getOriginalName()));
            }
        }
    }

    /**
     * Si el item tiene algun modelo asignado.
     *
     * <p>Lo pregunta la exportacion para decidir si emite el componente de modelo. Un item
     * sin modelos no emite un componente vacio: en el juego, no tener el componente y
     * tenerlo sin nada dentro significarian lo mismo, y de las dos formas de decirlo solo
     * una no obliga a comprobar el interior.</p>
     */
    public boolean hasModels() {
        for (ItemModelStage stage : modelStages) {
            if (!stage.isEmpty()) return true;
        }
        return false;
    }

}