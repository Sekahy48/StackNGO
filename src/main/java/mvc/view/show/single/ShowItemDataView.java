package mvc.view.show.single;

import java.util.List;

import dataTransportLayer.ComponentDefinitionDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import utilities.ThemeManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.ItemComponentValue;

public class ShowItemDataView extends AbstractShowEntryView {

    private VBox centralPanel, rightPanel;
    private VBox componentsList;
    private ImageView plusIconR, plusIconI;
    private HBox actionButtonHBox;
 

    public ShowItemDataView() {
        super();
    } 

    @Override
    protected void buildFields() {
        build();
    }

    @Override
    protected void build() {
        super.build();
        
        this.root = new BorderPane();
        this.root.setPadding(new Insets(15));

        //this.goBackIcon = new ImageView(new Image("images/volver.png"));
        this.goBackIcon.setFitHeight(32);
        this.goBackIcon.setFitWidth(32);

        this.goBackButton = new Button();
        this.goBackButton.setGraphic(this.goBackIcon);
        this.goBackButton.getStyleClass().add("action-button");

        this.plusIconR = new ImageView(ThemeManager.getThemedImage("añadir.png"));
        this.plusIconR.setFitHeight(16);
        this.plusIconR.setFitWidth(16);

        this.addRecipeButton = new Button();
        this.addRecipeButton.setGraphic(this.plusIconR);
        this.addRecipeButton.getStyleClass().add("icon-button");

        this.plusIconI = new ImageView(ThemeManager.getThemedImage("añadir.png"));
        this.plusIconI.setFitHeight(16);
        this.plusIconI.setFitWidth(16);

        this.addItemButton = new Button();
        this.addItemButton.setGraphic(this.plusIconI);
        this.addItemButton.getStyleClass().add("icon-button");

        this.centralPanel = new VBox(10);
        this.centralPanel.setPadding(new Insets(10, 20, 10, 20));
        this.centralPanel.setAlignment(Pos.CENTER);

        this.nameField = new TextField();
        this.nameField.setEditable(false);
        this.nameField.setFocusTraversable(false);

        this.descriptionArea = new TextArea();
        this.descriptionArea.setEditable(false);
        this.descriptionArea.setPrefRowCount(10);

        // Botones para eliminar, editar y volver atras

        this.modifyIcon = new ImageView(ThemeManager.getThemedImage("lapiz.png"));
        this.modifyIcon.setFitHeight(32);
        this.modifyIcon.setFitWidth(32);

        this.modifyButton = new Button();
        this.modifyButton.setGraphic(this.modifyIcon);
        this.modifyButton.getStyleClass().add("action-button");

        this.deleteIcon = new ImageView(ThemeManager.getThemedImage("papelera.png"));
        this.deleteIcon.setFitHeight(32);
        this.deleteIcon.setFitWidth(32);

        this.deleteButton = new Button();
        this.deleteButton.setGraphic(this.deleteIcon);
        this.deleteButton.getStyleClass().add("action-button");

        // Sección componentes (read-only)
        Label componentsTitle = new Label("Componentes");
        this.componentsList = new VBox(10);
        this.componentsList.setPadding(new Insets(5, 0, 5, 0));

        VBox componentsContainer = new VBox(5, componentsTitle, componentsList);
        componentsContainer.setPadding(new Insets(5));
        componentsContainer.getStyleClass().add("scrollable-list-box");

        ScrollPane componentsScroll = new ScrollPane(componentsContainer);
        componentsScroll.setFitToWidth(true);
        componentsScroll.setPrefHeight(180);
        VBox.setVgrow(componentsScroll, Priority.ALWAYS);

        // HBox que contiene a los dos botones
        this.actionButtonHBox = new HBox(10, goBackButton, modifyButton, deleteButton);
        this.actionButtonHBox.setPadding(new Insets(10, 0, 0, 0));
        this.actionButtonHBox.setAlignment(Pos.CENTER_RIGHT);

        this.centralPanel.getChildren().addAll(
                nameField,
                descriptionArea,
                componentsScroll,
                this.actionButtonHBox
        );

        this.root.setCenter(centralPanel);

        // Panel derecho donde está el icono de la colección

        this.rightPanel = new VBox();
        this.rightPanel.setMinWidth(100);
        this.rightPanel.setPadding(new Insets(10));
        this.rightPanel.setAlignment(Pos.CENTER);

        this.entryIcon = new ImageView();
        this.entryIcon.setFitWidth(200);
        this.entryIcon.setFitHeight(200);
        this.entryIcon.setPreserveRatio(true);

        this.rightPanel.getChildren().add(entryIcon);

        this.root.setRight(rightPanel);
    }

    /**
     * Muestra un componente del item en modo solo-lectura.
     * Replica la estructura visual de ItemModifyView.addComponentRow
     * pero con Labels en lugar de TextFields/ComboBoxes.
     */
    public void displayComponentRow(ComponentDefinitionDTO def, ItemComponentValue value) {
        Label nameLabel = new Label(def.name);
        nameLabel.getStyleClass().add("bold-label");

        HBox header = new HBox(10, nameLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox fieldsBox = new VBox(5);
        for (ComponentField field : def.fields) {
            String currentValue = value.getValue(field.getFieldName());
            String display = (currentValue != null && !currentValue.isEmpty()) ? currentValue : "—";

            Label fLabel = new Label(field.getFieldName() + " (" + field.getFieldType() + "):");
            Label vLabel = new Label(display);

            HBox row = new HBox(10, fLabel, vLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            fieldsBox.getChildren().add(row);
        }

        VBox componentBox = new VBox(5, header, fieldsBox);
        componentBox.getStyleClass().add("component-box");

        componentsList.getChildren().add(componentBox);
    }

    public void clearComponents() {
        componentsList.getChildren().clear();
    }
}