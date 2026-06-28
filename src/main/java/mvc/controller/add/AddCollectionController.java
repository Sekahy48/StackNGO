package mvc.controller.add;
 
import java.util.Set;

import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import domain.accounts.Account;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import logger.Logger;
import mvc.model.entries.Collection;
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

    @Override
    public void handleButtons() {

        commonHandleButton();
        super.handleButtons();

        AddCollectionView view = (AddCollectionView) this.getView();

        Button addButton = view.getAddButton(); 
        Button goBackButton = view.getGoBackButton();

        addButton.setOnAction(
                e -> {
                    String name = view.getNameLabel().getText();
                    String iconLabel = view.getIconLabel().getText();
                    String description = view.getDescriptionLabel().getText(); 
                    CollectionDTO dto = DTOFactory.collection(null,
                                    null,
                                    name,
                                    iconLabel,
                                    description,
                                    this.idGenerator.generateId());
                    this.onCreateEvent(dto);
                }
        ); 

        goBackButton.setOnAction(
                e -> {onReturnEvent();;}
        ); 
    }
 
    public void onReturnEvent() {
        EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));
    } 

    @Override
    public void onCreateEvent(CollectionDTO dto) { 
        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre vacio", "Una coleccion debe tener un nombre", Alert.AlertType.ERROR);
        } else {
            CollectionService service = this.getService(ServiceType.COLLECTION);
            Account currentAccount = this.<SessionService>getService(ServiceType.SESSION).getCurrentAccount();
            int[] extraData = {currentAccount.getId().value()};
            Collection newCollection = service.saveEntry(dto, extraData);
            
            if (newCollection != null) {
                this.view.showAlert("Colleccion creada","Coleccion " + dto.name + " creada correctamente", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + currentAccount.getUsername() + " ha creado una coleccion con nombre " + dto.name);
                EventBus.getInstance().publish(new NavigateEvent(ViewType.PRIVATE_ZONE));
            } else {
                this.view.showAlert("Coleccion existente", "La coleccion llamada " + dto.name + " ya existe, utilice otro nombre o modifique la ya existente.", Alert.AlertType.ERROR);
        
            }
        } 
    }

    @Override
    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.COLLECTION, ServiceType.SESSION); 
    }
}
