package mvc.controller.modify;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import logger.Logger;
import mvc.model.entries.component.ItemComponentValue;
import mvc.view.ViewType;
import mvc.view.modify.ItemModifyView; 
import service.ComponentService;
import service.ItemService;
import service.ServiceType;
import service.SessionService;

public class ItemModifyController extends AbstractModifyController<ItemModifyView, ItemDTO>{

    private List<ItemComponentValue> componentValues = new ArrayList<>();

    @Override
    public void attachView(ItemModifyView view) {
        super.attachView(view);
        view.getAddComponentButton().setOnAction(e -> onAddComponent());
    }

    private void onAddComponent() {
        ComponentDefinitionDTO def = view.getComponentCombo().getValue();
        if (def == null) return;

        if (componentValues.stream().anyMatch(v -> v.getComponentDefId() == def.id)) {
            view.showAlert("Componente duplicado", "Este item ya tiene el componente " + def.name, Alert.AlertType.ERROR);
            return;
        }

        ItemComponentValue value = new ItemComponentValue(def.id);
        componentValues.add(value);
        view.addComponentRow(def, value, () -> onRemoveComponent(value));
    }

    private void onRemoveComponent(ItemComponentValue value) {
        componentValues.remove(value);
        view.removeComponentRow(value);
    }

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

        return DTOFactory.item(newName, iconPath, description, dto.id, new ArrayList<>(componentValues));

            
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

        componentValues.clear();
        view.clearComponentRows();
    }

    @Override
    public void updateAtShow() {
        super.updateAtShow();
        componentValues = new ArrayList<>(getCurrentDTO().components);

        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        List<ComponentDefinitionDTO> available = componentService.getAllDTO(sessionService.getCurrentAccount().getId().value());
        view.setAvailableComponents(available);

        view.clearComponentRows();
        for (ItemComponentValue value : componentValues) {
            ComponentDefinitionDTO def = available.stream()
                    .filter(d -> d.id == value.getComponentDefId())
                    .findFirst()
                    .orElse(null);
            if (def != null) {
                view.addComponentRow(def, value, () -> onRemoveComponent(value));
            }
        }
    }

    @Override
    public void onReturnEvent() { 
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ITEM));
    }

    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ITEM, ServiceType.COMPONENT, ServiceType.SESSION); 
    }

    protected ItemDTO getCurrentDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentItemDTO();
    }

    @Override
    protected void updateCurrentDTO(ItemDTO dto) {
        this.<SessionService>getService(ServiceType.SESSION).setCurrentItem(dto);
    }
}