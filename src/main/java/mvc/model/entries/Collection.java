package mvc.model.entries;

import identificators.EntryId;

import java.util.ArrayList;

public class Collection extends Entry{
    private ArrayList<EntryId> items = new ArrayList<>();
    private ArrayList<EntryId> recipes = new ArrayList<>();

    public Collection(String name, String description, String imagePath, int id) {
        super(name, description, imagePath, id);
    }

    public Collection(String name, int id) {
        super(name, id);
    }

    //#region Getters and Setters
    public ArrayList<EntryId> getItems() {
        return items.size() > 0 ? this.items : null;
    }

    public ArrayList<EntryId> getRecipes() {
        return recipes.size() > 0 ? this.recipes : null;
    }

    public void setItems(ArrayList<EntryId> items) {
        this.items = items;
    }

    public void setRecipes(ArrayList<EntryId> recipes) {
        this.recipes = recipes;
    }
    //#endregion

    //#region Content Related Methods
    public boolean addItem(Item item) {
        boolean out = false;
        if (item != null && !this.containsItem(item)) {
            items.add(item.getId());
            out = true;
        }
        return out;
    }

    public boolean removeItem(Item item) {
        boolean out = false;
        if (item != null ) {
            out = items.remove(item.getId());
        }
        return out;
    }

    public boolean containsItem(Item item) {
        boolean out = false;
        if (item != null) {
            out = items.contains(item.getId());
        }
        return out;
    } 

    public boolean addRecipe(Recipe recipe) {
        boolean out = false;

        if (recipe != null && !this.containsRecipe(recipe)) {
            recipes.add(recipe.getId());
            out = true;
        }
        return out;
    }

    public boolean removeRecipe(Recipe recipe) {
        boolean out = false;
        if (recipe != null) {
            out = recipes.remove(recipe.getId());
        }
        return out;
    }

    public boolean containsRecipe(Recipe recipe) {
        boolean out = false;
        if (recipe != null) {
            out = recipes.contains(recipe.getId());
        }
        return out;
    }

}