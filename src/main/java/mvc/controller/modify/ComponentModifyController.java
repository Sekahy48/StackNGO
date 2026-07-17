package mvc.controller.modify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dataTransportLayer.ComponentDefinitionDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.view.ViewType;
import mvc.view.modify.ComponentModifyView;
import service.ComponentService;
import service.ServiceType;
import service.SessionService;

public class ComponentModifyController extends AbstractModifyController<ComponentModifyView, ComponentDefinitionDTO> {

    private List<ComponentField> fields = new ArrayList<>();

    @Override
    public void attachView(ComponentModifyView view) {
        super.attachView(view);
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
        if (type == FieldType.ENUM) {
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
    protected ComponentDefinitionDTO composeDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ComponentService componentService = this.getService(ServiceType.COMPONENT);

        ComponentDefinitionDTO current = getCurrentDTO();

        String newName = !view.getNewName().isEmpty() ? view.getNewName() : current.name;
        String newDescription = !view.getNewDescription().isEmpty() ? view.getNewDescription() : current.description;
        String newImagePath = view.getNewImagePath() != null ? view.getNewImagePath() : current.imagePath; 

        if (!newName.equals(current.name) && componentService.containsEntryByName(newName, sessionService.getCurrentAccount().getId().value())) {
            view.showAlert("Nombre duplicado", "Ya existe un componente con ese nombre", Alert.AlertType.ERROR);
            return null;
        }

        return new ComponentDefinitionDTO(newName, newImagePath, newDescription, current.id, new ArrayList<>(fields));
    }

    @Override
    protected void onUpdateEvent(ComponentDefinitionDTO dto) {
        ComponentService componentService = this.getService(ServiceType.COMPONENT);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        int[] extraData = {sessionService.getCurrentAccount().getId().value()};
        componentService.saveEntry(dto, extraData);

        view.showAlert("Componente modificado", "El componente ha sido modificado correctamente", Alert.AlertType.INFORMATION);
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENT));
    }

    @Override
    protected void updateCurrentDTO(ComponentDefinitionDTO dto) {
        this.<SessionService>getService(ServiceType.SESSION).setCurrentComponent(dto);
    }

    @Override
    protected ComponentDefinitionDTO getCurrentDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentComponentDTO();
    }

    @Override
    public void updateAtShow() {
        super.updateAtShow();
        fields = new ArrayList<>(getCurrentDTO().fields);
        view.setFields(fields, this::onEditField, this::onRemoveField);
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COMPONENT));
    }

    @Override
    public java.util.Set<ServiceType> requiredServices() {
        return java.util.Set.of(ServiceType.COMPONENT, ServiceType.SESSION);
    }
}