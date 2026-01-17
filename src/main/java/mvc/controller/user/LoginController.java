package mvc.controller.user;

import command.screen.ChangeScreenCommand;
import command.screen.ChangeToCommand;
import command.user.LoginCommand;
import dataTransportLayer.EventBuffer;
import domain.accounts.Account;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.user.LoginView;
import static security.State.BLOCKED;

/**
 *
 * Controler that manages logic related to {@link LoginView}
 *
 */
public class LoginController extends AbstractUserController<LoginView> {


    public LoginController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(LoginView view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButton() {

        Button confirmButton = view.getConfirmButton();
        Button signUpButton = view.getSignUpBtn();



        confirmButton.setOnAction(
                e -> {
                    String user = view.getUsername().getText().trim();
                    String password = view.getPassword().getText().trim();
                    buffer.publish(new LoginCommand(user, password));
                }
        );
        signUpButton.setOnAction(
                e -> {
                    buffer.publish(new ChangeScreenCommand(ViewType.SIGN_UP));
                }
        );
    }

    public void login(String user, String password) {
        Account account = context.getAccount(user);
        if  (account == null) {

            this.view.showAlert("Cuenta incorrecta", "No existe ninguna cuenta con ese nombre", Alert.AlertType.WARNING);
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "No existe una cuenta con nombre " + user);

        } else if (!account.verify(password)) {

            this.view.showAlert("Contraseña incorrecta", "Esta contraseña es incorrecta", Alert.AlertType.WARNING);
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "Contraseña incorrecta para el usuario " + user);

        } else if (account.getState() == BLOCKED) {

            this.view.showAlert("Cuenta bloqueada", "Esta cuenta esta bloqueada", Alert.AlertType.WARNING);

        } else {

            this.view.showAlert("Acceso realizado", "Has iniciado sesion correctamente", Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "El usuario " + user + " ha iniciado sesion");
            context.setAccount(account);
            buffer.publish(new ChangeToCommand(ViewType.PRIVATE_ZONE, user));

            this.context.getCoreController().setUserNameInPrivateZone();
        }
    }
}