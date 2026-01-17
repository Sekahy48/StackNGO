package mvc.model.entries;

import static org.junit.jupiter.api.Assertions.*;

import identificators.EntryId;
import org.junit.jupiter.api.Test;

public class EntryIdTest {

    /* =========================================================
     * CONSTRUCTOR
     * ========================================================= */

    @Test
    void testConstructorPositiveValue() {
        EntryId id = new EntryId(1);
        assertEquals(1, id.value());
    }

    @Test
    void testConstructorZeroFails() {
        IllegalArgumentException ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new EntryId(0)
        );
        assertEquals("EntryId must be positive", ex.getMessage());
    }

    @Test
    void testConstructorNegativeFails() {
        IllegalArgumentException ex = assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new EntryId(-5)
        );
        assertEquals("EntryId must be positive", ex.getMessage());
    }

    /* =========================================================
     * GETTER
     * ========================================================= */

    @Test
    void testGetValue() {
        EntryId id = new EntryId(42);
        assertEquals(42, id.value());
    }

    /* =========================================================
     * EQUALS & HASHCODE
     * ========================================================= */

    @Test
    void testEqualsSameObject() {
        EntryId id = new EntryId(10);
        assertEquals(id, id);
    }

    @Test
    void testEqualsSameValue() {
        EntryId a = new EntryId(7);
        EntryId b = new EntryId(7);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentValueFails() {
        EntryId a = new EntryId(7);
        EntryId b = new EntryId(8);

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsNullFails() {
        EntryId id = new EntryId(1);
        assertNotEquals(id, null);
    }

    @Test
    void testEqualsDifferentTypeFails() {
        EntryId id = new EntryId(1);
        String str = "1";
        assertNotEquals(id, str);
    }

    /* =========================================================
     * COMPARETO
     * ========================================================= */

    @Test
    void testCompareToSameValueReturnsZero() {
        EntryId a = new EntryId(5);
        EntryId b = new EntryId(5);

        assertEquals(0, a.compareTo(b));
    }

    @Test
    void testCompareToLowerAndHigher() {
        EntryId low = new EntryId(3);
        EntryId high = new EntryId(10);

        assertTrue(low.compareTo(high) < 0);
        assertTrue(high.compareTo(low) > 0);
    }

    /* =========================================================
     * TOSTRING
     * ========================================================= */

    @Test
    void testToStringReturnsValue() {
        EntryId id = new EntryId(99);
        assertEquals("99", id.toString());
    }
}
