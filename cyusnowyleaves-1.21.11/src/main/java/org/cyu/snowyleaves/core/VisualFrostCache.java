package org.cyu.snowyleaves.core;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VisualFrostCache {
    private static final int MAX_ELAPSED_TICKS = 40;

    private final Map<Long, FrostEntry> values;
    private boolean pendingTransitions;

    public VisualFrostCache(int maxSize) {
        this.values = new LinkedHashMap<>(maxSize, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Long, FrostEntry> eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized float approach(long key, long gameTime, float target, double accumulationSpeed, double meltingSpeed) {
        FrostEntry entry = values.get(key);
        if (entry == null) {
            entry = new FrostEntry(0.0F, gameTime);
            values.put(key, entry);
            if (Math.abs(entry.amount - target) > 0.01F) {
                pendingTransitions = true;
            }
            return entry.amount;
        }

        long rawElapsed = gameTime - entry.lastGameTime;
        if (rawElapsed <= 0L) {
            if (Math.abs(entry.amount - target) > 0.01F) {
                pendingTransitions = true;
            }
            return entry.amount;
        }

        long elapsed = Math.min(MAX_ELAPSED_TICKS, rawElapsed);
        double speed = target > entry.amount ? accumulationSpeed : meltingSpeed;
        float step = (float) Math.max(0.0, speed * elapsed);
        entry.amount = approach(entry.amount, target, step);
        entry.lastGameTime = gameTime;

        if (Math.abs(entry.amount - target) > 0.01F) {
            pendingTransitions = true;
        }
        return entry.amount;
    }

    public synchronized boolean consumePendingTransitions() {
        boolean result = pendingTransitions;
        pendingTransitions = false;
        return result;
    }

    public synchronized void clear(long key) {
        if (values.remove(key) != null) {
            pendingTransitions = true;
        }
    }

    public synchronized void clear() {
        values.clear();
        pendingTransitions = false;
    }

    private static float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(target, current + step);
        }
        if (current > target) {
            return Math.max(target, current - step);
        }
        return current;
    }

    private static final class FrostEntry {
        private float amount;
        private long lastGameTime;

        private FrostEntry(float amount, long lastGameTime) {
            this.amount = amount;
            this.lastGameTime = lastGameTime;
        }
    }
}
