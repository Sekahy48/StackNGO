package command.add.recipe;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;

public class AddRecipeImageCommand implements ICommand {

    @Override
    public void execute(AbstractController controller) {
        ((AbstractAddController) controller).chooseImage();
    }

    @Override
    public void clear() {

    }
}
