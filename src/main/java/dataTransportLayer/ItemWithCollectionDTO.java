package dataTransportLayer;

public class ItemWithCollectionDTO implements GenericDTO{
    public final ItemDTO item;
    public final String collection;

    public ItemWithCollectionDTO(ItemDTO item, String collection){
        this.item = item;
        this.collection = collection;
    }

    @Override
    public String getImagePath() {
        return item.getImagePath();
    }

    @Override
    public String getName() {
        return item.getName();
    }

    @Override
    public int getIdValue() {
       return item.id; 
    }
}
