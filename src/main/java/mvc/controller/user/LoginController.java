package mvc.controller.user;
 
import domain.accounts.Account;
import event.EventBus;
import event.NavigateEvent;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; 
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.user.LoginView;
import service.AccountService;
import service.ServiceType;
import service.SessionService;

import static security.State.BLOCKED;

/**
 *
 * Controler that manages logic related to {@link LoginView}
 *
 */
public class LoginController extends AbstractUserController<LoginView> {

    @Override
    public void attachView(LoginView view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButtons() {

        Button confirmButton = view.getConfirmButton();
        Button signUpButton = view.getSignUpBtn(); 

        confirmButton.setOnAction(
                e -> {
                    String user = view.getUsername().getText().trim();
                    String password = view.getPassword().getText().trim(); 
                    login(user, password);
                }
        );
        signUpButton.setOnAction(
                e -> {
                    EventBus.getInstance().publish(new NavigateEvent(ViewType.SIGN_UP));
                }
        );
    }

    public void login(String user, String password) {
        AccountService accountService = this.getService(ServiceType.ACCOUNT);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        
        Account account = accountService.getAccount(user);
        if  (account == null) {

            this.view.showAlert("Cuenta incorrecta", "No existe ninguna cuenta con ese nombre", Alert.AlertType.WARNING);
            Logger.getInstance().error(this.getClass().toString(), "No existe una cuenta con nombre " + user);

        } else if (!account.verify(password)) {

            this.view.showAlert("Contraseña incorrecta", "La contraseña es incorrecta", Alert.AlertType.WARNING);
            Logger.getInstance().error(this.getClass().toString(), "Contraseña incorrecta para el usuario " + user);

        } else if (account.getState() == BLOCKED) {

            this.view.showAlert("Cuenta bloqueada", "Esta cuenta esta bloqueada", Alert.AlertType.WARNING);

        } else {

            this.view.showAlert("Acceso permitido", "Has iniciado sesion correctamente", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "El usuario " + user + " ha iniciado sesion");
            sessionService.setCurrentAccount(account);
            EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));

            
        }
    }

    @Override
    public void onReturnEvent() {
        Platform.exit();
    }

    @Override
    public void updateAtShow() {
        this.view.clearFields();
    }
}