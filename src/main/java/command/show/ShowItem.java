package command.show;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.show.entry.data.ShowItemDataController;

public class ShowItem implements ICommand {

    private Integer id;

    public ShowItem(Integer id) {
        this.id = id;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowItemDataController) controller).showItem(this.id);
        clear();
    }

    @Override
    public void clear() {
        this.id = null;
    }
}