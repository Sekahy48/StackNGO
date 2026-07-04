package mvc.controller.add;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import creational.DTOFactory; 
import dataTransportLayer.ComponentDefinitionDTO; 
import dataTransportLayer.ItemDTO; 
import event.EventBus;
import event.NavigateEvent;
import mvc.model.entries.Item;
import mvc.model.entries.component.ItemComponentValue;
import mvc.view.ViewType; 
import mvc.view.add.AddItemView; 
import service.ComponentService;
import service.ItemService;
import service.ServiceType;
import service.SessionService; 

/**
 *
 * Controller that manages the logic related to {@link AddItemView}
 *
 */
public class AddItemController extends AbstractAddController<ItemDTO, Item, AddItemView>{ 
    //Estaria guay poder no tener esto aquí, TODO preguntar
    private List<ItemComponentValue> componentValues = new ArrayList<>();
 

    @Override
    public void attachView(AddItemView view) {
        super.attachView(view);
        wireComponentButtons();
    }

    private void wireComponentButtons() {
        view.getAddComponentButton().setOnAction(e -> onAddComponent());
    }

    private void onAddComponent() {
        ComponentDefinitionDTO def = view.getComponentCombo().getValue();
        if (def == null) return;

        if (componentValues.stream().anyMatch(v -> v.getComponentDefId() == def.id)) {
            view.showAlert("Componente duplicado", "Este item ya tiene el componente " + def.name, javafx.scene.control.Alert.AlertType.ERROR);
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

    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.ITEM);
        out.add(ServiceType.COMPONENT);
        return out;
    }

    @Override
    public ItemService getEntryService() {
        return this.<ItemService>getService(ServiceType.ITEM);
    }

    @Override
    public String getEntryType() { 
        return "El item";
    }

    @Override
    public void updateAtShow() {
        super.updateAtShow();
        componentValues.clear();
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        List<ComponentDefinitionDTO> available = componentService.getAllDTO(sessionService.getCurrentAccount().getId().value());
        view.setAvailableComponents(available);
    }

    @Override
    public ItemDTO getDTOFromView() {
        String name = this.view.getNameLabel().getText();
        String iconLabel = this.view.getIconLabel().getText();
        String description = this.view.getDescriptionLabel().getText();
        
        ItemDTO dto = DTOFactory.item(
                        name,
                        iconLabel,
                        description,
                        this.idGenerator.generateId(),
                        new ArrayList<>(componentValues)
        );

        return dto;
    }

    @Override
    public void onCreateEvent(ItemDTO dto) {
        super.onCreateEvent(dto);
        componentValues.clear();
        view.clearComponentRows();
    }

    @Override
    public int getParentId() {
        return this.<SessionService>getService(ServiceType.SESSION).getCurrentCollectionDTO().id;
    }
}