package command.add.recipe;

import command.ICommand;
import dataTransportLayer.RecipeDTO;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;
import mvc.controller.add.AddItemController;
import mvc.controller.add.AddRecipeController;

public class AddRecipeCommand implements ICommand {

    private RecipeDTO dto;
    public AddRecipeCommand(RecipeDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((AddRecipeController) controller).create(dto);
        clear();
    }

    @Override
    public void clear() {
        this.dto = null;
    }
}
