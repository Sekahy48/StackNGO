package mvc.controller.inventory;

import java.util.List;
import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.RecipeDTO; 
import javafx.scene.control.Alert;
import mvc.view.inventory.InventoryView;
import service.CollectionService;
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;

public class InventoryController extends AbstractInventoryController<InventoryView>{ 

    @Override
    public void handleButtons() {
        super.handleButtons();

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
    }

}
