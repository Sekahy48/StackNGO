package dataTransportLayer;

public class EntryDTO implements GenericDTO {

    public String name;
    public String iconPath;
    public String description;
    public int id;

    /**
     * 
     * Constructor of a generic DTO class
     * 
     * @param name of the entry
     * @param iconPath where the icon of the entry is allocated
     * @param description of the entry
     * @param id of the entry
     * 
     */
    public EntryDTO(String name, String iconPath, String description, int id) {
        
        this.name = name;
        this.iconPath = iconPath;
        this.description = description;
        this.id = id;
    }

    public int getId() {return id;}

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public String getName() {
        return name;
    }
}