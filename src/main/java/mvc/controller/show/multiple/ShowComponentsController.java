package mvc.controller.show.multiple;

import java.util.List;
import java.util.Set;

import dataTransportLayer.ComponentDefinitionDTO;
import event.EventBus;
import event.NavigateEvent;
import mvc.view.ViewType;
import service.ComponentService;
import service.ServiceType;
import service.SessionService; 

public class ShowComponentsController extends ShowGridDisplayController<ComponentDefinitionDTO>{

    @Override
    protected List<ComponentDefinitionDTO> getElements() {
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        System.out.println(componentService.getAllDTO(sessionService.getCurrentAccount().getId().value()).size());
        return componentService.getAllDTO(sessionService.getCurrentAccount().getId().value());
    }

    @Override
    protected void onClickElementEvent(ComponentDefinitionDTO dto) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        sessionService.setCurrentComponent(dto);
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENT));
    }

    @Override
    public void onReturnEvent() { 
        throw new UnsupportedOperationException("Unimplemented method 'onReturnEvent'");
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COMPONENT, ServiceType.SESSION); 
    }

}
