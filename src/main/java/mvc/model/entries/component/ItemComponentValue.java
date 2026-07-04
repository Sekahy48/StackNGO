package mvc.model.entries.component;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa la instancia de un {@link ComponentDefinition} concreto asignada
 * a un item, con los valores reales para cada field definido en la plantilla.
 */
public class ItemComponentValue {

    private int componentDefId;
    private Map<String, String> fieldValues;

    public ItemComponentValue(int componentDefId) {
        this(componentDefId, new HashMap<>());
    }

    public ItemComponentValue(int componentDefId, Map<String, String> fieldValues) {
        this.componentDefId = componentDefId;
        this.fieldValues = fieldValues != null ? fieldValues : new HashMap<>();
    }

    public int getComponentDefId() { return componentDefId; }
    public Map<String, String> getFieldValues() { return fieldValues; }

    public String getValue(String fieldName) {
        return fieldValues.get(fieldName);
    }

    public void setValue(String fieldName, String value) {
        fieldValues.put(fieldName, value);
    }

    public void removeValue(String fieldName) {
        fieldValues.remove(fieldName);
    }
}