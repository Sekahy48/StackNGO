package service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import dataTransportLayer.ItemDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.Item;

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
        }
        return out;
    }

    @Override
    public Item getEntryByName(String name) {
        Item out = data.getEntriesRepo().getItemByName(name);  
        if (out == null) {
            ItemDTO dto = getDTOByName(name);
            out = createEntry(dto);
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
        }
        return out;
    }
    //#endregion

    //#region DTO operations
    @Override
    public ItemDTO getDTOById(int id) {
        ItemDTO out = data.getItemDAO().read(id);
        if (out == null) {
            String error = "Item with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public ItemDTO getDTOByName(String name) {
        ItemDTO out = data.getItemDAO().readByName(name);
        if (out == null) {
            String error = "Item with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public List<ItemDTO> getAllDTO(int parentId) {
        return data.getItemDAO().readAllByParent(parentId);
    }
    //#endregion

    @Override
    public Item createEntry(ItemDTO dto) {
        return this.entriesFactory.createItem(dto);
    }

    @Override
    public Item saveEntry(ItemDTO dto, int[] extraData) {
        return null; //TODO
    }
}
