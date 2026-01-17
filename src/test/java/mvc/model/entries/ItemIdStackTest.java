package mvc.model.entries;

import static org.junit.jupiter.api.Assertions.*;

import identificators.EntryId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemIdStackTest {

    private Item stick;
    private EntryId stickId;

    @BeforeEach
    void setUp() {
        stick = new Item("Stick", null, null, 1);
        stickId = stick.getId();
    }

    /* =========================================================
     * CONSTRUCTORS
     * ========================================================= */

    @Test
    void testConstructorWithPrimitiveId() {
        ItemIdStack stack = new ItemIdStack(1, 5);

        assertEquals(new EntryId(1), stack.getId());
        assertEquals(5, stack.getAmount());
    }

    @Test
    void testConstructorWithEntryId() {
        ItemIdStack stack = new ItemIdStack(stickId, 3);

        assertEquals(stickId, stack.getId());
        assertEquals(3, stack.getAmount());
    }

    @Test
    void testConstructorWithItem() {
        ItemIdStack stack = new ItemIdStack(stick, 7);

        assertEquals(stickId, stack.getId());
        assertEquals(7, stack.getAmount());
    }

    /* =========================================================
     * GETTERS
     * ========================================================= */

    @Test
    void testGetIdAndAmount() {
        ItemIdStack stack = new ItemIdStack(stick, 2);

        assertEquals(stickId, stack.getId());
        assertEquals(2, stack.getAmount());
    }

    /* =========================================================
     * EQUALS & HASHCODE
     * ========================================================= */

    @Test
    void testEqualsSameObject() {
        ItemIdStack stack = new ItemIdStack(stick, 1);
        assertEquals(stack, stack);
    }

    @Test
    void testEqualsSameValues() {
        ItemIdStack a = new ItemIdStack(stickId, 4);
        ItemIdStack b = new ItemIdStack(new EntryId(1), 4);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentAmountFails() {
        ItemIdStack a = new ItemIdStack(stickId, 4);
        ItemIdStack b = new ItemIdStack(stickId, 5);

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentIdFails() {
        ItemIdStack a = new ItemIdStack(stickId, 4);
        ItemIdStack b = new ItemIdStack(2, 4);

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNullAndOtherTypeFails() {
        ItemIdStack stack = new ItemIdStack(stickId, 1);

        assertNotEquals(stack, null);
        assertNotEquals(stack, stick);
    }

    /* =========================================================
     * COMPARABLE
     * ========================================================= */

    @Test
    void testCompareToSameIdReturnsZero() {
        ItemIdStack a = new ItemIdStack(1, 1);
        ItemIdStack b = new ItemIdStack(1, 99);

        assertEquals(0, a.compareTo(b));
    }

    @Test
    void testCompareToOrdersDescendingById() {
        ItemIdStack low = new ItemIdStack(1, 1);
        ItemIdStack high = new ItemIdStack(2, 1);

        assertTrue(high.compareTo(low) < 0);
        assertTrue(low.compareTo(high) > 0);
    }
}
