package mvc.view.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import creational.DTOFactory;
import creational.ImageUtils;
import creational.UIPrefabsFactory; 
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button; 
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mvc.context.RuntimeContext; 
import mvc.view.AbstractView;

public class InventoryView extends AbstractView {

    private static final int CELL_SIZE = 64;
    private static final int INITIAL_ROWS = 6;


    // Inventario
    private ScrollPane inventoryScroll;
    private GridPane inventoryGrid;
    private Label inventoryLabel;

    // Contexto
    private Label selectedCollectionLabel;
    private Button selectCollectionButton;
    private Label selectedRecipeLabel;
    private Button selectRecipeButton;
    private Button executeRecipeButton;

    // Visualización y edición de ingredientes y resultados
    private VBox ingredientsBox;
    private VBox resultsBox;
    private Button addItemButton;
    private Button removeItemButton;
    private Button addContainerItemButton;
    private VBox ingredients;
    private VBox results; 

    // Botón limpiar inventario
    private Button clearButton;

    // PopUps
    private VBox selectCollections; 
    
    // Eventos
    private Runnable onGridResized;

    public void setOnGridResized(Runnable r) {
        this.onGridResized = r;
    }

    private java.util.function.Consumer<StackPane> onCellDoubleClicked;

    public void setOnCellDoubleClicked(Consumer<StackPane> action) {
        this.onCellDoubleClicked = action;
    }


    @Override
    protected void build() {

        BorderPane mainPane = new BorderPane();
        VBox.setVgrow(mainPane, Priority.ALWAYS);

        // ======================
        // CENTRO: INVENTARIO
        // ======================

        inventoryGrid = new GridPane();
        inventoryGrid.setHgap(5);
        inventoryGrid.setVgap(5);
        inventoryGrid.setPadding(new Insets(10));

        inventoryScroll = new ScrollPane(inventoryGrid);
        inventoryScroll.setFitToWidth(true);
        inventoryScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        inventoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        inventoryScroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            int columns = Math.max(1, (int) (newVal.getWidth() / CELL_SIZE));
            rebuildInventoryGrid(columns);
            onGridResized.run();
        });

        inventoryLabel = new Label("INVENTARIO");
        inventoryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        inventoryLabel.setMaxWidth(Double.MAX_VALUE);
        inventoryLabel.setAlignment(Pos.CENTER);

        VBox inventoryBox = new VBox(10, inventoryScroll, inventoryLabel);
        VBox.setVgrow(inventoryScroll, Priority.ALWAYS);

        mainPane.setCenter(inventoryBox);

        rebuildInventoryGrid(1);

        // ======================
        // DERECHA: CONTEXTO Y RECETAS
        // ======================

        selectedCollectionLabel = new Label("Colección: Ninguna");
        selectedCollectionLabel.setMaxWidth(Double.MAX_VALUE);
        selectedCollectionLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");

        selectCollectionButton = new Button("Seleccionar colección");
        selectCollectionButton.setMaxWidth(Double.MAX_VALUE);

        selectedRecipeLabel = new Label("Receta: Ninguna");
        selectedRecipeLabel.setMaxWidth(Double.MAX_VALUE);
        selectedRecipeLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");

        selectRecipeButton = new Button("Seleccionar receta");
        selectRecipeButton.setMaxWidth(Double.MAX_VALUE);

        executeRecipeButton = new Button("Ejecutar receta");
        executeRecipeButton.setMaxWidth(Double.MAX_VALUE);

        // ---- Ingredientes ----
        Label ingredientsLabel = new Label("Ingredientes");
        ingredients = new VBox(5);
        ingredientsBox = UIPrefabsFactory.createScrollableModifableListNoButton(ingredientsLabel, ingredients);
        

        ScrollPane ingredientsScroll = new ScrollPane(ingredientsBox);
        ingredientsScroll.setFitToWidth(true);
        ingredientsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // ---- Resultados ----
        Label resultsLabel = new Label("Resultados");
        results = new VBox(5);
        resultsBox = UIPrefabsFactory.createScrollableModifableListNoButton(resultsLabel, results); 

        ScrollPane resultsScroll = new ScrollPane(resultsBox);
        resultsScroll.setFitToWidth(true);
        resultsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox recipeIOBox = new HBox(10, ingredientsScroll, resultsScroll);
        HBox.setHgrow(ingredientsScroll, Priority.ALWAYS);
        HBox.setHgrow(resultsScroll, Priority.ALWAYS);

        // ---- Botones inferiores ----
        addItemButton = new Button("Añadir ítems");
        removeItemButton = new Button("Eliminar ítems");

        addItemButton.setMaxWidth(Double.MAX_VALUE);
        removeItemButton.setMaxWidth(Double.MAX_VALUE);

        // --- BOTÓN NUEVO ---
        addContainerItemButton = new Button("Añadir ítem contenedor");
        addContainerItemButton.setMaxWidth(Double.MAX_VALUE);

        // Caja con los tres botones
        HBox topButtons = new HBox(10, addItemButton, removeItemButton);
        HBox.setHgrow(addItemButton, Priority.ALWAYS);
        HBox.setHgrow(removeItemButton, Priority.ALWAYS);

        VBox recipeButtons = new VBox(10, topButtons, addContainerItemButton);
        VBox.setVgrow(topButtons, Priority.NEVER);
        addContainerItemButton.setMaxWidth(Double.MAX_VALUE);



        VBox rightPanel = new VBox(
                10,
                selectedCollectionLabel,
                selectCollectionButton,
                selectedRecipeLabel,
                selectRecipeButton,
                executeRecipeButton,
                recipeIOBox,
                recipeButtons
        );

        rightPanel.setPrefWidth(300);
        mainPane.setRight(rightPanel);

        // ======================
        // ABAJO: LIMPIAR INVENTARIO
        // ======================

        clearButton = new Button("Limpiar inventario");

        HBox bottomBar = new HBox(clearButton);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(10));

        // ======================
        // POPUPS
        // ======================

        
        // ======================
        // ROOT
        // ======================
        // Contenedor único para mainPane + bottomBar
        // Contenedor de contenido para el SplitPane
        BorderPane contentContainer = new BorderPane();
        contentContainer.setCenter(mainPane);
        contentContainer.setBottom(bottomBar);

        // Aquí NO hay VBox extra
        UIPrefabsFactory.initSideBar(this.sideBar,
                                    this.inventoryButton,
                                    this.userButton,
                                    this.collectionButton,
                                    this.splitPane,
                                    contentContainer);

        // Root
        this.root = new VBox();
        this.root.getChildren().add(splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS); 

    }

    private void rebuildInventoryGrid(int columns) {
        inventoryGrid.getChildren().clear();

        int cellIndex = 0;

        for (int row = 0; row < INITIAL_ROWS; row++) {
            for (int col = 0; col < columns; col++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setMinSize(CELL_SIZE, CELL_SIZE);
                cell.setMaxSize(CELL_SIZE, CELL_SIZE);

                cell.setStyle("""
                    -fx-border-color: #444;
                    -fx-background-color: #1e1e1e;
                """);

                final int index = cellIndex++;

                cell.setOnMouseClicked(e -> {
                // Selección normal (igual que ahora)
                for (javafx.scene.Node n : inventoryGrid.getChildren()) {
                    if (n instanceof StackPane c) {
                        c.setStyle("""
                            -fx-border-color: #444;
                            -fx-background-color: #1e1e1e;
                        """);
                    }
                }

                inventoryGrid.setUserData(index);
                cell.setStyle("""
                    -fx-border-color: gold;
                    -fx-background-color: #2a2a2a;
                """);

                // === NUEVO: DOBLE CLICK ===
                if (e.getClickCount() == 2) {
                    if (onCellDoubleClicked != null) {
                        onCellDoubleClicked.accept(cell);
                    }
                }
            });



                inventoryGrid.add(cell, col, row);
            }
        }
    
    }

    public void clearInventory() {
        rebuildInventoryGrid(Math.max(1, (int) (inventoryScroll.getViewportBounds().getWidth() / CELL_SIZE)));
        this.selectedRecipeLabel.setText("Receta: Ninguna");
        this.selectedCollectionLabel.setText("Colección: Ninguna");
        this.ingredients.getChildren().clear();
        this.results.getChildren().clear(); 
    }

    public void clearGridInventory() {
        rebuildInventoryGrid(Math.max(1, (int) (inventoryScroll.getViewportBounds().getWidth() / CELL_SIZE))); 
    }

    public void updateRecipeRelatedLists(RecipeDTO recipeDTO, RuntimeContext context) {

        
        this.ingredients.getChildren().clear();
        this.results.getChildren().clear();

        List<ItemStackDTO> ing = new ArrayList<>();
        List<ItemStackDTO> res = new ArrayList<>();

        for (ItemIdStackDTO elem : recipeDTO.ingredients) {
            ing.add(DTOFactory.itemStack(
                context.getItemDTOById(elem.id),
                elem.amount
            ));
        }

        for (ItemIdStackDTO elem : recipeDTO.results) {
            res.add(DTOFactory.itemStack(
                context.getItemDTOById(elem.id),
                elem.amount
            ));
        }
        
        UIPrefabsFactory.addSeveralItemStackRows(ing, ingredients);
        UIPrefabsFactory.addSeveralItemStackRows(res, results);

    }

    public void addElementToGrid(ItemStackDTO dto) {
        for (javafx.scene.Node node : inventoryGrid.getChildren()) {
            if (node instanceof StackPane cell) {

                if (cell.getChildren().isEmpty()) {
                    
                    // Guardar id
                    cell.setUserData(dto.item.id);

                    // Tooltip SOLO para esta celda
                    Tooltip tooltip = new Tooltip(
                        dto.item.name + "\n" +
                        "Cantidad: " + dto.amount + "\n" +
                        "Descripción: " + dto.item.description
                    );

                    tooltip.setWrapText(true);
                    tooltip.setMaxWidth(250); 


                    tooltip.setShowDelay(Duration.millis(150));
                    Tooltip.install(cell, tooltip);

                    // Imagen grande del item
                    ImageView icon = new ImageView(ImageUtils.getImage(dto.item.iconPath));
                    icon.setFitWidth(CELL_SIZE - 10);
                    icon.setFitHeight(CELL_SIZE - 10);
                    icon.setPreserveRatio(true);

                    // Cantidad esquina
                    Label amountLabel = new Label(String.valueOf(dto.amount));
                    amountLabel.setStyle("""
                        -fx-background-color: rgba(0,0,0,0.6);
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        -fx-padding: 2;
                    """);
                    StackPane.setAlignment(amountLabel, Pos.TOP_RIGHT);

                    cell.getChildren().addAll(icon, amountLabel);
                    return;
                }
            }
        }

        
    }

    


    //#region Getters
    // ======================
    // GETTERS
    // ======================
    public Label getSelectedCollectionLabel() {
        return selectedCollectionLabel;
    }

    public Button getSelectCollectionButton() {
        return selectCollectionButton;
    }

    public Label getSelectedRecipeLabel() {
        return selectedRecipeLabel;
    }

    public Button getSelectRecipeButton() {
        return selectRecipeButton;
    }

    public Button getExecuteRecipeButton() {
        return executeRecipeButton;
    }

    public VBox getIngredientsBox() {
        return ingredientsBox;
    }

    public VBox getResultsBox() {
        return resultsBox;
    }

    public Button getAddItemButton() {
        return addItemButton;
    }

    public Button getRemoveItemButton() {
        return removeItemButton;
    }

    public GridPane getInventoryGrid() {
        return inventoryGrid;
    }

    public Label getInventoryLabel() {
        return inventoryLabel;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public VBox getSelectCollections() {
        return selectCollections;
    }

    public Button getAddContainerItemButton() {
        return addContainerItemButton;
    }
    //#endregion
}
