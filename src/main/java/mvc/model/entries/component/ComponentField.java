package mvc.model.entries.component;

public class ComponentField {
    private String fieldName;
    private FieldType fieldType; 

    public ComponentField(String fieldName, FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String getFieldName() { return fieldName; }
    public FieldType getFieldType() { return fieldType; }
}
