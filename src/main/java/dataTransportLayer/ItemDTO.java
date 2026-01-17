package dataTransportLayer;

public class ItemDTO extends EntryDTO {
    
    /**
     * 
     * Constructor of a DTO for the item
     * 
     * @param name of the item
     * @param iconPath where the icon of the item is allocated
     * @param description of the item
     * @param id of the item
     */
    public ItemDTO(String name, String iconPath, String description, int id) {
        super(name, iconPath, description, id);
    }
}
