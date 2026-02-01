package service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import dataTransportLayer.CollectionDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.Collection;

public class CollectionService extends AbstractEntryService<CollectionDTO, Collection> {

    public CollectionService(DataContext data) {
        super(data);
    }

    //#region Entry operations
    @Override
    public Collection getEntryById(int id) {
        Collection out = data.getEntriesRepo().getCollection(new EntryId(id));
        if (out == null) {
            CollectionDTO dto = getDTOById(id);
            out = createEntry(dto);
        }
        return out;
    }

    @Override
    public Collection getEntryByName(String name) {
        Collection out = data.getEntriesRepo().getCollectionByName(name);
        if (out == null) {
            CollectionDTO dto = getDTOByName(name);
            out = createEntry(dto);
        }
        return out;
    }

    @Override
    public List<Collection> getAllEntry(int parentId) {
        List<CollectionDTO> dtos = getAllDTO(parentId);
        List<Collection> out = new ArrayList<>();
        for (CollectionDTO dto : dtos) {
            Collection col = data.getEntriesRepo().getCollection(new EntryId(dto.id));
            if (col == null) col = createEntry(dto);
            out.add(col);
        }
        return out;
    }
    //#endregion

    //#region DTO operations
    @Override
    public CollectionDTO getDTOById(int id) {
        CollectionDTO out = data.getCollectionDAO().read(id);
        if (out == null) {
            String error = "Collection with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public CollectionDTO getDTOByName(String name) {
        CollectionDTO out = data.getCollectionDAO().readByName(name);
        if (out == null) {
            String error = "Collection with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public List<CollectionDTO> getAllDTO(int parentId) {
        return data.getCollectionDAO().readAllByParent(parentId);
    }
    //#endregion

    @Override
    public Collection createEntry(CollectionDTO dto) {
        return this.entriesFactory.createCollection(dto);
    }
}
