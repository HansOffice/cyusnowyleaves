package org.cyu.snowyleaves.core;

public final class StableNoise {
    private StableNoise() {
    }

    public static double normalized(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return (value & 0xFFFF) / 65535.0;
    }
}
