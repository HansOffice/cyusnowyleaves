package org.cyu.snowyleaves.neoforge.frost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import org.cyu.snowyleaves.neoforge.color.SnowyLeafColorRegistry;
import org.cyu.snowyleaves.neoforge.config.SnowyLeavesConfig;

public final class FrostController {
    private final SnowyLeavesConfig config;
    private double globalFrostAmount;
    private int renderRefreshCooldown;
    private int cacheRefreshCooldown;
    private int lastRenderRefreshBucket = -1;
    private ClientLevel cachedLevel;

    public FrostController(SnowyLeavesConfig config) {
        this.config = config;
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        double previousFrostAmount = globalFrostAmount;
        clearVisualStateIfWorldChanged(client.level);
        refreshWeatherCacheIfNeeded();
        if (!config.enabled || client.player == null || client.level == null) {
            globalFrostAmount = approach(globalFrostAmount, 0.0, config.meltingSpeed);
            refreshLeafRenderIfNeeded(client, previousFrostAmount);
            return;
        }

        double target = shouldAccumulate(client.level) ? config.maxFrostAmount : 0.0;
        double speed = target > globalFrostAmount ? config.accumulationSpeed : config.meltingSpeed;
        globalFrostAmount = approach(globalFrostAmount, target, speed);
        refreshLeafRenderIfNeeded(client, previousFrostAmount);
    }

    public float frostAt(ClientLevel level, BlockPos pos, double biomeWeight, double variation) {
        if (!config.enabled) {
            return 0.0F;
        }
        double frost = globalFrostAmount * biomeWeight * variation;
        return (float) Mth.clamp(frost, 0.0, config.maxFrostAmount);
    }

    private boolean shouldAccumulate(ClientLevel level) {
        return level.isRaining();
    }

    private static double approach(double current, double target, double step) {
        if (current < target) {
            return Math.min(target, current + step);
        }
        if (current > target) {
            return Math.max(target, current - step);
        }
        return current;
    }

    private void refreshLeafRenderIfNeeded(Minecraft client, double previousFrostAmount) {
        if (client.player == null || client.level == null) {
            return;
        }
        if (renderRefreshCooldown > 0) {
            renderRefreshCooldown--;
            return;
        }

        int bucket = (int) Math.round(globalFrostAmount * 24.0);
        boolean changedEnough = bucket != lastRenderRefreshBucket
                || Math.abs(globalFrostAmount - previousFrostAmount) >= 0.05
                || SnowyLeafColorRegistry.consumePendingVisualTransitions();
        if (!changedEnough) {
            return;
        }

        BlockPos center = client.player.blockPosition();
        int horizontalRadius = 64;
        int verticalRadius = 40;
        client.level.setSectionRangeDirty(
                SectionPos.blockToSectionCoord(center.getX() - horizontalRadius),
                SectionPos.blockToSectionCoord(center.getY() - verticalRadius),
                SectionPos.blockToSectionCoord(center.getZ() - horizontalRadius),
                SectionPos.blockToSectionCoord(center.getX() + horizontalRadius),
                SectionPos.blockToSectionCoord(center.getY() + verticalRadius),
                SectionPos.blockToSectionCoord(center.getZ() + horizontalRadius)
        );
        lastRenderRefreshBucket = bucket;
        renderRefreshCooldown = 24;
    }

    private void refreshWeatherCacheIfNeeded() {
        if (cacheRefreshCooldown > 0) {
            cacheRefreshCooldown--;
            return;
        }
        SnowyWeatherRules.clearCache();
        cacheRefreshCooldown = 40;
    }

    private void clearVisualStateIfWorldChanged(ClientLevel level) {
        if (cachedLevel == level) {
            return;
        }
        cachedLevel = level;
        SnowyLeafColorRegistry.clearVisualCache();
        lastRenderRefreshBucket = -1;
    }
}
