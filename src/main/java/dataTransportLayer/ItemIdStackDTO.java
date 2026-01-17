package dataTransportLayer;

public class ItemIdStackDTO implements GenericDTO{
    public int id;
    public int amount;

    public ItemIdStackDTO(int id, int amount){
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
