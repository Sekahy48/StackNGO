package mvc.model.entries;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.component.ItemComponentValue;

public class Item extends Entry {

    private List<ItemComponentValue> components = new ArrayList<>();

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
    
}