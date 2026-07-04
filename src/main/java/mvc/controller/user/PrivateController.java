package mvc.controller.user;
 
import dataTransportLayer.AccountDTO;
import domain.accounts.Account;
import event.EventBus;
import event.NavigateEvent;

import static domain.accounts.AccountType.ADMIN;

import java.util.Set;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import logger.Logger;
import mvc.utils.DataExporter;
import mvc.utils.DataImporter;
import mvc.view.ViewType;
import mvc.view.user.PrivateView;
import service.AccountService;
import service.ServiceType;
import service.SessionService;

public class PrivateController extends AbstractUserController<PrivateView> {

    private DataExporter exporter;
    private DataImporter importer; 

    public PrivateController setExporter(DataExporter exporter) {
        this.exporter = exporter;
        return this;
    }

    public PrivateController setImporter(DataImporter importer) {
        this.importer = importer;
        return this;
    }
    
    @Override
    public void attachView(PrivateView view) {
        this.view = view;
        super.attachView(view);
        
    }

    @Override
    public void handleButtons() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        AccountService accountService = this.getService(ServiceType.ACCOUNT); 
        commonHandleButton();

        PrivateView view = this.getView();

        Button addCollectionButton = view.getAddCollectionButton();
        Button seeCollectionsButton = view.getSeeCollectionsButton();
        Button addComponentButton = view.getAddComponentButton();
        Button seeComponentsButton = view.getSeeComponentsButton();
        MenuItem logout = view.getLogoutItem();
        MenuItem delete = view.getDeleteAccountItem();
        Button adminButton = view.getAdminButton();
        Button exportCollectionsButton = view.getExportCollectionsButton();
        Button importCollectionsButton = view.getImportCollectionsButton();
        exportCollectionsButton.setOnAction(event -> {
            this.exporter.exportUserData();
        });

        importCollectionsButton.setOnAction(event -> {
            this.importer.importUserData();
        });

        adminButton.setOnAction(
            event -> {
                Account currentAccount = sessionService.getCurrentAccount();
                boolean isAdmin = currentAccount.getType() == ADMIN;
                if (isAdmin) {
                    EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ACCOUNTS));
                } else {
                    this.view.showAlert("Accion no permitida", "Funcionalidad exclusiva para administradores", Alert.AlertType.ERROR);
                }

            });

        addCollectionButton.setOnAction(e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.ADD_COLLECTION));});

        addComponentButton.setOnAction(e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.ADD_COMPONENT));});

        delete.setOnAction(
            e -> { 

                Account account = sessionService.getCurrentAccount();
                AccountDTO dto = accountService.getAccountDTOById(account.getId().value());
                this.delete(dto);

            }
        );

        seeCollectionsButton.setOnAction(e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTIONS)); });

        seeComponentsButton.setOnAction(e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENTS)); });

        logout.setOnAction(
            e -> {
                EventBus.getInstance().publish(new NavigateEvent(ViewType.LOG_IN));
                sessionService.untrackCurrentAccount();
            }
        ); 

        /*themeButton.setOnAction(
            e -> {
                this.context.getScreenManager().changeTheme();
            }
        );*/
    }

    public void delete(AccountDTO account) {
        AccountService accountService = this.getService(ServiceType.ACCOUNT);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        boolean confirmed = view.showAlert(
                "Confirmar eliminacion",
                "Seguro que quieres borrar esta cuenta?",
                Alert.AlertType.CONFIRMATION
        );
        if (confirmed) {
            EventBus.getInstance().publish(new NavigateEvent(ViewType.LOG_IN));
            accountService.deleteAccount(account.id); 
            this.view.showAlert("Cuenta eliminada", "Tu cuenta con nombre " + account.name + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "el usuario " + sessionService.getCurrentAccount().getUsername() + " ha borrado su cuenta");
        }
    }

    @Override
    public void updateAtShow(){
        view.getUserTitleLabel().setText(view.getParentName());
    }

    @Override
    public void onReturnEvent() {
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ACCOUNT, ServiceType.SESSION); 
    }
}