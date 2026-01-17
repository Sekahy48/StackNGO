package dataTransportLayer;

import java.util.ArrayList;

public class CollectionDTO extends EntryDTO {

    public ArrayList<Integer> items;
    public ArrayList<Integer> recipes;

    /**
     * 
     * Constructor of a DTO for the collection
     * 
     * @param items a collection holds
     * @param recipes a collection holds
     * @param name of the collection
     * @param iconPath where the icon of the collection is allocated
     * @param description of the collection
     * @param id of the collection
     */
    public CollectionDTO(ArrayList<Integer> items, ArrayList<Integer> recipes, String name, String iconPath, String description, int id) {
        super(name, iconPath, description, id);

        this.items = items;
        this.recipes = recipes;
    }
}
