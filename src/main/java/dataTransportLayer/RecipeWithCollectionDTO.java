package dataTransportLayer; 

public class RecipeWithCollectionDTO implements GenericDTO{
    public final RecipeDTO recipe; // asumo que tu RecipeDTO es ItemDTO, como en tu código
    public final String collection;

    public RecipeWithCollectionDTO(RecipeDTO recipe, String collection) {
        this.recipe = recipe;
        this.collection = collection;
    }

    @Override
    public String getImagePath() {
        return recipe.getImagePath();
    }

    @Override
    public String getName() {
        return recipe.getName();
    }

    @Override
    public int getIdValue() {
       return recipe.id; 
    }
}
