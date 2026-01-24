package mvc.controller.user;

import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowAccounts;
import command.show.ShowCollections;
import command.user.DeleteAccountCommand;
import dataAccessLayer.DAO.AccountDAO;
import dataAccessLayer.DAO.DAOType;
import dataTransportLayer.AccountDTO;
import dataTransportLayer.EventBuffer;
import domain.accounts.Account;
import static domain.accounts.AccountType.ADMIN;
import identificators.AccountId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import logger.LogLevel;
import logger.Logger;
import mvc.utils.DataExporter;
import mvc.utils.DataImporter;
import mvc.view.ViewType;
import mvc.view.user.PrivateView;

public class PrivateController extends AbstractUserController<PrivateView> {

    public PrivateController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(PrivateView view) {
        this.view = view;
        super.attachView(view);
        
    }

     
    
    @Override
    public void handleButton() {

        commonHandleButton();

        PrivateView view = this.getView();

        Button addButton = view.getAddCollectionButton();
        Button seeCollectionsButton = view.getSeeCollectionsButton();
        MenuItem logout = view.getLogoutItem();
        MenuItem delete = view.getDeleteAccountItem();
        Button adminButton = view.getAdminButton();
        Button exportCollectionsButton = view.getExportCollectionsButton();
        Button importCollectionsButton = view.getImportCollectionsButton();
        DataImporter importer = new DataImporter(this.context);
        DataExporter exporter = new DataExporter(this.context);

        exportCollectionsButton.setOnAction(event -> {
            exporter.exportUserData();
        });

        importCollectionsButton.setOnAction(event -> {
            importer.importUserData();
        });

        adminButton.setOnAction(event -> {
            boolean isAdmin = context.getAccount().getType() == ADMIN;
            if (isAdmin) {
                AccountId a = this.context.getAccount().getId();
                AccountDAO dao = (AccountDAO) this.context.getDAO(DAOType.ACCOUNT);
                //TODO revisar esto
                AccountDTO dto = dao.read(a.value());
                this.buffer.publish(new RedirectCommand(
                        this.context.getSystemContext().getController(ViewType.SHOW_ACCOUNTS).getBuffer(),
                        new ShowAccounts()
                ));
                this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_ACCOUNTS));
            } else {
                this.view.showAlert("Accion no permitida", "Funcionalidad exclusiva para administradores", Alert.AlertType.ERROR);
            }

        });

        addButton.setOnAction(

            e -> {
                this.buffer.publish(new ChangeScreenCommand(ViewType.ADD_COLLECTION));
            }

        );

        delete.setOnAction(
                e -> {

                        Account account = this.context.getAccount();
                        this.buffer.publish(new DeleteAccountCommand(account));

                }
        );

        seeCollectionsButton.setOnAction(
                e -> {
                    this.buffer.publish(new RedirectCommand(
                            this.context.getSystemContext().getController(ViewType.SHOW_COLLECTIONS).getBuffer(),
                            new ShowCollections()
                            )
                    );
                    this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTIONS));
                }
        );

        logout.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.LOG_IN));
                }
        );

        /*themeButton.setOnAction(
            e -> {
                this.context.getScreenManager().changeTheme();
            }
        );*/
    }

    public void delete(Account account) {
        boolean confirmed = view.showAlert(
                "Confirmar eliminacion",
                "Seguro que quieres borrar esta cuenta?",
                Alert.AlertType.CONFIRMATION
        );
        if (confirmed) {
            this.buffer.publish(new ChangeScreenCommand(ViewType.LOG_IN));
            this.context.getDAO(DAOType.ACCOUNT).delete(account.getId().value());
            this.view.showAlert("Cuenta eliminada", "Tu cuenta con nombre " + account.getUsername() + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "el usuario " + this.context.getAccount().getUsername() + " ha borrado su cuenta");
        } else {

        }
    }

    @Override
    public void updateAtShow(){
        view.getUserTitleLabel().setText(view.getParentName());
    }
}