package org.cyu.snowyleaves.neoforge.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.cyu.snowyleaves.neoforge.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.neoforge.frost.FrostController;
import org.cyu.snowyleaves.neoforge.frost.SnowyWeatherRules;
import org.cyu.snowyleaves.core.SnowTint;
import org.cyu.snowyleaves.core.SnowVisualRules;
import org.cyu.snowyleaves.core.VisualFrostCache;

import java.util.List;
import java.util.function.Consumer;

public final class SnowyLeafColorRegistry {
    private static final int MAX_VISUAL_FROST_CACHE_SIZE = 32768;
    private static final VisualFrostCache VISUAL_FROST_CACHE = new VisualFrostCache(MAX_VISUAL_FROST_CACHE_SIZE);

    private SnowyLeafColorRegistry() {
    }

    public static Consumer<RegisterColorHandlersEvent.BlockTintSources> createRegisterListener(
            SnowyLeavesConfig config,
            FrostController frostController
    ) {
        return event -> {
            BlockTintSource color = new BlockTintSource() {
                @Override
                public int color(BlockState state) {
                    return SnowyLeafColorRegistry.color(state, null, null, config, frostController);
                }

                @Override
                public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                    return SnowyLeafColorRegistry.color(state, level, pos, config, frostController);
                }
            };
            event.register(List.of(color), LeafBlocks.SUPPORTED);
        };
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
            return opaque(originalColor);
        }

        double leafStrength = LeafBlocks.strength(state.getBlock());
        if (leafStrength <= 0.0) {
            return opaque(originalColor);
        }

        double biomeWeight = config.enableBiomeCheck ? SnowyWeatherRules.skyWeatherWeight(clientLevel, pos) : 1.0;
        double variation = config.enablePerLeafVariation ? stableVariation(pos) : 1.0;
        float targetFrost = frostController.frostAt(clientLevel, pos, biomeWeight, variation) * (float) leafStrength;
        if (targetFrost <= 0.0001F) {
            VISUAL_FROST_CACHE.clear(pos.asLong());
            return opaque(originalColor);
        }

        float frostAmount = VISUAL_FROST_CACHE.approach(
                pos.asLong(),
                clientLevel.getGameTime(),
                targetFrost,
                config.accumulationSpeed,
                config.meltingSpeed
        );

        return opaque(SnowTint.mix(originalColor, config.snowColorRgb(), frostAmount));
    }

    private static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0x00FFFFFF);
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
