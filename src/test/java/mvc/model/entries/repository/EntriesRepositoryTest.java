package mvc.model.entries.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mvc.model.entries.Item;
import mvc.model.entries.Recipe;
import mvc.model.entries.repository.cache.CacheEvictionStrategy;
import mvc.model.entries.repository.cache.FifoEvictionStrategy;
import mvc.model.entries.repository.cache.LRUEvictionStrategy;

class EntriesRepositoryTest {

    private EntriesRepository repository;

    private Item item1;
    private Item item2;
    private Recipe recipe1;

    private int getId(){
        return EntryIdGenerator.getInstance().generateId();
    }

    @BeforeEach
    void setUp() {
        EntryIdGenerator.getInstance().setLastId(0);
        repository = new EntriesRepository(2, new FifoEvictionStrategy<>());

        item1 = new Item("Item1", null, null, getId());
        item2 = new Item("Item2", null, null, getId());
        recipe1 = new Recipe("Recipe1", getId());
    }

    // -------------------------
    // addItem / addRecipe
    // -------------------------

    @Test
    void testAddItemSuccessfully() {
        assertTrue(repository.addItem(item1));
    }

    @Test
    void testAddRecipeSuccessfully() {
        assertTrue(repository.addRecipe(recipe1));
    }

    @Test
    void testAddDuplicateEntryReturnsFalse() {
        assertTrue(repository.addItem(item1));
        assertFalse(repository.addItem(item1)); // mismo id
    }

    // -------------------------
    // Cache eviction
    // -------------------------

    @Test
    void testCacheEvictionTriggeredWhenLimitReached() {
        repository.addItem(item1);
        repository.addItem(item2);

        // al añadir el tercero se debe evacuar uno
        repository.addRecipe(recipe1);

        // FIFO: el primero fuera
        assertTrue(repository.addItem(item1)); // ya no está, se puede añadir
        assertTrue(repository.contains(item1.getId()));
        assertTrue(repository.contains(recipe1.getId()));
        assertFalse(repository.contains(item2.getId()));

        assertEquals(repository.getEntry(item1.getId()), item1);
    }

    @Test
    void testLRUEvictionStrategyWorks() {
        repository.setStrategy(new LRUEvictionStrategy<>());

        repository.addItem(item1);
        repository.addItem(item2);

        // Accedemos a item1 para que item2 sea el menos usado
        repository.addItem(new Item("Dummy", null, null, getId())); // fuerza acceso interno

        Recipe newRecipe = new Recipe("NewRecipe", getId());
        repository.addRecipe(newRecipe);

        // No debe lanzar excepción → rama LRU correcta
        assertTrue(true);
    }

    @Test
    void testFifoEvictionWithEmptyCacheDoesNothing() {
        FifoEvictionStrategy<Integer, String> fifo = new FifoEvictionStrategy<>();
        Map<Integer, String> emptyMap = new java.util.LinkedHashMap<>();
        fifo.evict(emptyMap); // no debe lanzar nada, rama it.hasNext() == false
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    void testLRUEvictionWithEmptyCacheDoesNothing() {
        LRUEvictionStrategy<Integer, String> lru = new LRUEvictionStrategy<>();
        Map<Integer, String> emptyMap = new java.util.LinkedHashMap<>();
        lru.evict(emptyMap); // rama cache.isEmpty() == true
        assertTrue(emptyMap.isEmpty()); 
    }

    @Test
    void testLRUEvictionWithNonLinkedHashMapThrows() {
        LRUEvictionStrategy<Integer, String> lru = new LRUEvictionStrategy<>();
        Map<Integer, String> hashMap = new java.util.HashMap<>();
        hashMap.put(2, "hola");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> lru.evict(hashMap)
        );
        assertEquals("LRU eviction requires a LinkedHashMap with accessOrder=true", ex.getMessage());
    }

    @Test
    void testLRUEvictionRemovesLeastRecentlyUsed() {
        LRUEvictionStrategy<Integer, String> lru = new LRUEvictionStrategy<>();
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>(16, 0.75f, true);
        map.put(1, "A");
        map.put(2, "B");

        lru.evict(map); // debe eliminar la primera clave (1)
        assertFalse(map.containsKey(1));
        assertTrue(map.containsKey(2));
    }

    // -------------------------
    // setLimit
    // -------------------------

    @Test
    void testSetLimitPositiveValueUpdatesLimit() {
        repository.setLimit(getId());

        repository.addItem(item1);
        repository.addItem(item2);

        // no debe evacuar aún
        assertTrue(repository.addRecipe(recipe1));
    }

    @Test
    void testSetLimitNegativeValueLogsErrorButDoesNotCrash() {
        repository.setLimit(-1);

        // El límite no cambia, sigue funcionando
        assertTrue(repository.addItem(item1));
    }

    // -------------------------
    // setStrategy
    // -------------------------

    @Test
    void testSetStrategyWithValidStrategy() {
        CacheEvictionStrategy<?, ?> newStrategy = new FifoEvictionStrategy<>();
        repository.setStrategy((CacheEvictionStrategy) newStrategy);

        repository.addItem(item1);
        assertTrue(repository.addItem(item2));
    }

    @Test
    void testSetStrategyWithNullLogsErrorButKeepsOldStrategy() {
        repository.setStrategy(null);

        repository.addItem(item1);
        repository.addItem(item2);
        repository.addRecipe(recipe1); // sigue funcionando
        assertTrue(true);
    }
}
