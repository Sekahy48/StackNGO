package command.add.collection;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;

public class AddCollectionImageCommand implements ICommand {

    @Override
    public void execute(AbstractController controller) {
        ((AbstractAddController) controller).chooseImage();
    }

    @Override
    public void clear() {

    }

}
