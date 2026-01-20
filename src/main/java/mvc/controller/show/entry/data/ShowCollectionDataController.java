package mvc.controller.show.entry.data;

import java.util.List;

import command.delete.DeleteCollectionCommand;
import command.modify.screen.ModifyCollectionCommand;
import command.screen.ChangeScreenCommand;
import command.screen.ChangeToCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import command.show.ShowCollections;
import command.show.ShowItem;
import command.show.ShowRecipe;
import creational.ImageUtils;
import creational.UIPrefabsFactory;
import dataAccessLayer.DAO.CollectionDAO;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import identificators.GenericId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logger.LogLevel;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.show.entry.data.ShowCollectionDataView;

public class ShowCollectionDataController extends AbstractShowDataController<ShowCollectionDataView> {

    public ShowCollectionDataController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        ShowCollectionDataView view = this.getView();

        Button deleteButton = view.getDeleteButton();
        Button modifyButton = view.getModifyButton();
        Button goBackButton = view.getGoBackButton();
        Button addRecipeButton = view.getAddRecipeButton();
        Button addItemButton = view.getAddItemButton();

        addItemButton.setOnAction(

                e -> {
                    GenericId id = this.view.getParentId();
                    CollectionDTO dto = context.getCollectionById(id.value());

                    this.buffer.publish(new ChangeToCommand(ViewType.ADD_ITEM, dto));
                }
        );

        addRecipeButton.setOnAction(
                e -> {
                    GenericId id = this.view.getParentId();
                    CollectionDTO dto = context.getCollectionById(id.value());
                    this.buffer.publish(new ChangeToCommand(ViewType.ADD_RECIPE, dto));
                }
        );

        deleteButton.setOnAction(
                e -> {
                        GenericId id = this.view.getParentId();
                        EntryId entryId = new EntryId(id.value());
                        this.buffer.publish(new DeleteCollectionCommand(entryId));
                    }
        );

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );

        modifyButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.MODIFY_COLLECTION));
                    this.buffer.publish(new RedirectCommand(context.getCoreController().getController(ViewType.MODIFY_COLLECTION).getBuffer(), 
                                                            new ModifyCollectionCommand(context.getCurrentCollection())));
                }
        );

    }

    protected void goBack() {
        this.buffer.publish(new RedirectCommand(
                this.context.getCoreController().getController(ViewType.SHOW_COLLECTIONS).getBuffer(),
                new ShowCollections()
        ));
        this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTIONS));
    }

    public void deleteCollection(EntryId id) {

        boolean delete = this.view.showAlert(
                "Eliminar coleccion",
                "Seguro que quieres borrar esta coleccion?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) {
            CollectionDAO dao = (CollectionDAO) this.context.getDAO(DAOType.COLLECTION);
            CollectionDTO dto = dao.read(id.value());
            dao.delete(id.value());
            this.context.getEntriesRepo().tryToRemoveEntry(id);
            this.view.showAlert("Coleccion eliminada", "La coleccion con nombre " + dto.name + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "El usuario " + this.context.getAccount().getUsername() + " ha borrado la coleccion con nombre " + dto.name);
            goBack();
        }
    }

    public void showCollection(Integer id) {

        this.context.setCurrentCollection(this.context.getCollectionById(id));

        CollectionDAO dao = (CollectionDAO) this.context.getDAO(DAOType.COLLECTION);
        CollectionDTO dto = dao.read(id);
        this.context.getEntriesRepo().addCollection(this.context.getEntriesFactory().createCollection(dto));
        Image image = ImageUtils.getImage(dto.iconPath);
        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(image);

        fillMenus(id);

        this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTION));
    }

    private void fillMenus(Integer id) {
        VBox items = view.getItemList();
        VBox recipes = view.getRecipeList();

        items.getChildren().clear();
        recipes.getChildren().clear();

        ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
        List<ItemDTO> listaItems = itemDAO.readAll(id);

        RecipeDAO recipeDAO = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);
        List<RecipeDTO> listaRecetas = recipeDAO.readAll(id);

        for (ItemDTO dto : listaItems) {
            Button button = new Button();
            button.setOnAction(e -> {
               this.buffer.publish(new RedirectCommand(
                       this.context.getCoreController().getController(ViewType.SHOW_ITEM).getBuffer(),
                       new ShowItem(dto.id, new RedirectCommand(buffer, new ShowCollection(context.getCurrentCollection())))
               ));
               this.buffer.publish(new ChangeToCommand(ViewType.SHOW_ITEM, dto));
            });

            HBox row = UIPrefabsFactory.createRow(dto.name, ImageUtils.getImage(dto.iconPath), button);
            items.getChildren().add(row);
        }

        for (RecipeDTO dto : listaRecetas) {
            Button button = new Button();
            button.setOnAction(e -> {
                this.buffer.publish(new RedirectCommand(
                        this.context.getCoreController().getController(ViewType.SHOW_RECIPE).getBuffer(),
                        new ShowRecipe(dto)
                ));
                this.buffer.publish(new ChangeToCommand(ViewType.SHOW_RECIPE, dto));
            });
            HBox row = UIPrefabsFactory.createRow(dto.name, ImageUtils.getImage(dto.iconPath), button);
            recipes.getChildren().add(row);
        }
    }
}