package dataTransportLayer;

import java.util.ArrayList;

public class RecipeDTO extends EntryDTO {

    public ArrayList<ItemIdStackDTO> ingredients;
    public ArrayList<ItemIdStackDTO> results;

    /**
     * 
     * Constructor of a DTO for the recipe 
     * 
     * @param ingredients needed for the recipe to be made
     * @param results amount of items once the recipe is made
     * @param name of the recipe
     * @param iconPath where the icon of the recipe is allocated
     * @param description of the recipe
     * @param id of the recipe
     * 
     */
    public RecipeDTO(ArrayList<ItemIdStackDTO> ingredients, ArrayList<ItemIdStackDTO> results, String name, String iconPath, String description, int id) {
        super(name, iconPath, description, id);
    
        this.ingredients = ingredients;
        this.results = results;
    }
}
