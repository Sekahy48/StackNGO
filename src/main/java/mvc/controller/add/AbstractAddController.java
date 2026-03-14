package mvc.controller.add;

import java.io.File;

import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import javafx.stage.FileChooser;
import mvc.controller.AbstractController;
import mvc.model.entries.Entry;
import mvc.model.entries.repository.EntryIdGenerator;
import mvc.view.ViewType;
import mvc.view.add.AbstractAddView;
import service.ControllerService;
import service.ServiceType;
import service.SessionService;

public abstract class AbstractAddController<D extends EntryDTO> extends AbstractController<AbstractAddView> {

    protected EntryIdGenerator idGenerator;

    public AbstractAddController(EventBuffer buffer) {

        super(buffer);
        this.idGenerator = EntryIdGenerator.getInstance();
    }

    @Override
    public void attachView(AbstractAddView view) {
        this.view = (AbstractAddView) view;
        super.attachView(view);
    }

    @Override
    public abstract void handleButton();

    protected void goBack() {
        CollectionDTO dto = this.<SessionService>getService(ServiceType.SESSION).getCurrentCollectionDTO();

        this.buffer.publish(new RedirectCommand(
                this.<ControllerService>getService(ServiceType.CONTROLLER).getControllerBuffer(ViewType.SHOW_COLLECTION),
                new ShowCollection(dto)
        ));
        this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTION));
    }

    /**
     * Creates and persists a system entity given a DTO.
     * @param dto to create the system entity. 
     */
    public abstract void onCreateEvent(D dto);

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
}
