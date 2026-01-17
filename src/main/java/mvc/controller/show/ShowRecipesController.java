package mvc.controller.show;
 
import java.util.List;

import command.screen.RedirectCommand;
import command.show.ShowCollection;
import command.show.ShowItem;
import command.show.ShowRecipe;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.RecipeWithCollectionDTO; 

/**
 * Controlador para la vista de cuentas.
 */
public class ShowRecipesController extends ShowGridDisplayController<RecipeWithCollectionDTO> {

    public ShowRecipesController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    protected List<RecipeWithCollectionDTO> getElements() {
        return this.context.getRecipes();
    }

    @Override
    protected String getTitle(RecipeWithCollectionDTO dto) {
        return dto.recipe.name;
    }

    @Override
    protected String getImagePath(RecipeWithCollectionDTO dto) {
        return dto.recipe.iconPath; // accounts no tienen imagen
    }

    @Override
    protected RedirectCommand createCommand(RecipeWithCollectionDTO dto) {
        return new RedirectCommand(
                this.context.getCoreController().getShowRecipeDataBuffer(),
                new ShowRecipe(dto.recipe));
    }
}
