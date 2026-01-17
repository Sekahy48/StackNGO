package dataTransportLayer; 

public class RecipeWithCollectionDTO implements GenericDTO{
    public final RecipeDTO recipe; // asumo que tu RecipeDTO es ItemDTO, como en tu código
    public final String collection;

    public RecipeWithCollectionDTO(RecipeDTO recipe, String collection) {
        this.recipe = recipe;
        this.collection = collection;
    }

    @Override
    public String getIconPath() {
        return recipe.getIconPath();
    }

    @Override
    public String getName() {
        return recipe.getName();
    }
}
