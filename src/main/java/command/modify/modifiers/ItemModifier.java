package command.modify.modifiers;

import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO; 
import mvc.context.RuntimeContext;
import mvc.controller.AbstractController;
import mvc.model.entries.Item;
import mvc.model.entries.repository.EntriesRepository;

public class ItemModifier implements EntryModifier {
    @Override
    public void modify(EntryDTO dto, AbstractController controller) {
        // Lógica para añadir/modificar un item
        RuntimeContext context = controller.getRuntimeContext();
        EntriesRepository repo = context.getRepo(); 
        Item item = context.getEntriesFactory().createItem((ItemDTO) dto);

        ((ItemDAO) context.getDAO(DAOType.ITEM)).update(item, item.getId().value());

        if (!repo.contains(item.getId())){
            repo.addItem(item);
        }else{
            repo.modifyEntry(item);
        }


    }
}
