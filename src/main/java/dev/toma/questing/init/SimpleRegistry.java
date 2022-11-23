package dev.toma.questing.init;

import java.util.*;

public class SimpleRegistry<K, V> {

    private final Map<K, V> registry = new HashMap<>();

    public synchronized void register(K key, V value) {
        if (registry.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key found - " + key);
        }
    }

    public V getValue(K key) {
        return this.registry.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueUnsafe(K key) {
        return (T) this.getValue(key);
    }

    public Optional<V> getOptionalValue(K key) {
        return Optional.ofNullable(this.getValue(key));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalValueUnsafe(K key) {
        return (Optional<T>) this.getOptionalValue(key);
    }

    public boolean contains(K key) {
        return this.registry.containsKey(key);
    }

    public Set<Map.Entry<K, V>> getEntries() {
        return registry.entrySet();
    }

    public Set<K> getKeys() {
        return registry.keySet();
    }

    public Collection<V> getValues() {
        return registry.values();
    }
}
