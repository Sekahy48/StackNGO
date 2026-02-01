package creational;

import dataTransportLayer.*;
import mvc.model.entries.*;

public interface IEntriesFactory {
    public Item createItem(ItemDTO dto);
    public Recipe createRecipe(RecipeDTO dto);
    public Collection createCollection(CollectionDTO dto);
}
