package command.delete;

import command.ICommand;
import dataTransportLayer.CollectionDTO;
import identificators.EntryId;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowCollectionDataController;

public class DeleteCollectionCommand implements ICommand {

    private EntryId id;

    public DeleteCollectionCommand(EntryId id) {
        this.id = id;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowCollectionDataController) controller).deleteCollection(this.id);
        clear();
    }

    public void clear() {
        this.id = null;
    }
}