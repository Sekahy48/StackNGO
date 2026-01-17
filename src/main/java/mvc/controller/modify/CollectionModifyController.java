package mvc.controller.modify;

import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataAccessLayer.DAO.CollectionDAO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import javafx.scene.control.Alert;
import mvc.controller.InyectableController;
import mvc.view.modify.CollectionModifyView;

public class CollectionModifyController extends AbstractModifyController<CollectionModifyView> implements InyectableController{
    private List<EntryDTO> list;
    private CollectionDAO dao;

    public CollectionModifyController(EventBuffer buffer) {
        super(buffer); 
        this.modifyType = ModType.COLLECTION;
        this.dao = new CollectionDAO();
    }
    
    @Override
    public void setListWhereAdd(List<EntryDTO> list){
        this.list = list;
    }

    @Override
    protected EntryDTO composeDTO() { 
        ArrayList<Integer> items = new ArrayList<>(), 
                           recipes = new ArrayList<>();
        ArrayList<EntryDTO> viewItems = new ArrayList<>(),
                           viewRecipes = new ArrayList<>();

        viewItems.addAll(this.view.getItems());
        viewRecipes.addAll(this.view.getRecipes());
 

        for (EntryDTO elem : viewItems) {
            items.add(elem.id);
        }

        for (EntryDTO elem : viewRecipes) {
            recipes.add(elem.id);
        }
        
        CollectionDTO dto = (CollectionDTO) this.view.getEntryDTO();
        String newName = !this.view.getNewName().isEmpty() ? this.view.getNewName() : dto.name;

        if (!newName.equals(dto.name)) {
            if (dao.existsCollectionByName(newName, dto.id, this.context.getAccount().getId().value())){
                this.view.showAlert("Nombre duplicado","Ya existe una coleccion con ese nombre", Alert.AlertType.ERROR);

                return null;
            }
        }
        String imagePath = this.view.getNewImagePath() != null ? this.view.getNewImagePath() : dto.iconPath;

        return DTOFactory.collection(
                !items.isEmpty() ? items : dto.items,
                !recipes.isEmpty() ? recipes : dto.recipes,
                newName,
                imagePath,
                !this.view.getNewDescription().isEmpty() ? this.view.getNewDescription() : dto.description,
                dto.id
        );

    }

}
