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
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "El limite del cache del repositorio no puede ser negativo o nulo");
        }
    }

    public void setStrategy(CacheEvictionStrategy<EntryId, Entry> newStrategy){
        if(newStrategy != null){
            this.evictionStrategy = newStrategy;
        }else{
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "El repositorio no puede tener una expulsion de cache nula");
        }
    }

    public Entry getEntry(EntryId id){
        return repo.get(id);
    }

    public Item getItem(EntryId id){
        if (repo.get(id) instanceof  Item) return (Item) repo.get(id);
        Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "Entry con id " + id.value() + " no es un item.");
        return null;
    }

    public Recipe getRecipe(EntryId id){
        if (repo.get(id) instanceof  Recipe) return (Recipe) repo.get(id);
        Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "Entry con id " + id.value() + " no es una receta.");
        return null;
    }

    public Collection getCollection(EntryId id){
        if (repo.get(id) instanceof  Collection) return (Collection) repo.get(id);
        Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "Entry con id " + id.value() + " no es una coleccion.");
        return null;
    }

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

    public void modifyEntry(Entry entry){
        if (this.repo.containsKey(entry.getId())){
            this.repo.put(entry.getId(), entry);
        }else{
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString(), "Entry con id " + entry.getId() + " no se encuentra cargada");
        }
    }

    private boolean addEntry(Entry entry) {
        //if (repo.containsKey(entry.getId())) return false;

        releaseCacheIfNeeded();
        repo.put(entry.getId(), entry);
        return true;
    }

    public void tryToRemoveEntry(EntryId id){
        if (this.repo.containsKey(id)) this.repo.remove(id);
    }

    private void releaseCacheIfNeeded() {
        if (repo.size() >= cacheLimit) {
            evictionStrategy.evict(repo);
        }
    }

    
}
