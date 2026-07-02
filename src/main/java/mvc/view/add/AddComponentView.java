package mvc.view.add;

import java.util.ArrayList;
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
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType;

/**
 *
 * View que muestra lo que ve el usuario al añadir un nuevo {@code ComponentDefinition}.
 * Ademas de los campos comunes (nombre, icono, descripcion), permite ir "pre-añadiendo"
 * fields (nombre + tipo) a la definicion del componente, con opcion de editarlos o
 * quitarlos antes de confirmar la creacion.
 *
 */
public class AddComponentView extends AbstractAddView {

    private TextField fieldNameField;
    private ComboBox<FieldType> fieldTypeCombo;
    private Button addFieldButton;

    private VBox fieldsList;

    // Fila que se esta editando actualmente, o null si se esta añadiendo una nueva 

    @Override
    protected void buildSpecificFields() {
        this.nameLabel.setText("Nombre del componente");
        this.addButton.setText("Añadir componente");
    }

    @Override
    protected void addExtraContent(VBox root) {

        Label fieldsTitle = new Label("Campos del componente");

        fieldNameField = new TextField();
        fieldNameField.setPromptText("Nombre del campo");
        HBox.setHgrow(fieldNameField, Priority.ALWAYS);

        fieldTypeCombo = new ComboBox<>();
        fieldTypeCombo.getItems().addAll(FieldType.values());
        fieldTypeCombo.setPromptText("Tipo");

        addFieldButton = new Button("Añadir campo");

        HBox fieldInputRow = new HBox(10, fieldNameField, fieldTypeCombo, addFieldButton);
        fieldInputRow.setAlignment(Pos.CENTER_LEFT);

        fieldsList = new VBox(5);
        fieldsList.setPadding(new Insets(5, 0, 5, 0));

        ScrollPane fieldsScroll = new ScrollPane(fieldsList);
        fieldsScroll.setFitToWidth(true);
        fieldsScroll.setPrefHeight(150);

        VBox fieldsBox = new VBox(5, fieldsTitle, fieldsScroll, fieldInputRow);

        root.getChildren().add(fieldsBox);
    }

    /**
     *
     * Metodo que devuelve el boton usado para pre-añadir un field a la lista
     *
     * @return boton de añadir field
     */
    public Button getAddFieldButton() { return this.addFieldButton; }

    /**
     *
     * Metodo que devuelve el campo de texto donde se introduce el nombre del field
     *
     * @return campo de nombre del field
     */
    public TextField getFieldNameField() { return this.fieldNameField; }

    /**
     *
     * Metodo que devuelve el combo donde se selecciona el tipo del field
     *
     * @return combo de tipo del field
     */
    public ComboBox<FieldType> getFieldTypeCombo() { return this.fieldTypeCombo; }

    /**
     *
     * Metodo que devuelve el contenedor visual donde se listan los fields ya pre-añadidos
     *
     * @return VBox contenedor de filas de fields
     */
    public VBox getFieldsList() { return this.fieldsList; }

    /**
     *
     * Añade visualmente una fila representando un field pre-añadido, con botones
     * para editarlo o quitarlo.
     *
     * @param field field a mostrar
     * @param onEdit callback invocado cuando se pulsa el boton de editar esa fila
     * @param onRemove callback invocado cuando se pulsa el boton de quitar esa fila
     */
    public void addFieldRow(ComponentField field, Runnable onEdit, Runnable onRemove) {

        Label nameLabel = new Label(field.getFieldName());
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label typeLabel = new Label(field.getFieldType().toString());
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

    /**
     *
     * Quita del listado visual la fila asociada a un field concreto
     *
     * @param field field cuya fila se quiere quitar
     */
    public void removeFieldRow(ComponentField field) {
        fieldsList.getChildren().removeIf(node -> node.getUserData() == field);
    }

    /**
     *
     * Limpia el listado visual de fields pre-añadidos
     *
     */
    public void clearFieldRows() {
        fieldsList.getChildren().clear();
    }

    /**
     *
     * Limpia los campos de alta de un field (nombre y tipo)
     *
     */
    public void clearFieldInputs() {
        fieldNameField.clear();
        fieldTypeCombo.getSelectionModel().clearSelection();
    }

    @Override
    public void clearFields() {
        super.clearFields();
        clearFieldRows();
        clearFieldInputs();
    }
}