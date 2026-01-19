package mvc.controller.show.entry.data;

import command.delete.DeleteItemCommand;
import command.modify.screen.ModifyItemCommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import creational.ImageUtils;
import dataAccessLayer.DAO.CollectionDAO;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO;
import identificators.EntryId;
import identificators.GenericId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import logger.LogLevel;
import logger.Logger;
import mvc.view.ViewType;
import mvc.view.show.entry.data.ShowItemDataView;

public class ShowItemDataController extends AbstractShowDataController<ShowItemDataView> {

    public ShowItemDataController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        ShowItemDataView view = this.getView();

        Button modifyBtn = view.getModifyButton();
        Button deleteBtn = view.getDeleteButton();
        Button goBackButton = view.getGoBackButton();

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );

        deleteBtn.setOnAction(
                e -> {
                    GenericId id = this.view.getParentId();
                    EntryId entryId = new EntryId(id.value());
                    this.buffer.publish(new DeleteItemCommand(entryId));
                }
        );

        modifyBtn.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.MODIFY_ITEM));
                    this.buffer.publish(new RedirectCommand(context.getCoreController().getModifyItemBuffer(),
                                                            new ModifyItemCommand(context.getItemDTOById(this.view.getParentId().value()))
                                                            ));
                }
        );
    }

    public void deleteItem(EntryId id) {

        boolean delete = this.view.showAlert(
                "Eliminar item",
                "Seguro que quieres borrar este item?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) {

            ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
            int recipeID = itemDAO.isInRecipe(id.value());
            if (recipeID != -1) {
                this.view.showAlert("Item ya en uso" , "No puedes eliminar un item que pertenece a una coleccion",  Alert.AlertType.WARNING);
            } else {
                ItemDAO dao = (ItemDAO) this.context.getDAO(DAOType.ITEM);
                ItemDTO dto = dao.read(id.value());
                dao.delete(id.value());
                this.context.getEntriesRepo().tryToRemoveEntry(id);
                this.view.showAlert("Item eliminado", "El item con nombre " + dto.name + " ha sido eliminado", Alert.AlertType.INFORMATION);
                Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "El usuario " + this.context.getAccount().getUsername() + " ha borrado el item con nombre " + dto.name + " en la coleccion " + this.context.getCurrentCollection().getName());
                goBack();
            }
        }
    }

    /*protected void goBack() {
        CollectionDAO dao = (CollectionDAO) this.context.getDAO(DAOType.COLLECTION);
        CollectionDTO dto = dao.read(this.context.getCurrentCollection().getId());

        this.buffer.publish(new RedirectCommand(
                this.context.getCoreController().getShowCollectionDataBuffer(),
                new ShowCollection(dto)
        ));
    }*/

    public void showItem(Integer id) {

        ItemDAO dao = (ItemDAO) this.context.getDAO(DAOType.ITEM);
        ItemDTO dto = dao.read(id);

        this.context.getEntriesRepo().addItem(this.context.getEntriesFactory().createItem(dto));
        Image image = ImageUtils.getImage(dto.iconPath);

        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(image);

        this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_ITEM));
    }
}