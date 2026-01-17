package mvc.view.modify;

import java.util.ArrayList; 
import java.util.List; 
 
import dataTransportLayer.EntryDTO; 
import dataTransportLayer.ItemStackDTO; 

public class RecipeModifyView extends AbstractModifyView{

    protected List<ItemStackDTO> ingredients = new ArrayList<>(), 
                                    results = new ArrayList<>();
    
    @Override 
    public void modifyFields(EntryDTO dto) { 
        super.modifyFields(dto);
    } 

    public List<ItemStackDTO> getIngredients(){
        return ingredients;
    }

    public List<ItemStackDTO> getResults(){
        return results;
    }

    public void putIngredient(EntryDTO dto, int amount){
        ingredients.add(new ItemStackDTO(null, amount));
    }
    public void putResult(EntryDTO dto, int amount){
        results.add(new ItemStackDTO(null, amount));
    }

    public void removeIngredient(EntryDTO dto){
        ItemStackDTO toRemove = getItemStackDTO(dto);
        ingredients.remove(toRemove);
    }
    public void removeResult(EntryDTO dto){
        ItemStackDTO toRemove = getItemStackDTO(dto);
        results.remove(toRemove);
    }
 

    private ItemStackDTO getItemStackDTO(EntryDTO dto){
        for (ItemStackDTO elem : ingredients) {
            if(elem.item.equals(dto)) return elem;
        }
        return null;
    }

 
 

}