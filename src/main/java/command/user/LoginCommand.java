package command.user;

import command.ICommand;
import mvc.controller.AbstractController;
import mvc.controller.user.LoginController;

public class LoginCommand implements ICommand {

    private String user, password;

    public LoginCommand(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void execute(AbstractController controller) {
        ((LoginController)controller).login(user, password);
        clear();
    }

    public void clear() {
        this.user = "";
        this.password = "";
    }
}