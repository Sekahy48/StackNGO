package mvc.controller.inventory;

import java.util.ArrayList; 
import java.util.List;

import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO; 
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.controller.AbstractController;
import mvc.model.entries.Item;
import mvc.model.entries.ItemIdStack;
import mvc.model.entries.Recipe;
import mvc.model.inventory.IInventoryElement;
import mvc.model.inventory.ItemStack;
import mvc.view.inventory.InventoryPopupView;
import service.ItemService;
import service.RecipeService;
import service.SessionService;
import service.ServiceType;

public class InventoryPopupController
        extends AbstractController<InventoryPopupView> {

    private IInventoryElement inventory;
    private RecipeDTO currentRecipe;
    private CollectionDTO currentCollection;

    public InventoryPopupController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(InventoryPopupView view) {
        this.view = view;

        view.setOnGridResized(this::populateGrid);
        view.setOnCellDoubleClicked(this::handleCellDoubleClick);

        handleButton();
        populateGrid();
    }

    @Override
    public void handleButton() {
        ItemService itemService = this.getService(ServiceType.ITEM);
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        view.getAddItemButton().setOnAction(e -> {
            if(currentCollection == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(itemService.getAllDTO(currentCollection.id));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    itemsDTOs,
                    selected -> {
                        Integer amount = UIPrefabsFactory.showAmount("Cantidad a añadir",
                            "Indica la cantidad " + selected.name + " que deseas añadir al inventario:");
                        Item toAdd = itemService.getEntryById(selected.id);
                        if(amount == null) return;
                        if(amount <= 0) this.view.showAlert(
                            "Cantidad no valida",
                            "La cantidad a añadir debe ser mayor que 0.",
                            Alert.AlertType.WARNING
                        );
                        this.inventory.addItemHere(toAdd, amount, false);
                        this.populateGrid();
                    });
        });

        view.getRemoveItemButton().setOnAction(e -> {
            if(this.view.getInventoryGrid().getUserData() == null) return;
            int selectedIndex = (int) this.view.getInventoryGrid().getUserData();
            StackPane selectedCell = (StackPane) this.view.getInventoryGrid().getChildren().get(selectedIndex);
            int id = selectedCell.getUserData() != null ? (int) selectedCell.getUserData() : -1;
            if(id == -1) return;

            // Pedimos cantidad
            Integer amount = UIPrefabsFactory.showAmount("Cantidad a eliminar",
                "Indica la cantidad de " + itemService.getEntryById(id).getName() + " item que deseas eliminar del inventario:\n(Introduzca el valor en positivo)");

            if(amount == null) return;
            if(amount <= 0) this.view.showAlert(
                "Cantidad no valida",
                "La cantidad a eliminar debe ser mayor que 0.",
                Alert.AlertType.WARNING
            );
            else this.inventory.modifyAmountHere(itemService.getEntryById(id), amount * -1);
            this.populateGrid();
        });

        view.getAddContainerItemButton().setOnAction(e -> {
            if(currentCollection == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(itemService.getAllDTO(currentCollection.id));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    itemsDTOs,
                    selected -> {
                        
                        Item toAdd = itemService.getEntryById(selected.id);

                        if(!inventory.containsHere(toAdd) || inventory.findHere(toAdd).isLeaf()) {
                            this.view.showAlert("Advertencia", "Cuidado, si añades este item como contenedor no podrás" + 
                            " utilizarlo como item normal, por tanto no podras ejecutar las recetas que lo involucren. " + 
                            "Si te arrepientes cancela en la selección de cantidad o elimina todos los items todos los items-contenedor " + 
                            "de cierto tipo para poder inesertarlo como item normal.", Alert.AlertType.WARNING);
                        }

                        Integer amount = UIPrefabsFactory.showAmount("Cantidad a añadir",
                            "Indica la cantidad " + selected.name + " que deseas añadir al inventario:");
                         
                        if(amount == null) return;
                        if(amount <= 0) this.view.showAlert(
                            "Cantidad no valida",
                            "La cantidad a añadir debe ser mayor que 0.",
                            Alert.AlertType.WARNING
                        );
                        this.inventory.addItemHere(toAdd, amount, true);
                        this.populateGrid();
                    });
        });

        view.getExecuteRecipeButton().setOnAction(e -> {
            if(this.currentRecipe == null){
                this.view.showAlert(
                    "Ninguna Receta Seleccionada",
                    "Por favor selecciona una Receta antes de ejecutarla.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            Recipe recipe = recipeService.getEntryById(sessionService.getCurrentInventoryRecipeDTO().id);
            ArrayList<ItemIdStack> inventoryItems = new ArrayList<>();
            for(IInventoryElement element : this.inventory.flattenInventory()){ 
                inventoryItems.add(new ItemIdStack(element.getItem().getId(), element.getAmount()));
            }
            boolean canExecute = recipe.canBeExecuted(inventoryItems);

            if(!canExecute){
                this.view.showAlert(
                    "Receta no ejecutable",
                    "No se pueden cumplir los requisitos de la receta con el inventario actual.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            ArrayList<ItemIdStack> results = recipe.executeRecipe(inventoryItems);
            ArrayList<ItemIdStack> inputs = recipe.getIngredients();

            ArrayList<ItemStack> toModify = new ArrayList<>();
            for(ItemIdStack input : inputs){
                toModify.add(new ItemStack(itemService.getEntryById(input.getId().value()), -input.getAmount()));
            }
            for(ItemIdStack result : results){
                toModify.add(new ItemStack(itemService.getEntryById(result.getId().value()), result.getAmount()));
            }

            for(ItemStack mod : toModify){
                if(this.inventory.containsHere(mod.getItem())){
                    this.inventory.modifyAmountHere(mod.getItem(), mod.getAmount());
                } else if (mod.getAmount() > 0){
                    this.inventory.addItemHere(mod.getItem(), mod.getAmount(), false);
                } else if (this.inventory.contains(mod.getItem()) && mod.getAmount() < 0){
                    this.inventory.modifyAmount(mod.getItem(), mod.getAmount());
                }
            } 

            this.populateGrid();
        });
        
        view.getClearButton().setOnAction(e -> {
            inventory.clearInventory();
            populateGrid();
        });
    }

    private void populateGrid() {
        view.clearGridInventory();

        for (IInventoryElement elem : inventory.getInventory()) {
            ItemDTO dto = DTOFactory.item(
                elem.getItem().getName(),
                elem.getItem().getImagePath(),
                elem.getItem().getDescription(),
                elem.getItem().getId().value()
            );
            view.addElementToGrid(
                DTOFactory.itemStack(dto, elem.getAmount())
            );
        }
    }

    private void handleCellDoubleClick(StackPane cell) {

        if (cell.getUserData() == null) return;

        int id = (int) cell.getUserData();
        Item item = this.<ItemService>getService(ServiceType.ITEM).getEntryById(id);

        IInventoryElement elem = inventory.findHere(item);
        if (elem.isLeaf()) return;

        openPopup(elem);
    }

    private void openPopup(IInventoryElement container) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(container.getItem().getName());

        InventoryPopupView view = new InventoryPopupView();
        InventoryPopupController controller =
            new InventoryPopupController(container, buffer);

        controller.attachView(view);  
        controller.addService(sessionService);

        RecipeDTO currentRecipeDTO = sessionService.getCurrentInventoryRecipeDTO();
        controller.getView().updateRecipeRelatedLists(currentRecipe, context);

        popup.setScene(new Scene(view.getRoot(), 400, 400));
        popup.show();
    } 
}
