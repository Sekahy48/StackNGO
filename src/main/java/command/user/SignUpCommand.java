package command.user;

import command.ICommand;
import dataTransportLayer.AccountDTO;
import domain.accounts.Account;
import mvc.controller.AbstractController;
import mvc.controller.user.SignUpController;

public class SignUpCommand implements ICommand {

    private AccountDTO dto;
    private String checkPassword;

    public SignUpCommand(AccountDTO dto, String checkPassword) {
        this.dto = dto;
        this.checkPassword = checkPassword;
    }

    @Override
    public void execute(AbstractController controller) {
        ((SignUpController) controller).signUp(dto, checkPassword);
    }

    @Override
    public void clear() {

    }
}