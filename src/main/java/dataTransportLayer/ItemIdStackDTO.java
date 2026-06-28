package dataTransportLayer;

public class ItemIdStackDTO implements GenericDTO{
    public int id;
    public int amount;

    // Añadido para poder buscar por nombre en el DataImporter
    public String name;

    public ItemIdStackDTO(int id, int amount){
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getIdValue() {
       return id; 
    }
}
