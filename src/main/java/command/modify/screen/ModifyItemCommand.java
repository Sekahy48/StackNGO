package command.modify.screen;

import command.ICommand;
import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO;
import identificators.EntryId;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.AbstractController;
import mvc.controller.modify.CollectionModifyController;
import mvc.controller.modify.ItemModifyController;
import mvc.controller.modify.ModType;

public class ModifyItemCommand implements ICommand{

    private ItemDTO dto;

    public ModifyItemCommand(ItemDTO dto){
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ItemModifyController) controller).getView().modifyFields((EntryDTO)dto);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

}


