package command.show;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.show.ShowCollectionsController;

public class ShowCollections implements ICommand {


    public ShowCollections() { 
    }

    @Override
    public void execute(AbstractController controller) {
        ShowCollectionsController ctrlr = (ShowCollectionsController) controller;
        ctrlr.populateGrid();
    }

    @Override
    public void clear() {

    }
}
