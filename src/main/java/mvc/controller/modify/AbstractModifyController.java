package mvc.controller.modify;

import java.io.File;
 
import dataTransportLayer.EntryDTO; 
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import mvc.controller.AbstractController; 
import mvc.view.modify.AbstractModifyView;

public abstract class AbstractModifyController<T extends AbstractModifyView<E>, E extends EntryDTO> extends AbstractController<T>{ 
    
    @Override
    public void attachView(T view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButtons(){

        this.view.getConfirmButton().setOnAction(
            e -> {
                E dto = this.composeDTO();

                if (dto != null) {
                    this.onUpdateEvent(dto); 
                }
            }
        );

        this.view.getIconPreviewButton().setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
            );

            File file = chooser.showOpenDialog(
                this.view.getRoot().getScene().getWindow()
            );

            if (file != null) {
                String path = file.toURI().toString();
                Image img = new Image(path);

                view.setSelectedIconPath(path);
                view.setIconPreview(img);
            }
        });


    }

    protected abstract E composeDTO();
    
    protected abstract void onUpdateEvent(E dto);
 

    @Override
    public void updateAtShow() {
        this.view.clear();
    }
}
