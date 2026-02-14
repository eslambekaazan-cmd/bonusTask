package cache;

import java.util.List;
import java.util.Objects;

public final class CacheKey {
    private final String namespace;
    private final String name;
    private final List<Object> params;

    private CacheKey(String namespace, String name, List<Object> params) {
        this.namespace = namespace;
        this.name = name;
        this.params = params;
    }

    public static CacheKey of(String namespace, String name, Object... params) {
        return new CacheKey(namespace, name, List.of(params));
    }

    public String namespace() {
        return namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey)) return false;
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(namespace, cacheKey.namespace)
                && Objects.equals(name, cacheKey.name)
                && Objects.equals(params, cacheKey.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name, params);
    }

    @Override
    public String toString() {
        return namespace + ":" + name + ":" + params;
    }
}
