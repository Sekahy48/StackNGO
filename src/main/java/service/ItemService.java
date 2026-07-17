package service;

import java.util.ArrayList;
import java.util.List; 

import creational.DTOFactory;
import dataAccessLayer.DAO.AbstractEntryDAO; 
import dataAccessLayer.DAO.ItemDAO; 
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.ItemWithCollectionDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.Item;
import mvc.model.entries.repository.EntriesRepository;

public class ItemService extends AbstractEntryService<ItemDTO, Item> {

    public ItemService(DataContext data) {
        super(data);
    }

    public ServiceType getType() {
        return ServiceType.ITEM;
    }

    //#region Entry operations
    @Override
    public Item getEntryById(int id) {
        Item out = data.getEntriesRepo().getItem(new EntryId(id));
        if (out == null) {
            ItemDTO dto = getDTOById(id);
            out = createEntry(dto);
            data.getEntriesRepo().addItem(out);
        }
        return out;
    }

    @Override
    public Item getEntryByName(String name, int parentId) {
        Item out = data.getEntriesRepo().getItemByName(name);  
        if (out == null) {
            ItemDTO dto = getDTOByName(name, parentId);
            out = createEntry(dto);
            data.getEntriesRepo().addItem(out);
        }
        return out;
    }

    @Override
    public List<Item> getAllEntry(int parentId) {
        List<ItemDTO> dtos = getAllDTO(parentId);
        List<Item> out = new ArrayList<>();
        for (ItemDTO dto : dtos) {
            Item item = data.getEntriesRepo().getItem(new EntryId(dto.id));
            if (item == null) item = createEntry(dto);
            out.add(item);
            data.getEntriesRepo().addItem(item);
        }
        return out;
    }

    @Override
    public boolean removeEntry(int id) {
        this.untrackEntryById(id);
        return this.data.getItemDAO().delete(id);
    }
 
    //#endregion

    //#region DTO operations
    @Override
    public ItemDTO getDTOById(int id) {
        ItemDTO out = data.getItemDAO().read(id);
        if (out == null) {
            String error = "Item with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    }

    @Override
    public ItemDTO getDTOByName(String name, int parentId) {
        ItemDTO out = data.getItemDAO().readByName(name, parentId);
        if (out == null) {
            String error = "Item with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    }

    @Override
    public List<ItemDTO> getAllDTO(int parentId) {
        return data.getItemDAO().readAllByParent(parentId);
    }
 
    public List<ItemWithCollectionDTO> getAllWithCollectionDTO(int accountId) {
        return this.data.getItemDAO().readAllWithCollection(accountId);
    }

    public List<ItemDTO> getAllDTO() {
        return data.getItemDAO().readAll();
    }

    //#endregion

    @Override
    protected Item createEntry(ItemDTO dto) {
        return this.entriesFactory.createItem(dto);
    }

    @Override
    public Item saveEntry(ItemDTO dto, int[] extraData) {
        Item out = null;
        ItemDAO dao = this.data.getItemDAO();
        out = this.createEntry(dto);
        ItemDTO existingDTO = dao.read(dto.id);
        if (existingDTO != null) {
            dao.update(out, existingDTO.id);
        } else {
            dao.create(out, extraData);
        }
        
        EntriesRepository repo = this.data.getEntriesRepo();
        if (repo.contains(new EntryId(dto.id))) {
            repo.modifyEntry(out);
        } else {
            repo.addItem(out);
        } 
        return out;
    }

    @Override
    public Item saveFromImport(ItemDTO dto, int[] extraData) {
        ItemDAO dao = this.data.getItemDAO();
        ItemDTO existingDTO = dao.readByName(dto.name, extraData[0]);
        if (existingDTO != null) {
            dto.id = existingDTO.id;
        }
        return saveEntry(dto, extraData);
    }

    public List<ItemStackDTO> idStackToStackList(List<ItemIdStackDTO> idStacks) {
        List<ItemStackDTO> stacks = new ArrayList<>(); 
        ItemDTO itemDTO;
        for (ItemIdStackDTO elem : idStacks) { 
            itemDTO = this.getDTOById(elem.id);
            stacks.add(DTOFactory.itemStack(itemDTO, elem.amount));
        }

        return stacks;
    }

    public boolean isContainedInARecipe(int id) {
        return this.data.getItemDAO().isInRecipe(id) != -1;
    }

    @Override
    protected  boolean addConcreteEntry(Item entry) {
        return this.data.getEntriesRepo().addItem(entry);
    }

    @Override 
    protected Item getConcreteEntry(int id) {
        return this.data.getEntriesRepo().getItem(new EntryId(id));
    }

    @Override 
    protected Item getConcreteEntryByName(String name) {
        return this.data.getEntriesRepo().getItemByName(name);
    }

    @Override
    protected AbstractEntryDAO<ItemDTO, Item> getDAO() {
        return this.data.getItemDAO();
    }
}
