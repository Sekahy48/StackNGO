package dataTransportLayer;

public class EntryDTO implements GenericDTO {

    public String name;
    public String imagePath;
    public String description;
    public int id;

    /**
     * 
     * Constructor of a generic DTO class
     * 
     * @param name of the entry
     * @param imagePath where the icon of the entry is allocated
     * @param description of the entry
     * @param id of the entry
     * 
     */
    public EntryDTO(String name, String imagePath, String description, int id) {
        
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
        this.id = id;
    }

    public int getId() {return id;}

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIdValue() {
       return this.getId(); 
    }

    
}