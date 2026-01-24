package command.screen;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.view.ViewType;

public class ChangeScreenCommand implements ICommand {

    protected ViewType view;

    public ChangeScreenCommand(ViewType type) {
        this.view = type;
    }

    @Override
    public void execute(AbstractController controller) {
        controller.getRuntimeContext().getSystemContext().show(view);
    }

    @Override
    public void clear() {
        this.view = null;
    }

}
