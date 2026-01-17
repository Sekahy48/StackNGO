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

public class InventoryPopupView extends AbstractView {

    private static final int CELL_SIZE = 64;
    private static final int INITIAL_ROWS = 6;

    // Inventario
    private ScrollPane inventoryScroll;
    private GridPane inventoryGrid;

    // Ingredientes / resultados
    private VBox ingredients;
    private VBox results;

    // Botones
    private Button addItemButton;
    private Button removeItemButton;
    private Button addContainerItemButton;
    private Button executeRecipeButton;
    private Button clearButton;

    // Eventos
    private Runnable onGridResized;
    private Consumer<StackPane> onCellDoubleClicked;

    public void setOnGridResized(Runnable r) {
        this.onGridResized = r;
    }

    public void setOnCellDoubleClicked(Consumer<StackPane> c) {
        this.onCellDoubleClicked = c;
    }

    @Override
    protected void build() {

        BorderPane mainPane = new BorderPane();

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

        inventoryScroll.viewportBoundsProperty().addListener((obs, o, n) -> {
            if (n == null) return;
            int cols = Math.max(1, (int)(n.getWidth() / CELL_SIZE));
            rebuildInventoryGrid(cols);
            if (onGridResized != null) onGridResized.run();
        });

        rebuildInventoryGrid(1);
        mainPane.setCenter(inventoryScroll);

        // ======================
        // DERECHA: RECETA
        // ======================

        ingredients = new VBox(5);
        results = new VBox(5);

        VBox ingredientsBox =
            UIPrefabsFactory.createScrollableModifableListNoButton(
                new Label("Ingredientes"), ingredients);

        VBox resultsBox =
            UIPrefabsFactory.createScrollableModifableListNoButton(
                new Label("Resultados"), results);

        HBox recipeIO = new HBox(10, ingredientsBox, resultsBox);
        HBox.setHgrow(ingredientsBox, Priority.ALWAYS);
        HBox.setHgrow(resultsBox, Priority.ALWAYS);

        executeRecipeButton = new Button("Ejecutar receta");
        executeRecipeButton.setMaxWidth(Double.MAX_VALUE);

        VBox rightPanel = new VBox(10, recipeIO, executeRecipeButton);
        rightPanel.setPrefWidth(300);
        mainPane.setRight(rightPanel);

        // ======================
        // ABAJO: BOTONES
        // ======================

        addItemButton = new Button("Añadir ítems");
        removeItemButton = new Button("Eliminar ítems");
        addContainerItemButton = new Button("Añadir ítem contenedor");
        clearButton = new Button("Limpiar inventario");

        HBox row1 = new HBox(10, addItemButton, removeItemButton);
        HBox.setHgrow(addItemButton, Priority.ALWAYS);
        HBox.setHgrow(removeItemButton, Priority.ALWAYS);

        VBox bottom = new VBox(10, row1, addContainerItemButton, clearButton);
        bottom.setPadding(new Insets(10));

        mainPane.setBottom(bottom);

        this.root = new VBox(mainPane);
    }

    private void rebuildInventoryGrid(int columns) {

        inventoryGrid.getChildren().clear();
        int index = 0;

        for (int row = 0; row < INITIAL_ROWS; row++) {
            for (int col = 0; col < columns; col++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("""
                    -fx-border-color: #444;
                    -fx-background-color: #1e1e1e;
                """);

                final int cellIndex = index++;

                cell.setOnMouseClicked(e -> {

                    for (var n : inventoryGrid.getChildren()) {
                        if (n instanceof StackPane c) {
                            c.setStyle("""
                                -fx-border-color: #444;
                                -fx-background-color: #1e1e1e;
                            """);
                        }
                    }

                    inventoryGrid.setUserData(cellIndex);
                    cell.setStyle("""
                        -fx-border-color: gold;
                        -fx-background-color: #2a2a2a;
                    """);

                    if (e.getClickCount() == 2 && onCellDoubleClicked != null) {
                        onCellDoubleClicked.accept(cell);
                    }
                });

                inventoryGrid.add(cell, col, row);
            }
        }
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
    
    public void clearGridInventory() {
        rebuildInventoryGrid(
            Math.max(1, (int)(inventoryScroll.getViewportBounds().getWidth() / CELL_SIZE))
        );
    }

    public void addElementToGrid(ItemStackDTO dto) {

        for (var n : inventoryGrid.getChildren()) {
            if (n instanceof StackPane cell && cell.getChildren().isEmpty()) {

                cell.setUserData(dto.item.id);

                Tooltip tooltip = new Tooltip(
                    dto.item.name + "\nCantidad: " + dto.amount
                );
                tooltip.setShowDelay(Duration.millis(150));
                Tooltip.install(cell, tooltip);

                ImageView icon =
                    new ImageView(ImageUtils.getImage(dto.item.iconPath));
                icon.setFitWidth(CELL_SIZE - 10);
                icon.setFitHeight(CELL_SIZE - 10);

                Label amount =
                    new Label(String.valueOf(dto.amount));
                    amount.setStyle("""
                    -fx-background-color: rgba(0,0,0,0.6);
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-padding: 2;
                    """);

                StackPane.setAlignment(amount, Pos.TOP_RIGHT);

                cell.getChildren().addAll(icon, amount);
                return;
            }
        }
    }

    // Getters
    public Button getAddItemButton() { return addItemButton; }
    public Button getRemoveItemButton() { return removeItemButton; }
    public Button getAddContainerItemButton() { return addContainerItemButton; }
    public Button getExecuteRecipeButton() { return executeRecipeButton; }
    public Button getClearButton() { return clearButton; }
    public VBox getIngredients() { return ingredients; }
    public VBox getResults() { return results; }
    public GridPane getInventoryGrid() { return inventoryGrid; }
}
