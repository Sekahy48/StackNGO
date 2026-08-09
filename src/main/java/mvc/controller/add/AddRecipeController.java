package mvc.controller.add;
 
import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataTransportLayer.*; 
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import utilities.ThemeManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup; 
import mvc.controller.InyectableController;
import mvc.model.entries.Recipe;
import mvc.view.ViewType;
import mvc.view.add.AddRecipeView; 
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * Controller that manages the logic related to {@link AddRecipeView}
 *
 */
public class AddRecipeController extends AbstractAddController<RecipeDTO, Recipe, AddRecipeView> implements InyectableController {
    protected List<EntryDTO> listWhereAdd; 

    @Override
    public void setListWhereAdd(List<EntryDTO> list){
        listWhereAdd = list;
    }

    @Override
    public void handleButtons() { 
        super.handleButtons();
 
 
        Button addIngredientButton = this.view.getAddIngredientButton();
        Button addResultButton = this.view.getAddResultButton(); 
        VBox ingredientsList = this.view.getIngredientsList();
        VBox resultsList = this.view.getResultsList();
  
        addIngredientButton.setOnAction(e  -> {addPopUp(addIngredientButton, this.view, ingredientsList);});

        addResultButton.setOnAction(e -> {addPopUp(addResultButton, this.view, resultsList);});
    }

    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.SHOW_COLLECTION));
    }
     
    private void addRow(AddRecipeView view, String resultName, String iconPath, Button button) {

        VBox targetList;
        if (button == view.getAddIngredientButton()){
            targetList = view.getIngredientsList();
        } else{
            targetList = view.getResultsList();
        }

        if (rowExists(targetList, resultName)){
            return;
        }

        Image icon = ImageUtils.getImage(iconPath);
        Image deleteIcon = ThemeManager.getThemedImage("papelera.png");

        ImageView deleteView = new ImageView(deleteIcon);
        deleteView.setFitHeight(16);
        deleteView.setFitWidth(16);

        Button deleteButton = new Button();
        deleteButton.setGraphic(deleteView);
        deleteButton.getStyleClass().add("icon-button");

        TextField amount = new TextField("1");
        amount.setPrefWidth(50);

        amount.setOnAction(
                e->{
                    String value = amount.getText().trim();
                    if (!value.matches("\\d+")){
                        amount.setText("1");
                    }
                }
        );

        HBox row = UIPrefabsFactory.createRow(resultName, icon, deleteButton, amount);

        if(button == view.getAddIngredientButton()){
            view.getIngredientsList().getChildren().add(row);
        } else if (button == view.getAddResultButton()) {
            view.getResultsList().getChildren().add(row);
        }

        deleteButton.setOnAction(
            e -> {
                boolean response =this.view.showAlert("Eliminar receta", "Seguro que quieres eliminar la receta?", Alert.AlertType.CONFIRMATION);

                if (response) {
                    if (button == view.getAddIngredientButton()) {
                        view.getIngredientsList().getChildren().remove(row);
                    } else{
                        view.getResultsList().getChildren().remove(row);
                    }
                }
            }
        );
    }

    private boolean rowExists(VBox box, String name){
        for (Node row : box.getChildren()) {
            if (row instanceof HBox){
                HBox hbox = (HBox) row;

                if (hbox.getChildren().size() > 1 && hbox.getChildren().get(1) instanceof Label){
                    Label label = (Label) hbox.getChildren().get(1);

                    if (label.getText().equals(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addPopUp(Button button, AddRecipeView view, VBox targetList){
        Popup popup = new Popup();
        popup.setAutoHide(true);

        ListView<String> list = new ListView<>();

        if(button == view.getAddIngredientButton()){
            targetList = view.getIngredientsList();
        } else{
            targetList = view.getResultsList();
        }

        //EntryId collectionId = new EntryId(view.getParentId().value());
        //ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
        //List<ItemDTO> listItems = itemDAO.readAllByParent(collectionId.value());

        int collectionId = this.<SessionService>getService(ServiceType.SESSION).getCurrentCollectionDTO().id;
        ItemService itemService = this.getService(ServiceType.ITEM);
        List<ItemDTO> listItems = itemService.getAllDTO(collectionId); 

        Map<String, String> items = new HashMap<>();

        for(ItemDTO itemDTO : listItems){
            list.getItems().add(itemDTO.getName());
            items.put(itemDTO.getName(), itemDTO.getImagePath());
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);

        scroll.setPrefWidth(300);
        scroll.setPrefHeight(250);

        popup.getContent().add(scroll);


        // Al hacer click en un item
        list.setOnMouseClicked(
            e -> {
                String selected = list.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String iconPath = items.get(selected);
                    addRow(view, selected, iconPath, button);
                    popup.hide();
                }
            }
        );

        double x, y;
        if (button == view.getAddIngredientButton()){
            x = 110;
            y = 500;
        } else {
            x = 800;
            y = 500;
        }
        popup.show(view.getAddIngredientButton().getScene().getWindow(), x, y);
    }

    private ArrayList<ItemIdStackDTO> extractItemsFromVBox(VBox box){
        ArrayList<ItemIdStackDTO> list = new ArrayList<>();
        //ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
        ItemService service = this.getService(ServiceType.ITEM);
        SessionService sessionService = this.getService(ServiceType.SESSION);

        for (Node row : box.getChildren()) {
            if (row instanceof HBox){
                HBox hbox = (HBox) row;

                if (hbox.getChildren().size() > 1 && hbox.getChildren().get(1) instanceof Label){
                    Label label = (Label) hbox.getChildren().get(1);
                    String itemName = label.getText();

                    TextField amountField = null;
                    for (Node child : hbox.getChildren()) {
                        if (child instanceof TextField){
                            amountField = (TextField) child;
                            break;
                        }
                    }

                    int amount = 1;
                    if (amountField != null) {
                        try {
                            amount = Integer.parseInt(amountField.getText());
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ItemDTO itemDTO = service.getDTOByName(itemName, sessionService.getCurrentCollectionDTO().id);
                    int itemId = itemDTO.getId();

                    list.add(new ItemIdStackDTO(itemId, amount));
                }
            }
        }
        return list;
    }

    @Override
    public Set<ServiceType> requiredServices() {
        Set<ServiceType> out = new HashSet<>(super.requiredServices());
        out.add(ServiceType.RECIPE);
        out.add(ServiceType.ITEM);
        return out;
    }

    @Override
    public RecipeService getEntryService() {
        return this.<RecipeService>getService(ServiceType.RECIPE);
    }

    @Override
    public String getEntryType() {
        return "La receta";
    }

    @Override
    public RecipeDTO getDTOFromView() { 
        String name = view.getNameLabel().getText();
        String iconLabel = view.getIconLabel().getText();
        String description = view.getDescriptionLabel().getText();

        ArrayList<ItemIdStackDTO> ingredients = extractItemsFromVBox(view.getIngredientsList());
        ArrayList<ItemIdStackDTO> results = extractItemsFromVBox(view.getResultsList());


        RecipeDTO dto = DTOFactory.recipe(
                        ingredients,
                        results,
                        name,
                        iconLabel,
                        description,
                        this.idGenerator.generateId()
                );

        return dto;
    }

    @Override
    public int getParentId() {
        return this.<SessionService>getService(ServiceType.SESSION).getCurrentCollectionDTO().id;
    }
}
