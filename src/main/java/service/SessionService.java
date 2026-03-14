package service;
  

import dataTransportLayer.AccountDTO;
import dataTransportLayer.CollectionDTO;
import domain.accounts.Account;
import mvc.context.SessionContext;

public class SessionService implements IService{

    private final SessionContext context;

    public SessionService(SessionContext context) {
        this.context = context;
    }

    @Override
    public ServiceType getType() {
        return ServiceType.SESSION;
    }
 
    public CollectionDTO getCurrentCollectionDTO() {
        return this.context.getCurrentCollection();
    }

    public Account getCurrentAccount() {
        return this.context.getCurrentAccount();
    }

}
