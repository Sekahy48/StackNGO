package mvc.view.show.single;

import creational.UIPrefabsFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShowRecipeDataView extends AbstractShowEntryView {

    private VBox leftPanel, centralPanel, rightPanel;
    private Label outputLabel, inputLabel;

    private VBox inputList;
    private VBox outputList;

    private VBox inputs;
    private VBox outputs;

    private ImageView plusIconI, plusIconO;
    private HBox actionButtonHBox; 
    private Button addOutputButton, addInputButton;

    public ShowRecipeDataView() {
        super();
    }

    @Override
    protected void build() {
        super.build();
        
        this.root = new BorderPane();
        this.root.setPadding(new Insets(15));

        // --- PANEL IZQUIERDO ---
        leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(10));

        inputLabel = new Label("Ingredientes");
        plusIconI = new ImageView(new Image("images/añadir.png"));
        plusIconI.setFitHeight(16);
        plusIconI.setFitWidth(16);

        addInputButton = new Button();
        addInputButton.setGraphic(plusIconI);
        addInputButton.setStyle("-fx-background-color: transparent;");

        inputs = new VBox(5);
        inputList = UIPrefabsFactory.createScrollableModifableList(inputLabel, inputs, addInputButton);
        inputList.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(inputList, Priority.ALWAYS);

        outputLabel = new Label("Resultados");
        plusIconO = new ImageView(new Image("images/añadir.png"));
        plusIconO.setFitHeight(16);
        plusIconO.setFitWidth(16);

        addOutputButton = new Button();
        addOutputButton.setGraphic(plusIconO);
        addOutputButton.setStyle("-fx-background-color: transparent;");

        outputs = new VBox(5);
        outputList = UIPrefabsFactory.createScrollableModifableList(outputLabel, outputs, addOutputButton);
        outputList.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(outputList, Priority.ALWAYS);

        leftPanel.getChildren().addAll(inputList, outputList);
        leftPanel.setPrefWidth(150);

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

        // --- BOTONES ---
        goBackIcon.setFitHeight(32);
        goBackIcon.setFitWidth(32);
        goBackButton = new Button();
        goBackButton.setGraphic(goBackIcon);
        goBackButton.setStyle("-fx-background-color: transparent;-fx-border-color: black;-fx-border-width: 2;");

        modifyIcon = new ImageView(new Image("images/lapiz.png"));
        modifyIcon.setFitHeight(32);
        modifyIcon.setFitWidth(32);
        modifyButton = new Button();
        modifyButton.setGraphic(modifyIcon);
        modifyButton.setStyle("-fx-background-color: transparent;-fx-border-color: black;-fx-border-width: 2;");

        deleteIcon = new ImageView(new Image("images/papelera.png"));
        deleteIcon.setFitHeight(32);
        deleteIcon.setFitWidth(32);
        deleteButton = new Button();
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: transparent;-fx-border-color: black;-fx-border-width: 2;");

        actionButtonHBox = new HBox(10, goBackButton, modifyButton, deleteButton);
        actionButtonHBox.setPadding(new Insets(10, 0, 0, 0));
        actionButtonHBox.setStyle("-fx-alignment: center-right;");

        centralPanel.getChildren().addAll(nameField, descriptionArea, actionButtonHBox);

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

        // --- HBOX INTERMEDIO PARA IZQUIERDA + CENTRAL ---
        HBox middleBox = new HBox(10, leftPanel, centralPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(centralPanel, Priority.ALWAYS);

        this.root.setCenter(middleBox);
        this.root.setRight(rightPanel);
    }

    @Override
    protected void buildFields() {
        build();
    }

    public VBox getInputList() { return inputs; }
    public VBox getOutputList() { return outputs; }
    public Button getGoBackButton() { return goBackButton; }
    public Button getAddOutputButton() { return addOutputButton; }
    public Button getAddInputButton() { return addInputButton; }
}
