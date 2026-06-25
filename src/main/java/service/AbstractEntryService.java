package service;

import java.util.List;

import creational.IEntriesFactory;
import creational.StandardEntryFactory;
import dataTransportLayer.EntryDTO;
import identificators.EntryId;
import mvc.context.DataContext;
import mvc.model.entries.Entry;
/**
 * Define a serie of operations for query/obtain, create, modify and interact with entries 
 */
public abstract class AbstractEntryService<T extends EntryDTO, E extends Entry> implements IService{
    protected final DataContext data;
    protected final IEntriesFactory entriesFactory;
    
    public AbstractEntryService(DataContext data){
        this.data = data;
        this.entriesFactory = new StandardEntryFactory();
    }

    //#region Entry operations

    /**
     * Retrieves an entry by its numeric identifier.
     *
     * @param id the entry identifier
     * @return the entry of type E 
     */
    public abstract E getEntryById(int id);

    /**
     * Retrieves an entry by its name.
     *
     * @param name the entry name
     * @return the entry of type E
     */
    public abstract E getEntryByName(String name);

    /**
     * Returns TRUE if there is any entry with the specified name in the context of the concrete service.
     * 
     * @param name entries name to search.
     * @return true if found
     */
    public boolean containsEntryByName(String name) {
        return getEntryByName(name) != null;
    }

    /**
     * Retrieves all entries of this service type contained by a parent entity.
     *
     * @param parentId the parent entity identifier
     * @return a list of entries of type E
     */
    public abstract List<E> getAllEntry(int parentId);

    public abstract boolean removeEntry(int id);

    public boolean untrackEntryById(int id) {
        return this.data.getEntriesRepo().tryToRemoveEntry(new EntryId(id));
    }
    
    //#endregion


    //#region DTO operations

    /**
     * Retrieves the DTO associated with an entry by its numeric ID.
     *
     * @param id the identifier of the entry
     * @return the DTO of type T 
     */
    public abstract T getDTOById(int id);

    /**
     * Retrieves the DTO associated with an entry by its name.
     *
     * @param name the name of the entry
     * @return the DTO of type T 
     */
    public abstract T getDTOByName(String name);

    /**
     * Retrieves all DTOs of entries contained by a parent entity with the given ID.
     *
     * @param parentId the ID of the parent entity
     * @return a list of DTOs of type T
     */
    public abstract List<T> getAllDTO(int parentId);
    //#endregion

    /**
     * Creates or updates a new entry from a DTO and some extra data. Then it persists it in SQL DB and
     * caches it in the entries repository.
     *
     * @param dto the DTO containing the data needed to create/update the entry
     * @param extraData extra data needed for the building of the entity. Is needed for the user of this method
     * to know what data is needed to be contained inside this parameter.
     * @return the newly created entry of type E or the updated one if one equivalent
     * is alredy present in database.
     */ 
    public abstract E saveEntry(T dto, int[] extraData);

    /**
     * Adapted version of {@link #saveEntry(EntryDTO, int[])} for the case of importing data from a JSON file.
     *
     * @param dto the DTO containing the data needed to create/update the entry
     * @param extraData extra data needed for the building of the entity. Is needed for the user of this method
     * to know what data is needed to be contained inside this parameter.
     * @return the newly created entry of type E or the updated one if one equivalent
     * is alredy present in database.
     */ 
    public abstract E saveFromImport(T dto, int[] extraData);

    /**
     * Just creates an Entry given a DTO and returns it.
     * @param dto the DTO containing the data needed to create the entry
     * @return the entry
     */
    public abstract E createEntry(T dto);
    //TODO considerar hacer el metodo de arriba prrotected
}
