package command.show;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowItemDataController;

public class ShowItem implements ICommand {

    private Integer id;
    private ICommand backCommand;

    public ShowItem(Integer itemId, ICommand backCommand) {
        this.id = itemId;
        this.backCommand = backCommand;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowItemDataController) controller).showItem(this.id);
        controller.setBackNavigation(backCommand);
        clear();
    }

    @Override
    public void clear() {
        this.id = null;
        this.backCommand = null;
    }

    
}