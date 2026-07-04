package creational;
 
import java.util.ArrayList;
import java.util.List;

import identificators.EntryId;
import mvc.model.entries.*;
import mvc.model.entries.component.ComponentDefinition;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.ItemComponentValue;

public class EntryBuilder {
    private Entry entry;

    //#region Generic 
    public EntryBuilder setName(String name) {
        this.entry.setName(name);
        return this;
    }

    public EntryBuilder setIconPath(String iconPath) {
        this.entry.setImagePath(iconPath);
        return this;
    }

    public EntryBuilder setDescription(String description) {
        this.entry.setDescription(description);
        return this;
    }

    public EntryBuilder setId(int id) {
        this.entry.setId(id);
        return this;
    }
    //#endregion

    //#region Recipe specific
    public EntryBuilder addIngredients(ArrayList<ItemIdStack> ingredients) {
        ((Recipe) this.entry).setIngredients(ingredients);
        return this;
    }

    public EntryBuilder addResults(ArrayList<ItemIdStack> results) {
        ((Recipe) this.entry).setResults(results);
        return this;
    }

    //#endregion

    //#region Collection specific
    public EntryBuilder addItems(ArrayList<EntryId> items) {
        ((Collection) this.entry).setItems(items);
        return this;
    }

    public EntryBuilder addRecipes(ArrayList<EntryId> recipes) {
        ((Collection) this.entry).setRecipes(recipes);
        return this;
    }

    //#endregion

    //#region Component specific
    public EntryBuilder addFields(List<ComponentField> fields) {
        ((ComponentDefinition) this.entry).setFields(fields);
        return this;
    }
    
    //#endregion

    //#region Item specific
    public EntryBuilder addComponents(List<ItemComponentValue> components) {
        ((Item) this.entry).setComponents(components);
        return this;
    }
    //#endregion

    public Entry build() {
        Entry out = this.entry;
        this.reset();
        return out;
    }

    private void reset(){
        this.entry = null;
    }

    public void restoreEntry(Entry entry) {
        this.entry = entry;
    }
}