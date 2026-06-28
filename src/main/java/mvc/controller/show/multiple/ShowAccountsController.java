package mvc.controller.show.multiple;

import java.util.List;
import java.util.Set;

import dataTransportLayer.AccountDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import logger.Logger;
import mvc.view.ViewType;
import service.AccountService;
import service.ServiceType;
import service.SessionService;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowAccountsController extends ShowGridDisplayController<AccountDTO> { 

    @Override
    protected List<AccountDTO> getElements() {
        AccountService accountService = this.getService(ServiceType.ACCOUNT);
        return accountService.getAllAccounts();
    }  

    @Override
    protected void onClickElementEvent(AccountDTO dto) {
        this.delete(dto);
    }


    public void delete(AccountDTO account) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        AccountService accountService = this.getService(ServiceType.ACCOUNT);
        boolean isCurrentAccount = account.id == sessionService.getCurrentAccount().getId().value();
        
        boolean confirmed = view.showAlert(

                "Confirmar eliminacion",
                "Seguro que quieres borrar esta cuenta?" +
                (isCurrentAccount ? "Estas a punto de borrar tu PROPIA cuenta" : ""),
                Alert.AlertType.CONFIRMATION
        );
        if (confirmed) { 
            String adminName = sessionService.getCurrentAccount().getUsername();
            accountService.deleteAccount(account.id);

            this.view.showAlert("Cuenta eliminada", "La cuenta con nombre " + account.name + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(),"El administrador " + adminName + " ha borrado la cuenta con nombre " + account.name);

            }
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));    
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ACCOUNT, ServiceType.SESSION); 
    }
}
