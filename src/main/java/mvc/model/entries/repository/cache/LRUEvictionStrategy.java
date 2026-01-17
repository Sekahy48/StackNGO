package mvc.model.entries.repository.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUEvictionStrategy<K, V> implements CacheEvictionStrategy<K, V> {

    @Override
    public void evict(Map<K, V> cache) {
        if (cache.isEmpty()) {
            return;
        }

        if (!(cache instanceof LinkedHashMap)) {
            throw new IllegalArgumentException(
                "LRU eviction requires a LinkedHashMap with accessOrder=true"
            );
        }

        Iterator<K> it = cache.keySet().iterator();
        // hasNext falso es una rama sin cubrir, no es accesible, pero por seguir
        // el estandar de iterator, dejo hasNext como comrpobacion extra
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}
