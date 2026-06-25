package mvc.view.inventory;

import java.util.List;
import java.util.function.Consumer;

import creational.UIPrefabsFactory;
import dataTransportLayer.ItemStackDTO; 
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
import mvc.view.AbstractView; 
import utilities.ImageUtils;

/**
 * Abstract base for inventory views.
 * Contains: inventory grid, recipe IO panel, add/remove/container item buttons, execute recipe button, clear button.
 * Subclasses add collection/recipe selection (InventoryView) or nothing extra (InventoryPopupView).
 */
public abstract class AbstractInventoryView extends AbstractView {

    protected static final int CELL_SIZE = 64;
    protected static final int INITIAL_ROWS = 6;

    // Piezas
    protected BorderPane contentContainer;
    protected HBox recipeIOBox;
    protected VBox recipeButtons;
    // Inventario
    protected ScrollPane inventoryScroll;
    protected GridPane inventoryGrid;
    protected Label inventoryLabel;

    // Contexto
    protected Label selectedCollectionLabel;
    protected Label selectedRecipeLabel;
    protected Button executeRecipeButton;

    // Visualización y edición de ingredientes y resultados
    protected VBox ingredientsBox;
    protected VBox resultsBox;
    protected Button addItemButton;
    protected Button removeItemButton;
    protected Button addContainerItemButton;
    protected VBox ingredients;
    protected VBox results; 

    // Botón limpiar inventario
    protected Button clearButton; 
    
    // Eventos
    protected Runnable onGridResized;

    protected java.util.function.Consumer<StackPane> onCellDoubleClicked;

    //#region Setters
    public void setOnGridResized(Runnable r) {
        this.onGridResized = r;
    }

    public void setOnCellDoubleClicked(Consumer<StackPane> action) {
        this.onCellDoubleClicked = action;
    }

    //#endregion

    //#region Metodos operativos
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
            if (onGridResized != null) onGridResized.run();
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

        /* Lo elimino pq en la clase padre no se decide si se puede seleccionar o no coleccion
        selectCollectionButton = new Button("Seleccionar colección");
        selectCollectionButton.setMaxWidth(Double.MAX_VALUE);
         */

        selectedRecipeLabel = new Label("Receta: Ninguna");
        selectedRecipeLabel.setMaxWidth(Double.MAX_VALUE);
        selectedRecipeLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");

        /* Lo elimino pq en la clase padre no se decide si se puede seleccionar o no receta
        selectRecipeButton = new Button("Seleccionar receta");
        selectRecipeButton.setMaxWidth(Double.MAX_VALUE);
        */

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

        recipeIOBox = new HBox(10, ingredientsScroll, resultsScroll);
        HBox.setHgrow(ingredientsScroll, Priority.ALWAYS);
        HBox.setHgrow(resultsScroll, Priority.ALWAYS);

        // ---- Botones inferiores ----
        addItemButton = new Button("Añadir ítems");
        removeItemButton = new Button("Eliminar ítems");
        addContainerItemButton = new Button("Añadir ítem contenedor");

        addItemButton.setMaxWidth(Double.MAX_VALUE);
        removeItemButton.setMaxWidth(Double.MAX_VALUE);
        addContainerItemButton.setMaxWidth(Double.MAX_VALUE);

        // Caja con los tres botones
        HBox topButtons = new HBox(10, addItemButton, removeItemButton);
        HBox.setHgrow(addItemButton, Priority.ALWAYS);
        HBox.setHgrow(removeItemButton, Priority.ALWAYS);

        recipeButtons = new VBox(10, topButtons, addContainerItemButton);
        VBox.setVgrow(topButtons, Priority.NEVER);
        addContainerItemButton.setMaxWidth(Double.MAX_VALUE);



        VBox rightPanel = this.buildRightPanel();
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
        this.contentContainer = new BorderPane();
        contentContainer.setCenter(mainPane);
        contentContainer.setBottom(bottomBar);

        // Aquí NO hay VBox extra
        // Quito la sidebar this.initSidebar(contentContainer);

        // Root
        this.root = new VBox();
        this.root.getChildren().add(splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS); 

    }

    /**
     * Metodo interno que redibuja la grid del inventario y asigna funcionalidad a sus botones con cierto
     * numero de columnas a priori.
     * @param columns número de columnas con las que dibujar.
     * Hace que cada celda de la rejilla del inventario pueda ser seleccionada (un click) o accionada (doble click).     */
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
                if (e.getClickCount() == 2 && onCellDoubleClicked != null) {
                    onCellDoubleClicked.accept(cell);
                    
                }
            });



                inventoryGrid.add(cell, col, row);
            }
        }
    
    }

    /**
     * Método con la finalidad de limpiar el inventario entero, reseteando toda su información (colección y receta actuales, contenido de la rejilla, etc).
     */
    public void clearInventory() {
        rebuildInventoryGrid(Math.max(1, (int) (inventoryScroll.getViewportBounds().getWidth() / CELL_SIZE)));
        this.selectedRecipeLabel.setText("Receta: Ninguna");
        this.selectedCollectionLabel.setText("Colección: Ninguna");
        this.ingredients.getChildren().clear();
        this.results.getChildren().clear(); 
    }

    /**
     * Método que SOLO vacia la rejilla del inventario.
     * Llama internamente al metodo que redibuja la rejilla pero con unos parámetros predefinidos.
     */
    public void clearGridInventory() {
        rebuildInventoryGrid(Math.max(1, (int) (inventoryScroll.getViewportBounds().getWidth() / CELL_SIZE))); 
    }

    /**
     * Método que actualiza la lista de ingeredientes y resultados de la receta vigente.
     * @param ing ingredientes de la receta actual.
     * @param res resultados de ejecutar la receta actual.
     */
    public void updateRecipeRelatedLists(List<ItemStackDTO> ing, List<ItemStackDTO> res) {

        this.ingredients.getChildren().clear();
        this.results.getChildren().clear();

        UIPrefabsFactory.addSeveralItemStackRows(ing, ingredients);
        UIPrefabsFactory.addSeveralItemStackRows(res, results);

    }

    /**
     * Añade un ítem a la rejilla del inventario.
     * @param dto del ítem a añadir.
     */
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
                    ImageView icon = new ImageView(ImageUtils.getImage(dto.item.imagePath));
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
    
    /**
     * Monta el panel derecho de la vista, el panel referente a receta y coleccion.
     * @return
     */
    protected VBox buildRightPanel() {
        VBox rightPanel = new VBox(
                10,
                selectedCollectionLabel, 
                selectedRecipeLabel, 
                executeRecipeButton,
                recipeIOBox,
                recipeButtons
        );
        rightPanel.setPrefWidth(300);
        return rightPanel;
    }
    //#endregion

    //#region Getters
    // ======================
    // GETTERS
    // ======================
    public Label getSelectedCollectionLabel() {
        return selectedCollectionLabel;
    }

    public Label getSelectedRecipeLabel() {
        return selectedRecipeLabel;
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

    public Button getAddContainerItemButton() {
        return addContainerItemButton;
    }
    //#endregion
}