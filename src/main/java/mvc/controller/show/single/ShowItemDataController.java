package mvc.controller.show.single;
 
import java.util.Set;

import dataTransportLayer.ItemDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image; 
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.show.single.ShowItemDataView;
import service.ItemService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

public class ShowItemDataController extends AbstractShowDataController<ShowItemDataView> {
 

    @Override
    public void handleButtons() {
        commonHandleButton();  
        super.handleButtons();
 
        this.view.getModifyButton().setOnAction(
                e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.MODIFY_ITEM)); });
    }

    public void deleteShowingEntry(int id) {
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        boolean delete = this.view.showAlert(
                "Eliminar item",
                "Seguro que quieres borrar este item?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) { 
            if (itemService.isContainedInARecipe(id)) {
                this.view.showAlert("Item ya en uso" , "No puedes eliminar un item que pertenece a una coleccion",  Alert.AlertType.WARNING);
            } else { 
                ItemDTO dto = itemService.getDTOById(id);
                itemService.removeEntry(id);  
                this.view.showAlert("Item eliminado", "El item con nombre " + dto.name + " ha sido eliminado", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + sessionService.getCurrentAccount().getUsername() + " ha borrado el item con nombre " + dto.name + " en la coleccion " + sessionService.getCurrentCollectionDTO().getName()); 
                EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
            }
        }
    }

    @Override
    public void updateAtShow() {
        SessionService sessionService = this.getService(ServiceType.SESSION);

        ItemDTO dto = sessionService.getCurrentItemDTO();

        Image image = ImageUtils.getImage(dto.imagePath);

        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(image); 
    }

    @Override
    public int getShowingEntryId() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentItemDTO().id;
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ITEM, ServiceType.SESSION); 
    }
}