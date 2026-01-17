package command.show;

import command.ICommand;
import command.screen.ChangeToCommand;
import command.screen.RedirectCommand;
import dataTransportLayer.AccountDTO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowCollectionDataController;
import mvc.view.ViewType;

public class ShowCollection implements ICommand {

    private CollectionDTO dto;

    public ShowCollection(CollectionDTO dto) {
        this.dto = dto;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowCollectionDataController) controller).showCollection(dto.id);

        controller.getBuffer().publish(new ChangeToCommand(ViewType.SHOW_COLLECTION, dto));


    }

    @Override
    public void clear() {
        this.dto = null;
    }
}
