package mvc.controller.show.multiple;
 
import java.util.List;
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemWithCollectionDTO;
import event.EventBus;
import event.NavigateEvent;
import mvc.view.ViewType;
import service.CollectionService;
import service.ItemService;
import service.ServiceType;
import service.SessionService;

/**
 * Controlador para la vista de cuentas.
 */
public class ShowItemsController extends ShowGridDisplayController<ItemWithCollectionDTO> {

    @Override
    protected List<ItemWithCollectionDTO> getElements() {
        ItemService itemService = this.getService(ServiceType.ITEM); 
        return itemService.getAllWithCollectionDTO();
    } 

    @Override
    protected void onClickElementEvent(ItemWithCollectionDTO dto) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        sessionService.setCurrentItem(dto.item);
        sessionService.setCurrentCollection(collectionService.getDTOByName(dto.collection));
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ITEM));
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ITEM, ServiceType.SESSION, ServiceType.COLLECTION); 
    }

}