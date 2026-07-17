package mvc.context;
  
import java.util.HashMap; 
import java.util.Map; 
import dataAccessLayer.DAO.*; 
import dataTransportLayer.*; 
import mvc.model.entries.repository.EntriesRepository; 


public class DataContext implements Context{
    private final Map<DAOType, GenericDAO<? extends GenericDTO, ?>> daoCollection; 
    private final EntriesRepository repo;

    public DataContext() {
        this.daoCollection = new HashMap<>(); 
        this.repo = new EntriesRepository(15, null);
        initDAOs();
    }

    private void initDAOs() {
        daoCollection.put(DAOType.ACCOUNT, new AccountDAO());
        daoCollection.put(DAOType.COLLECTION, new CollectionDAO());
        daoCollection.put(DAOType.ITEM, new ItemDAO());
        daoCollection.put(DAOType.RECIPE, new RecipeDAO());
        daoCollection.put(DAOType.COMPONENT, new ComponentDefinitionDAO());
    }
 
    private GenericDAO<? extends GenericDTO, ?> getDAO(DAOType type) {
        return daoCollection.get(type);
    } 
    
    public AccountDAO getAccountDAO() {
        return (AccountDAO) this.getDAO(DAOType.ACCOUNT);
    }

    public CollectionDAO getCollectionDAO() {
        return (CollectionDAO) this.getDAO(DAOType.COLLECTION);
    }

    public RecipeDAO getRecipeDAO() {
        return (RecipeDAO) this.getDAO(DAOType.RECIPE);
    }

    public ItemDAO getItemDAO() {
        return (ItemDAO) this.getDAO(DAOType.ITEM);
    }

    public ComponentDefinitionDAO getComponentDAO() {
        return (ComponentDefinitionDAO) this.getDAO(DAOType.COMPONENT);
    }

    public AccountDAO getAccountDAOById(int id) {
        return (AccountDAO) this.getDAO(DAOType.ACCOUNT);
    }

    public EntriesRepository getEntriesRepo() {
        return repo;
    }
}
