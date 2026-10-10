package org.cyu.snowyleaves.core;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BoundedLongDoubleCache {
    private final Map<Long, Double> values;

    public BoundedLongDoubleCache(int maxSize) {
        this.values = new LinkedHashMap<>(maxSize, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Double> eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized Double get(long key) {
        return values.get(key);
    }

    public synchronized void put(long key, double value) {
        values.put(key, value);
    }

    public synchronized void clear() {
        values.clear();
    }
}
