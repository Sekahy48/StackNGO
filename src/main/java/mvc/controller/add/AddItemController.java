package mvc.controller.add;

import java.util.List;

import command.add.item.AddItemImageCommand;
import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO;
import domain.accounts.Account;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.Logger;
import mvc.controller.InyectableController;
import mvc.model.entries.Item;
import mvc.view.add.AddItemView;
import service.ItemService;
import service.ServiceType;
import service.SessionService;

/**
 *
 * Controller that manages the logic related to {@link AddItemView}
 *
 */
public class AddItemController extends AbstractAddController<ItemDTO> implements InyectableController{
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
                    
                    ItemDTO dto = DTOFactory.item(
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
    public void onCreateEvent(ItemDTO dto) {
        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre vacio", "Un ítem debe tener un nombre", Alert.AlertType.ERROR);
        } else { 
            ItemService itemService = this.getService(ServiceType.ITEM);
            SessionService sessionService = this.getService(ServiceType.SESSION);

            // TODO mirar si se pueden simplificar cosas como la extraccion del id de la coleccion vigente sin tanta variable intermedia
            CollectionDTO currentCollectionDTO = sessionService.getCurrentCollectionDTO();
            Account currentAccount = sessionService.getCurrentAccount();
            int[] extraData = {currentCollectionDTO.id};
            Item newItem = itemService.saveEntry(dto, extraData);
            
            if (newItem != null) {
                this.view.showAlert("Item creado","Item " + dto.name + " creado correctamente", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + currentAccount.getUsername() + " ha creado un  ítem con nombre " + dto.name);
            } else {
                this.view.showAlert("Item existente", "El ítem llamado " + dto.name + " ya ha sido creado previamente", Alert.AlertType.ERROR);
                this.goBack();
            }
        }  
    }
}