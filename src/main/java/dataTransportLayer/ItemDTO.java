package dataTransportLayer;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.component.ItemComponentValue;

public class ItemDTO extends EntryDTO {

    public List<ItemComponentValue> components;
    
    public ItemDTO(String name, String iconPath, String description, int id) {
        this(name, iconPath, description, id, new ArrayList<>());
    }

    public ItemDTO(String name, String iconPath, String description, int id, List<ItemComponentValue> components) {
        super(name, iconPath, description, id);
        this.components = components != null ? components : new ArrayList<>();
    }
}