package mvc.controller.add;

import command.add.collection.AddCollectionCommand;
import command.add.collection.AddCollectionImageCommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import creational.DTOFactory;
import dataAccessLayer.DAO.CollectionDAO;
import dataAccessLayer.DAO.DAOType;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import domain.accounts.Account;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.model.entries.Collection;
import mvc.model.entries.Entry;
import mvc.view.ViewType;
import mvc.view.add.AddCollectionView;
import service.CollectionService;
import service.ServiceType;
import service.SessionService;

/**
 *
 * Controller that manages the logic related to {@link AddCollectionView}
 *
 */
public class AddCollectionController extends AbstractAddController<CollectionDTO> {

    public AddCollectionController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        AddCollectionView view = (AddCollectionView) this.getView();

        Button addButton = view.getAddButton();
        Button imageButton = view.getImageButton();
        Button goBackButton = view.getGoBackButton();

        addButton.setOnAction(
                e -> {
                    String name = view.getNameLabel().getText();
                    String iconLabel = view.getIconLabel().getText();
                    String description = view.getDescriptionLabel().getText();
                    /*
                    if (name.isEmpty()) {
                        this.view.showAlert("Nombre vacio", "Una coleccion debe tener un nombre", Alert.AlertType.ERROR);
                    } else {
                        try {
                            CollectionDTO dto = DTOFactory.collection(null,
                                    null,
                                    name,
                                    iconLabel,
                                    description,
                                    this.idGenerator.generateId());

                            this.buffer.publish(new AddCollectionCommand(dto));
                        } catch (Exception ex) {
                            this.view.showAlert("Coleccion existente", "La coleccion llamada " + name + " ya ha sido creada previamente", Alert.AlertType.ERROR);
                        }
                    } */

                    //NUEVO
                    CollectionDTO dto = DTOFactory.collection(null,
                                    null,
                                    name,
                                    iconLabel,
                                    description,
                                    this.idGenerator.generateId());
                    this.onCreateEvent(dto);
                }
        );

        imageButton.setOnAction(
                e -> {
                    this.buffer.publish(new AddCollectionImageCommand());
                }
        );

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );
    }

    @Override
    protected void goBack() {
        this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE));
    }

    @Override
    public void onCreateEvent(CollectionDTO dto) {
        /*Lo que habia antes
        CollectionDAO dao = (CollectionDAO) this.context.getDAO(DAOType.COLLECTION);
        int accountId = this.context.getAccount().getId().value();
        int[] foreignKeys = {accountId};
        String accountName = this.context.getAccount().getUsername();
        Collection collection = context.getEntriesFactory().createCollection((CollectionDTO) dto);
        this.context.getRepo().addCollection(collection);
         */
        // NUEVO
        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre vacio", "Una coleccion debe tener un nombre", Alert.AlertType.ERROR);
        } else {
            CollectionService service = this.<CollectionService>getService(ServiceType.COLLECTION);
            Account currentAccount = this.<SessionService>getService(ServiceType.SESSION).getCurrentAccount();
            int[] extraData = {currentAccount.getId().value()};
            Collection newCollection = service.saveEntry(dto, extraData);
            
            if (newCollection != null) {
                this.view.showAlert("Colleccion creada","Coleccion " + dto.name + " creada correctamente", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + currentAccount.getUsername() + " ha creado una coleccion con nombre " + dto.name);
            } else {
                this.view.showAlert("Coleccion existente", "La coleccion llamada " + dto.name + " ya ha sido creada previamente", Alert.AlertType.ERROR);
                this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE));
            }
        }
        // NUEVO

        /*
        dao.create(
                collection,
                foreignKeys);

        this.view.showAlert("Colleccion creada","Coleccion " + dto.name + " creada correctamente", Alert.AlertType.INFORMATION);
        Logger.getInstance().info(this.getClass().toString(), "El usuario " + accountName + " ha creado una coleccion con nombre " + dto.name);

        this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE));
         */
    }
}
