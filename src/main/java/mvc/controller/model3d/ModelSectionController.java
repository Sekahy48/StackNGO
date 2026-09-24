package mvc.controller.model3d;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dataTransportLayer.ComponentDefinitionDTO;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.model.entries.model3d.ItemModelStage;
import mvc.model.entries.model3d.ModelFile;
import mvc.model.entries.model3d.ModelMagnitude;
import mvc.view.AbstractView;
import mvc.view.model3d.ModelSectionView;
import utilities.ModelFileStore;

/**
 * Logica de la seccion de modelos 3D de un item.
 *
 * <p>No es un controlador navegable ni se registra en ningun sitio: es un colaborador de
 * los controladores de alta y modificacion de item, que son quienes lo
 * crean y quienes deciden cuando cargar y cuando leer lo editado. Existe por la misma razon
 * que {@link ModelSectionView}: los dos casos de uso hacen exactamente lo mismo con los
 * modelos, y la unica diferencia esta en de donde salen las etapas iniciales.</p>
 *
 * <p>Mantiene las etapas en memoria y no toca disco ni base de datos. Los ficheros elegidos
 * quedan pendientes hasta que el item se guarda, de modo que cancelar una edicion no deja
 * nada detras.</p>
 */
public class ModelSectionController {

    private final ModelSectionView view;
    private final AbstractView owner;
    private final List<ItemModelStage> stages = new ArrayList<>();

    /**
     * @param view seccion que este controlador gobierna
     * @param owner vista que la contiene, necesaria para colgar de ella los dialogos
     */
    public ModelSectionController(ModelSectionView view, AbstractView owner) {
        this.view = view;
        this.owner = owner;
        this.view.getAddStageButton().setOnAction(e -> onAddStage());
    }

    /**
     * Carga las etapas de un item y la magnitud que las gobierna.
     *
     * @param stages etapas existentes, que se copian para no editar las del DTO de origen
     * @param drivenBy magnitud cualificada, o null
     */
    public void load(List<ItemModelStage> stages, String drivenBy) {
        this.stages.clear();

        // Copia, no referencia: las etapas que llegan son las del DTO que vive en sesion, y
        // editar sobre ellas dejaria los cambios puestos aunque el autor cancele.
        if (stages != null) {
            for (ItemModelStage stage : stages) {
                this.stages.add(new ItemModelStage(stage.getThreshold(), stage.getFiles()));
            }
        }

        this.view.setSelectedMagnitude(drivenBy);
        this.render();
    }

    /**
     * Deja la seccion como recien abierta. Lo llama el alta, que reutiliza la misma vista
     * para cada item nuevo.
     */
    public void clear() {
        this.stages.clear();
        this.view.setSelectedMagnitude(null);
        this.view.clearThresholdField();
        this.render();
    }

    /**
     * Rellena el desplegable con los campos numericos de todos los componentes definidos.
     *
     * <p>Solo numericos porque un umbral es una comparacion de orden: un booleano o un enum
     * no tienen "mayor o igual". Se muestran cualificados con el componente porque dos
     * componentes distintos pueden tener un campo que se llame igual, y el juego necesita
     * saber en cual mirar.</p>
     *
     * @param definitions definiciones de componente disponibles en la cuenta
     */
    public void setAvailableMagnitudes(List<ComponentDefinitionDTO> definitions) {
        List<String> magnitudes = new ArrayList<>();

        if (definitions != null) {
            for (ComponentDefinitionDTO def : definitions) {
                if (def.fields == null) continue;

                for (ComponentField field : def.fields) {
                    if (isNumeric(field.getFieldType())) {
                        magnitudes.add(ModelMagnitude.qualify(def.name, field.getFieldName()));
                    }
                }
            }
        }

        view.setAvailableMagnitudes(magnitudes);
    }

    /**
     * Etapas editadas, ordenadas de mayor a menor umbral y sin las que quedaron vacias.
     *
     * <p>El descarte se hace aqui y no al quitar la ultima variante para que el autor pueda
     * vaciar una etapa y volver a llenarla sin que desaparezca bajo sus pies.</p>
     */
    public List<ItemModelStage> getStages() {
        List<ItemModelStage> out = new ArrayList<>();
        for (ItemModelStage stage : stages) {
            if (!stage.isEmpty()) out.add(stage);
        }
        out.sort(Comparator.comparingDouble(ItemModelStage::getThreshold).reversed());
        return out;
    }

    /**
     * Magnitud elegida, o null si no hay etapas suficientes para que signifique algo.
     *
     * <p>Con una sola etapa no hay nada que decidir, asi que guardar una magnitud solo
     * dejaria una referencia que envejece sin que nadie la lea: si mas tarde se borrase ese
     * campo del componente, el item quedaria apuntando a algo inexistente sin haberlo usado
     * jamas.</p>
     */
    public String getDrivenBy() {
        return getStages().size() > 1 ? view.getSelectedMagnitude() : null;
    }

    /**
     * Anade una etapa con el umbral escrito, si es un numero y no lo tiene ya otra.
     */
    private void onAddStage() {
        String raw = view.getThresholdField().getText();

        float threshold;
        try {
            threshold = Float.parseFloat(raw != null ? raw.trim().replace(',', '.') : "");
        } catch (NumberFormatException e) {
            owner.showAlert("Umbral no valido",
                    "El umbral tiene que ser un numero. Usa 0 para la etapa por defecto.",
                    Alert.AlertType.ERROR);
            return;
        }

        for (ItemModelStage stage : stages) {
            if (stage.getThreshold() == threshold) {
                owner.showAlert("Umbral repetido",
                        "Ya hay una etapa con umbral " + threshold + ".",
                        Alert.AlertType.ERROR);
                return;
            }
        }

        stages.add(new ItemModelStage(threshold));
        view.clearThresholdField();
        render();
    }

    /**
     * Pide un fichero al autor y lo anade a la etapa como variante pendiente.
     */
    private void onAddFile(ItemModelStage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Elegir modelo 3D");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Modelos glTF binarios", "*" + ModelFileStore.EXTENSION)
        );

        File file = chooser.showOpenDialog(owner.getRoot().getScene().getWindow());
        if (file == null) return;

        stage.addFile(ModelFile.pending(file.getAbsolutePath(), file.getName()));
        render();
    }

    private void onRemoveFile(ItemModelStage stage, ModelFile file) {
        stage.removeFile(file);
        render();
    }

    private void onRemoveStage(ItemModelStage stage) {
        stages.remove(stage);
        render();
    }

    /**
     * Repinta la seccion entera.
     *
     * <p>Se repinta todo en vez de tocar la fila afectada porque el orden por umbral es
     * parte de lo que la seccion comunica, y cualquier cambio puede alterarlo. Son unas
     * pocas filas: el coste de mantener dos representaciones sincronizadas seria mayor que
     * el de rehacerlas.</p>
     */
    private void render() {
        view.clearStageRows();

        List<ItemModelStage> ordered = new ArrayList<>(stages);
        ordered.sort(Comparator.comparingDouble(ItemModelStage::getThreshold).reversed());

        for (ItemModelStage stage : ordered) {
            view.addStageRow(
                    stage,
                    () -> onAddFile(stage),
                    file -> onRemoveFile(stage, file),
                    () -> onRemoveStage(stage)
            );
        }

        updateMagnitudeState();
    }

    /**
     * Habilita la magnitud solo cuando hay mas de una etapa, y explica por que si no.
     */
    private void updateMagnitudeState() {
        int usable = getStages().size();

        view.setMagnitudeEnabled(usable > 1);

        if (usable == 0) {
            view.setMagnitudeHint("Sin etapas: el item se exportara sin modelo.");
        } else if (usable == 1) {
            view.setMagnitudeHint("Con una sola etapa no hace falta magnitud.");
        } else {
            view.setMagnitudeHint("");
        }
    }

    private boolean isNumeric(FieldType type) {
        return type == FieldType.FLOAT || type == FieldType.INT;
    }
}
