package mvc.controller.add;

import java.io.File;
import java.util.Set;

import dataTransportLayer.EntryDTO;
import domain.accounts.Account;
import event.EventBus;
import event.NavigateEvent;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import logger.Logger;
import mvc.controller.AbstractController;
import mvc.model.entries.Entry;
import mvc.model.entries.repository.EntryIdGenerator;
import mvc.view.ViewType;
import mvc.view.add.AbstractAddView;
import service.AbstractEntryService; 
import service.ServiceType;
import service.SessionService;

public abstract class AbstractAddController<D extends EntryDTO, E extends Entry, V extends AbstractAddView> extends AbstractController<V> {

    protected EntryIdGenerator idGenerator;

    public AbstractAddController() { 
        this.idGenerator = EntryIdGenerator.getInstance();
    }

    @Override
    public void attachView(V view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButtons() {
        
        commonHandleButton();
        this.view.getImageButton().setOnAction(e -> chooseImage());
        this.view.getAddButton().setOnAction(e -> {
            this.onCreateEvent(this.getDTOFromView());
            this.onReturnEvent();
        });  
    
        this.view.getGoBackButton().setOnAction(
                e -> {this.onReturnEvent();}
        ); 
    } 

    public Set<ServiceType> requiredServices() {
        return Set.of(ServiceType.SESSION);
    }

    public void chooseImage() {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes", "*.jpg", "*.png", "*.jpeg", "*.gif")
        );


        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            view.setImage(file);
        }
    }

    /**
     * Creates and persists a system entity given a DTO.
     * @param dto to create the system entity. 
     */ 
    public void onCreateEvent(D dto) { 

        if (dto.name.isEmpty()) {
            this.view.showAlert("Nombre vacio", "Debe indicar un nombre para " + this.getEntryType(), Alert.AlertType.ERROR);
        } else { 
            Account currentAccount = this.<SessionService>getService(ServiceType.SESSION).getCurrentAccount();
            int[] extraData = {this.getParentId()};
            E newEntry = this.getEntryService().saveEntry(dto, extraData);
            
            if (newEntry != null) {
                this.view.showAlert("Creación exitosa", "Creación de " + this.getEntryType() + " " + dto.name + " realizada correctamente", Alert.AlertType.INFORMATION);
                Logger.getInstance().info(this.getClass().toString(), "El usuario " + currentAccount.getUsername() + " ha creado " + this.getEntryType() + " " + dto.name);
             
            } else {
                this.view.showAlert(this.getEntryType() + " ya existe", "Nombre " + dto.name + " ya en uso.", Alert.AlertType.ERROR);
        
            }
        } 
    }

    public abstract AbstractEntryService<D, E> getEntryService();
    public abstract String getEntryType();
    public abstract D getDTOFromView();

    @Override 
    public void updateAtShow() {
        this.getView().clearFields();
    }
    
    public abstract int getParentId();
}
