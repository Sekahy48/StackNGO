package mvc.controller.modify;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import creational.DTOFactory;
import creational.UIPrefabsFactory;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.ItemWithCollectionDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image; 
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mvc.view.modify.RecipeModifyView;

public class RecipeModifyController extends AbstractModifyController<RecipeModifyView>{

    private RecipeDAO dao;

    public RecipeModifyController(EventBuffer buffer) {
        super(buffer); 
        this.modifyType = ModType.RECIPE;
        this.dao = new RecipeDAO();
    }

    private void onAddList(Button button, VBox targetList, boolean isIng){
        List<ItemWithCollectionDTO> items = this.context.getItemsByCollection(new EntryId(view.getParentId().value()));
        List<EntryDTO> dtos = new ArrayList<>();
        for (ItemWithCollectionDTO elem : items) {
            dtos.add( elem.item);
        }
        TextField amount = new TextField("1");
        UIPrefabsFactory.createSelectionPopup(
            button,
            targetList,
            dtos,
            selected -> {   
                Button removeBtn = UIPrefabsFactory.createRemoveButton();
                HBox row = UIPrefabsFactory.createRow(selected.name, new Image(selected.iconPath), removeBtn, amount);

                removeBtn.setOnAction(e -> {
                    targetList.getChildren().remove(row);
                    if(isIng){
                        this.view.removeIngredient(selected);
                    }else{
                        this.view.removeResult(selected);
                    }
                });

                amount.setOnAction(e -> {
                    int newAmount = Integer.parseInt(amount.getText());
                    if(isIng){
                        this.view.putIngredient(selected, newAmount);
                    }else{
                        this.view.putResult(selected, newAmount);
                    }

                });

                targetList.getChildren().add(row);

                this.view.putIngredient(selected, 1);
            }
        );
    }
 

    @Override
    protected EntryDTO composeDTO() { 
        ArrayList<ItemIdStackDTO> ingredients = new ArrayList<>(), 
                                  results = new ArrayList<>();

        List<ItemStackDTO> ingMap = this.view.getIngredients(), resMap = this.view.getResults();

        for (ItemStackDTO elem : ingMap) {
            ingredients.add(new ItemIdStackDTO(elem.item.id, elem.amount));
        }

        for (ItemStackDTO elem : resMap) {
            results.add(new ItemIdStackDTO(elem.item.id, elem.amount));
        }
        
        RecipeDTO dto = (RecipeDTO) this.view.getEntryDTO();

        String newName = !Objects.isNull(this.view.getNewName()) && !this.view.getNewName().isEmpty()
              ? this.view.getNewName() 
              : dto.name;

        if (!newName.equals(dto.name)) {

            if (dao.existsEntryByName(newName, dto.id, this.context.getCurrentCollection().getId())) {
                this.view.showAlert("Nombre duplicado","Ya existe una receta con ese nombre", Alert.AlertType.ERROR);
                return null;
            }
        }

        String iconPath = !Objects.isNull(this.view.getNewImagePath()) && !this.view.getNewImagePath().isEmpty()
                        ? this.view.getNewImagePath() 
                        : dto.iconPath;

        String description = !Objects.isNull(this.view.getNewDescription()) && !this.view.getNewDescription().isEmpty()
                            ? this.view.getNewDescription()
                            : dto.description;

        return DTOFactory.recipe(
                dto.ingredients,
                dto.results,
                newName,
                iconPath, 
                description,
                dto.id);
    }
 
}
