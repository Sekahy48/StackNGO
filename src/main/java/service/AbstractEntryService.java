package service;
 
import java.util.ArrayList;
import java.util.List;

import creational.IEntriesFactory;
import creational.StandardEntryFactory;
import dataAccessLayer.DAO.AbstractEntryDAO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.Entry;
import mvc.model.entries.repository.EntriesRepository;
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
    public E getEntryById(int id) {
        E out = getConcreteEntry(id);
        if (out == null) {
            T dto = getDTOById(id);
            out = createEntry(dto);
            addConcreteEntry(out);
        }
        return out;
    }

    /**
     * Retrieves an entry by its name.
     *
     * @param name the entry name
     * @return the entry of type E
     */
    public E  getEntryByName(String name) {
        E out = getConcreteEntryByName(name);
        if (out == null) {
            T dto = getDTOByName(name);
            out = createEntry(dto);
            addConcreteEntry(out);
        }
        return out;
    }

    /**
     * Returns TRUE if there is any entry with the specified name in the context of the concrete service.
     * 
     * @param name entries name to search.
     * @return true if found
     */
    public boolean containsEntryByName(String name) {
        return getDAO().readByName(name) != null;
    }

    /**
     * Retrieves all entries of this service type contained by a parent entity.
     *
     * @param parentId the parent entity identifier
     * @return a list of entries of type E
     */
    public List<E> getAllEntry(int parentId) {
        List<T> dtos = getAllDTO(parentId);
        List<E> out = new ArrayList<>();
        for (T dto : dtos) {
            E col = getConcreteEntry(dto.id);
            if (col == null) col = createEntry(dto);
            out.add(col);
            addConcreteEntry(col);
        }
        return out;
    }

    /**
     * Removes an entry from the entries repo and DB.
     * @param id
     * @return
     */
    public boolean removeEntry(int id) {
        this.untrackEntryById(id);
        return getDAO().delete(id);
    } 

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
    public T getDTOById(int id)  {
        T out = this.getDAO().read(id);
        if (out == null) {
            String error = "Entry with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().getSimpleName(), error); 
        }
        return out;
    }

    /**
     * Retrieves the DTO associated with an entry by its name.
     *
     * @param name the name of the entry
     * @return the DTO of type T 
     */
    public T getDTOByName(String name) {
        T out = getDAO().readByName(name);
        if (out == null) {
            String error = "Entry with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    }

    /**
     * Retrieves all DTOs of entries contained by a parent entity with the given ID.
     *
     * @param parentId the ID of the parent entity
     * @return a list of DTOs of type T
     */
    public List<T> getAllDTO(int parentId) {
        return getDAO().readAllByParent(parentId);
    }
    //#endregion

    /**
     * Retrieves all DTOs of entries existing at all.
     * @return a list of DTOs of type T
     */
    public List<T> getAllDTO() {
        return getDAO().readAll();
    }

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
    public E saveEntry(T dto, int[] extraData) {
        E out = null; 
        out = this.createEntry(dto);
        T existingDTO = getDAO().read(dto.id);
        if (existingDTO != null) {
            getDAO().update(out, existingDTO.id);
        } else {
            getDAO().create(out, extraData);
        }
        
        EntriesRepository repo = this.data.getEntriesRepo();
        if (repo.contains(new EntryId(dto.id))) {
            repo.modifyEntry(out);
        } else {
            addConcreteEntry(out);
        } 
        return out;
    }

    /**
     * Adapted version of {@link #saveEntry(EntryDTO, int[])} for the case of importing data from a JSON file.
     *
     * @param dto the DTO containing the data needed to create/update the entry
     * @param extraData extra data needed for the building of the entity. Is needed for the user of this method
     * to know what data is needed to be contained inside this parameter.
     * @return the newly created entry of type E or the updated one if one equivalent
     * is alredy present in database.
     */ 
    public E saveFromImport(T dto, int[] extraData) {
        T existingDTO = getDAO().readByName(dto.name);
        if (existingDTO != null) {
            dto.id = existingDTO.id;
        }
        return saveEntry(dto, extraData);
    }
    /**
     * Just creates an Entry given a DTO and returns it.
     * @param dto the DTO containing the data needed to create the entry
     * @return the entry
     */
    protected abstract E createEntry(T dto);  

    protected abstract boolean addConcreteEntry(E entry);

    protected abstract E getConcreteEntry(int id);
    protected abstract E getConcreteEntryByName(String name);
    protected abstract AbstractEntryDAO<T, E> getDAO();
}
