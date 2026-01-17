package command.modify.modifiers;

import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.CollectionDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.CollectionDTO; 
import mvc.context.RuntimeContext;
import mvc.controller.AbstractController;
import mvc.model.entries.Collection;
import mvc.model.entries.repository.EntriesRepository;

public class CollectionModifier implements EntryModifier {
    @Override
    public void modify(EntryDTO dto, AbstractController controller) {
        // Lógica para añadir/modificar un collection
        RuntimeContext context = controller.getRuntimeContext();
        EntriesRepository repo = context.getRepo(); 
        Collection collection = context.getEntriesFactory().createCollection((CollectionDTO) dto);

        ((CollectionDAO) context.getDAO(DAOType.COLLECTION)).update(collection, collection.getId().value());

        if (!repo.contains(collection.getId())){
            repo.addCollection(collection);
        }else{
            repo.modifyEntry(collection);
        }
    }
}
