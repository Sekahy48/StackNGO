package mvc.context;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import creational.StandardEntryFactory;
import creational.AccountFactory;
import creational.DTOFactory;
import dataAccessLayer.DAO.*; 
import dataTransportLayer.*;
import domain.accounts.Account;
import identificators.EntryId;
import mvc.model.entries.Collection;
import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
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

    //NO DEBERIA TENERSE QUE USAR
    public GenericDAO<? extends GenericDTO, ?> getDAO(DAOType type) {
        return daoCollection.get(type);
    }
    //NO DEBERIA TENERSE QUE USAR
    public StandardEntryFactory getEntriesFactory() { 
        return entriesFactory; 
    }
    //NO DEBERIA TENERSE QUE USAR
    public AccountFactory getAccountFactory() { 
        return accountFactory; 
    }

    //NO DEBERIA TENERSE QUE USAR
    public EntriesRepository getEntriesRepo() {
        return repo;
    }

    //#region Get DTO by id
    public CollectionDTO getCollectionDTOById(int id){
        CollectionDAO dao = (CollectionDAO) this.getDAO(DAOType.COLLECTION);
        return dao.read(id);
    }

    public ItemDTO getItemDTOById(int id){
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        return dao.read(id);
    }

    public RecipeDTO getRecipeDTOById(int id){
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        return dao.read(id);
    }

    public List<ItemStackDTO> getInputsDTOById(int id){
        return null;
    }

    public List<ItemStackDTO> getOutputsDTOById(int id){
        return null;
    }

    public AccountDTO getAccountDTOById(int id){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(id);
    }

    //#endregion
    
    //#region Get DTO by name
    public CollectionDTO getCollectionDTOByName(String collection) {
        CollectionDAO dao = (CollectionDAO) daoCollection.get(DAOType.COLLECTION);
        return dao.readByName(collection);
    }

    public RecipeDTO getRecipeDTOByName(String collection) {
        RecipeDAO dao = (RecipeDAO) daoCollection.get(DAOType.COLLECTION);
        return dao.readByName(collection);
    }

    public ItemDTO getItemDTOByName(String collection) {
        ItemDAO dao = (ItemDAO) daoCollection.get(DAOType.COLLECTION);
        return dao.readByName(collection);
    }
    //#endregion

    //#region Get Entry by id
    public Item getItemById(int id){ 
        if (this.repo.contains(new EntryId(id))){
            return (Item) this.repo.getItem(new EntryId(id));
        } else {
            Item item = this.entriesFactory.createItem(getItemDTOById(id));
            this.repo.addItem(item);
            return item;
        }
    }

    public Recipe getRecipeById(int id){ 
        if (this.repo.contains(new EntryId(id))){
            return (Recipe) this.repo.getRecipe(new EntryId(id));
        } else {
            Recipe item = this.entriesFactory.createRecipe(getRecipeDTOById(id));
            this.repo.addRecipe(item);
            return item;
        }
    }
    //#endregion

    //#region Get Entry by name
    public Account getAccountByName(String account){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.read(account) != null ? this.accountFactory.loadAccount(dao.read(account)) : null;
    }
    //#endregion

    //#region Get by parent
    
    public List<ItemWithCollectionDTO> getItemsByCollection(int collectionId, int accountId) {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);

        CollectionDTO dto = null;

        for (CollectionDTO c : getCollections(accountId)) {
            if (c.id == collectionId) {
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

    public List<RecipeWithCollectionDTO> getRecipesByCollection(int collectionId, int accountId) {
        List<RecipeWithCollectionDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);

        CollectionDTO dto = null;
        for (CollectionDTO c : getCollections(accountId)) {
            if (c.id == collectionId) {
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
    
    public List<ItemDTO> getItemDTOsByCollection(EntryId collectionId) {
        List<ItemDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);

        out = dao.readAllByParent(collectionId.value());
        return out;
    }
 
    public List<RecipeDTO> getRecipesDTOsByCollection(EntryId collectionId) {
        List<RecipeDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        out = dao.readAllByParent(collectionId.value());
        return out;
    }
    
    public List<CollectionDTO> getCollections(int accountId){
        CollectionDAO dao = (CollectionDAO) this.daoCollection.get(DAOType.COLLECTION);
        return dao.readAllByParent(accountId);
    }
    //#endregion

    //#region Get all DTO
    public List<ItemWithCollectionDTO> getItems(int accountId) {
        List<ItemWithCollectionDTO> out = new ArrayList<>();
        ItemDAO dao = (ItemDAO) this.daoCollection.get(DAOType.ITEM);
        for (CollectionDTO c : getCollections(accountId)) {
            for (ItemDTO i : dao.readAllByParent(c.id)) {
                out.add(DTOFactory.itemWithCollection(i, c.name));
            }
        }
        return out;
    }

    public List<RecipeWithCollectionDTO> getRecipes(int accountId) { 
        List<RecipeWithCollectionDTO> out = new ArrayList<>();
        RecipeDAO dao = (RecipeDAO) this.daoCollection.get(DAOType.RECIPE);
        for (CollectionDTO c : getCollections(accountId)) {
            for (RecipeDTO r : dao.readAllByParent(c.id)) {
                out.add(DTOFactory.recipeWithCollection(r, c.name));
            }
        }
        return out;
    }

    public List<AccountDTO> getAccounts(){
        AccountDAO dao = (AccountDAO) this.daoCollection.get(DAOType.ACCOUNT);
        return dao.readAll();
    } 
    //#endregion
    
    //#region Create entities
    public Account createAccount(AccountDTO dto){
        return this.accountFactory.createAccount(dto);
    }

    public Collection createCollection(CollectionDTO dto){
        return this.entriesFactory.createCollection(dto);
    }

    public Item createItem(ItemDTO dto){
        return this.entriesFactory.createItem(dto);
    }

    public Recipe createRecipe(RecipeDTO dto){
        return this.entriesFactory.createRecipe(dto);
    }

    //#endregion
    
}

