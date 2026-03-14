package mvc.controller.add;

import command.add.recipe.AddRecipeCommand;
import command.add.recipe.AddRecipeImageCommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataAccessLayer.DAO.CollectionDAO;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.*;
import domain.accounts.Account;
import identificators.EntryId;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.InyectableController;
import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
import mvc.view.ViewType;
import mvc.view.add.AddRecipeView;
import service.CollectionService;
import service.ItemService;
import service.RecipeService;
import service.ServiceType;
import service.SessionService;
import utilities.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * Controller that manages the logic related to {@link AddRecipeView}
 *
 */
public class AddRecipeController extends AbstractAddController<RecipeDTO> implements InyectableController {
    protected List<EntryDTO> listWhereAdd;
    public AddRecipeController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void setListWhereAdd(List<EntryDTO> list){
        listWhereAdd = list;
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        AddRecipeView view = (AddRecipeView) this.getView();

        Button addButton = view.getAddButton();
        Button imageButton = view.getImageButton();
        Button addIngredientButton = view.getAddIngredientButton();
        Button addResultButton = view.getAddResultButton();
        Button goBackButton = view.getGoBackButton();
        VBox ingredientsList = view.getIngredientsList();
        VBox resultsList = view.getResultsList();


        addButton.setOnAction(
                e -> {
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

                    this.onCreateEvent(dto);
                }
        );

        imageButton.setOnAction(
                e -> {
                    this.buffer.publish(new AddRecipeImageCommand());
                }
        );

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );

        addIngredientButton.setOnAction(
                e  -> {
                    addPopUp(addIngredientButton, view, ingredientsList);
                }
        );

        addResultButton.setOnAction(
                e -> {
                    addPopUp(addResultButton, view, resultsList);
                }
        );
    }

    @Override
    public void onCreateEvent(RecipeDTO dto) { 
        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre vacio", "Una receta debe tener un nombre", Alert.AlertType.ERROR);
        } else if (dto.ingredients.isEmpty()) {
            this.view.showAlert("Ingredientes vacios", "Una receta debe tener al menos un ingrediente", Alert.AlertType.ERROR);
        } else if (dto.results.isEmpty()) {
            this.view.showAlert("Resultados vacíos", "Una receta debe tener al menos un resultado", Alert.AlertType.ERROR);
        }else {
            RecipeService recipeService = this.getService(ServiceType.RECIPE);
            SessionService sessionService = this.getService(ServiceType.SESSION);

            CollectionDTO currentCollectionDTO = sessionService.getCurrentCollectionDTO();
            Account currentAccount = sessionService.getCurrentAccount();
            int[] extraData = {currentCollectionDTO.id};
            Recipe newRecipe = recipeService.saveEntry(dto, extraData);
            
            if (newRecipe != null) {
                this.view.showAlert("Receta creada","Coleccion " + dto.name + " creada correctamente", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + currentAccount.getUsername() + " ha creado una receta con nombre " + dto.name);
            } else {
                this.view.showAlert("Receta existente", "La receta llamada " + dto.name + " ya ha sido creada previamente", Alert.AlertType.ERROR);
                this.goBack();
            }
        } 
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
        Image deleteIcon = new Image("images/papelera.png");

        ImageView deleteView = new ImageView(deleteIcon);
        deleteView.setFitHeight(16);
        deleteView.setFitWidth(16);

        Button deleteButton = new Button();
        deleteButton.setGraphic(deleteView);
        deleteButton.setStyle("-fx-background-color: transparent;");

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
            items.put(itemDTO.getName(), itemDTO.getIconPath());
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

                    ItemDTO itemDTO = service.getDTOByName(itemName);
                    int itemId = itemDTO.getId();

                    list.add(new ItemIdStackDTO(itemId, amount));
                }
            }
        }
        return list;
    }

    //private void goBack() {
    //    CollectionDTO dto = this.context.getCurrentCollection();
//
    //    this.buffer.publish(new RedirectCommand(
    //            this.context.getCoreController().getShowCollectionDataBuffer(),
    //            new ShowCollection(dto)
    //    ));
    //}
}
