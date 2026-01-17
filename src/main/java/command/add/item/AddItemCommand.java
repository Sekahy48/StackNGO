package command.add.item;

import command.ICommand;
import dataTransportLayer.ItemDTO;
import mvc.controller.AbstractController;
import mvc.controller.add.AbstractAddController;
import mvc.controller.add.AddItemController;

public class AddItemCommand implements ICommand {

    private ItemDTO dto;
    public AddItemCommand(ItemDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((AddItemController) controller).create(dto);
    }

    @Override
    public void clear() {
        this.dto = null;
    }
}
