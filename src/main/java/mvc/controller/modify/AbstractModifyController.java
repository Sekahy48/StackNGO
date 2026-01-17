package mvc.controller.modify;

import java.io.File;

import command.modify.ModifyEntryCommand;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import mvc.controller.AbstractController; 
import mvc.view.modify.AbstractModifyView;

public abstract class AbstractModifyController<T extends AbstractModifyView> extends AbstractController<T>{
    protected ModType modifyType;

    public AbstractModifyController(EventBuffer buffer) {
        super(buffer); 
    }
    
    @Override
    public void attachView(T view) {
        this.view = view;
        super.attachView(view);
    }

    @Override
    public void handleButton(){
        this.view.getConfirmButton().setOnAction(
            e -> {
                EntryDTO dto = this.composeDTO();

                if (dto != null) {
                    buffer.publish(new ModifyEntryCommand(dto, modifyType));
                    this.clear();
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

    public void setModType(ModType type){
        this.modifyType = type;
    }

    protected abstract  EntryDTO composeDTO();

    protected void clear(){
        this.view.clear();
    }
}
