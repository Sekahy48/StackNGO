package creational;


import java.util.ArrayList;

import dataTransportLayer.CollectionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.RecipeDTO;
import mvc.model.entries.Collection;
import identificators.EntryId;
import mvc.model.entries.Item;
import mvc.model.entries.ItemIdStack;
import mvc.model.entries.Recipe;

public class StandardEntryFactory implements IEntriesFactory{
    private EntryBuilder builder = new EntryBuilder();
    
    public Item createItem(ItemDTO dto){
        this.builder.restoreEntry(new Item("default_item", 1));
        this.createEntry((EntryDTO) dto); 
        return (Item) this.builder.build();
    }

    public Recipe createRecipe(RecipeDTO dto){
        this.builder.restoreEntry(new Recipe("default_recipe", 1));

        ArrayList<ItemIdStack> ingredients = new ArrayList<>();
        if(!dto.ingredients.isEmpty()){
            for(ItemIdStackDTO elem : dto.ingredients){
                ingredients.add(new ItemIdStack(elem.id, elem.amount));
            }
        }

        ArrayList<ItemIdStack> results = new ArrayList<>();
        if(!dto.results.isEmpty()){
            for(ItemIdStackDTO elem : dto.results){
                results.add(new ItemIdStack(elem.id, elem.amount));
            }
        }

        builder.addIngredients(ingredients).addResults(results);
        this.createEntry((EntryDTO) dto);
        return (Recipe) this.builder.build();
    }

    public Collection createCollection(CollectionDTO dto){
        this.builder.restoreEntry(new Collection("default_collection", 1));
        ArrayList<EntryId> items = new ArrayList<>();
        if(dto.items != null && !dto.items.isEmpty()){
            
            for(Integer elem : dto.items){
                items.add(new EntryId(elem));
            }
        }
        ArrayList<EntryId> recipes = new ArrayList<>();
        if(dto.recipes != null && !dto.recipes.isEmpty()){
            
            for(Integer elem : dto.recipes){
                recipes.add(new EntryId(elem));
            }
        }

        this.createEntry((EntryDTO) dto);
        builder.addItems(items).addRecipes(recipes);
        return (Collection) this.builder.build();
    }

    public void createEntry(EntryDTO dto){
        builder.setName(dto.name);
        builder.setIconPath(dto.iconPath);
        builder.setDescription(dto.description);
        builder.setId(dto.id);
    }

}
