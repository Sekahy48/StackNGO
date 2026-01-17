package command.show;

import command.ICommand;
import command.screen.ChangeToCommand;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.RecipeDTO;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowCollectionDataController;
import mvc.controller.show.entry.data.ShowRecipeDataController;
import mvc.view.ViewType;

public class ShowRecipe implements ICommand {

    private RecipeDTO dto;

    public ShowRecipe(RecipeDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        controller.getBuffer().publish(new ChangeToCommand(ViewType.SHOW_RECIPE, dto));
        ((ShowRecipeDataController) controller).showRecipe(dto.id);

    }

    @Override
    public void clear() {
        this.dto = null;
    }
}
