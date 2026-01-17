package command.delete;

import command.ICommand;
import identificators.EntryId;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowRecipeDataController;

public class DeleteRecipeCommand implements ICommand {

    private EntryId id;

    public DeleteRecipeCommand(EntryId id) {
        this.id = id;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowRecipeDataController) controller).deleteRecipe(this.id);
    }

    @Override
    public void clear() {
        this.id = null;
    }
}