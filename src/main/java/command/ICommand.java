package command;

import mvc.controller.AbstractController;

public interface ICommand {
    void execute(AbstractController controller);
    void clear();
}
