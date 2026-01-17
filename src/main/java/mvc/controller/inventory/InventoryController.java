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
import mvc.model.inventory.InventoryObject;
import mvc.model.inventory.ItemStack;
import mvc.view.inventory.InventoryPopupView;
import mvc.view.inventory.InventoryView;

public class InventoryController extends AbstractController<InventoryView>{

    private IInventoryElement inventory;
    private CollectionDTO currentCollection;
    private RecipeDTO currentRecipe; 

    

    public InventoryController(EventBuffer buffer) {
        super(buffer);
        this.inventory = new InventoryObject(null);
    }
    
    @Override
    public void attachView(InventoryView view) {
        this.view = view;
        this.view.setOnGridResized(() -> this.populateGrid());
        this.view.setOnCellDoubleClicked(cell -> handleCellDoubleClick(cell));
        super.attachView(view); 
    }

    @Override
    public void handleButton() {
        commonHandleButton();

        
        this.view.getSelectCollectionButton().setOnAction(e -> {

            List<EntryDTO> collectionsDTO = DTOFactory.collectionsAsEntries(context.getCollections());
            
            boolean resetColl = this.view.showAlert(
                "Seleccionar otra coleccion",
                "Estas seguro de querer cambiar la coleccion sobre la que operar?\n" +
                "El inventario se reseteara al clicar sobre Aceptar",
                Alert.AlertType.CONFIRMATION
            );

            if(resetColl){
                this.clearInventory();
                this.currentRecipe = null;

                UIPrefabsFactory.createSelectionPopup(
                    view.getSelectCollectionButton(),
                    view.getSelectCollections(),
                    collectionsDTO,
                    selected -> {
                        view.getSelectedCollectionLabel().setText(selected.name);
                        this.currentCollection = context.getCollectionById(selected.id);
                    }
            );
            }
            
        });

        this.view.getSelectRecipeButton().setOnAction(e -> {

            if(currentCollection == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de elegir una Receta.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> recipeDTO = DTOFactory.recipesAsEntries(context.getRecipesByCollection(new EntryId(currentCollection.id)));

            UIPrefabsFactory.createSelectionPopup(
                    view.getSelectCollectionButton(),
                    view.getSelectCollections(),
                    recipeDTO,
                    selected -> {
                        view.getSelectedRecipeLabel().setText(selected.name); 
                        this.currentRecipe = context.getRecipeDTOById(selected.id);
                        this.view.updateRecipeRelatedLists(currentRecipe, context);
                    }
            );

            

        });

        this.view.getExecuteRecipeButton().setOnAction(e -> {
            if(this.currentRecipe == null){
                this.view.showAlert(
                    "Ninguna Receta Seleccionada",
                    "Por favor selecciona una Receta antes de ejecutarla.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            //NOTA, cambiar por un relkoad en repo
            Recipe actRecipe = context.getRecipeById(this.currentRecipe.id);
            this.context.getEntriesRepo().modifyEntry(actRecipe);
            

            Recipe recipe = context.getRecipeByIdFromBD(this.currentRecipe.id);
            ArrayList<ItemIdStack> inventoryItems = new ArrayList<>();
            for(IInventoryElement element : this.inventory.flattenInventory()){
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
                toModify.add(new ItemStack(context.getItemById(input.getId().value()), -input.getAmount()));
            }
            for(ItemIdStack result : results){
                toModify.add(new ItemStack(context.getItemById(result.getId().value()), result.getAmount()));
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

        this.view.getAddItemButton().setOnAction(e -> {
            if(currentCollection == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(context.getItemsByCollection(new EntryId(currentCollection.id)));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    null,
                    itemsDTOs,
                    selected -> {
                        Integer amount = UIPrefabsFactory.showAmount("Cantidad a añadir",
                            "Indica la cantidad " + selected.name + " que deseas añadir al inventario:");
                        Item toAdd = context.getItemById(selected.id);
                        if(amount == null) return;
                        if(amount <= 0) {this.view.showAlert(
                            "Cantidad no valida",
                            "La cantidad a añadir debe ser mayor que 0.",
                            Alert.AlertType.WARNING
                        );
                        } else{
                        this.inventory.addItemHere(toAdd, amount, false);
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
                "Indica la cantidad de " + context.getItemById(id).getName() + " item que deseas eliminar del inventario:\n(Introduzca el valor en positivo)");

            if(amount == null) return;
            if(amount <= 0) this.view.showAlert(
                "Cantidad no valida",
                "La cantidad a eliminar debe ser mayor que 0.",
                Alert.AlertType.WARNING
            );
            else this.inventory.modifyAmountHere(context.getItemById(id), amount * -1);
            this.populateGrid();
        });

        this.view.getClearButton().setOnAction(e -> {
            this.clearInventory(); 
            this.currentCollection = null;
            this.currentRecipe = null;
        });

        this.view.getAddContainerItemButton().setOnAction(e -> {

            if(currentCollection == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de añadir items al inventario.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            List<EntryDTO> itemsDTOs = DTOFactory.itemsAsEntries(context.getItemsByCollection(new EntryId(currentCollection.id)));
            UIPrefabsFactory.createSelectionPopup(
                    view.getAddItemButton(),
                    null,
                    itemsDTOs,
                    selected -> {
                        
                        Item toAdd = context.getItemById(selected.id);

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
    }
 
    public void populateGrid(){ 
        this.view.clearGridInventory();
        List<IInventoryElement> inv = inventory.getInventory();
        for (IInventoryElement obj : inv) {
            ItemDTO itemDTO = DTOFactory.item(obj.getItem().getName(), obj.getItem().getImagePath(), obj.getItem().getDescription(), obj.getItem().getId().value());
            this.view.addElementToGrid(DTOFactory.itemStack(itemDTO, obj.getAmount()));
        }
        
    }

    private void clearInventory(){
            this.inventory = new InventoryObject(null);
            this.view.clearInventory();
        }

    private void handleCellDoubleClick(StackPane cell) {
        Object data = cell.getUserData();
        if (data == null) return;

        int itemId = (int) data; // o el tipo que uses
        ItemDTO dto = context.getItemDTOById(itemId);

        // Si no es contenedor, ignoramos
        if (inventory.findHere(context.getItemById(itemId)).isLeaf()) { 
            return;
        }

        // Si es contenedor, abrimos popup con su inventario
        openContainerInventory(dto); // <--- aquí va
    }


    private void openContainerInventory(ItemDTO containerItem) {

        IInventoryElement elem =
            inventory.findHere(context.getItemById(containerItem.id));

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        InventoryPopupView view = new InventoryPopupView();
        InventoryPopupController controller =
            new InventoryPopupController(elem, buffer);

        controller.attachView(view);
        controller.setRuntimeContext(context);
        controller.setCurrentRecipe(this.currentRecipe);
        controller.setCurrentCollection(this.currentCollection);
        controller.getView().updateRecipeRelatedLists(currentRecipe, context);
        popup.setScene(new Scene(view.getRoot(), 400, 400));
        popup.show();

        
    }


}
