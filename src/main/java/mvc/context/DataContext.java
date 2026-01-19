package mvc.context;
 
import java.util.HashMap; 
import java.util.Map;

import creational.StandardEntryFactory;
import creational.AccountFactory; 
import dataAccessLayer.DAO.*; 
import dataTransportLayer.*;
import mvc.model.entries.repository.EntriesRepository; 


public class DataContext implements Context{
    private final Map<DAOType, GenericDAO<? extends GenericDTO, ?>> daoCollection;
    private final StandardEntryFactory entriesFactory;
    private final AccountFactory accountFactory;
    private final EntriesRepository repo;

    public DataContext() {
        this.daoCollection = new HashMap<>();
        this.entriesFactory = new StandardEntryFactory();
        this.accountFactory = new AccountFactory();
        this.repo = new EntriesRepository(15, null);
        initDAOs();
    }

    private void initDAOs() {
        daoCollection.put(DAOType.ACCOUNT, new AccountDAO());
        daoCollection.put(DAOType.COLLECTION, new CollectionDAO());
        daoCollection.put(DAOType.ITEM, new ItemDAO());
        daoCollection.put(DAOType.RECIPE, new RecipeDAO());
    }

    public GenericDAO<? extends GenericDTO, ?> getDAO(DAOType type) {
        return daoCollection.get(type);
    }

    public StandardEntryFactory getEntriesFactory() { 
        return entriesFactory; 
    }

    public AccountFactory getAccountFactory() { 
        return accountFactory; 
    }
}

