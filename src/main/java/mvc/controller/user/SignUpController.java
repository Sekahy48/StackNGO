package mvc.controller.user;

import command.screen.ChangeScreenCommand;
import command.user.SignUpCommand;
import creational.DTOFactory;
import dataAccessLayer.DAO.AccountDAO;
import dataAccessLayer.DAO.DAOType;
import dataTransportLayer.AccountDTO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EventBuffer;
import domain.accounts.Account;
import domain.accounts.AccountType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.model.entries.repository.EntryIdGenerator;
import mvc.view.ViewType;
import mvc.view.user.SignUpView;
import security.Hasher;
import security.Salt;
import security.SecuritySystem;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * Controller that manages logic related to {@link mvc.view.user.SignUpView}
 *
 */
public class SignUpController extends AbstractUserController<SignUpView> {

    public SignUpController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(SignUpView view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButton() {

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

                    buffer.publish(new SignUpCommand(dto, checkPassword));
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

                    buffer.publish(new SignUpCommand(dto, checkPassword));
                }
        );

        logInButton.setOnAction(
                e -> {
                    buffer.publish(new ChangeScreenCommand(ViewType.LOG_IN));
                }
        );
    }

    public void signUp(AccountDTO dto, String checkPassword) {
        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre incorrecto", "Una cuenta debe tener un nombre valido", Alert.AlertType.WARNING);

        } else if (dto.password.isEmpty()) {
            this.view.showAlert("Contraseña incorrecta", "Una cuenta debe tener una contraseña valida", Alert.AlertType.WARNING);

        } else if (context.getAccount(dto.name) != null) {
            this.view.showAlert("Cuenta ya existente", "La cuenta con nombre " + dto.name + " ya existe", Alert.AlertType.WARNING);
            Logger.getInstance().log(LogLevel.WARNING, this.getClass().toString(), "Intento de creacion de una cuenta existente con nombre " + dto.name);

        } else if (!SecuritySystem.same(dto.password, checkPassword)) {
            this.view.showAlert("Contraseñas incorrectas", "Ambas contraseñas no son iguales", Alert.AlertType.WARNING);
        } else {

            this.view.showAlert("Registro correcto", "La cuenta con nombre " + dto.name + " ha sido creada correctamente" , Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "El usuario " + dto.name + " ha creado su cuenta correctamente" );

            AccountDAO accountDAO = (AccountDAO) this.context.getDAO(DAOType.ACCOUNT);
            accountDAO.create(
                    context.createAccount(dto),
                    null
            );
            this.buffer.publish(new ChangeScreenCommand(ViewType.LOG_IN));
        }
    }
}