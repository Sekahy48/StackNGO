package mvc.model.entries.component;

import java.util.ArrayList;
import java.util.List;

public class ComponentField {
    private String fieldName;
    private FieldType fieldType;
    private List<String> enumValues;

    public ComponentField(String fieldName, FieldType fieldType) {
        this(fieldName, fieldType, new ArrayList<>());
    }

    public ComponentField(String fieldName, FieldType fieldType, List<String> enumValues) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.enumValues = enumValues != null ? enumValues : new ArrayList<>();
    }

    public String getFieldName() { return fieldName; }
    public FieldType getFieldType() { return fieldType; }
    public List<String> getEnumValues() { return enumValues; }

    /**
     * Nombre identificativo del enum, derivado de componente+campo.
     * Solo relevante si fieldType == ENUM.
     *
     * @param componentName nombre del componente al que pertenece este field
     * @return nombre identificativo tipo NombreComponenteNombreCampoEnum
     */
    public String getEnumTypeName(String componentName) {
        return componentName + fieldName + "Enum";
    }
}