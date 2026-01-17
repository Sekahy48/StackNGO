package mvc.view.modify;

import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import creational.EventPrefabFactory;
import creational.ImageUtils;
import creational.UIPrefabsFactory;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.GenericDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 


public class CollectionModifyView extends AbstractModifyWithListsViews{
    
    protected List<EntryDTO> items = new ArrayList<>(), recipes = new ArrayList<>();

    @Override
    public void modifyFields(EntryDTO dto, List<GenericDTO> firstList, List<GenericDTO> secondList) {
        this.list1Label.setText("Items");
        this.list2Label.setText("Recetas");

        if (dto.iconPath != null && !dto.iconPath.isEmpty()){
            Image currentIcon = ImageUtils.getImage(dto.iconPath);
            this.setIconPreview(currentIcon);
        }
        super.modifyFields(dto, firstList, secondList);
    }

    public List<EntryDTO> getItems(){
        return this.items;
    }

    public List<EntryDTO> getRecipes(){
        return this.recipes;
    }

    public void setItems(List<EntryDTO> items){
        this.items = items;
    }

    public void setRecipes(List<EntryDTO> recipes){
        this.recipes = recipes;
    }

    @Override
    protected List<GenericDTO> getCurrentList1() {
        return DTOFactory.genericsFromEntries(getItems());
    }

    @Override
    protected List<GenericDTO> getCurrentList2() { 
        return DTOFactory.genericsFromEntries(getRecipes());
    }

    @Override
    protected void createListTable(VBox table, List<GenericDTO> list, int whatList) {
        Image iconView;
        Button deleteButton; 

        for (GenericDTO elem : list) { 
                
            EntryDTO dto = (EntryDTO)elem;

            deleteButton = UIPrefabsFactory.createRemoveButton();
            
            
            iconView = ImageUtils.getImage(dto.iconPath);
            HBox row = UIPrefabsFactory.createRow(dto.name, iconView, deleteButton); 
            row.setAlignment(Pos.CENTER_LEFT);

            EventHandler<ActionEvent> event = EventPrefabFactory.getDeleteSelfRowEvent(table, row, whatList == 1 ? this.getCurrentList1() : this.getCurrentList2(), dto);
            deleteButton.setOnAction(event);

            table.getChildren().add(row);
        };
    }

    @Override
    protected void setCurrentList1(List<GenericDTO> list) {
        this.items = DTOFactory.entriesFromGenerics(list);
    }

    @Override
    protected void setCurrentList2(List<GenericDTO> list) {
        this.recipes = DTOFactory.entriesFromGenerics(list);
    }
 
}
