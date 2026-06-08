package mvc.controller.inventory;

import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
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
import mvc.view.inventory.AbstractInventoryView;
import mvc.view.inventory.InventoryPopupView;
import service.InventoryService;
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;

public class AbstractInventoryController<T extends AbstractInventoryView> extends AbstractController<T>{

    public AbstractInventoryController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void attachView(T view) {
        this.view = view;
        this.view.setOnGridResized(() -> this.populateGrid());
        this.view.setOnCellDoubleClicked(cell -> handleCellDoubleClick(cell));
        super.attachView(view); 
    }

    @Override
    public void handleButton() {
        commonHandleButton();
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ItemService itemService = this.getService(ServiceType.ITEM);

        this.view.getExecuteRecipeButton().setOnAction(e -> {
            if(sessionService.getCurrentInventoryRecipeDTO() == null){
                this.view.showAlert(
                    "Ninguna Receta Seleccionada",
                    "Por favor selecciona una Receta antes de ejecutarla.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            RecipeService recipeService = this.getService(ServiceType.RECIPE);
            Recipe recipe = recipeService.getEntryById(sessionService.getCurrentInventoryRecipeDTO().id);
            ArrayList<ItemIdStack> inventoryItems = new ArrayList<>();
            for(IInventoryElement element : this.getCurrentInventory().flattenInventory()){
                if(element.isLeaf())inventoryItems.add(new ItemIdStack(element.getItem().getId(), element.getAmount()));
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
                if(this.getCurrentInventory().containsHere(mod.getItem())){
                    this.getCurrentInventory().modifyAmountHere(mod.getItem(), mod.getAmount());
                } else if (mod.getAmount() > 0){
                    this.getCurrentInventory().addItemHere(mod.getItem(), mod.getAmount(), false);
                } else if (this.getCurrentInventory().contains(mod.getItem()) && mod.getAmount() < 0){
                    this.getCurrentInventory().modifyAmount(mod.getItem(), mod.getAmount());
                }
            }

            this.populateGrid();
        });

        this.view.getAddItemButton().setOnAction(e -> {
            if(sessionService.getCurrentInventoryCollectionDTO() == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(itemService.getAllDTO(sessionService.getCurrentInventoryCollectionDTO().id));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    itemsDTOs,
                    selected -> {
                        Integer amount = UIPrefabsFactory.showAmount("Cantidad a añadir",
                            "Indica la cantidad " + selected.name + " que deseas añadir al inventario:");
                        Item toAdd = itemService.getEntryById(selected.id);
                        if(amount == null) return;
                        if(amount <= 0) {this.view.showAlert(
                            "Cantidad no valida",
                            "La cantidad a añadir debe ser mayor que 0.",
                            Alert.AlertType.WARNING
                        );
                        } else{
                        this.getCurrentInventory().addItemHere(toAdd, amount, false);
                        }
                        this.populateGrid();
                    });

            
        });

        this.view.getRemoveItemButton().setOnAction(e -> {
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
            else this.getCurrentInventory().modifyAmountHere(itemService.getEntryById(id), amount * -1);
            this.populateGrid();
        });

        this.view.getClearButton().setOnAction(e -> {
            this.clearInventory(); 
            sessionService.setCurrentInventoryCollection(null);
            sessionService.setCurrentInventoryRecipe(null);
        });

        this.view.getAddContainerItemButton().setOnAction(e -> {

            if(sessionService.getCurrentInventoryCollectionDTO() == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(itemService.getAllDTO(sessionService.getCurrentInventoryCollectionDTO().id));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    itemsDTOs,
                    selected -> {
                        
                        Item toAdd = itemService.getEntryById(selected.id);

                        if(this.<InventoryService>getService(ServiceType.INVENTORY).containsAsContainer(toAdd)) {
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
                        this.getCurrentInventory().addItemHere(toAdd, amount, true);
                        this.populateGrid();
                    });

        });
    }
    
    /**
     * Method that fils the view's grid with the items contained by the current inventory.
     */
    public void populateGrid(){ 
        this.view.clearGridInventory();
        List<IInventoryElement> inv = this.getCurrentInventory().getInventory();
        for (IInventoryElement obj : inv) {
            ItemDTO itemDTO = DTOFactory.item(obj.getItem().getName(), obj.getItem().getImagePath(), obj.getItem().getDescription(), obj.getItem().getId().value());
            this.view.addElementToGrid(DTOFactory.itemStack(itemDTO, obj.getAmount()));
        }
        
    }

    /**
     * Method that empties the current inventory content, not the whole inventory or upwards nodes in the hierarchy.
     */
    protected void clearInventory(){
            this.getInventoryService().clearCurrentInventory();
            this.view.clearInventory();
        }
    
    /**
     * Method that tries to open a inventory popup view based on the clicked cell's item information. If it isn't a container item it does nothing.
     * Expected to be used as a button-asigned handler.
     * @param cell double-clicked with the item-related information (an integer itemId).
     */
    protected void handleCellDoubleClick(StackPane cell) {
        ItemService itemService = this.getService(ServiceType.ITEM);
        Object data = cell.getUserData();
        if (data == null) return;

        int itemId = (int) data; 
        ItemDTO dto = itemService.getDTOById(itemId);

        // Si es contenedor, accedemos
        if (this.getInventoryService().containsAsContainer(itemService.getEntryById(itemId))) openContainerInventory(dto);
    }

    /**
     * Method that opens a popup view related to the inventory structure of a concrete item contained by the current inventory.
     * @param containerItem root target of the new inventory view.
     */
    private void openContainerInventory(ItemDTO containerItem) {
        // Resolve services.
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        InventoryService inventoryService = this.getService(ServiceType.INVENTORY);

        // Search the corresponding inventory node and set it as the new current inventory.
        IInventoryElement elem =
            this.getCurrentInventory().findHere(itemService.getEntryById(containerItem.id));

        inventoryService.pushCurrentInventory(elem);

        // Create popup's view and controler.
        InventoryPopupView view = new InventoryPopupView();
        InventoryPopupController controller = new InventoryPopupController(buffer);

        // Transfer services.
        controller.addService(itemService);
        controller.addService(inventoryService);
        controller.attachView(view); 
        controller.addService(sessionService); 

        // Update popup view.
        RecipeDTO currentRecipeDTO = sessionService.getCurrentInventoryRecipeDTO();
        controller.getView().updateRecipeRelatedLists(itemService.idStackToStackList(currentRecipeDTO.ingredients),
                                                           itemService.idStackToStackList(currentRecipeDTO.results));
        
        // Create and configurate the visual popup element.
        Stage popup = new Stage();
        popup.setTitle(containerItem.name);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setScene(new Scene(view.getRoot(), 400, 400));
        // Event for the closing of the view.
        popup.setOnHidden(e -> inventoryService.returnToParentInventory());
        popup.show();

    }

    protected IInventoryElement getCurrentInventory() {
        return this.getInventoryService().getCurrentInventory();
    }

    protected InventoryService getInventoryService() {
        return this.<InventoryService>getService(ServiceType.INVENTORY);
    }

    

}
