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

public class ShowCollectionDataView extends AbstractShowDataView {

    private VBox leftPanel, centralPanel, rightPanel;
    private Label recipesLabel, itemsLabel;

    private VBox itemList;
    private VBox recipeList;

    private VBox items;
    private VBox recipes;

    private ImageView plusIconR, plusIconI;
    private HBox actionButtonHBox; 

    public Button getGoBackButton() { return this.goBackButton; }

    @Override
    protected void buildFields(){
        build();
    }

    @Override
    protected void build() {
        super.build();
        
        this.root = new BorderPane();
        this.root.setPadding(new Insets(15));

        // --- PANEL IZQUIERDO ---
        leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(10));

        recipesLabel = new Label("Recipes");
        plusIconR = new ImageView(new Image("images/añadir.png"));
        plusIconR.setFitHeight(16);
        plusIconR.setFitWidth(16);

        addRecipeButton = new Button();
        addRecipeButton.setGraphic(plusIconR);
        addRecipeButton.setStyle("-fx-background-color: transparent;");

        recipes = new VBox(5);
        recipeList = UIPrefabsFactory.createScrollableModifableList(recipesLabel, recipes, addRecipeButton);
        recipeList.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(recipeList, Priority.ALWAYS);

        itemsLabel = new Label("Items");
        plusIconI = new ImageView(new Image("images/añadir.png"));
        plusIconI.setFitHeight(16);
        plusIconI.setFitWidth(16);

        addItemButton = new Button();
        addItemButton.setGraphic(plusIconI);
        addItemButton.setStyle("-fx-background-color: transparent;");

        items = new VBox(5);
        itemList = UIPrefabsFactory.createScrollableModifableList(itemsLabel, items, addItemButton);
        itemList.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(itemList, Priority.ALWAYS);

        leftPanel.getChildren().addAll(recipeList, itemList);
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

    public VBox getItemList() { return items; }
    public VBox getRecipeList() { return recipes; }
}
