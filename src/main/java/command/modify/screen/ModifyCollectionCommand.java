package command.modify.screen;

import command.ICommand;
import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import identificators.EntryId;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.AbstractController;
import mvc.controller.modify.CollectionModifyController;
import mvc.controller.modify.ModType;

public class ModifyCollectionCommand implements ICommand{

    private CollectionDTO dto;

    public ModifyCollectionCommand(CollectionDTO dto){
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((CollectionModifyController) controller).getView().modifyFields((EntryDTO)dto,
                                                                         DTOFactory.genericsFromEntries(DTOFactory.itemsAsEntries(controller.getRuntimeContext().getItemsByCollection(new EntryId(dto.id)))), 
                                                                         DTOFactory.genericsFromEntries(DTOFactory.recipesAsEntries(controller.getRuntimeContext().getRecipesByCollection(new EntryId(dto.id))))
                                                                         )
                                                                         ;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

}

