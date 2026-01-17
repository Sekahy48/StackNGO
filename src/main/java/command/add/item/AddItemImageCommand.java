package command.add.item;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;

public class AddItemImageCommand implements ICommand {

    @Override
    public void execute(AbstractController controller) {
        ((AbstractAddController) controller).chooseImage();
    }

    @Override
    public void clear() {

    }
}