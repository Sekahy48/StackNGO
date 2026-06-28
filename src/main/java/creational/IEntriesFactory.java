package creational;

import dataTransportLayer.*;
import mvc.model.entries.*;
import mvc.model.entries.component.ComponentDefinition;

public interface IEntriesFactory {
    public Item createItem(ItemDTO dto);
    public Recipe createRecipe(RecipeDTO dto);
    public Collection createCollection(CollectionDTO dto);
    public ComponentDefinition createComponent(ComponentDefinitionDTO dto);
}
