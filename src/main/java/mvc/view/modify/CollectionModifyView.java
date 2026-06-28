package mvc.view.modify;

import java.util.ArrayList;
import java.util.List;

import creational.EventPrefabFactory;
import creational.UIPrefabsFactory;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utilities.ImageUtils; 


public class CollectionModifyView extends AbstractModifyWithListsViews<CollectionDTO, ItemDTO, RecipeDTO>{
    
    protected List<ItemDTO> items = new ArrayList<>();
    protected List<RecipeDTO> recipes = new ArrayList<>();

    @Override
    public void modifyFields(CollectionDTO dto, List<ItemDTO> firstList, List<RecipeDTO> secondList) {
        this.list1Label.setText("Items");
        this.list2Label.setText("Recetas");

        if (dto.imagePath != null && !dto.imagePath.isEmpty()){
            Image currentIcon = ImageUtils.getImage(dto.imagePath);
            this.setIconPreview(currentIcon);
        }
        super.modifyFields(dto, firstList, secondList);
    }

    public List<ItemDTO> getItems(){
        return this.items;
    }

    public List<RecipeDTO> getRecipes(){
        return this.recipes;
    }

    public void setItems(List<ItemDTO> items){
        this.items = items;
    }

    public void setRecipes(List<RecipeDTO> recipes){
        this.recipes = recipes;
    }

    @Override
    protected List<ItemDTO> getCurrentList1() {
        return getItems();
    }

    @Override
    protected List<RecipeDTO> getCurrentList2() { 
        return getRecipes();
    }

    @Override
    protected void createListTable1(VBox table, List<ItemDTO> list) {
        Image iconView;
        Button deleteButton; 

        for (ItemDTO elem : list) { 
                

            deleteButton = UIPrefabsFactory.createRemoveButton();
            
            
            iconView = ImageUtils.getImage(elem.imagePath);
            HBox row = UIPrefabsFactory.createRow(elem.name, iconView, deleteButton); 
            row.setAlignment(Pos.CENTER_LEFT);

            EventHandler<ActionEvent> event = EventPrefabFactory.getDeleteSelfRowEvent(table, row, this.getCurrentList1(), elem);
            deleteButton.setOnAction(event);

            table.getChildren().add(row);
        };
    }

    @Override
    protected void createListTable2(VBox table, List<RecipeDTO> list) {
        Image iconView;
        Button deleteButton; 

        for (RecipeDTO elem : list) { 
                

            deleteButton = UIPrefabsFactory.createRemoveButton();
            
            
            iconView = ImageUtils.getImage(elem.imagePath);
            HBox row = UIPrefabsFactory.createRow(elem.name, iconView, deleteButton); 
            row.setAlignment(Pos.CENTER_LEFT);

            EventHandler<ActionEvent> event = EventPrefabFactory.getDeleteSelfRowEvent(table, row, this.getCurrentList2(), elem);
            deleteButton.setOnAction(event);

            table.getChildren().add(row);
        };
    }

    @Override
    protected void setCurrentList1(List<ItemDTO> list) {
        this.items = list;
    }

    @Override
    protected void setCurrentList2(List<RecipeDTO> list) {
        this.recipes = list;
    }
 
}
