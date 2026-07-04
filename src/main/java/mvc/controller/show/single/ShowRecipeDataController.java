package mvc.controller.show.single;

import java.util.List;
import java.util.Set;

import creational.UIPrefabsFactory;
import static creational.UIPrefabsFactory.addPopUp;
import static creational.UIPrefabsFactory.rowExists;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logger.Logger;
import mvc.model.entries.RecipeIOType;
import mvc.view.ViewType;
import mvc.view.show.single.ShowRecipeDataView;
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

public class ShowRecipeDataController extends AbstractShowDataController<ShowRecipeDataView> {

    

    @Override
    public void handleButtons() {
        super.handleButtons();
        
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ItemService itemService = this.getService(ServiceType.ITEM);

        commonHandleButton();

        ShowRecipeDataView view = this.getView();
 
        Button modifyButton = view.getModifyButton(); 
        Button addOutputButton = view.getAddOutputButton();
        Button addInputButton = view.getAddInputButton();

        modifyButton.setOnAction(
                e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.MODIFY_RECIPE));}
        ); 

        addInputButton.setOnAction(
                e -> {  
                    List<ItemDTO> items = itemService.getAllDTO(sessionService.getCurrentCollectionDTO().getId());
                    addPopUp(
                            addInputButton,
                            items,
                            item -> addInput(item, view.getInputList()));
                }
        );

        addOutputButton.setOnAction(
                e -> {

                    List<ItemDTO> items = itemService.getAllDTO(sessionService.getCurrentCollectionDTO().getId());
                    addPopUp(
                            addOutputButton,
                            items,
                            item -> addOutput(item, view.getOutputList()));
                }
        );
    }

    private void fillItems(Integer id) {
        RecipeService recipeService = this.getService(ServiceType.RECIPE);

        VBox input = view.getInputList();
        VBox output = view.getOutputList();

        input.getChildren().clear();
        output.getChildren().clear();
 

        List<ItemStackDTO> listaInput = recipeService.getRecipeInputs(id);
        List<ItemStackDTO> listaOutput = recipeService.getRecipeOutputs(id);
        for (ItemStackDTO dto : listaInput) {
            createRecipeRow(
                    dto.item,
                    dto.amount,
                    input, 
                    RecipeIOType.INPUT,
                    false
            );
        }

        for (ItemStackDTO dto : listaOutput) {
            createRecipeRow(
                    dto.item,
                    dto.amount,
                    output, 
                    RecipeIOType.OUTPUT,
                    false
            );
        }
    }

    private void addInput(ItemDTO item, VBox target) {
        if (rowExists(target, item.name)) return;

        createRecipeRow(item, 1, target, RecipeIOType.INPUT, true);
    }

    private void addOutput(ItemDTO item, VBox target) {
        if (rowExists(target, item.name)) return;

        createRecipeRow(item, 1, target, RecipeIOType.OUTPUT, true);
    }

    @Override
    public void deleteShowingEntry(int id) {
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        boolean delete = this.view.showAlert(
                "Eliminar receta",
                "Seguro que quieres eliminar esta receta?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) { 
            RecipeDTO dto = recipeService.getDTOById(id);
            recipeService.removeEntry(id); 
            this.view.showAlert("Receta eliminada", "La receta con nombre " + dto.name + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "El usuario " + sessionService.getCurrentAccount().getUsername() + " ha borrado la receta con nombre " + dto.name + " en la coleccion " + sessionService.getCurrentCollectionDTO().name);
            EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
        }
    }

    private void createRecipeRow(
            ItemDTO item,
            int amountValue,
            VBox target, 
            RecipeIOType type,
            boolean persist
    ) {
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        RecipeDTO currentRecipeDTO = sessionService.getCurrentRecipeDTO();
        CollectionDTO currentCollectionDTO = sessionService.getCurrentCollectionDTO();
        String currentAcountName = sessionService.getCurrentAccount().getUsername();

        TextField amount = UIPrefabsFactory.createAmountField();
        amount.setText(String.valueOf(amountValue));

        amount.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d+")) return;
            int newAmount = Integer.parseInt(newVal);
            if (newAmount <= 0) {
                this.view.showAlert("Cantidad incorrecta", "No puede haber un input/output con cantidad igual a 0", Alert.AlertType.WARNING);
            } else {
                RecipeDTO dto = recipeService.getDTOById(sessionService.getCurrentCollectionDTO().id);

                if (type == RecipeIOType.INPUT) {
                    recipeService.updateInputAmount(currentRecipeDTO.id, item.id, newAmount);
                    Logger.getInstance().info(this.getClass().toString(),
                            "El usuario " + currentAcountName + " ha cambiado la cantidad a " + newAmount + " del input " + item.name + " en la receta " + dto.name + " en la coleccion " + currentCollectionDTO.name);
                } else {
                    recipeService.updateOutputAmount(currentRecipeDTO.id, item.id, newAmount);
                    Logger.getInstance().info(this.getClass().toString(),
                            "El usuario " + currentAcountName + " ha cambiado la cantidad a " + newAmount + " del output " + item.name + " en la receta " + dto.name + " en la coleccion " + currentCollectionDTO.name);
                }
            }

        });

        Button delete = UIPrefabsFactory.createRemoveButton();
        Image icon = ImageUtils.getImage(item.imagePath);

        HBox row = UIPrefabsFactory.createRow(item.name, icon, delete, amount);
        target.getChildren().add(row);

        if (persist) {
            if (type == RecipeIOType.INPUT) {
                Logger.getInstance().info(this.getClass().toString(),
                        "El usuario " + currentAcountName + " ha creado el input " + item.name + " en la receta " + currentRecipeDTO.name + " en la coleccion " + currentCollectionDTO.name);
                recipeService.insertSingleInput(view.getParentId().value(), item.id, amountValue, sessionService.getCurrentCollectionDTO().id);
            } else {
                Logger.getInstance().info(this.getClass().toString(),
                        "El usuario " + currentAcountName + " ha creado el output " + item.name + " en la receta " + currentRecipeDTO.name + " en la coleccion " + currentCollectionDTO.name);
                recipeService.insertSingleOutput(view.getParentId().value(), item.id, amountValue, sessionService.getCurrentCollectionDTO().id);
            }
        }

        delete.setOnAction(e -> {
            if (target.getChildren().size() <= 1) {
                this.view.showAlert("Item no eliminado",  "Una receta debe tener al menos un " + (type == RecipeIOType.INPUT ? "ingrediente" : "resultado"), Alert.AlertType.WARNING);
            } else { 
                target.getChildren().remove(row);
                if (type == RecipeIOType.INPUT) {
                    Logger.getInstance().info(this.getClass().toString(),
                            "El usuario " + currentAcountName + " ha eliminado el input " + item.name + " en la receta " + currentRecipeDTO.name + " en la coleccion " + currentCollectionDTO.name);
                    recipeService.deleteSingleInput(view.getParentId().value(), item.id);
                } else {
                    Logger.getInstance().info(this.getClass().toString(),
                            "El usuario " + currentAcountName + " ha eliminado el output " + item.name + " en la receta " + currentRecipeDTO.name + " en la coleccion " + currentCollectionDTO.name);
                    recipeService.deleteSingleOutput(view.getParentId().value(), item.id);
                }
            }
        });
    }

    @Override
    public void updateAtShow() {  
        SessionService sessionService = this.getService(ServiceType.SESSION);

        RecipeDTO dto = sessionService.getCurrentRecipeDTO(); 
        Image image = ImageUtils.getImage(dto.imagePath);

        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(image);

        fillItems(dto.id);

    }

    @Override
    public int getShowingEntryId() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentRecipeDTO().id;
    }

    @Override
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));    
    }
    
    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.ITEM, ServiceType.RECIPE, ServiceType.SESSION); 
    }

}