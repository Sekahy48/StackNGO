package mvc.controller.add;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import creational.DTOFactory;
import dataTransportLayer.ComponentDefinitionDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import mvc.model.entries.component.ComponentDefinition;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.view.ViewType;
import mvc.view.add.AddComponentView; 
import service.ComponentService;
import service.ServiceType;
import service.SessionService; 

public class AddComponentController extends AbstractAddController<ComponentDefinitionDTO, ComponentDefinition, AddComponentView> { 

    private List<ComponentField> fields = new ArrayList<>();

    @Override
    public void attachView(AddComponentView view) {
        super.attachView(view);
        wireFieldButtons();
    }

    private void wireFieldButtons() {
        view.getAddFieldButton().setOnAction(e -> onAddField());
    }

    private void onAddField() {
        String name = view.getFieldNameField().getText();
        FieldType type = view.getFieldTypeCombo().getValue();

        if (name == null || name.isBlank()) {
            view.showAlert("Nombre vacio", "El campo debe tener un nombre", Alert.AlertType.ERROR);
            return;
        }
        if (type == null) {
            view.showAlert("Tipo vacio", "Debe seleccionar un tipo para el campo", Alert.AlertType.ERROR);
            return;
        }
        if (fields.stream().anyMatch(f -> f.getFieldName().equalsIgnoreCase(name))) {
            view.showAlert("Campo duplicado", "Ya existe un campo llamado " + name, Alert.AlertType.ERROR);
            return;
        }

        List<String> enumValues = new ArrayList<>();
        if (type == FieldType.ENUM || type == FieldType.ENUMLIST) {
            String raw = view.getEnumValuesField().getText();
            if (raw == null || raw.isBlank()) {
                view.showAlert("Opciones vacias", "Un campo de tipo enum debe tener al menos una opcion", Alert.AlertType.ERROR);
                return;
            }
            enumValues = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (enumValues.isEmpty()) {
                view.showAlert("Opciones vacias", "Un campo de tipo enum debe tener al menos una opcion", Alert.AlertType.ERROR);
                return;
            }
        }

        ComponentField field = new ComponentField(name, type, enumValues);
        fields.add(field);
        view.addFieldRow(field, () -> onEditField(field), () -> onRemoveField(field));
        view.clearFieldInputs();
    }

    private void onEditField(ComponentField field) {
        view.getFieldNameField().setText(field.getFieldName());
        view.getFieldTypeCombo().setValue(field.getFieldType());
        view.getEnumValuesField().setText(String.join(",", field.getEnumValues()));
        onRemoveField(field);
    }

    private void onRemoveField(ComponentField field) {
        fields.remove(field);
        view.removeFieldRow(field);
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));
    }

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.COMPONENT);
        return out; 
    }

    @Override
    public ComponentService getEntryService() {
        return this.<ComponentService>getService(service.ServiceType.COMPONENT);
    }

    @Override
    public String getEntryType() {
        return "El componente";
    }

    @Override
    public ComponentDefinitionDTO getDTOFromView() {
        String name = view.getNameLabel().getText();
        String iconLabel = view.getIconLabel().getText();
        String description = view.getDescriptionLabel().getText();

        ComponentDefinitionDTO dto = DTOFactory.component(
                this.idGenerator.generateId(),
                name,
                iconLabel,
                description,
                new ArrayList<>(fields)
        );
        
        return dto;
    }

    @Override
    public void onCreateEvent(ComponentDefinitionDTO dto) {
        super.onCreateEvent(dto);
        fields.clear();
        view.clearFieldRows();
    }

    @Override
    public int getParentId() {
        return this.<SessionService>getService(ServiceType.SESSION).getCurrentAccount().getId().value();
    }

}