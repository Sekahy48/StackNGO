package service;

import dataAccessLayer.DAO.AbstractEntryDAO;
import dataAccessLayer.DAO.CollectionDAO;
import dataTransportLayer.CollectionDTO;
import identificators.EntryId;
import mvc.context.DataContext;
import mvc.model.entries.Collection;

public class CollectionService extends AbstractEntryService<CollectionDTO, Collection> {

    public CollectionService(DataContext data) {
        super(data);
    }

    public ServiceType getType() {
        return ServiceType.COLLECTION;
    }
    
    //#region Entry operations
    /* @Override
    public Collection getEntryById(int id) {
        Collection out = data.getEntriesRepo().getCollection(new EntryId(id));
        if (out == null) {
            CollectionDTO dto = getDTOById(id);
            out = createEntry(dto);
            data.getEntriesRepo().addCollection(out);
        }
        return out;
    } */

    /* @Override
    public Collection getEntryByName(String name) {
        Collection out = data.getEntriesRepo().getCollectionByName(name);
        if (out == null) {
            CollectionDTO dto = getDTOByName(name);
            out = createEntry(dto);
            data.getEntriesRepo().addCollection(out);
        }
        return out;
    } */

    /* @Override
    public List<Collection> getAllEntry(int parentId) {
        List<CollectionDTO> dtos = getAllDTO(parentId);
        List<Collection> out = new ArrayList<>();
        for (CollectionDTO dto : dtos) {
            Collection col = data.getEntriesRepo().getCollection(new EntryId(dto.id));
            if (col == null) col = createEntry(dto);
            out.add(col);
            data.getEntriesRepo().addCollection(col);
        }
        return out;
    } */

/*     @Override
    public boolean removeEntry(int id) {
        this.untrackEntryById(id);
        return this.data.getCollectionDAO().delete(id);
    }  */

    //#endregion

    //#region DTO operations
/*     @Override
    public CollectionDTO getDTOById(int id) {
        CollectionDTO out = data.getCollectionDAO().read(id);
        if (out == null) {
            String error = "Collection with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    } */

/*     @Override
    public CollectionDTO getDTOByName(String name) {
        CollectionDTO out = data.getCollectionDAO().readByName(name);
        if (out == null) {
            String error = "Collection with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    } */

/*     @Override
    public List<CollectionDTO> getAllDTO(int parentId) {
        return data.getCollectionDAO().readAllByParent(parentId);
    } */

    /* public List<CollectionDTO> getAllDTO() {
        return data.getCollectionDAO().readAll();
    } */
    //#endregion
    
    @Override
    protected Collection createEntry(CollectionDTO dto) {
        return this.entriesFactory.createCollection(dto);
    }
  /*   
    @Override
    public Collection saveEntry(CollectionDTO dto, int[] extraData) {
        Collection out = null;
        CollectionDAO dao = this.data.getCollectionDAO();
        out = this.createEntry(dto);
        CollectionDTO existingDTO = dao.read(dto.id);
    
        if (existingDTO != null) {
            dao.update(out, existingDTO.id);
        } else {
            dao.create(out, extraData);
        }
        
        EntriesRepository repo = this.data.getEntriesRepo();
        if (repo.contains(new EntryId(dto.id))) {
            repo.modifyEntry(out);
        } else {
            repo.addCollection(out);
        } 
        return out;
    } */

    @Override
    public Collection saveFromImport(CollectionDTO dto, int[] extraData) {
        CollectionDAO dao = this.data.getCollectionDAO();
        CollectionDTO existingDTO = dao.readByName(dto.name, extraData[0]);
        if (existingDTO != null) {
            dto.id = existingDTO.id;
        }
        return saveEntry(dto, extraData);
    }

    @Override
    protected  boolean addConcreteEntry(Collection entry) {
        return this.data.getEntriesRepo().addCollection(entry);
    }

    @Override 
    protected Collection getConcreteEntry(int id) {
        return this.data.getEntriesRepo().getCollection(new EntryId(id));
    }
    
    @Override 
    protected Collection getConcreteEntryByName(String name) {
        return this.data.getEntriesRepo().getCollectionByName(name);
    }

    @Override
    protected AbstractEntryDAO<CollectionDTO, Collection> getDAO() {
        return this.data.getCollectionDAO();
    }
}
