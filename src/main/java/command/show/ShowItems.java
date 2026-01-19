package command.show;

import command.ICommand;
import command.screen.ChangeScreenCommand;
import mvc.controller.AbstractController; 
import mvc.controller.show.ShowItemsController;
import mvc.view.ViewType;

public class ShowItems implements ICommand {


    public ShowItems() { 
    }

    @Override
    public void execute(AbstractController controller) {
        ShowItemsController ctrlr = (ShowItemsController) controller;
        ctrlr.populateGrid();
        controller.getBuffer().publish(new ChangeScreenCommand(ViewType.SHOW_ITEMS));
    }

    @Override
    public void clear() {

    }
}
