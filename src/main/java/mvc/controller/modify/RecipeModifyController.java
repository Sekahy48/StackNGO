package mvc.controller.modify;

import java.util.Objects;
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.modify.RecipeModifyView; 
import service.RecipeService;
import service.ServiceType;
import service.SessionService;

public class RecipeModifyController extends AbstractModifyController<RecipeModifyView, RecipeDTO>{
 

/*     private void onAddList(Button button, VBox targetList, boolean isIng){
        ItemService itemService = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        List<ItemWithCollectionDTO> items = DTOFactory.itemsToWithCollection(itemService.getAllDTO(sessionService.getCurrentCollectionDTO().getId()), sessionService.getCurrentCollectionDTO().name);
        List<EntryDTO> dtos = new ArrayList<>();
        for (ItemWithCollectionDTO elem : items) {
            dtos.add( elem.item);
        }
        TextField amount = new TextField("1");
        //Aqui se queja pq createSeñectionPopup ahora ya no acepta la VBox target List, no se usaba en el metodo asique lo quite, pero bueno, aparte de esto no hay mas errores de compilacion
        UIPrefabsFactory.createSelectionPopup(
            button,
            targetList,
            dtos,
            selected -> {   
                Button removeBtn = UIPrefabsFactory.createRemoveButton();
                HBox row = UIPrefabsFactory.createRow(selected.name, new Image(selected.iconPath), removeBtn, amount);

                removeBtn.setOnAction(e -> {
                    targetList.getChildren().remove(row);
                    if(isIng){
                        this.view.removeIngredient(selected);
                    }else{
                        this.view.removeResult(selected);
                    }
                });

                amount.setOnAction(e -> {
                    int newAmount = Integer.parseInt(amount.getText());
                    if(isIng){
                        this.view.putIngredient(selected, newAmount);
                    }else{
                        this.view.putResult(selected, newAmount);
                    }

                });

                targetList.getChildren().add(row);

                this.view.putIngredient(selected, 1);
            }
        );
    } */
 

    @Override
    protected RecipeDTO composeDTO() { 
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        
        RecipeDTO dto = sessionService.getCurrentRecipeDTO();

        String newName = !this.view.getNewName().isBlank() ? this.view.getNewName() : dto.name;

        if (!newName.equals(dto.name)) {  
            if (recipeService.containsEntryByName(newName, sessionService.getCurrentCollectionDTO().id)) {
                this.view.showAlert("Nombre duplicado","Ya existe una receta con ese nombre", Alert.AlertType.ERROR);
                return null;
            }
        }

        String iconPath = !Objects.isNull(this.view.getNewImagePath()) && !this.view.getNewImagePath().isEmpty()
                        ? this.view.getNewImagePath() 
                        : dto.imagePath;

        String description = !Objects.isNull(this.view.getNewDescription()) && !this.view.getNewDescription().isEmpty()
                            ? this.view.getNewDescription()
                            : dto.description;

        return DTOFactory.recipe(
                dto.ingredients,
                dto.results,
                newName,
                iconPath, 
                description,
                dto.id);
    }

    @Override
    protected void onUpdateEvent(RecipeDTO dto) {
        RecipeService recipeService = this.getService(ServiceType.RECIPE);
        SessionService sessionService = this.getService(ServiceType.SESSION);
        RecipeDTO oldDto = sessionService.getCurrentRecipeDTO();
        recipeService.saveEntry(dto, new int[]{sessionService.getCurrentAccount().getId().value()});
        
        String alert = "La colección llamada " + oldDto.name + " ha sido modificada.";
        if (oldDto.name != dto.name) alert = "La coleccion antes llamada " + oldDto.name + " ha sido modificada pasandose a llamar " + dto.name + ".";

        Logger.getInstance().info(
            this.getClass().toString(),
            alert
        ); 
    }

    @Override
    public void onReturnEvent() { 
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_RECIPE));
    }
    
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.RECIPE, ServiceType.SESSION); 
    }

    protected RecipeDTO getCurrentDTO() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentRecipeDTO();
    }

    @Override
    protected void updateCurrentDTO(RecipeDTO dto) {
        this.<SessionService>getService(ServiceType.SESSION).setCurrentRecipe(dto);
    }
    
}
