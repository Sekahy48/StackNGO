package command.modify.screen;

import command.ICommand;
import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.AbstractController;
import mvc.controller.modify.CollectionModifyController;
import mvc.controller.modify.ModType;
import mvc.controller.modify.RecipeModifyController;

public class ModifyRecipeCommand implements ICommand{

    private RecipeDTO dto;

    public ModifyRecipeCommand(RecipeDTO dto){
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((RecipeModifyController) controller).getView().modifyFields(dto);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }
}
