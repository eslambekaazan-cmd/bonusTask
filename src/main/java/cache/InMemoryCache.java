package cache;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class InMemoryCache implements Cache {

    private static volatile InMemoryCache instance;

    private final Map<CacheKey, CacheEntry> store = new ConcurrentHashMap<>();
    private final Duration ttl;

    private InMemoryCache(Duration ttl) {
        this.ttl = ttl; 
    }

   
    public static InMemoryCache getInstance(Duration ttl) {
        if (instance == null) {
            synchronized (InMemoryCache.class) {
                if (instance == null) {
                    instance = new InMemoryCache(ttl);
                }
            }
        }
        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(CacheKey key, Class<T> type) {
        CacheEntry entry = store.get(key);
        if (entry == null) return Optional.empty();

        if (entry.isExpired()) {
            store.remove(key);
            return Optional.empty();
        }

        Object value = entry.value();
        if (!type.isInstance(value)) return Optional.empty();
        return Optional.of((T) value);
    }

    @Override
    public <T> void put(CacheKey key, T value) {
        Instant expiresAt = (ttl == null) ? null : Instant.now().plus(ttl);
        store.put(key, new CacheEntry(value, expiresAt));
    }

    @Override
    public void invalidate(CacheKey key) {
        store.remove(key);
    }

    @Override
    public void invalidateNamespace(String namespace) {
        store.keySet().removeIf(k -> k.namespace().equals(namespace));
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public <T> T getOrLoad(CacheKey key, Class<T> type, Supplier<T> loader) {
        return get(key, type).orElseGet(() -> {
            T loaded = loader.get();
            put(key, loaded);
            return loaded;
        });
    }

    private record CacheEntry(Object value, Instant expiresAt) {
        boolean isExpired() {
            return expiresAt != null && Instant.now().isAfter(expiresAt);
        }
    }
}
