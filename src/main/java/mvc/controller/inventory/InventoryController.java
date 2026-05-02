package mvc.controller.inventory;

import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.Session;

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
import service.CollectionService;
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;

public class InventoryController extends AbstractController<InventoryView>{

    private IInventoryElement inventory;
    /*Movidos a SessionContext, accesibles por SessionService
    private CollectionDTO currentCollection;
    private RecipeDTO currentRecipe;  */

    

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
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ItemService itemService = this.getService(ServiceType.ITEM);
        this.view.getSelectCollectionButton().setOnAction(e -> {
            CollectionService collectionService = this.getService(ServiceType.COLLECTION); 

            List<EntryDTO> collectionsDTO = DTOFactory.collectionsAsEntries(collectionService.getAllDTO(sessionService.getCurrentAccount().getId().value()));
            
            boolean resetColl = this.view.showAlert(
                "Seleccionar otra coleccion",
                "Estas seguro de querer cambiar la coleccion sobre la que operar?\n" +
                "El inventario se reseteara al clicar sobre Aceptar",
                Alert.AlertType.CONFIRMATION
            );

            if(resetColl){
                this.clearInventory();
                sessionService.setCurrentInventoryRecipe(null);

                UIPrefabsFactory.createSelectionPopup(
                    view.getSelectCollectionButton(),
                    collectionsDTO,
                    selected -> {
                        view.getSelectedCollectionLabel().setText(selected.name);
                        sessionService.setCurrentInventoryCollection(collectionService.getDTOById(selected.id));
                    }
            );
            }
            
        });

        this.view.getSelectRecipeButton().setOnAction(e -> {

            if(sessionService.getCurrentInventoryCollectionDTO() == null){
                this.view.showAlert(
                    "Ninguna Coleccion Seleccionada",
                    "Por favor selecciona una Coleccion antes de elegir una Receta.",
                    Alert.AlertType.WARNING
                );
                return;
            }
            //TODO AHORA buscar si se puede extraer la extraccion de ing y res
            RecipeService recipeService = this.getService(ServiceType.RECIPE);

            List<EntryDTO> recipeDTO = DTOFactory.recipesAsEntries(recipeService.getAllDTO(sessionService.getCurrentInventoryCollectionDTO().id)); 
            UIPrefabsFactory.createSelectionPopup(
                    view.getSelectCollectionButton(),
                    recipeDTO,
                    selected -> {
                        view.getSelectedRecipeLabel().setText(selected.name); 
                        RecipeDTO currentRecipeDTO = recipeService.getDTOById(selected.id);
                        sessionService.setCurrentInventoryRecipe(currentRecipeDTO);
                        this.view.updateRecipeRelatedLists(itemService.idStackToStackList(currentRecipeDTO.ingredients),
                                                           itemService.idStackToStackList(currentRecipeDTO.results));
                    }
            );

            

        });

        this.view.getExecuteRecipeButton().setOnAction(e -> {
            if(sessionService.getCurrentInventoryRecipeDTO() == null){
                this.view.showAlert(
                    "Ninguna Receta Seleccionada",
                    "Por favor selecciona una Receta antes de ejecutarla.",
                    Alert.AlertType.WARNING
                );
                return;
            }

            //NOTA, si algo da error revisar no vaya a ser que la omision de esto de problemas, a priori veo absurdo el que se haga
            //Recipe actRecipe = context.getRecipeById(this.currentRecipe.id);
            //this.context.getEntriesRepo().modifyEntry(actRecipe);

            RecipeService recipeService = this.getService(ServiceType.RECIPE);
            Recipe recipe = recipeService.getEntryById(sessionService.getCurrentInventoryRecipeDTO().id);
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
        ItemService itemService = this.getService(ServiceType.ITEM);
        Object data = cell.getUserData();
        if (data == null) return;

        int itemId = (int) data; // o el tipo que uses
        ItemDTO dto = itemService.getDTOById(itemId);

        // Si es contenedor, accedemos
        if (!inventory.findHere(itemService.getEntryById(itemId)).isLeaf()) { 
            openContainerInventory(dto);
        }

        // Si no, ignoramos
    }


    private void openContainerInventory(ItemDTO containerItem) {
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        IInventoryElement elem =
            inventory.findHere(itemService.getEntryById(containerItem.id));

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        InventoryPopupView view = new InventoryPopupView();
        InventoryPopupController controller =
            new InventoryPopupController(elem, buffer);

        controller.attachView(view); 
        controller.addService(sessionService); 

        //Considerar que esta invocación se realice en el InventoryPopUpController y no aqui. 
        RecipeDTO currentRecipeDTO = sessionService.getCurrentInventoryRecipeDTO();
        controller.getView().updateRecipeRelatedLists(itemService.idStackToStackList(currentRecipeDTO.ingredients),
                                                           itemService.idStackToStackList(currentRecipeDTO.results));
        popup.setScene(new Scene(view.getRoot(), 400, 400));
        popup.show();

        
    }

    /*
    List<ItemStackDTO> ing = new ArrayList<>();
    List<ItemStackDTO> res = new ArrayList<>();

    for (ItemIdStackDTO elem : recipeDTO.ingredients) {
        ing.add(DTOFactory.itemStack(
            service.getDTOById(elem.id),
            elem.amount
        ));
    }

    for (ItemIdStackDTO elem : recipeDTO.results) {
        res.add(DTOFactory.itemStack(
            service.getDTOById(elem.id),
            elem.amount
        ));
    }    */


}
