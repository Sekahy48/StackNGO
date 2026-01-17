package command.user;

import command.ICommand;
import domain.accounts.Account;
import mvc.controller.AbstractController;
import mvc.controller.user.PrivateController;

public class DeleteAccountCommand implements ICommand {

    private Account account;

    public DeleteAccountCommand(Account target) {
        this.account = target;
    }

    @Override
    public void execute(AbstractController controller) {
        ((PrivateController) controller).delete(this.account);
        clear();
    }

    @Override
    public void clear() {
        this.account = null;
    }
}