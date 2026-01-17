package command.user;

import command.ICommand;
import domain.accounts.Account;
import mvc.controller.AbstractController;
import mvc.controller.show.ShowAccountsController;
import mvc.controller.user.PrivateController;

public class DeleteAccountAdminCommand implements ICommand {

    private Account account;

    public DeleteAccountAdminCommand(Account target) {
        this.account = target;
    }

    @Override
    public void execute(AbstractController controller) {
        ((ShowAccountsController) controller).delete(this.account);
        clear();
    }

    @Override
    public void clear() {
        this.account = null;
    }
}