package mvc.controller.show.single;

import java.util.Set;

import event.EventBus;
import event.NavigateEvent;
import dataTransportLayer.ComponentDefinitionDTO;
import javafx.scene.control.Alert;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.show.single.ShowComponentDataView;
import service.ComponentService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

public class ShowComponentDataController extends AbstractShowDataController<ShowComponentDataView> {

    @Override
    public void handleButtons() {
        super.handleButtons();
        this.view.getModifyButton().setOnAction(
                e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.MODIFY_COMPONENT)); }
        );
    }

    @Override
    public void deleteShowingEntry(int id) {
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        boolean delete = this.view.showAlert(
                "Eliminar componente",
                "Seguro que quieres eliminar este componente?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) {
            ComponentDefinitionDTO dto = componentService.getDTOById(id);
            componentService.removeEntry(id);
            this.view.showAlert("Componente eliminado", "El componente con nombre " + dto.name + " ha sido eliminado", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "El usuario " + sessionService.getCurrentAccount().getUsername() + " ha borrado el componente con nombre " + dto.name);
            EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));
        }
    }

    @Override
    public void updateAtShow() {
        SessionService sessionService = this.getService(ServiceType.SESSION);

        ComponentDefinitionDTO dto = sessionService.getCurrentComponentDTO();

        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(ImageUtils.getImage(dto.imagePath));
        this.view.setFields(dto.fields);
    }

    @Override
    public int getShowingEntryId() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentComponentDTO().id;
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENTS));
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COMPONENT, ServiceType.SESSION);
    }
}