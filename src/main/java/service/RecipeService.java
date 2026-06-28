package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataAccessLayer.DAO.AbstractEntryDAO;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import logger.Logger;
import mvc.context.DataContext;
import mvc.model.entries.ItemIdStack;
import mvc.model.entries.Recipe;
import mvc.model.entries.repository.EntriesRepository;
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
            data.getEntriesRepo().addRecipe(out);
        }
        return out;
    }

    @Override
    public Recipe getEntryByName(String name) {
        Recipe out = data.getEntriesRepo().getRecipeByName(name);
        if (out == null) {
            RecipeDTO dto = getDTOByName(name);
            out = createEntry(dto);
            data.getEntriesRepo().addRecipe(out);
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
            data.getEntriesRepo().addRecipe(r);
        }
        return out;
    }

    @Override
    public boolean removeEntry(int id) {
        this.untrackEntryById(id);
        return this.data.getRecipeDAO().delete(id);
    }

    //#endregion

    //#region DTO operations
    @Override
    public RecipeDTO getDTOById(int id) {
        RecipeDTO out = data.getRecipeDAO().read(id);
        if (out == null) {
            String error = "Recipe with id " + id + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    }

    @Override
    public RecipeDTO getDTOByName(String name) {
        RecipeDTO out = data.getRecipeDAO().readByName(name);
        if (out == null) {
            String error = "Recipe with name " + name + " not found in database.";
            Logger.getInstance().warning(this.getClass().toString(), error); 
        }
        return out;
    }

    @Override
    public List<RecipeDTO> getAllDTO(int parentId) {
        return data.getRecipeDAO().readAllByParent(parentId);
    }

    public List<RecipeDTO> getAllDTO() {
        return data.getRecipeDAO().readAll();
    }

    //#endregion

    @Override
    protected Recipe createEntry(RecipeDTO dto) {
        return this.entriesFactory.createRecipe(dto);
    } 

    @Override
    public Recipe saveEntry(RecipeDTO dto, int[] extraData) {
        Recipe out = null;
        RecipeDAO dao = this.data.getRecipeDAO();
        out = this.createEntry(dto);
        RecipeDTO existingDTO = dao.read(dto.id);
        if (existingDTO != null) {
            dao.update(out, existingDTO.id);
        } else {
            dao.create(out, extraData);
        }
        
        EntriesRepository repo = this.data.getEntriesRepo();
        if (repo.contains(new EntryId(dto.id))) {
            repo.modifyEntry(out);
        } else {
            repo.addRecipe(out);
        } 
        return out;
    }
    
    @Override
    public Recipe saveFromImport(RecipeDTO dto, int[] extraData) {
        RecipeDAO dao = this.data.getRecipeDAO();
        RecipeDTO existingDTO = dao.readByName(dto.name);
        if (existingDTO != null) {
            dto.id = existingDTO.id;
        }
        return saveEntry(dto, extraData);
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

    public List<ItemStackDTO> getRecipeInputs(Integer recipeId) {
        return this.data.getRecipeDAO().getInputs(recipeId);
    }

    public List<ItemStackDTO> getRecipeOutputs(Integer recipeId) {
        return this.data.getRecipeDAO().getOutputs(recipeId);
    }
    

    public void updateInputAmount(int recipeId, int itemId, int amount) {
        this.data.getRecipeDAO().updateInputAmount(recipeId, itemId, amount);
    }

    public void updateOutputAmount(int recipeId, int itemId, int amount) {
        this.data.getRecipeDAO().updateOutputAmount(recipeId, itemId, amount);
    }

    public void insertSingleInput(int recipeId, int itemId, int amount) {
        this.data.getRecipeDAO().insertSingleInput(recipeId, itemId, amount);
    }

    public void insertSingleOutput(int recipeId, int itemId, int amount) {
        this.data.getRecipeDAO().insertSingleOutput(recipeId, itemId, amount);
    }

    public void deleteSingleInput(int recipeId, int itemId) { 
        this.data.getRecipeDAO().deleteSingleInput(recipeId, itemId);
    }

    public void deleteSingleOutput(int recipeId, int itemId) { 
        this.data.getRecipeDAO().deleteSingleOutput(recipeId, itemId);
    }

    @Override
    protected  boolean addConcreteEntry(Recipe entry) {
        return this.data.getEntriesRepo().addRecipe(entry);
    }

    @Override 
    protected Recipe getConcreteEntry(int id) {
        return this.data.getEntriesRepo().getRecipe(new EntryId(id));
    }

    @Override 
    protected Recipe getConcreteEntryByName(String name) {
        return this.data.getEntriesRepo().getRecipeByName(name);
    }

    @Override
    protected AbstractEntryDAO<RecipeDTO, Recipe> getDAO() {
        return this.data.getRecipeDAO();
    }

    //#endregion
}
