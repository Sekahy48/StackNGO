package command.delete;

import command.ICommand;
import identificators.EntryId;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowCollectionDataController;
import mvc.controller.show.entry.data.ShowItemDataController;

public class DeleteItemCommand implements ICommand {

    private EntryId id;

    public DeleteItemCommand(EntryId id) {
        this.id = id;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowItemDataController) controller).deleteItem(this.id);
        clear();
    }

    public void clear() {
        this.id = null;
    }
}
