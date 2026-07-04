package mvc.controller.add;
 
import java.util.HashSet;
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.CollectionDTO; 
import event.EventBus;
import event.NavigateEvent;  
import mvc.model.entries.Collection;
import mvc.view.ViewType;
import mvc.view.add.AddCollectionView; 
import service.CollectionService;
import service.ServiceType;
import service.SessionService; 

/**
 *
 * Controller that manages the logic related to {@link AddCollectionView}
 *
 */
public class AddCollectionController extends AbstractAddController<CollectionDTO, Collection, AddCollectionView> {
  
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));
    }  

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.COLLECTION);
        return out;
    }

    @Override
    public CollectionService getEntryService() { 
        return this.<CollectionService>getService(ServiceType.COLLECTION);
    }

    @Override
    public String getEntryType() {
        return "La coleccion";
    }

    @Override
    public CollectionDTO getDTOFromView() {
        String name = view.getNameLabel().getText();
        String iconLabel = view.getIconLabel().getText();
        String description = view.getDescriptionLabel().getText(); 
        CollectionDTO dto = DTOFactory.collection(null,
                        null,
                        name,
                        iconLabel,
                        description,
                        this.idGenerator.generateId());
        return dto;
    }

    @Override
    public int getParentId() {
        return this.<SessionService>getService(ServiceType.SESSION).getCurrentAccount().getId().value();
    }
}
