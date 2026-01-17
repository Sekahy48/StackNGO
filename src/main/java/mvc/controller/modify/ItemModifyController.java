package mvc.controller.modify;

import java.util.Objects;

import creational.DTOFactory;
import dataAccessLayer.DAO.ItemDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import javafx.scene.control.Alert;
import logger.LogLevel;
import logger.Logger;
import mvc.view.modify.ItemModifyView;

public class ItemModifyController extends AbstractModifyController<ItemModifyView>{

    private ItemDAO dao;

    public ItemModifyController(EventBuffer buffer) {
        super(buffer); 
        this.modifyType = ModType.ITEM;
        this.dao = new ItemDAO();
    }

    @Override
    protected EntryDTO composeDTO() { 
        
        EntryDTO dto = this.view.getEntryDTO();

        String newName = !Objects.isNull(this.view.getNewName()) && !this.view.getNewName().isEmpty()
              ? this.view.getNewName() 
              : dto.name;

        if (!newName.equals(dto.name)) {
            if (dao.existsEntryByName(newName, dto.id, this.context.getCurrentCollection().getId())) {
                this.view.showAlert("Nombre duplicado","Ya existe un item con ese nombre.", Alert.AlertType.ERROR);
                return null;
            }
        }

        String iconPath = !Objects.isNull(this.view.getNewImagePath()) && !this.view.getNewImagePath().isEmpty()
                        ? this.view.getNewImagePath() 
                        : dto.iconPath;

        String description = !Objects.isNull(this.view.getNewDescription()) && !this.view.getNewDescription().isEmpty()
                            ? this.view.getNewDescription()
                            : dto.description;

        return DTOFactory.item(newName, iconPath, description, dto.id);

            
    }

}
