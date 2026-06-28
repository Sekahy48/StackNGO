package mvc.controller.show.multiple;


import java.util.List;
import java.util.Set;

import dataTransportLayer.CollectionDTO;
import event.EventBus;
import event.NavigateEvent;
import mvc.view.ViewType;
import service.CollectionService;
import service.ServiceType;
import service.SessionService;

/**
 * Controlador para la vista de colecciones.
 */
public class ShowCollectionsController extends ShowGridDisplayController<CollectionDTO> {

    @Override
    protected List<CollectionDTO> getElements() {
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return collectionService.getAllDTO(sessionService.getCurrentAccount().getId().value());
    } 

    @Override
    public void onClickElementEvent(CollectionDTO dto) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        sessionService.setCurrentCollection(dto);
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COLLECTION, ServiceType.SESSION); 
    }
}