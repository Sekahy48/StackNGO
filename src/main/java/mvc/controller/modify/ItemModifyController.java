package mvc.controller.modify;

import java.util.Objects;
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.modify.ItemModifyView; 
import service.ItemService;
import service.ServiceType;
import service.SessionService;

public class ItemModifyController extends AbstractModifyController<ItemModifyView, ItemDTO>{
 
    @Override
    protected ItemDTO  composeDTO() { 
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        EntryDTO dto = sessionService.getCurrentItemDTO();

        String newName = !this.view.getNewName().isBlank() ? this.view.getNewName() : dto.name;

        if (!newName.equals(dto.name)) {
            if (itemService.containsEntryByName(newName)) {
                this.view.showAlert("Nombre duplicado","Ya existe un item con ese nombre.", Alert.AlertType.ERROR);
                return null;
            }
        }

        String iconPath = !Objects.isNull(this.view.getNewImagePath()) && !this.view.getNewImagePath().isEmpty()
                        ? this.view.getNewImagePath() 
                        : dto.imagePath;

        String description = !Objects.isNull(this.view.getNewDescription()) && !this.view.getNewDescription().isEmpty()
                            ? this.view.getNewDescription()
                            : dto.description;

        return DTOFactory.item(newName, iconPath, description, dto.id);

            
    }

    @Override
    protected void onUpdateEvent(ItemDTO dto) {
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        itemService.saveEntry(dto, new int[]{sessionService.getCurrentCollectionDTO().id});
        
        ItemDTO oldDto = sessionService.getCurrentItemDTO();
        String alert = "El item llamado " + oldDto.name + " ha sido modificado.";
        if (oldDto.name != dto.name) alert = "El item antes llamado " + oldDto.name + " ha sido modificado pasandose a llamar " + dto.name + ".";

        Logger.getInstance().info(
            this.getClass().toString(),
            alert
        );
        
        this.onReturnEvent();
    }

    @Override
    public void onReturnEvent() { 
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ITEM));
    }

    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ITEM, ServiceType.SESSION); 
    }

    protected ItemDTO getCurrentDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentItemDTO();
    }
}
