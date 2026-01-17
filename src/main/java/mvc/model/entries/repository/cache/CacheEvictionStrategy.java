package mvc.model.entries.repository.cache;

import java.util.Map;

public interface CacheEvictionStrategy<K, V> {
    void evict(Map<K, V> cache);
}
