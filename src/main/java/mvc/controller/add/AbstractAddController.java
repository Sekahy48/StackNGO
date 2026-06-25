package mvc.controller.add;

import java.io.File;
 
import dataTransportLayer.EntryDTO; 
import javafx.stage.FileChooser;
import mvc.controller.AbstractController;
import mvc.model.entries.repository.EntryIdGenerator;
import mvc.view.add.AbstractAddView;

public abstract class AbstractAddController<D extends EntryDTO> extends AbstractController<AbstractAddView> {

    protected EntryIdGenerator idGenerator;

    public AbstractAddController() { 
        this.idGenerator = EntryIdGenerator.getInstance();
    }

    @Override
    public void attachView(AbstractAddView view) {
        this.view = (AbstractAddView) view;
        super.attachView(view);
    }

    @Override
    public void handleButtons() {
        this.view.getImageButton().setOnAction(e -> chooseImage());
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

    @Override 
    public void updateAtShow() {
        this.getView().clearFields();
    }
}
