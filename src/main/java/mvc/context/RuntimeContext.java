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
import mvc.controller.CoreController;
import identificators.EntryId;
import mvc.model.entries.*;
import mvc.model.entries.repository.EntriesRepository;
import dataTransportLayer.*;
import mvc.view.ScreenManager;

/**
 * Clase que contiene todo lo necesario en referencias sobre informacion o como
 * acceder a ella en el runtime
 */
public class RuntimeContext {
    private EntriesRepository repo;
    private Account currentAccount;
    private CollectionDTO currentCollection;
    private ScreenManager screenManager;
    private Map<DAOType, GenericDAO<? extends GenericDTO, ?>> daoCollection;
    private StandardEntryFactory entriesFactory;
    private AccountFactory accountFactory;
    private CoreController coreController;
    






    private SystemContext sysCtx;
    private DataContext dataCtx;
    private SessionContext sessionCtx;

    

    public RuntimeContext(DataContext data, SessionContext session, SystemContext system){
        this.repo = new EntriesRepository(32, null);
        this.daoCollection = new HashMap<DAOType, GenericDAO<? extends GenericDTO, ?>>();

        this.setDAOs();
        this.entriesFactory = new StandardEntryFactory();
        this.accountFactory = new AccountFactory();
        this.screenManager = system.getScreenManager();
        this.coreController = system.getCoreController();
        //Lo de arriba habra que quitarlo

        this.dataCtx = data;
        this.sessionCtx = session;
        this.sysCtx = system;
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






















    
    public void setCurrentCollection(CollectionDTO currentCollection){
        this.currentCollection = currentCollection;
    }

    public CollectionDTO getCurrentCollection(){
        return this.currentCollection;
    }

    public CollectionDTO getCollectionById(int id){
        CollectionDAO dao = (CollectionDAO) this.getDAO(DAOType.COLLECTION);
        return dao.read(id);
    }

    public EntriesRepository getRepo() {
        return repo;
    }

    public StandardEntryFactory getEntriesFactory(){
        return entriesFactory;
    }

    public void setCoreController(CoreController controller) {
        this.coreController = controller;
    }

    public CoreController getCoreController() {
        return coreController;
    }

    public void setScreenManager(ScreenManager screenManager){
        this.screenManager = screenManager;
    }

    public void setAccount(Account account){
        this.currentAccount = account;
    }

    private void setDAOs(){
        daoCollection.put(DAOType.ACCOUNT, new AccountDAO());
        daoCollection.put(DAOType.COLLECTION, new CollectionDAO());
        daoCollection.put(DAOType.ITEM, new ItemDAO());
        daoCollection.put(DAOType.RECIPE, new RecipeDAO());

    }
    public EntriesRepository getEntriesRepo(){
        return this.repo;
    }

    public Account getAccount(){
        return this.currentAccount;
    }

    public Account createAccount(AccountDTO dto){
        return this.accountFactory.createAccount(dto);
    }

    public List<CollectionDTO> getCollections(){
        CollectionDAO dao = (CollectionDAO) this.daoCollection.get(DAOType.COLLECTION);
        return dao.readAll(currentAccount.getId().value());
    }

    public List<AccountDTO> getAccounts(){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.readAll(currentAccount.getId().value());
    } 

    public List<ItemWithCollectionDTO> getItems() {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        for (CollectionDTO c : getCollections()) {
            for (ItemDTO i : dao.readAll(c.id)) {
                out.add(DTOFactory.itemWithCollection(i, c.name));
            }
        }
        return out;
    }

    public List<RecipeWithCollectionDTO> getRecipes() { 
        List<RecipeWithCollectionDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        for (CollectionDTO c : getCollections()) {
            for (RecipeDTO r : dao.readAll(c.id)) {
                out.add(DTOFactory.recipeWithCollection(r, c.name));
            }
        }
        return out;
    }

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
            for (RecipeDTO r : dao.readAll(dto.id)) {
                out.add(DTOFactory.recipeWithCollection(r, dto.name));
            }
        }
        return out;
    }

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
            for (ItemDTO i : dao.readAll(dto.id)){
                out.add(DTOFactory.itemWithCollection(i, dto.name));
            }
        }
        return out;
    }

    public List<ItemDTO> getItemsAsEntriesByCollection(EntryId collectionId) {
        List<ItemDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);

        out = dao.readAll(collectionId.value());
        return out;
    }

    public List<RecipeDTO> getRecipesAsEntriesByCollection(EntryId collectionId) {
        List<RecipeDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);

        out = dao.readAll(collectionId.value());
        return out;
    }

    public ItemDTO getItemDTOById(int id){
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        return dao.read(id);
    }

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

    public RecipeDTO getRecipeDTOById(int id){
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        return dao.read(id);
    }

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

    public Recipe getRecipeByIdFromBD(int id){
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        RecipeDTO dto = dao.read(id);
        Recipe item = this.entriesFactory.createRecipe(dto);
        this.repo.addRecipe(item);
        return item;
        
    }

    public List<ItemStackDTO> getInputs(int id){
        return null;
    }

    public List<ItemStackDTO> getOutputs(int id){
        return null;
    }

    public Account getAccount(String account){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(account) != null ? this.accountFactory.loadAccount(dao.read(account)) : null;
    }

    public AccountDTO getAccountDTO(int id){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(id);
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public GenericDAO<? extends GenericDTO, ?> getDAO(DAOType type) {
        return this.daoCollection.get(type);
    }
 
}
