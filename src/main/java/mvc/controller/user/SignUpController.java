package mvc.controller.user;
 
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.AccountDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.Logger;
import mvc.model.entries.repository.EntryIdGenerator;
import mvc.view.ViewType;
import mvc.view.user.SignUpView;
import security.SecuritySystem;
import service.AccountService;
import service.ServiceType;

/**
 *
 * Controller that manages logic related to {@link mvc.view.user.SignUpView}
 *
 */
public class SignUpController extends AbstractUserController<SignUpView> {
 
    @Override
    public void attachView(SignUpView view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButtons() {

        Button confirmButton = view.getConfirmButton();
        Button logInButton = view.getLoginButton();
        Button createAdminButton = view.getCreateAdminButton();


        confirmButton.setOnAction(
            e -> {
                String user = view.getUsername().getText().trim();
                String password = view.getPassword().getText().trim();
                String checkPassword = view.getCheckPassword().getText().trim();


                AccountDTO dto = DTOFactory.accountRegister(
                    user, "USER", password, EntryIdGenerator.getInstance().generateId()
                );

                this.signUp(dto, checkPassword); 
                EventBus.getInstance().publish(new NavigateEvent(ViewType.LOG_IN));
            }
        );

        createAdminButton.setOnAction(
            e -> {
                String user = view.getUsername().getText().trim();
                String password = view.getPassword().getText().trim();
                String checkPassword = view.getCheckPassword().getText().trim();

                AccountDTO dto = DTOFactory.accountRegister(
                        user, "ADMIN", password, EntryIdGenerator.getInstance().generateId()
                );

                this.signUp(dto, checkPassword);
                EventBus.getInstance().publish(new NavigateEvent(ViewType.LOG_IN));
            }
        );

        logInButton.setOnAction(
            e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.LOG_IN));}
        );
    }

    public void signUp(AccountDTO dto, String checkPassword) {
        AccountService accountService = this.getService(ServiceType.ACCOUNT);

        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre incorrecto", "Una cuenta debe tener un nombre valido", Alert.AlertType.WARNING);

        } else if (dto.password.isEmpty()) {
            this.view.showAlert("Contraseña incorrecta", "Una cuenta debe tener una contraseña valida", Alert.AlertType.WARNING);

        } else if (accountService.existsAccount(dto.name)) {
            this.view.showAlert("Cuenta ya existente", "La cuenta con nombre " + dto.name + " ya existe", Alert.AlertType.WARNING);
            Logger.getInstance().warning(this.getClass().toString(), "Intento de creacion de una cuenta existente con nombre " + dto.name);

        } else if (!SecuritySystem.same(dto.password, checkPassword)) {
            this.view.showAlert("Contraseñas incorrectas", "Ambas contraseñas no son iguales", Alert.AlertType.WARNING);
        } else {

            this.view.showAlert("Registro correcto", "La cuenta con nombre " + dto.name + " ha sido creada correctamente" , Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "El usuario " + dto.name + " ha creado su cuenta correctamente" );

            accountService.saveAccount(dto); 
        }
    }

    @Override
    public void onReturnEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ACCOUNT); 
    }
}