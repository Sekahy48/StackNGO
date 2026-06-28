package mvc.model.entries.component;

import java.util.List;

import mvc.model.entries.Entry;

public class ComponentDefinition extends Entry {
    private List<ComponentField> fields;

    public ComponentDefinition(String name, String description, String imagePath, int id, List<ComponentField> fields) {
        super(name, description, imagePath, id);
        this.fields = fields;
    }

    public ComponentDefinition(String name, int id) {
        super(name, id);
    }

    public List<ComponentField> getFields() { return fields; }
    public void setFields(List<ComponentField> fields) { this.fields = fields; }
    public void addFields(ComponentField field) {this.fields.add(field);}
}
