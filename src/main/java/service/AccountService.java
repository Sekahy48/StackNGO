package service;

import java.util.List;

import creational.AccountFactory;
import dataTransportLayer.AccountDTO;
import domain.accounts.Account;
import mvc.context.DataContext;

public class AccountService implements IService {

    private final DataContext context;
    private final AccountFactory accountFactory;
    
    public AccountService(DataContext context) {
        this.context = context;
        this.accountFactory = new AccountFactory();
    }

    @Override
    public ServiceType getType() { 
        return ServiceType.ACCOUNT;
    }

    public AccountDTO getAccountDTOByName(String name) {
        return context.getAccountDAO().read(name);
    }

    public AccountDTO getAccountDTOById(int id) {
        return context.getAccountDAO().read(id);
    }

    public List<AccountDTO> getAllAccounts() {
        return context.getAccountDAO().readAll();
    }

    public Account createAccount(AccountDTO accountDTO) {
        return accountFactory.createAccount(accountDTO);
    }

    public boolean saveAccount(AccountDTO accountDTO) {
        return context.getAccountDAO().create(this.createAccount(accountDTO), null);
    }

    public Account getAccount(String name) {
        AccountDTO accountDTO = context.getAccountDAO().read(name);
        if (accountDTO == null) {
            return null;
        }
        return accountFactory.createAccount(accountDTO);
    }
    
    public boolean deleteAccount(int id) {
        return context.getAccountDAO().delete(id);
    }

    public boolean existsAccount(String name) {
        return context.getAccountDAO().read(name) != null;
    }

}
