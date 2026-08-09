package mvc.view.show.single;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import utilities.ThemeManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mvc.model.entries.component.ComponentField;

public class ShowComponentDataView extends AbstractShowEntryView {

    private VBox centralPanel, rightPanel;
    private VBox fieldsList;
    private Label fieldsTitle;
    private ScrollPane fieldsScroll;
    private HBox actionButtonHBox;

    public ShowComponentDataView() {
        super();
    }

    @Override
    protected void build() {
        super.build();

        this.root = new BorderPane();
        this.root.setPadding(new Insets(15));

        // --- PANEL CENTRAL ---
        centralPanel = new VBox(10);
        centralPanel.setPadding(new Insets(10, 20, 10, 20));
        centralPanel.setAlignment(Pos.CENTER);

        nameField = new TextField();
        nameField.setEditable(false);
        nameField.setFocusTraversable(false);

        descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(descriptionArea, Priority.ALWAYS);

        fieldsTitle = new Label("Campos");
        fieldsList = new VBox(5);
        fieldsList.setPadding(new Insets(5, 0, 5, 0));

        VBox fieldsContainer = new VBox(5, fieldsTitle, fieldsList);
        fieldsContainer.setPadding(new Insets(5));
        fieldsContainer.getStyleClass().add("scrollable-list-box");

        fieldsScroll = new ScrollPane(fieldsContainer);
        fieldsScroll.setFitToWidth(true);
        fieldsScroll.setPrefHeight(150);

        // --- BOTONES ---
        goBackIcon.setFitHeight(32);
        goBackIcon.setFitWidth(32);
        goBackButton = new Button();
        goBackButton.setGraphic(goBackIcon);
        goBackButton.getStyleClass().add("action-button");

        modifyIcon = new ImageView(ThemeManager.getThemedImage("lapiz.png"));
        modifyIcon.setFitHeight(32);
        modifyIcon.setFitWidth(32);
        modifyButton = new Button();
        modifyButton.setGraphic(modifyIcon);
        modifyButton.getStyleClass().add("action-button");

        deleteIcon = new ImageView(ThemeManager.getThemedImage("papelera.png"));
        deleteIcon.setFitHeight(32);
        deleteIcon.setFitWidth(32);
        deleteButton = new Button();
        deleteButton.setGraphic(deleteIcon);
        deleteButton.getStyleClass().add("action-button");

        actionButtonHBox = new HBox(10, goBackButton, modifyButton, deleteButton);
        actionButtonHBox.setPadding(new Insets(10, 0, 0, 0));
        actionButtonHBox.setAlignment(Pos.CENTER_RIGHT);

        centralPanel.getChildren().addAll(nameField, descriptionArea, fieldsScroll, actionButtonHBox);

        // --- PANEL DERECHO ---
        rightPanel = new VBox();
        rightPanel.setMinWidth(100);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setAlignment(Pos.CENTER);

        entryIcon = new ImageView();
        entryIcon.setFitWidth(200);
        entryIcon.setFitHeight(200);
        entryIcon.setPreserveRatio(true);
        rightPanel.getChildren().add(entryIcon);

        this.root.setCenter(centralPanel);
        this.root.setRight(rightPanel);
    }

    @Override
    protected void buildFields() {
        build();
    }

    public void setFields(List<ComponentField> fields) {
        fieldsList.getChildren().clear();
        for (ComponentField f : fields) {
            Label nameLbl = new Label(f.getFieldName());
            nameLbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            Label typeLbl = new Label(f.getFieldType().toString());
            typeLbl.setMinWidth(80);

            HBox row = new HBox(10, nameLbl, typeLbl);
            row.setAlignment(Pos.CENTER_LEFT);

            fieldsList.getChildren().add(row);
        }
    }
}