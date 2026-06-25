package mvc.controller.show.multiple;
 
import java.util.List;
 
import dataTransportLayer.RecipeDTO;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowRecipesController extends ShowGridDisplayController<RecipeDTO> {

 

    @Override
    protected List<RecipeDTO> getElements() {
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return recipeService.getAllDTO(sessionService.getCurrentCollectionDTO().id);
    } 

    @Override
    protected void onClickElementEvent(RecipeDTO dto) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        sessionService.setCurrentRecipe(dto);
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }
 
}
