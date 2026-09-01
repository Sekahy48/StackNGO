package mvc.view.add;  

import java.util.List;

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
import dataTransportLayer.ComponentDefinitionDTO;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.ItemComponentValue;

/**
 * View para añadir item. Ademas de campos comunes, permite asignar
 * componentes (previamente creados) con valores concretos por field.
 */
public class AddItemView extends AbstractAddView {  

    private ComboBox<ComponentDefinitionDTO> componentCombo;
    private Button addComponentButton;
    private VBox componentsList;

    @Override
    protected void buildSpecificFields() {
        this.nameLabel.setText("Nombre del item");
        this.addButton.setText("Añadir item");
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
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox box = new VBox(5, title, scroll);
        VBox.setVgrow(box, Priority.ALWAYS);
        root.getChildren().addAll(box, inputRow);
    }

    public ComboBox<ComponentDefinitionDTO> getComponentCombo() { return this.componentCombo; }
    public Button getAddComponentButton() { return this.addComponentButton; }
    public VBox getComponentsList() { return this.componentsList; }

    public void setAvailableComponents(List<ComponentDefinitionDTO> components) {
        componentCombo.getItems().setAll(components);
    }

    /**
     * Añade una fila visual representando un componente ya asignado al item,
     * con un TextField editable por cada field definido.
     *
     * @param def definicion del componente
     * @param value instancia con los valores actuales (se rellenan al escribir)
     * @param onRemove callback al pulsar quitar
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

            if (field.getFieldType() == mvc.model.entries.component.FieldType.ENUM) {
                ComboBox<String> valueCombo = new ComboBox<>();
                valueCombo.getItems().addAll(field.getEnumValues());
                valueCombo.valueProperty().addListener((obs, oldV, newV) -> value.setValue(field.getFieldName(), newV));
                HBox row = new HBox(10, fLabel, valueCombo);
                row.setAlignment(Pos.CENTER_LEFT);
                fieldsBox.getChildren().add(row);
            } else if (field.getFieldType() == mvc.model.entries.component.FieldType.BOOLEAN) {
                ComboBox<String> boolCombo = new ComboBox<>();
                boolCombo.getItems().addAll("true", "false");
                boolCombo.setValue("false");
                value.setValue(field.getFieldName(), "false");
                boolCombo.valueProperty().addListener((obs, oldV, newV) -> value.setValue(field.getFieldName(), newV));
                HBox row = new HBox(10, fLabel, boolCombo);
                row.setAlignment(Pos.CENTER_LEFT);
                fieldsBox.getChildren().add(row);
            } else {
                TextField valueField = new TextField();
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
    public void clearFields() {
        super.clearFields();
        clearComponentRows();
        componentCombo.getSelectionModel().clearSelection();
    }
}