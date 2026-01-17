package dataTransportLayer;

public class ItemStackDTO implements GenericDTO{
    public ItemDTO item;
    public int amount;

    public ItemStackDTO(ItemDTO item, int amount){
        this.item = item;
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
