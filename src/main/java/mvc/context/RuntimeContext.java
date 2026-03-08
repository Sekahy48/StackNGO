package mvc.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import creational.StandardEntryFactory;
import creational.AccountFactory;
import creational.DTOFactory;
import dataAccessLayer.DAO.*;
import domain.accounts.Account; 
import identificators.EntryId;
import mvc.model.entries.*;
import mvc.model.entries.repository.EntriesRepository;
import dataTransportLayer.*; 

/**
 * Clase que contiene todo lo necesario en referencias sobre informacion o como
 * acceder a ella en el runtime
 */
public class RuntimeContext {
    private EntriesRepository repo;
    private Account currentAccount;
    private CollectionDTO currentCollection; 
    private Map<DAOType, GenericDAO<? extends GenericDTO, ?>> daoCollection;
    private StandardEntryFactory entriesFactory;
    private AccountFactory accountFactory; 
    






    private SystemContext sysCtx;
    private DataContext dataCtx;
    private SessionContext sessionCtx;

    

    public RuntimeContext(DataContext data, SessionContext session, SystemContext system){
        this.repo = new EntriesRepository(32, null);
        this.daoCollection = new HashMap<DAOType, GenericDAO<? extends GenericDTO, ?>>();

        
        this.entriesFactory = new StandardEntryFactory();
        this.accountFactory = new AccountFactory(); 
        //this.coreController = system.getCoreController();
        //Lo de arriba habra que quitarlo

        this.dataCtx = data;
        this.sessionCtx = session;
        this.sysCtx = system;

        this.setDAOs();
    }

    public void setSystemContext(SystemContext sys){
        this.sysCtx = sys;
    }

    public void setDataContext(DataContext data){
        this.dataCtx = data;
    }

    public void setSessionContext(SessionContext session){
        this.sessionCtx = session;
    }

    public SystemContext getSystemContext(){
        return this.sysCtx;
    }

    public DataContext getDataContext(){
        return this.dataCtx;
    }

    public SessionContext getSessionContext(){
        return this.sessionCtx;
    }

    //TODO eliminard
    private void setDAOs(){
        daoCollection.put(DAOType.ACCOUNT, new AccountDAO());
        daoCollection.put(DAOType.COLLECTION, new CollectionDAO());
        daoCollection.put(DAOType.ITEM, new ItemDAO());
        daoCollection.put(DAOType.RECIPE, new RecipeDAO());

    }




















     

     /*
    //TODO eliminar
    public CollectionDTO getCollectionById(int id){
        CollectionDAO dao = (CollectionDAO) this.getDAO(DAOType.COLLECTION);
        return dao.read(id);
    }

    //TODO eliminar
    public EntriesRepository getRepo() {
        return repo;
    }

    //TODO eliminar
    public StandardEntryFactory getEntriesFactory(){
        return entriesFactory;
    }
 

     
    //TODO eliminar (ahora esta en session)
    public void setAccount(Account account){
        this.currentAccount = account;
    }

    //TODO eliminar, ya hace lo mismo otro metodo
    public EntriesRepository getEntriesRepo(){
        return this.repo;
    }

    //TODO eliminar (ahora esta en session)
    public Account getAccount(){
        return this.currentAccount;
    }

    //TODO eliminar
    public Account createAccount(AccountDTO dto){
        return this.accountFactory.createAccount(dto);
    }

    // TODO eliminar
    public List<CollectionDTO> getCollections(){
        CollectionDAO dao = (CollectionDAO) this.daoCollection.get(DAOType.COLLECTION);
        return dao.readAllByParent(currentAccount.getId().value());
    }

    // TODO eliminar
    public List<AccountDTO> getAccounts(){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.readAll();
    } 

    //TODO eliminar
    public List<ItemWithCollectionDTO> getItems() {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        for (CollectionDTO c : getCollections()) {
            for (ItemDTO i : dao.readAllByParent(c.id)) {
                out.add(DTOFactory.itemWithCollection(i, c.name));
            }
        }
        return out;
    }
    //TODO eliminar
    public List<RecipeWithCollectionDTO> getRecipes() { 
        List<RecipeWithCollectionDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        for (CollectionDTO c : getCollections()) {
            for (RecipeDTO r : dao.readAllByParent(c.id)) {
                out.add(DTOFactory.recipeWithCollection(r, c.name));
            }
        }
        return out;
    }

    //TODO eliminar
    public List<RecipeWithCollectionDTO> getRecipesByCollection(EntryId collectionId) {
        List<RecipeWithCollectionDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);

        CollectionDTO dto = null;
        for (CollectionDTO c : getCollections()) {
            if (c.id == collectionId.value()) {
                dto = c;
                break;
            }
        }

        if (dto != null) {
            for (RecipeDTO r : dao.readAllByParent(dto.id)) {
                out.add(DTOFactory.recipeWithCollection(r, dto.name));
            }
        }
        return out;
    }

    //TODO eliminar
    public List<ItemWithCollectionDTO> getItemsByCollection(EntryId collectionId) {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);

        CollectionDTO dto = null;

        for (CollectionDTO c : getCollections()) {
            if (c.id == collectionId.value()) {
                dto = c;
                break;
            }
        }

        if (dto != null) {
            for (ItemDTO i : dao.readAllByParent(dto.id)){
                out.add(DTOFactory.itemWithCollection(i, dto.name));
            }
        }
        return out;
    }

    //TODO eliminar
    public List<ItemDTO> getItemsAsEntriesByCollection(EntryId collectionId) {
        List<ItemDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);

        out = dao.readAllByParent(collectionId.value());
        return out;
    }

    //TODO eliminar
    public List<RecipeDTO> getRecipesAsEntriesByCollection(EntryId collectionId) {
        List<RecipeDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);

        out = dao.readAllByParent(collectionId.value());
        return out;
    }
    //TODO eliminar
    public ItemDTO getItemDTOById(int id){
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        return dao.read(id);
    }
    //TODO eliminar
    public Item getItemById(int id){
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        ItemDTO dto = dao.read(id);
        if (this.repo.contains(new EntryId(id))){
            return (Item) this.repo.getItem(new EntryId(id));
        } else {
            Item item = this.entriesFactory.createItem(dto);
            this.repo.addItem(item);
            return item;
        }
    }
    //TODO eliminar
    public RecipeDTO getRecipeDTOById(int id){
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        return dao.read(id);
    }
    //TODO eliminar
    public Recipe getRecipeById(int id){
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        RecipeDTO dto = dao.read(id);
        if (this.repo.contains(new EntryId(id))){
            return (Recipe) this.repo.getRecipe(new EntryId(id));
        } else {
            Recipe item = this.entriesFactory.createRecipe(dto);
            this.repo.addRecipe(item);
            return item;
        }
    }

     

        //TODO eliminar
    public List<ItemStackDTO> getInputs(int id){
        return null;
    }
    //TODO eliminar
    public List<ItemStackDTO> getOutputs(int id){
        return null;
    }

    //TODO eliminar
    public Account getAccount(String account){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(account) != null ? this.accountFactory.loadAccount(dao.read(account)) : null;
    }

    //TODO eliminar
    public AccountDTO getAccountDTO(int id){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(id);
    }

     
    

    
    //TODO eliminar
    public CollectionDTO getCollectionByName(String collection) {
        CollectionDAO dao = (CollectionDAO) daoCollection.get(DAOType.COLLECTION);
        return dao.readByName(collection);
    }
         */
    
    //REVISAR
    public GenericDAO<? extends GenericDTO, ?> getDAO(DAOType type) {
        return this.daoCollection.get(type);
    }
 
}
