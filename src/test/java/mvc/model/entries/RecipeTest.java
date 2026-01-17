package mvc.model.entries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logger.Logger;
import logger.ShellLogAppender;

public class RecipeTest {

    private Item stick, cobblestone, pickaxe;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        Logger.getInstance().setLogAppender(new ShellLogAppender());

        stick = new Item("Stick", null, null, 1);
        cobblestone = new Item("Cobblestone", null, null, 2);
        pickaxe = new Item("Pickaxe", null, null, 3);

        recipe = new Recipe("Pickaxe recipe", null, null, 100);
    }

    /* =========================================================
     * CONSTRUCTORS
     * ========================================================= */ 

    @Test
    void testYConstructorStringAndIntNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Recipe(null, 1));
    }

    @Test
    void testYConstructorStringDescriptionImageAndIdNameNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Recipe(null, "desc", "img.png", 1));
    }
    
    @Test
    void testConstructorWithNullListsThrowsException() {
        IllegalArgumentException ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new Recipe("Bad", null, null, 1, null, null)
        );
        assertEquals("Input and Output lists cannot be null", ex.getMessage());
        
         ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new Recipe("Bad", null, null, 1, new ArrayList<>(), null)
        );
        assertEquals("Input and Output lists cannot be null", ex.getMessage());
        
         ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new Recipe("Bad", null, null, 1, null, new ArrayList<>())
        );
        assertEquals("Input and Output lists cannot be null", ex.getMessage());
        
    }

    @Test
    void testConstructorWithValidListsKeepsThem() {
        ArrayList<ItemIdStack> in = new ArrayList<>();
        ArrayList<ItemIdStack> out = new ArrayList<>();

        Recipe r = new Recipe("Ok", null, null, 2, in, out);

        assertEquals(in, r.getIngredients());
        assertEquals(out, r.getResults());
    }

    @Test
    void testConstructorNameId(){
        new Recipe("Pan con queso", 123);
    }
    /* =========================================================
     * SETTERS / GETTERS
     * ========================================================= */

    @Test
    void testSetIngredientsNullThrowsException() {
        IllegalArgumentException ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> recipe.setIngredients(null)
        );
        assertEquals("Input list cannot be null", ex.getMessage());
    }

    @Test
    void testSetResultsNullThrowsException() {
        IllegalArgumentException ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> recipe.setResults(null)
        );
        assertEquals("Output list cannot be null", ex.getMessage());
    }

    @Test
    void testSetIngredientsSortsList() {
        ArrayList<ItemIdStack> input = new ArrayList<>();
        input.add(new ItemIdStack(cobblestone.getId(), 3));
        input.add(new ItemIdStack(stick.getId(), 2));

        recipe.setIngredients(input);

        assertEquals(input, recipe.getIngredients());
    }

    @Test
    void testYSetResultsWithNonNullListSortsOutput() {
        Recipe r = new Recipe("Recipe", 1);

        Item a = new Item("A", null, null, 10);
        Item b = new Item("B", null, null, 5);

        ArrayList<ItemIdStack> unsortedOutput = new ArrayList<>();
        unsortedOutput.add(new ItemIdStack(b.getId(), 1));
        unsortedOutput.add(new ItemIdStack(a.getId(), 1)); // id menor → debe ir antes tras sort

        r.setResults(unsortedOutput);

        ArrayList<ItemIdStack> results = r.getResults();

        assertEquals(2, results.size());
        assertEquals(a.getId(), results.get(0).getId());
        assertEquals(b.getId(), results.get(1).getId());
    }


    /* =========================================================
     * ADD / REMOVE INGREDIENTS & RESULTS
     * ========================================================= */

    @Test
    void testAddIngredientAddsAndSorts() {
        recipe.addIngredient(stick, 2);
        recipe.addIngredient(cobblestone, 3);

        assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    void testAddIngredientWithNullDoesNothing() {
        recipe.addIngredient(null, 1);
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void testRemoveIngredientExisting() {
        recipe.addIngredient(stick, 2);

        assertTrue(recipe.removeIngredient(stick));
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void testRemoveIngredientNonExistingReturnsFalse() {
        assertFalse(recipe.removeIngredient(stick));
    }

    @Test
    void testAddResultWithNullDoesNothing() {
        recipe.addResult(null, 1);
        assertTrue(recipe.getResults().isEmpty());
    }

    @Test
    void testAddAndRemoveResult() {
        recipe.addResult(pickaxe, 1);

        assertTrue(recipe.removeResult(pickaxe));
        assertTrue(recipe.getResults().isEmpty());
    }

    @Test
    void testYRemoveResultWhenResultNotPresentReturnsFalse() {
        Recipe r = new Recipe("Recipe", 1);

        Item stick = new Item("Stick", null, null, 10);
        Item stone = new Item("Stone", null, null, 20);

        // La receta tiene UN resultado
        r.addResult(stick, 2);

        // Intentamos eliminar uno que NO está
        boolean removed = r.removeResult(stone);

        assertFalse(removed);
        assertEquals(1, r.getResults().size());
        assertEquals(stick.getId(), r.getResults().get(0).getId());
        assertEquals(2, r.getResults().get(0).getAmount());
    }


    /* =========================================================
     * CAN BE EXECUTED
     * ========================================================= */

    @Test
    void testCanBeExecutedWithExactInput() {
        recipe.addIngredient(stick, 2);
        recipe.addIngredient(cobblestone, 3);

        ArrayList<ItemIdStack> input = new ArrayList<>();
        input.add(new ItemIdStack(cobblestone.getId(), 3));
        input.add(new ItemIdStack(stick.getId(), 2));

        assertTrue(recipe.canBeExecuted(input));
    }

    @Test
    void testCanBeExecutedWithWrongInputReturnsFalse() {
        recipe.addIngredient(stick, 2);

        ArrayList<ItemIdStack> input = new ArrayList<>();
        input.add(new ItemIdStack(stick.getId(), 1));

        assertFalse(recipe.canBeExecuted(input));
    }

    @Test
    void testCanBeExecutedWithNullReturnsFalse() {
        assertFalse(recipe.canBeExecuted(null));
    }

    /* =========================================================
     * EXECUTE RECIPE
     * ========================================================= */

    @Test
    void testExecuteRecipeSuccessReturnsOutputCopy() {
        recipe.addIngredient(stick, 2);
        recipe.addResult(pickaxe, 1);

        ArrayList<ItemIdStack> input = new ArrayList<>();
        input.add(new ItemIdStack(stick.getId(), 2));

        ArrayList<ItemIdStack> output = recipe.executeRecipe(input);

        assertNotNull(output);
        assertEquals(recipe.getResults(), output);
        assertNotSame(recipe.getResults(), output);
    }

    @Test
    void testExecuteRecipeFailureReturnsNull() {
        recipe.addIngredient(stick, 2);

        ArrayList<ItemIdStack> badInput = new ArrayList<>();
        badInput.add(new ItemIdStack(stick.getId(), 1));

        assertNull(recipe.executeRecipe(badInput));
    }

    /* =========================================================
     * EQUALS
     * ========================================================= */

    @Test
    void testEqualsSameObject() {
        assertEquals(recipe, recipe);
    }

    @Test
    void testEqualsDifferentObjectSameContent() {
        recipe.addIngredient(stick, 2);
        recipe.addResult(pickaxe, 1);

        Recipe other = new Recipe("Other", null, null, 100);
        other.addIngredient(stick, 2);
        other.addResult(pickaxe, 1);

        assertEquals(recipe, other);
    }

    @Test
    void testEqualsDifferentIngredientsFails() {
        recipe.addIngredient(stick, 2);

        Recipe other = new Recipe("Other", null, null, 100);
        other.addIngredient(stick, 1);

        assertNotEquals(recipe, other);
    }

    @Test
    void testEqualsWithNullAndOtherTypeFails() {
        assertNotEquals(recipe, null);
        assertNotEquals(recipe, stick);
    }

    @Test
    void testEqualsSameClassDifferentIdFailsBySuperEquals() {
        recipe.addIngredient(stick, 2);
        recipe.addResult(pickaxe, 1);

        Recipe other = new Recipe("Same content", null, null, 999);
        other.addIngredient(stick, 2);
        other.addResult(pickaxe, 1);

        assertNotEquals(recipe, other);
    }
    
    @Test
    void testEqualsSameInputDifferentOutputFails() {
        recipe.addIngredient(stick, 2);
        recipe.addResult(pickaxe, 1);

        Recipe other = new Recipe("Other", null, null, 100);
        other.addIngredient(stick, 2);
        other.addResult(stick, 1); // distinto output

        assertNotEquals(recipe, other);
    }

}
