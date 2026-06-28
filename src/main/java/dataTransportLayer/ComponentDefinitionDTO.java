package dataTransportLayer;

import java.util.List;

import mvc.model.entries.component.ComponentField;

public class ComponentDefinitionDTO extends EntryDTO {
    public List<ComponentField> fields;

    public ComponentDefinitionDTO(String name, String iconPath, String description, int id, List<ComponentField> fields) {
        super(name, iconPath, description, id);
        this.fields = fields;
    }
}