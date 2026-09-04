package mvc.view.modify;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;
import mvc.model.entries.component.ItemComponentValue;

public class ItemModifyView extends AbstractModifyView<ItemDTO>{

    private ComboBox<ComponentDefinitionDTO> componentCombo;
    private Button addComponentButton;
    private VBox componentsList;

    @Override
    public void modifyFields(ItemDTO dto) {
        super.modifyFields(dto);
    }

    @Override
    protected void addExtraContent(VBox root) {

        Label title = new Label("Componentes");

        componentCombo = new ComboBox<>();
        componentCombo.setPromptText("Elegir componente");
        componentCombo.setConverter(new javafx.util.StringConverter<ComponentDefinitionDTO>() {
            @Override public String toString(ComponentDefinitionDTO d) { return d != null ? d.name : ""; }
            @Override public ComponentDefinitionDTO fromString(String s) { return null; }
        });
        componentCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(ComponentDefinitionDTO d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.name);
            }
        });
        HBox.setHgrow(componentCombo, Priority.ALWAYS);

        addComponentButton = new Button("Añadir componente");

        HBox inputRow = new HBox(10, componentCombo, addComponentButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        componentsList = new VBox(10);
        componentsList.setPadding(new Insets(5, 0, 5, 0));

        ScrollPane scroll = new ScrollPane(componentsList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);

        VBox box = new VBox(5, title, scroll, inputRow);
        box.setPadding(new Insets(10, 0, 0, 0));
        root.getChildren().add(box);
    }

    public ComboBox<ComponentDefinitionDTO> getComponentCombo() { return this.componentCombo; }
    public Button getAddComponentButton() { return this.addComponentButton; }
    public VBox getComponentsList() { return this.componentsList; }

    public void setAvailableComponents(List<ComponentDefinitionDTO> components) {
        componentCombo.getItems().setAll(components);
    }

    /**
     * Añade fila de componente ya asignado, con TextFields/ComboBox pre-rellenados
     * con los valores actuales de la instancia.
     */
    public void addComponentRow(ComponentDefinitionDTO def, ItemComponentValue value, Runnable onRemove) {

        Label nameLabel = new Label(def.name);
        nameLabel.getStyleClass().add("bold-label");

        Button removeBtn = new Button("Quitar");
        removeBtn.setOnAction(e -> onRemove.run());

        HBox header = new HBox(10, nameLabel, removeBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox fieldsBox = new VBox(5);
        for (ComponentField field : def.fields) {
            Label fLabel = new Label(field.getFieldName() + " (" + field.getFieldType() + "):");
            String currentValue = value.getValue(field.getFieldName());

            if (field.getFieldType() == FieldType.ENUM) {
                ComboBox<String> valueCombo = new ComboBox<>();
                valueCombo.getItems().addAll(field.getEnumValues());
                if (currentValue != null) valueCombo.setValue(currentValue);
                valueCombo.valueProperty().addListener((obs, oldV, newV) -> value.setValue(field.getFieldName(), newV));
                HBox row = new HBox(10, fLabel, valueCombo);
                row.setAlignment(Pos.CENTER_LEFT);
                fieldsBox.getChildren().add(row);
            } else if (field.getFieldType() == mvc.model.entries.component.FieldType.ENUMLIST) {

                ComboBox<String> enumCombo = new ComboBox<>();
                enumCombo.setPromptText("Selecciona un valor");
                enumCombo.getItems().addAll(field.getEnumValues());

                Button addButton = new Button("Añadir");
                Button removeButton = new Button("Retirar");

                TextField selectedValues = new TextField();
                selectedValues.setEditable(false);

                List<String> selected = new ArrayList<>();

                if (currentValue != null && !currentValue.isBlank()) {
                    selected.addAll(Arrays.asList(currentValue.split(",")));
                }

                selectedValues.setText(String.join(", ", selected));

                addButton.setOnAction(e -> {
                    String valueToAdd = enumCombo.getValue();

                    if (valueToAdd != null && !selected.contains(valueToAdd)) {
                        selected.add(valueToAdd);

                        selectedValues.setText(String.join(", ", selected));

                        value.setValue(
                            field.getFieldName(),
                            String.join(",", selected)
                        );
                    }
                });

                removeButton.setOnAction(e -> {
                    String valueToRemove = enumCombo.getValue();

                    if (valueToRemove != null && selected.remove(valueToRemove)) {
                        selectedValues.setText(String.join(", ", selected));

                        value.setValue(
                            field.getFieldName(),
                            String.join(",", selected)
                        );
                    }
                });

                HBox row = new HBox(
                    10,
                    fLabel,
                    enumCombo,
                    addButton,
                    removeButton,
                    selectedValues
                );

                row.setAlignment(Pos.CENTER_LEFT);

                fieldsBox.getChildren().add(row);
  
            } else if (field.getFieldType() == FieldType.BOOLEAN) {
                ComboBox<String> boolCombo = new ComboBox<>();
                boolCombo.getItems().addAll("true", "false");
                boolCombo.setValue("true".equalsIgnoreCase(currentValue) ? "true" : "false");
                value.setValue(field.getFieldName(), boolCombo.getValue());
                boolCombo.valueProperty().addListener((obs, oldV, newV) -> value.setValue(field.getFieldName(), newV));
                HBox row = new HBox(10, fLabel, boolCombo);
                row.setAlignment(Pos.CENTER_LEFT);
                fieldsBox.getChildren().add(row);
            } else {
                TextField valueField = new TextField(currentValue != null ? currentValue : "");
                valueField.textProperty().addListener((obs, oldV, newV) -> value.setValue(field.getFieldName(), newV));
                HBox row = new HBox(10, fLabel, valueField);
                row.setAlignment(Pos.CENTER_LEFT);
                fieldsBox.getChildren().add(row);
            }
        }

        VBox componentBox = new VBox(5, header, fieldsBox);
        componentBox.getStyleClass().add("component-box");
        componentBox.setUserData(value);

        componentsList.getChildren().add(componentBox);
    }

    public void removeComponentRow(ItemComponentValue value) {
        componentsList.getChildren().removeIf(node -> node.getUserData() == value);
    }

    public void clearComponentRows() {
        componentsList.getChildren().clear();
    }

    @Override
    public void clear() {
        super.clear();
        clearComponentRows();
        componentCombo.getSelectionModel().clearSelection();
    }
}