package command.add.collection;

import command.ICommand;
import dataTransportLayer.CollectionDTO;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;
import mvc.controller.add.AddCollectionController;

public class AddCollectionCommand implements ICommand {

    private CollectionDTO dto;

    public AddCollectionCommand(CollectionDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {

        ((AddCollectionController) controller).create(this.dto);
    }

    @Override
    public void clear() {
        this.dto = null;
    }
}