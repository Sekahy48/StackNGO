package command.show;

import command.ICommand;
import dataTransportLayer.AccountDTO;
import mvc.controller.AbstractController;
import mvc.controller.show.ShowAccountsController;

public class ShowAccounts implements ICommand {

    public ShowAccounts() {
    }

    @Override
    public void execute(AbstractController controller) {
        ShowAccountsController ctrlr = (ShowAccountsController) controller;
        ctrlr.populateGrid();
    }

    @Override
    public void clear() {

    }
}
