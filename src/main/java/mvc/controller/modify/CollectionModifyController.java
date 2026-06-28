package mvc.controller.modify;

import java.util.ArrayList;
import java.util.Set;

import creational.DTOFactory; 
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import logger.Logger; 
import mvc.view.ViewType;
import mvc.view.modify.CollectionModifyView;
import service.CollectionService;
import service.ServiceType;
import service.SessionService;

public class CollectionModifyController extends AbstractModifyController<CollectionModifyView, CollectionDTO>{   

    @Override
    protected CollectionDTO composeDTO() { 
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        ArrayList<Integer> items = new ArrayList<>(), 
                           recipes = new ArrayList<>();
        ArrayList<EntryDTO> viewItems = new ArrayList<>(),
                           viewRecipes = new ArrayList<>();

        viewItems.addAll(this.view.getItems());
        viewRecipes.addAll(this.view.getRecipes());
 

        for (EntryDTO elem : viewItems) {
            items.add(elem.id);
        }

        for (EntryDTO elem : viewRecipes) {
            recipes.add(elem.id);
        }
        
        CollectionDTO dto = (CollectionDTO) sessionService.getCurrentCollectionDTO();
        String newName = !this.view.getNewName().isBlank() ? this.view.getNewName() : dto.name;

        if (!newName.equals(dto.name)) { 
            if (collectionService.containsEntryByName(newName)){
                this.view.showAlert("Nombre duplicado","Ya existe una coleccion con ese nombre", Alert.AlertType.ERROR);
                return null;
            }
        }
        String imagePath = this.view.getNewImagePath() != null ? this.view.getNewImagePath() : dto.imagePath;

        return DTOFactory.collection(
                !items.isEmpty() ? items : dto.items,
                !recipes.isEmpty() ? recipes : dto.recipes,
                newName,
                imagePath,
                !this.view.getNewDescription().isEmpty() ? this.view.getNewDescription() : dto.description,
                dto.id
        );

    }

    public void onUpdateEvent(CollectionDTO dto) { 
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        collectionService.saveEntry(dto, new int[]{sessionService.getCurrentAccount().getId().value()});
        
        CollectionDTO oldDto = sessionService.getCurrentCollectionDTO();
        String alert = "La colección llamada " + oldDto.name + " ha sido modificada.";
        if (oldDto.name != dto.name) alert = "La coleccion antes llamada " + oldDto.name + " ha sido modificada pasandose a llamar " + dto.name + ".";

        Logger.getInstance().info(
            this.getClass().toString(),
            alert
        );
        sessionService.setCurrentCollection(collectionService.getDTOById(dto.id));
        this.onReturnEvent();
    }

    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }

    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COLLECTION, ServiceType.SESSION); 
    }

    protected CollectionDTO getCurrentDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentCollectionDTO();
    }
    
}
