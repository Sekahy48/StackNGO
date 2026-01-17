package mvc.model.entries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import identificators.EntryId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CollectionTest {

    private Collection collection;
    private Item stick, cobblestone;
    private Recipe recipe1, recipe2;

    @BeforeEach
    void setUp() {
        collection = new Collection("Minecraft", null, null, 2001);

        stick = new Item("Stick", null, null, 1);
        cobblestone = new Item("Cobblestone", null, null, 2);

        recipe1 = new Recipe("Recipe 1", null, null, 1001);
        recipe2 = new Recipe("Recipe 2", null, null, 1002);
    }

    /* =========================================================
     * CONSTRUCTORES
     * ========================================================= */

    @Test
    void testYConstructorStringAndIntNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Collection(null, 1));
    }

    @Test
    void testYConstructorStringDescriptionImageAndIdNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Collection(null, "desc", "img.png", 1));
    }

    @Test
    void testYConstructorNameAndIdOK() {
        new Collection("The Witcher 3", 2);
    }

    /* =========================================================
     * ITEMS
     * ========================================================= */

    @Test
    void testAddItemInitializesListAndAddsItem() {
        assertTrue(collection.addItem(stick));
        assertTrue(collection.containsItem(stick));
    }

    @Test
    void testAddItemTwiceFailsSecondTime() {
        assertTrue(collection.addItem(stick));
        assertFalse(collection.addItem(stick));
    }

    @Test
    void testAddNullItemDoesNothing() {
        assertFalse(collection.addItem(null));
        assertNull(collection.getItems());
    }

    @Test
    void testRemoveExistingItem() {
        collection.addItem(stick);
        assertTrue(collection.removeItem(stick));
        assertFalse(collection.containsItem(stick));
    }

    @Test
    void testRemoveNonExistingItemReturnsFalse() {
        assertFalse(collection.removeItem(stick));
    }

    @Test
    void testRemoveNullItemReturnsFalse() {
        assertFalse(collection.removeItem(null));
    }

    @Test
    void testContainsItemWithNullListReturnsFalse() {
        assertFalse(collection.containsItem(stick));
    }

    @Test
    void testContainsNullItemReturnsFalse() {
        assertFalse(collection.containsItem(null));
    }

    /* =========================================================
     * RECIPES
     * ========================================================= */

    @Test
    void testAddRecipeInitializesListAndAddsRecipe() {
        assertTrue(collection.addRecipe(recipe1));
        assertTrue(collection.containsRecipe(recipe1));
    }

    @Test
    void testAddRecipeTwiceFailsSecondTime() {
        assertTrue(collection.addRecipe(recipe1));
        assertFalse(collection.addRecipe(recipe1));
    }

    @Test
    void testAddNullRecipeDoesNothing() {
        assertFalse(collection.addRecipe(null));
        assertNull(collection.getRecipes());
    }

    @Test
    void testRemoveExistingRecipe() {
        collection.addRecipe(recipe1);
        assertTrue(collection.removeRecipe(recipe1));
        assertFalse(collection.containsRecipe(recipe1));
    }

    @Test
    void testRemoveNonExistingRecipeReturnsFalse() {
        assertFalse(collection.removeRecipe(recipe1));
    }

    @Test
    void testRemoveNullRecièReturnsFalse() {
        assertFalse(collection.removeRecipe(null));
    }

    @Test
    void testContainsRecipeWithNullListReturnsFalse() {
        assertFalse(collection.containsRecipe(recipe1));
    }

    @Test
    void testContainsNullRecipeReturnsFalse() {
        assertFalse(collection.containsRecipe(null));
    }

    /* =========================================================
     * SETTERS / GETTERS
     * ========================================================= */

    @Test
    void testSetItemsAndGetItems() {
        ArrayList<EntryId> items = new ArrayList<>();
        items.add(stick.getId());
        items.add(cobblestone.getId());

        collection.setItems(items);

        assertEquals(items, collection.getItems());
    }

    @Test
    void testSetRecipesAndGetRecipes() {
        ArrayList<EntryId> recipes = new ArrayList<>();
        recipes.add(recipe1.getId());
        recipes.add(recipe2.getId());

        collection.setRecipes(recipes);

        assertEquals(recipes, collection.getRecipes());
    }
}
