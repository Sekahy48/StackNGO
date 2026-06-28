package mvc.controller.show.single;
 
import java.util.List;
import java.util.Set; 
import creational.UIPrefabsFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logger.Logger; 
import mvc.view.ViewType;
import mvc.view.show.single.ShowCollectionDataView;
import service.CollectionService; 
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

public class ShowCollectionDataController extends AbstractShowDataController<ShowCollectionDataView> { 

    @Override
    public void handleButtons() {

        commonHandleButton();
        super.handleButtons();

        this.view.getAddItemButton().setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.ADD_ITEM));});

        this.view.getAddRecipeButton().setOnAction(e -> {EventBus.getInstance().publish(new NavigateEvent(ViewType.ADD_RECIPE));;});
 
        this.view.getModifyButton().setOnAction(
                e -> { EventBus.getInstance().publish(new NavigateEvent(ViewType.MODIFY_COLLECTION)); }
        );

    }
 
    public void deleteShowingEntry(int id) {
        CollectionService collectionService = this.getService(ServiceType.COLLECTION);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        boolean delete = this.view.showAlert(
                "Eliminar coleccion",
                "Seguro que quieres borrar esta coleccion?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) {  
            String collectionName = collectionService.getEntryById(id).getName();
            collectionService.removeEntry(id); 
            this.view.showAlert("Coleccion eliminada", "La coleccion con nombre " + collectionName + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().info(this.getClass().toString(), "El usuario " + sessionService.getCurrentAccount().getUsername() + " ha borrado la coleccion con nombre " + collectionName);
            EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTIONS));
        }
    }


    private void fillMenus(Integer id) {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        ItemService itemService = this.getService(ServiceType.ITEM);
        RecipeService recipeService = this.getService(ServiceType.RECIPE);

        VBox items = view.getItemList();
        VBox recipes = view.getRecipeList();

        items.getChildren().clear();
        recipes.getChildren().clear();

        List<ItemDTO> listaItems = itemService.getAllDTO(id);
        List<RecipeDTO> listaRecetas = recipeService.getAllDTO(id);

        for (ItemDTO dto : listaItems) {
            Button button = new Button();
            button.setOnAction(e -> { 
                sessionService.setCurrentItem(dto);
                EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_ITEM));
            });

            HBox row = UIPrefabsFactory.createRow(dto.name, ImageUtils.getImage(dto.imagePath), button);
            items.getChildren().add(row);
        }

        for (RecipeDTO dto : listaRecetas) {
            Button button = new Button();
            button.setOnAction(e -> {
                sessionService.setCurrentRecipe(dto);
                EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_RECIPE));
            });
            HBox row = UIPrefabsFactory.createRow(dto.name, ImageUtils.getImage(dto.imagePath), button);
            recipes.getChildren().add(row);
        }
    }

    @Override
    public void onReturnEvent() { 
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTIONS));
    }

    @Override
    public void updateAtShow() {
        SessionService sessionService = this.getService(ServiceType.SESSION);

        CollectionDTO toShow = sessionService.getCurrentCollectionDTO();
  
        Image image = ImageUtils.getImage(toShow.imagePath);
        this.view.getNameField().setText(toShow.name);
        this.view.getDescriptionArea().setText(toShow.description);
        this.view.getEntryIcon().setImage(image);

        fillMenus(toShow.id); 
    }

    @Override
    public int getShowingEntryId() {
        SessionService sessionService = this.getService(ServiceType.SESSION);
        return sessionService.getCurrentCollectionDTO().id;
    }
 
    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COLLECTION, ServiceType.ITEM, ServiceType.RECIPE, ServiceType.SESSION); 
    }
}