package dataTransportLayer;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.component.ItemComponentValue;
import mvc.model.entries.model3d.ItemModelStage;

public class ItemDTO extends EntryDTO {

    public List<ItemComponentValue> components;

    /** Magnitud que decide la etapa de modelo, cualificada como {@code Componente.campo}. */
    public String modelDrivenBy;

    /** Etapas de modelo, de mayor a menor umbral. */
    public List<ItemModelStage> modelStages;

    public ItemDTO(String name, String iconPath, String description, int id) {
        this(name, iconPath, description, id, new ArrayList<>());
    }

    public ItemDTO(String name, String iconPath, String description, int id, List<ItemComponentValue> components) {
        this(name, iconPath, description, id, components, null, new ArrayList<>());
    }

    public ItemDTO(
            String name,
            String iconPath,
            String description,
            int id,
            List<ItemComponentValue> components,
            String modelDrivenBy,
            List<ItemModelStage> modelStages
    ) {
        super(name, iconPath, description, id);
        this.components = components != null ? components : new ArrayList<>();
        this.modelDrivenBy = modelDrivenBy;
        this.modelStages = modelStages != null ? modelStages : new ArrayList<>();
    }
}
