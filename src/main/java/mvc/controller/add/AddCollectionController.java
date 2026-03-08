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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.LogLevel;
import logger.Logger;
import mvc.model.entries.Collection;
import mvc.view.ViewType;
import mvc.view.add.AddCollectionView;

/**
 *
 * Controller that manages the logic related to {@link AddCollectionView}
 *
 */
public class AddCollectionController extends AbstractAddController {

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
                    }
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
    public void create(EntryDTO dto) {
        CollectionDAO dao = (CollectionDAO) this.context.getDAO(DAOType.COLLECTION);
        int accountId = this.context.getAccount().getId().value();
        int[] foreignKeys = {accountId};
        String accountName = this.context.getAccount().getUsername();
        Collection collection = context.getEntriesFactory().createCollection((CollectionDTO) dto);
        this.context.getRepo().addCollection(collection);

        dao.create(
                collection,
                foreignKeys);

        this.view.showAlert("Colleccion creada","Coleccion " + dto.name + " creada correctamente", Alert.AlertType.INFORMATION);
        Logger.getInstance().info(this.getClass().toString(), "El usuario " + accountName + " ha creado una coleccion con nombre " + dto.name);

        this.buffer.publish(new ChangeScreenCommand(ViewType.PRIVATE_ZONE));
    }
}
