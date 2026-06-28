package mvc.view.modify;

import java.util.ArrayList; 
import java.util.List; 
  
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO; 

public class RecipeModifyView extends AbstractModifyView<RecipeDTO>{

    protected List<ItemStackDTO> ingredients = new ArrayList<>(), 
                                    results = new ArrayList<>();
    
 

    public List<ItemStackDTO> getIngredients(){
        return ingredients;
    }

    public List<ItemStackDTO> getResults(){
        return results;
    }

    public void putIngredient(ItemDTO dto, int amount){
        ingredients.add(new ItemStackDTO(null, amount));
    }
    public void putResult(ItemDTO dto, int amount){
        results.add(new ItemStackDTO(null, amount));
    }

    public void removeIngredient(ItemDTO dto){
        ItemStackDTO toRemove = getItemStackDTO(dto);
        ingredients.remove(toRemove);
    }
    public void removeResult(ItemDTO dto){
        ItemStackDTO toRemove = getItemStackDTO(dto);
        results.remove(toRemove);
    }
 

    private ItemStackDTO getItemStackDTO(ItemDTO dto){
        for (ItemStackDTO elem : ingredients) {
            if(elem.item.equals(dto)) return elem;
        }
        return null;
    }

 
 

}