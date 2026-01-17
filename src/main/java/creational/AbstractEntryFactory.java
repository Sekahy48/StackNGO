package creational;

import dataTransportLayer.*;
import mvc.model.entries.*;

public interface AbstractEntryFactory {
    public Item createItem(ItemDTO dto);
    public Recipe createRecipe(RecipeDTO dto);
    public Collection createCollection(CollectionDTO dto);
}
