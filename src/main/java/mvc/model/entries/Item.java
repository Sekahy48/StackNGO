package mvc.model.entries;
 

public class Item extends Entry {
    public Item(String name, String description, String imagePath, int id) {
        super(name, description, imagePath, id);
    }
    
    public Item(String name, int id) {
        super(name, id);
    }
    
}
