package dataTransportLayer;

public class ItemStackDTO implements GenericDTO{
    public ItemDTO item;
    public int amount;

    public ItemStackDTO(ItemDTO item, int amount){
        this.item = item;
        this.amount = amount;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getIdValue() {
       return item.id; 
    }
}
