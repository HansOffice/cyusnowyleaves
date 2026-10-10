package org.cyu.snowyleaves.fabric.color;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.cyu.snowyleaves.fabric.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.fabric.frost.FrostController;
import org.cyu.snowyleaves.fabric.frost.SnowyWeatherRules;
import org.cyu.snowyleaves.core.SnowTint;
import org.cyu.snowyleaves.core.SnowVisualRules;
import org.cyu.snowyleaves.core.VisualFrostCache;

public final class SnowyLeafColorRegistry {
    private static final int MAX_VISUAL_FROST_CACHE_SIZE = 32768;
    private static final VisualFrostCache VISUAL_FROST_CACHE = new VisualFrostCache(MAX_VISUAL_FROST_CACHE_SIZE);

    private SnowyLeafColorRegistry() {
    }

    public static void register(SnowyLeavesConfig config, FrostController frostController) {
        ColorProviderRegistry.BLOCK.register((state, level, pos, tintIndex) ->
                        color(state, level, pos, config, frostController),
                LeafBlocks.SUPPORTED
        );

    }

    private static int color(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            SnowyLeavesConfig config,
            FrostController frostController
    ) {
        int originalColor = LeafColorSampler.originalColor(state, level, pos);
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (!config.enabled || pos == null || clientLevel == null) {
            return originalColor;
        }

        double leafStrength = LeafBlocks.strength(state.getBlock());
        if (leafStrength <= 0.0) {
            return originalColor;
        }

        double biomeWeight = config.enableBiomeCheck ? SnowyWeatherRules.skyWeatherWeight(clientLevel, pos) : 1.0;
        double variation = config.enablePerLeafVariation ? stableVariation(pos) : 1.0;
        float targetFrost = frostController.frostAt(clientLevel, pos, biomeWeight, variation) * (float) leafStrength;
        if (targetFrost <= 0.0001F) {
            VISUAL_FROST_CACHE.clear(pos.asLong());
            return originalColor;
        }

        float frostAmount = VISUAL_FROST_CACHE.approach(
                pos.asLong(),
                clientLevel.getGameTime(),
                targetFrost,
                config.accumulationSpeed,
                config.meltingSpeed
        );

        return SnowTint.mix(originalColor, config.snowColorRgb(), frostAmount);
    }

    private static double stableVariation(BlockPos pos) {
        long patchKey = BlockPos.asLong(pos.getX() >> 2, pos.getY() >> 1, pos.getZ() >> 2);
        return SnowVisualRules.stableLeafVariation(pos.asLong(), patchKey);
    }

    public static void clearVisualCache() {
        VISUAL_FROST_CACHE.clear();
    }

    public static boolean consumePendingVisualTransitions() {
        return VISUAL_FROST_CACHE.consumePendingTransitions();
    }
}
