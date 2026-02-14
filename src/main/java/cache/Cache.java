package cache;

import java.util.Optional;
import java.util.function.Supplier;

public interface Cache {
    <T> Optional<T> get(CacheKey key, Class<T> type);
    <T> void put(CacheKey key, T value);
    void invalidate(CacheKey key);
    void invalidateNamespace(String namespace);
    void clear();
    <T> T getOrLoad(CacheKey key, Class<T> type, Supplier<T> loader);
}
