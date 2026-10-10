package org.cyu.snowyleaves.core;

public final class SnowVisualRules {
    private SnowVisualRules() {
    }

    public static double stableLeafVariation(long posKey, long patchKey) {
        double fine = StableNoise.normalized(posKey);
        double patch = StableNoise.normalized(patchKey);
        return 0.985 + fine * 0.02 + patch * 0.015;
    }

    public static double exposureVariation(boolean leafAbove, int horizontalLeafNeighbors) {
        return 1.0;
    }
}
