package mvc.model.entries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class EntryTest {

    private Item item1, item2;
    private Collection collection1, collection2;
    private Recipe recipe1, recipe2;

    @BeforeEach
    void setUp() {
        // Items
        item1 = new Item("Wood Stick", "Simple stick", null, 1);
        item2 = new Item("Stone Block", "Block of stone", null, 2);

        // Collections
        collection1 = new Collection("Minecraft Java", "Collection Java", null, 101);
        collection2 = new Collection("Minecraft Bedrock", "Collection Bedrock", null, 102);

        // Recipes
        ArrayList<ItemIdStack> input = new ArrayList<>();
        input.add(new ItemIdStack(item1, 2));

        ArrayList<ItemIdStack> output = new ArrayList<>();
        output.add(new ItemIdStack(item2, 1));

        recipe1 = new Recipe("Stick to Stone", "Converts sticks to stone", null, 201, input, output);
        recipe2 = new Recipe("Stone to Stick", "Converts stone to sticks", null, 202);
        recipe2.addIngredient(item2, 1);
        recipe2.addResult(item1, 2);
    }

    /* =========================================================
     * CONSTRUCTORES Y GETTERS
     * ========================================================= */
    @Test
    void testYConstructorsAndGettersItem() {
        assertEquals("Wood Stick", item1.getName());
        assertEquals("Simple stick", item1.getDescription());
        assertNull(item1.getImagePath());
        assertEquals(1, item1.getId().value());
    }

    @Test
    void testYConstructorsAndGettersCollection() {
        assertEquals("Minecraft Java", collection1.getName());
        assertEquals("Collection Java", collection1.getDescription());
        assertNull(collection1.getImagePath());
        assertEquals(101, collection1.getId().value());
    }

    @Test
    void testYConstructorsAndGettersRecipe() {
        assertEquals("Stick to Stone", recipe1.getName());
        assertEquals("Converts sticks to stone", recipe1.getDescription());
        assertEquals(201, recipe1.getId().value());
    }

    @Test
    void testYConstructorStringAndInt() {
        Item i = new Item("Simple Item", 50);
        assertEquals("Simple Item", i.getName());
        assertNull(i.getDescription());
        assertNull(i.getImagePath());
        assertEquals(50, i.getId().value());
    }

    @Test
    void testYConstructorStringAndIntNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Item(null, 1));
    }

    @Test
    void testYConstructorStringDescriptionImageAndIdNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Item(null, "desc", "img.png", 1));
    }

    /* =========================================================
     * SETTERS
     * ========================================================= */
    @Test
    void testYSettersItem() {
        item1.setName("New Stick");
        assertEquals("New Stick", item1.getName());

        item1.setDescription("Updated desc");
        assertEquals("Updated desc", item1.getDescription());

        item1.setImagePath("new/path");
        assertEquals("new/path", item1.getImagePath());

        item1.setId(99);
        assertEquals(99, item1.getId().value());

        assertThrowsExactly(IllegalArgumentException.class, () -> item1.setName(null));
    }

    @Test
    void testYSettersCollection() {
        collection1.setName("New Collection");
        assertEquals("New Collection", collection1.getName());

        collection1.setDescription("Updated Desc");
        assertEquals("Updated Desc", collection1.getDescription());

        collection1.setImagePath("new/img/path");
        assertEquals("new/img/path", collection1.getImagePath());

        collection1.setId(555);
        assertEquals(555, collection1.getId().value());

        assertThrowsExactly(IllegalArgumentException.class, () -> collection1.setName(null));
    }

    /* =========================================================
     * COMPARABLE
     * ========================================================= */
    @Test
    void testYCompareToAndEquals() {
        Item anotherItem1 = new Item("Other Name", "Other Desc", null, 1);
        Item anotherItem2 = new Item("Other Name", "Other Desc", null, 3);

        // compareTo
        assertTrue(item1.compareTo(item2) < 0);
        assertTrue(item2.compareTo(item1) > 0);
        assertEquals(0, item1.compareTo(anotherItem1));

        // equals
        assertEquals(item1, anotherItem1); // mismo ID
        assertNotEquals(item1, item2);
        assertNotEquals(item1, null);
        assertNotEquals(item1, new Object());

        // equals distinta subclase con mismo id falla
        Collection c = new Collection("Fake", null, null, 1);
        assertNotEquals(item1, c);
    }

    /* =========================================================
     * TOSTRING
     * ========================================================= */
    @Test
    void testYToString() {
        assertEquals("Wood Stick: Simple stick", item1.toString());
        assertEquals("Minecraft Java: Collection Java", collection1.toString());
        assertEquals("Stick to Stone: Converts sticks to stone", recipe1.toString());
    }
}
