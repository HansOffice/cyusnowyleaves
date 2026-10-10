package org.cyu.snowyleaves.fabric.frost;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import org.cyu.snowyleaves.core.BoundedLongDoubleCache;

public final class SnowyWeatherRules {
    private static final int MAX_SKY_WEIGHT_CACHE_SIZE = 8192;
    private static final BoundedLongDoubleCache SKY_WEIGHT_CACHE = new BoundedLongDoubleCache(MAX_SKY_WEIGHT_CACHE_SIZE);
    private static ClientLevel cachedLevel;

    private SnowyWeatherRules() {
    }

    public static double biomeSnowWeight(ClientLevel level, BlockPos pos) {
        double center = pointWeight(level, pos);
        double north = pointWeight(level, pos.north(16));
        double south = pointWeight(level, pos.south(16));
        double east = pointWeight(level, pos.east(16));
        double west = pointWeight(level, pos.west(16));
        return (center * 2.0 + north + south + east + west) / 6.0;
    }

    private static double pointWeight(ClientLevel level, BlockPos pos) {
        Holder<Biome> entry = level.getBiome(pos);
        float temperature = entry.value().getBaseTemperature();

        if (temperature >= 0.95F) {
            return 0.0;
        }
        if (temperature >= 0.8F) {
            return 0.12;
        }
        if (temperature >= 0.45F) {
            return 0.35;
        }
        if (temperature >= 0.15F) {
            return 0.75;
        }
        return 1.0;
    }

    public static double skyWeatherWeight(ClientLevel level, BlockPos pos) {
        if (cachedLevel != level) {
            clearCache();
            cachedLevel = level;
        }
        if (!level.isRaining()) {
            return 0.0;
        }

        long key = BlockPos.asLong(pos.getX(), pos.getY() >> 2, pos.getZ());
        Double cached = SKY_WEIGHT_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        double weight = canSnowAt(level, pos)
                ? Mth.clamp(biomeSnowWeight(level, pos), 0.0, 1.0)
                : 0.0;
        SKY_WEIGHT_CACHE.put(key, weight);
        return weight;
    }

    public static boolean canSnowAt(ClientLevel level, BlockPos pos) {
        return pointWeight(level, pos) >= 1.0;
    }

    public static void clearCache() {
        SKY_WEIGHT_CACHE.clear();
    }
}
