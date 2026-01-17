package mvc.model.entries.repository.cache;

import java.util.Iterator;
import java.util.Map;

public class FifoEvictionStrategy<K, V> implements CacheEvictionStrategy<K, V> {

    @Override
    public void evict(Map<K, V> cache) {
        Iterator<K> it = cache.keySet().iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}
