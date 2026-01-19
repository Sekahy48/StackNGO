package mvc.context;

import dataTransportLayer.CollectionDTO;
import domain.accounts.Account;

public class SessionContext implements Context{
    private Account currentAccount;
    private CollectionDTO currentCollection;

    public Account getCurrentAccount() { 
        return currentAccount; 
    }

    public void setCurrentAccount(Account account) { 
        this.currentAccount = account; 
    }

    public CollectionDTO getCurrentCollection() { 
        return currentCollection; 
    }

    public void setCurrentCollection(CollectionDTO collection) { 
        this.currentCollection = collection; 
    }
}
