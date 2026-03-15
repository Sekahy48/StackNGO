package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import creational.DTOFactory;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.Item;
import mvc.model.entries.ItemIdStack;
import mvc.model.entries.Recipe;
import mvc.model.inventory.IInventoryElement;

public class RecipeService extends AbstractEntryService<RecipeDTO, Recipe> {

    public RecipeService(DataContext data) {
        super(data);
    }

    public ServiceType getType() {
        return ServiceType.RECIPE;
    }

    //#region Entry operations
    @Override
    public Recipe getEntryById(int id) {
        Recipe out = data.getEntriesRepo().getRecipe(new EntryId(id));
        if (out == null) {
            RecipeDTO dto = getDTOById(id);
            out = createEntry(dto);
        }
        return out;
    }

    @Override
    public Recipe getEntryByName(String name) {
        Recipe out = data.getEntriesRepo().getRecipeByName(name);
        if (out == null) {
            RecipeDTO dto = getDTOByName(name);
            out = createEntry(dto);
        }
        return out;
    }

    @Override
    public List<Recipe> getAllEntry(int parentId) {
        List<RecipeDTO> dtos = getAllDTO(parentId);
        List<Recipe> out = new ArrayList<>();
        for (RecipeDTO dto : dtos) {
            Recipe r = data.getEntriesRepo().getRecipe(new EntryId(dto.id));
            if (r == null) r = createEntry(dto);
            out.add(r);
        }
        return out;
    }
    //#endregion

    //#region DTO operations
    @Override
    public RecipeDTO getDTOById(int id) {
        RecipeDTO out = data.getRecipeDAO().read(id);
        if (out == null) {
            String error = "Recipe with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public RecipeDTO getDTOByName(String name) {
        RecipeDTO out = data.getRecipeDAO().readByName(name);
        if (out == null) {
            String error = "Recipe with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error);
            throw new NoSuchElementException(error);
        }
        return out;
    }

    @Override
    public List<RecipeDTO> getAllDTO(int parentId) {
        return data.getRecipeDAO().readAllByParent(parentId);
    }
    //#endregion

    @Override
    public Recipe createEntry(RecipeDTO dto) {
        return this.entriesFactory.createRecipe(dto);
    } 

    @Override
    public Recipe saveEntry(RecipeDTO dto, int[] extraData) {
        return null; //TODO
    }
 
 

    //#region Logic
    public boolean canBeExecuted(IInventoryElement inventory, Recipe recipe) {
        if (inventory == null || recipe == null) {
            Logger.getInstance().error(this.getClass().toString(),
                        "Cannot execute method with some of the atributes null");
            return false;
        }
        
        Map<Integer, Integer> available = computeAvailable(inventory);

        for (ItemIdStack ingredient : recipe.getIngredients()) {
            int id = ingredient.getId().value();
            int needed = ingredient.getAmount();
            int have = available.getOrDefault(id, 0);

            if (have < needed) {
                return false;
            }
        }

        return true;
    }

    public boolean executeRecipe(IInventoryElement inventory, Recipe recipe) {
        if (inventory == null || recipe == null) {
            Logger.getInstance().error(this.getClass().toString(),
                        "Cannot execute method with some of the atributes null");
            return false;
        }

        Map<Integer, Integer> available = computeAvailable(inventory);

        for (ItemIdStack ingredient : recipe.getIngredients()) {
            int id = ingredient.getId().value();
            if (available.getOrDefault(id, 0) < ingredient.getAmount()) {
                return false;
            }
        }
 
        for (ItemIdStack ingredient : recipe.getIngredients()) { 
            inventory.modifyAmount(this.data.getEntriesRepo().getItem(ingredient.getId()), -ingredient.getAmount());
        }
 
        for (ItemIdStack result : recipe.getResults()) {
            inventory.modifyAmount(this.data.getEntriesRepo().getItem(result.getId()), result.getAmount());
        }

        return true;
    }


    private Map<Integer, Integer> computeAvailable(IInventoryElement inventory) {
        Map<Integer, Integer> available = new HashMap<>();

        for (IInventoryElement elem : inventory.flattenInventory()) {
            int id = elem.getItem().getId().value();
            available.merge(id, elem.getAmount(), Integer::sum);
        }

        return available;
    }



    //#endregion
}
