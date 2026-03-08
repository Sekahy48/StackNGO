package mvc.controller.add;

import java.util.List;

import command.add.item.AddItemCommand;
import command.add.item.AddItemImageCommand;
import creational.DTOFactory;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataTransportLayer.*;
import identificators.GenericId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.InyectableController;
import mvc.model.entries.Item;
import mvc.view.add.AddItemView;

/**
 *
 * Controller that manages the logic related to {@link AddItemView}
 *
 */
public class AddItemController extends AbstractAddController implements InyectableController{
    protected List<EntryDTO> listWhereAdd;

    public AddItemController(EventBuffer buffer) {
        super(buffer);
    }

    public void setListWhereAdd(List<EntryDTO> list){
        listWhereAdd = list;
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        AddItemView view = (AddItemView) this.getView();

        Button addButton = view.getAddButton();
        Button imageButton = view.getImageButton();
        Button goBackButton = view.getGoBackButton();

        addButton.setOnAction(
                e -> {
                    String name = view.getNameLabel().getText();
                    String iconLabel = view.getIconLabel().getText();
                    String description = view.getDescriptionLabel().getText();

                    if (name.isEmpty()) {
                        this.view.showAlert("Nombre vacio", "Un item debe tener un nombre", Alert.AlertType.ERROR);
                    } else {
                        try {
                            ItemDTO dto = DTOFactory.item(
                                    name,
                                    iconLabel,
                                    description,
                                    this.idGenerator.generateId()
                            );
                            this.buffer.publish(new AddItemCommand(dto));

                        } catch (Exception ex) {
                            this.view.showAlert("Item existente", "El item llamado " + name + " ya ha sido creado previamente", Alert.AlertType.ERROR);
                        }
                    }   
                         
                }
        );

        imageButton.setOnAction(
                e -> {
                    this.buffer.publish(new AddItemImageCommand());
                }
        );

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );
    }

    @Override
    public void create(EntryDTO dto) {
        GenericId collection_id = view.getParentId();
        ItemDAO dao = (ItemDAO) this.context.getDAO(DAOType.ITEM);
        int[] foreignKeys = {collection_id.value()};

        Item item = this.context.getEntriesFactory().createItem((ItemDTO) dto);
        this.context.getRepo().addItem(item);

        dao.create(item, foreignKeys);

        this.view.showAlert("Item creado", "Item con nombre " + dto.name +  " ha sido creado", Alert.AlertType.INFORMATION);
        Logger.getInstance().info(this.getClass().toString(), "El usuario " + this.context.getAccount().getUsername() + " ha creado un item llamado " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
        goBack();
    }
}