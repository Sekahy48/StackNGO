package mvc.model.entries.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import identificators.EntryId;
import logger.LogLevel;
import logger.Logger;
import mvc.model.entries.Collection;
import mvc.model.entries.Entry;
import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
import mvc.model.entries.component.ComponentDefinition;
import mvc.model.entries.repository.cache.CacheEvictionStrategy;
import mvc.model.entries.repository.cache.LRUEvictionStrategy;

public class EntriesRepository {
    private final Map<EntryId, Entry> repo = new LinkedHashMap<>(16, 0.75f, true);
    private int cacheLimit = 32;
    private CacheEvictionStrategy<EntryId, Entry> evictionStrategy;

    public EntriesRepository(int cacheLimit,
                            CacheEvictionStrategy<EntryId, Entry> evictionStrategy) {
        this.cacheLimit = cacheLimit;
        this.evictionStrategy = evictionStrategy != null ? evictionStrategy : new LRUEvictionStrategy<>();
    }

    //#region Getter & Setters
    public void setLimit(int limit){
        if(limit > 0){
            this.cacheLimit = limit;
        }else{
            Logger.getInstance().warning(this.getClass().toString(), "El limite del cache del repositorio no puede ser negativo o nulo");
        }
    }

    public void setStrategy(CacheEvictionStrategy<EntryId, Entry> newStrategy){
        if(newStrategy != null){
            this.evictionStrategy = newStrategy;
        }else{
            Logger.getInstance().warning(this.getClass().toString(), "El repositorio no puede tener una expulsion de cache nula");
        }
    }

    public Entry getEntry(EntryId id){
        return repo.get(id);
    }

    //#region Get by id
    public Item getItem(EntryId id){
        Entry e = repo.get(id);
        if (e instanceof Item) return (Item) e;
        if (e != null) Logger.getInstance().warning(this.getClass().toString(), "Entry con id " + id.value() + " no es un item."); 
        return null;
    } 

    public Recipe getRecipe(EntryId id){
        Entry e = repo.get(id);
        if (e instanceof Recipe) return (Recipe) e;
        if (e != null) Logger.getInstance().warning(this.getClass().toString(), "Entry con id " + id.value() + " no es una receta."); 
        return null;
    }

    public Collection getCollection(EntryId id){
        Entry e = repo.get(id);
        if (e instanceof Collection) return (Collection) e;
        if (e != null) Logger.getInstance().warning(this.getClass().toString(), "Entry con id " + id.value() + " no es una colección."); 
        return null;
    }
    
    public ComponentDefinition getComponent(EntryId id){
        Entry e = repo.get(id);
        if (e instanceof ComponentDefinition) return (ComponentDefinition) e;
        if (e != null) Logger.getInstance().warning(this.getClass().toString(), "Entry con id " + id.value() + " no es un componente."); 
        return null;
    }
    //#endregion

    //#region Get by name
    public Item getItemByName(String name) {
        for (Entry e : repo.values()) {
            if (e instanceof Item && e.getName().equals(name)) {
                return (Item) e;
            }
        }
        Logger.getInstance().warning(this.getClass().toString(),
                "Item with name \"" + name + "\" was not found.");
        return null;
    }

    public Recipe getRecipeByName(String name) {
        for (Entry e : repo.values()) {
            if (e instanceof Recipe && e.getName().equals(name)) {
                return (Recipe) e;
            }
        }
        Logger.getInstance().warning(this.getClass().toString(),
                "Recipe with name \"" + name + "\" was not found.");
        return null;
    }

    public Collection getCollectionByName(String name) {
        for (Entry e : repo.values()) {
            if (e instanceof Collection && e.getName().equals(name)) {
                return (Collection) e;
            }
        }
        Logger.getInstance().warning(this.getClass().toString(),
                "Collection with name \"" + name + "\" was not found.");
        return null;
    }

    public ComponentDefinition getComponentByName(String name) {
        for (Entry e : repo.values()) {
            if (e instanceof ComponentDefinition && e.getName().equals(name)) {
                return (ComponentDefinition) e;
            }
        }
        Logger.getInstance().warning(this.getClass().toString(),
                "Component with name \"" + name + "\" was not found.");
        return null;
    }

    //#endregion
    
    public boolean contains(EntryId id){
        return this.getEntry(id) != null;
    }
    //#endregion

    public boolean addItem(Item item) {
        return addEntry(item);
    }

    public boolean addRecipe(Recipe recipe) {
        return addEntry(recipe);
    }

    public boolean addCollection(Collection collection) {
        return addEntry(collection);
    }

    public boolean addComponent(ComponentDefinition component) {
        return addEntry(component);
    }

    public void modifyEntry(Entry entry){
        if (this.repo.containsKey(entry.getId())){
            this.repo.put(entry.getId(), entry);
        }else{
            Logger.getInstance().warning(this.getClass().toString(), "Entry con id " + entry.getId() + " no se encuentra cargada");
        }
    }

    private boolean addEntry(Entry entry) {
        //if (repo.containsKey(entry.getId())) return false;

        releaseCacheIfNeeded();
        repo.put(entry.getId(), entry);
        return true;
    }
 

    public boolean tryToRemoveEntry(EntryId id){
        boolean contains = this.repo.containsKey(id);
        if (contains) this.repo.remove(id);
        return contains;
    }

    private void releaseCacheIfNeeded() {
        if (repo.size() >= cacheLimit) {
            evictionStrategy.evict(repo);
        }
    }

    
}
