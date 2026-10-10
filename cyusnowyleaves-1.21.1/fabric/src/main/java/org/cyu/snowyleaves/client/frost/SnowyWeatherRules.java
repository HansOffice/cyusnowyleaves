package org.cyu.snowyleaves.client.frost;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import org.cyu.snowyleaves.core.BoundedLongDoubleCache;

public final class SnowyWeatherRules {
    private static final int MAX_SKY_WEIGHT_CACHE_SIZE = 8192;
    private static final BoundedLongDoubleCache SKY_WEIGHT_CACHE = new BoundedLongDoubleCache(MAX_SKY_WEIGHT_CACHE_SIZE);
    private static ClientWorld cachedWorld;

    private SnowyWeatherRules() {
    }

    public static double biomeSnowWeight(ClientWorld world, BlockPos pos) {
        double center = pointWeight(world, pos);
        double north = pointWeight(world, pos.north(16));
        double south = pointWeight(world, pos.south(16));
        double east = pointWeight(world, pos.east(16));
        double west = pointWeight(world, pos.west(16));
        return (center * 2.0 + north + south + east + west) / 6.0;
    }

    private static double pointWeight(ClientWorld world, BlockPos pos) {
        RegistryEntry<Biome> entry = world.getBiome(pos);
        Biome biome = entry.value();
        float temperature = biome.getTemperature();

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

    public static double skyWeatherWeight(ClientWorld world, BlockPos pos) {
        if (cachedWorld != world) {
            clearCache();
            cachedWorld = world;
        }
        if (!world.isRaining()) {
            return 0.0;
        }

        long key = BlockPos.asLong(pos.getX(), pos.getY() >> 2, pos.getZ());
        Double cached = SKY_WEIGHT_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        double weight = canSnowAt(world, pos)
                ? MathHelper.clamp(biomeSnowWeight(world, pos), 0.0, 1.0)
                : 0.0;
        SKY_WEIGHT_CACHE.put(key, weight);
        return weight;
    }

    public static boolean canSnowAt(ClientWorld world, BlockPos pos) {
        return pointWeight(world, pos) >= 1.0;
    }

    public static void clearCache() {
        SKY_WEIGHT_CACHE.clear();
    }
}
