package creational;

import java.util.ArrayList;
import java.util.List;

import dataTransportLayer.AccountDTO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.GenericDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO; 
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.ItemWithCollectionDTO;
import dataTransportLayer.RecipeDTO;
import dataTransportLayer.RecipeWithCollectionDTO;
import mvc.model.entries.component.ComponentField;

public final class DTOFactory {

    private DTOFactory() {
        // No se instancia. Esto no es un objeto, es una herramienta.
    }

    /* ===== ENTRY ===== */

    public static EntryDTO entry(
            String name,
            String iconPath,
            String description,
            int id
    ) {
        return new EntryDTO(name, iconPath, description, id);
    }

    /* ===== ITEM ===== */

    public static ItemDTO item(
            String name,
            String iconPath,
            String description,
            int id
    ) {
        return new ItemDTO(name, iconPath, description, id);
    }

    public static ItemDTO item(
            String name,
            String iconPath
    ) {
        return new ItemDTO(name, iconPath, null, -1);
    }

    public static List<ItemDTO> items(List<ItemWithCollectionDTO> dtos) {
        ArrayList<ItemDTO> out = new ArrayList<>();
        for (ItemWithCollectionDTO elem : dtos) {
            out.add(elem.item);
        }
        return out;
    }

    public static List<EntryDTO> itemsWithCollectionAsEntries(List<ItemWithCollectionDTO> dtos) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (ItemWithCollectionDTO elem : dtos) {
            out.add(elem.item);
        }
        return out;
    }

    public static List<ItemWithCollectionDTO> itemsToWithCollection(List<ItemDTO> dtos, String collectionName) {
        ArrayList<ItemWithCollectionDTO> out = new ArrayList<>();
        for (ItemDTO elem : dtos) {
            out.add(new ItemWithCollectionDTO(elem, collectionName));
        }
        return out;
    }

    public static List<EntryDTO> itemsAsEntries(List<ItemDTO> dtos) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (ItemDTO elem : dtos) {
            out.add(elem);
        }
        return out;
    }

    

    /* ===== RECIPE ===== */

    public static RecipeDTO recipe(
            ArrayList<ItemIdStackDTO> ingredients,
            ArrayList<ItemIdStackDTO> results,
            String name,
            String iconPath,
            String description,
            int id
    ) {
        return new RecipeDTO(
                ingredients,
                results,
                name,
                iconPath,
                description,
                id
        );
    }

    public static List<RecipeDTO> recipes(List<RecipeWithCollectionDTO> dtos) {
        ArrayList<RecipeDTO> out = new ArrayList<>();
        for (RecipeWithCollectionDTO elem : dtos) {
            out.add(elem.recipe);
        }
        return out;
    }

    public static List<EntryDTO> recipesWithCollectionAsEntries(List<RecipeWithCollectionDTO> dtos) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (RecipeWithCollectionDTO elem : dtos) {
            out.add(elem.recipe);
        }
        return out;
    }

    public static List<RecipeWithCollectionDTO> recipesToWithCollection(List<RecipeDTO> dtos, String collectionName) {
        ArrayList<RecipeWithCollectionDTO> out = new ArrayList<>();
        for (RecipeDTO elem : dtos) {
            out.add(new RecipeWithCollectionDTO(elem, collectionName));
        }
        return out;
    }

    public static List<EntryDTO> recipesAsEntries(List<RecipeDTO> dtos) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (RecipeDTO elem : dtos) {
            out.add(elem);
        }
        return out;
    }

    /* ===== COLLECTION ===== */

    public static CollectionDTO collection(
            ArrayList<Integer> items,
            ArrayList<Integer> recipes,
            String name,
            String iconPath,
            String description,
            int id
    ) {
        return new CollectionDTO(
                items,
                recipes,
                name,
                iconPath,
                description,
                id
        );
    }

    public static List<EntryDTO> collectionsAsEntries(List<CollectionDTO> dtos) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (CollectionDTO elem : dtos) {
            out.add(elem);
        }
        return out;
    }

    /* ===== ACCOUNT ===== */

    public static AccountDTO account(
            String name,
            List<Integer> collections,
            String type,
            int id,
            byte[] hashedPass,
            byte[] saltValue
    ) {
        return new AccountDTO(name, collections, type, id, hashedPass, saltValue);
    }

    public static AccountDTO accountRegister(
            String name,
            String type,
            String password,
            int id
    ) {
        return new AccountDTO(name, password, type, id);
    }

    /* ===== ITEM (ID) STACK ===== */


    public static ItemIdStackDTO itemIdStack(int id, int amount) {
        return new ItemIdStackDTO(id, amount);
    }

    public static ItemStackDTO itemStack (ItemDTO dto, int amount) { return new ItemStackDTO(dto, amount); }

    
    /* ===== ITEM WITH COLLECTION ===== */
    public static ItemWithCollectionDTO itemWithCollection(ItemDTO item, String collection){
        return new ItemWithCollectionDTO(item, collection);
    }

    public static ItemWithCollectionDTO itemWithCollection(
            String name,
            String iconPath,
            String description,
            int id,
            String collection
    ){
        return new ItemWithCollectionDTO(DTOFactory.item(name, iconPath, description, id), collection);
    }

    /* ===== RECIPE WITH COLLECTION ===== */
    public static RecipeWithCollectionDTO recipeWithCollection(RecipeDTO recipe, String collection){
        return new RecipeWithCollectionDTO(recipe, collection);
    }

    public static RecipeWithCollectionDTO recipeWithCollection(
            ArrayList<ItemIdStackDTO> ingredients,
            ArrayList<ItemIdStackDTO> results,
            String name,
            String iconPath,
            String description,
            int id,
            String collection
    ){
        return new RecipeWithCollectionDTO(DTOFactory.recipe(ingredients, results, name, iconPath, description, id), collection);
    }
 

    public static List<GenericDTO> genericsFromEntries(List<EntryDTO> entries) {
        ArrayList<GenericDTO> out = new ArrayList<>();
        for (EntryDTO entryDTO : entries) {
            out.add(entryDTO);
        };
        return out;
    }

    public static List<GenericDTO> genericsFromStacks(List<ItemStackDTO> recipes) {
        ArrayList<GenericDTO> out = new ArrayList<>();
        for (ItemStackDTO stacDTO : recipes) {
            out.add(stacDTO.item);
        };
        return out;
    }

    public static List<EntryDTO> entriesFromGenerics(List<GenericDTO> list) {
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (GenericDTO entryDTO : list) {
            out.add((EntryDTO)entryDTO);
        }
        return out;
    }

    public static List<ItemStackDTO> stackFromGenerics(List<GenericDTO> list) {
        ArrayList<ItemStackDTO> out = new ArrayList<>();
        for (GenericDTO entryDTO : list) {
            out.add((ItemStackDTO)entryDTO);
        }
        return out;
    }

    public static List<EntryDTO> withCollectionToRaw(){
        return new ArrayList<>();
    }

    public static List<EntryDTO> itemWithCollectionToEntry(List<ItemWithCollectionDTO> list){
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (ItemWithCollectionDTO item : list) {
            out.add(item.item);
        }
        return out;
    }

    public static List<EntryDTO> recipeWithCollectionToEntry(List<RecipeWithCollectionDTO> list){
        ArrayList<EntryDTO> out = new ArrayList<>();
        for (RecipeWithCollectionDTO recipe : list) {
            out.add(recipe.recipe);
        }
        return out;
    }
    
    /* ===== COMPONENT ===== */
    public static ComponentDefinitionDTO component(
            int id,
            String name,
            String iconPath,
            String description,
            List<ComponentField> fields
    ) {
        return new ComponentDefinitionDTO(name, iconPath, description, id, fields);
    }
    
}
