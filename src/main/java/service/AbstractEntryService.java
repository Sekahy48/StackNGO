package service;

import java.util.List;
import java.util.NoSuchElementException;

import creational.IEntriesFactory;
import creational.StandardEntryFactory;
import dataTransportLayer.EntryDTO;
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
     * @throws NoSuchElementException if the entry does not exist
     */
    public abstract E getEntryById(int id);

    /**
     * Retrieves an entry by its name.
     *
     * @param name the entry name
     * @return the entry of type E
     * @throws NoSuchElementException if the entry does not exist
     */
    public abstract E getEntryByName(String name);

    /**
     * Retrieves all entries of this service type contained by a parent entity.
     *
     * @param parentId the parent entity identifier
     * @return a list of entries of type E
     */
    public abstract List<E> getAllEntry(int parentId);

    //#endregion


    //#region DTO operations

    /**
     * Retrieves the DTO associated with an entry by its numeric ID.
     *
     * @param id the identifier of the entry
     * @return the DTO of type T
     * @throws NoSuchElementException if no entry exists with the given ID
     */
    public abstract T getDTOById(int id);

    /**
     * Retrieves the DTO associated with an entry by its name.
     *
     * @param name the name of the entry
     * @return the DTO of type T
     * @throws NoSuchElementException if no entry exists with the given name
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
     * Creates a new entry from a DTO.
     *
     * @param dto the DTO containing the data needed to create the entry
     * @return the newly created entity of type E
     */ 
    public abstract E createEntry(T dto);
}
