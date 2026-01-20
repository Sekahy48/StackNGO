package mvc.controller.show;

import java.util.List;

import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowAccounts;
import command.user.DeleteAccountAdminCommand;
import command.user.DeleteAccountCommand;
import dataAccessLayer.DAO.AccountDAO;
import dataAccessLayer.DAO.DAOType;
import dataTransportLayer.AccountDTO;
import dataTransportLayer.EventBuffer;
import domain.accounts.Account;
import javafx.scene.control.Alert;
import logger.LogLevel;
import logger.Logger;
import mvc.view.ViewType;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowAccountsController extends ShowGridDisplayController<AccountDTO> {

    public ShowAccountsController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    protected List<AccountDTO> getElements() {
        return context.getAccounts();
    }

    @Override
    protected String getTitle(AccountDTO dto) {
        return dto.name;
    }

    @Override
    protected String getImagePath(AccountDTO dto) {
        return null;
    }

    @Override
    protected RedirectCommand createCommand(AccountDTO dto) {
        Account account = context.getAccount(dto.name);
        if (account.getId().equals(this.context.getAccount().getId())) {
            return new RedirectCommand(this.context.getCoreController().getController(ViewType.PRIVATE_ZONE).getBuffer(),
                    new DeleteAccountCommand(account));
        }
        return new RedirectCommand(this.context.getCoreController().getController(ViewType.SHOW_ACCOUNTS).getBuffer(),
                new DeleteAccountAdminCommand(account));
    }

    public void delete(Account account) {
        boolean confirmed = view.showAlert(
                "Confirmar eliminacion",
                "Seguro que quieres borrar esta cuenta?",
                Alert.AlertType.CONFIRMATION
        );
        if (confirmed) {
            AccountDAO dao = (AccountDAO) this.context.getDAO(DAOType.ACCOUNT);
            String adminName = this.context.getAccount().getUsername();
            dao.delete(account.getId().value());

            this.view.showAlert("Cuenta eliminada", "La cuenta con nombre " + account.getUsername() + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),"El administrador " + adminName + " ha borrado la cuenta con nombre " + account.getUsername());

            this.buffer.publish(new ShowAccounts());
            this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_ACCOUNTS));

        }
    }
}
