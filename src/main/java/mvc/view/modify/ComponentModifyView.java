package mvc.view.modify;
 
import java.util.List;

import dataTransportLayer.ComponentDefinitionDTO;
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

public class ComponentModifyView extends AbstractModifyView<ComponentDefinitionDTO> {

    private VBox fieldsBox;
    private VBox fieldsList;

    private TextField fieldNameField;
    private ComboBox<FieldType> fieldTypeCombo;
    private TextField enumValuesField;
    private Button addFieldButton;

    @Override
    protected void build() {
        super.build();

        Label fieldsTitle = new Label("Campos");

        fieldsList = new VBox(5);
        fieldsList.setPadding(new Insets(5, 0, 5, 0));

        ScrollPane fieldsScroll = new ScrollPane(fieldsList);
        fieldsScroll.setFitToWidth(true);
        fieldsScroll.setPrefHeight(150);

        fieldNameField = new TextField();
        fieldNameField.setPromptText("Nombre del campo");
        HBox.setHgrow(fieldNameField, Priority.ALWAYS);

        fieldTypeCombo = new ComboBox<>();
        fieldTypeCombo.getItems().addAll(FieldType.values());
        fieldTypeCombo.setPromptText("Tipo");

        enumValuesField = new TextField();
        enumValuesField.setPromptText("Opciones (separadas por coma)");
        enumValuesField.setVisible(false);
        enumValuesField.setManaged(false);
        HBox.setHgrow(enumValuesField, Priority.ALWAYS);

        fieldTypeCombo.valueProperty().addListener((obs, oldV, newV) -> {
            boolean isEnum = newV == FieldType.ENUM || newV == FieldType.ENUMLIST;
            enumValuesField.setVisible(isEnum);
            enumValuesField.setManaged(isEnum);
        });

        addFieldButton = new Button("Añadir campo");

        HBox fieldInputRow = new HBox(10, fieldNameField, fieldTypeCombo, enumValuesField, addFieldButton);
        fieldInputRow.setAlignment(Pos.CENTER_LEFT);

        fieldsBox = new VBox(5, fieldsTitle, fieldsScroll, fieldInputRow);
        fieldsBox.setPadding(new Insets(10, 0, 0, 0));

        this.root.getChildren().add(fieldsBox);
    }

    public Button getAddFieldButton() { return this.addFieldButton; }
    public TextField getFieldNameField() { return this.fieldNameField; }
    public ComboBox<FieldType> getFieldTypeCombo() { return this.fieldTypeCombo; }
    public TextField getEnumValuesField() { return this.enumValuesField; }
    public VBox getFieldsList() { return this.fieldsList; }

    public void addFieldRow(ComponentField field, Runnable onEdit, Runnable onRemove) {

        Label nameLabel = new Label(field.getFieldName());
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label typeLabel = new Label(field.getFieldType().toString()
                + (field.getFieldType() == FieldType.ENUM || field.getFieldType() == FieldType.ENUMLIST ? " " + field.getEnumValues() : ""));
        typeLabel.setMinWidth(80);

        Button editBtn = new Button("Editar");
        Button removeBtn = new Button("Quitar");

        HBox row = new HBox(10, nameLabel, typeLabel, editBtn, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setUserData(field);

        editBtn.setOnAction(e -> onEdit.run());
        removeBtn.setOnAction(e -> onRemove.run());

        fieldsList.getChildren().add(row);
    }

    public void removeFieldRow(ComponentField field) {
        fieldsList.getChildren().removeIf(node -> node.getUserData() == field);
    }

    public void clearFieldRows() {
        fieldsList.getChildren().clear();
    }

    public void clearFieldInputs() {
        fieldNameField.clear();
        fieldTypeCombo.getSelectionModel().clearSelection();
        enumValuesField.clear();
    }

    public void setFields(List<ComponentField> fields, Runnable2<ComponentField> onEdit, Runnable2<ComponentField> onRemove) {
        clearFieldRows();
        for (ComponentField f : fields) {
            addFieldRow(f, () -> onEdit.run(f), () -> onRemove.run(f));
        }
    }

    public interface Runnable2<X> { void run(X arg); }

    @Override
    public void clear() {
        super.clear();
        clearFieldRows();
        clearFieldInputs();
    }
}