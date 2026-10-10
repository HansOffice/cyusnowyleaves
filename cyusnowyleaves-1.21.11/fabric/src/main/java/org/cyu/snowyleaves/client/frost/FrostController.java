package org.cyu.snowyleaves.client.frost;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.cyu.snowyleaves.client.color.SnowyLeafColorRegistry;
import org.cyu.snowyleaves.client.config.SnowyLeavesConfig;

public final class FrostController {
    private final SnowyLeavesConfig config;
    private double globalFrostAmount;
    private int renderRefreshCooldown;
    private int cacheRefreshCooldown;
    private int lastRenderRefreshBucket = -1;
    private ClientWorld cachedWorld;

    public FrostController(SnowyLeavesConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient client) {
        double previousFrostAmount = globalFrostAmount;
        clearVisualStateIfWorldChanged(client.world);
        refreshWeatherCacheIfNeeded();
        if (!config.enabled || client.player == null || client.world == null) {
            globalFrostAmount = approach(globalFrostAmount, 0.0, config.meltingSpeed);
            refreshLeafRenderIfNeeded(client, previousFrostAmount);
            return;
        }

        double target = shouldAccumulate(client.world) ? config.maxFrostAmount : 0.0;
        double speed = target > globalFrostAmount ? config.accumulationSpeed : config.meltingSpeed;
        globalFrostAmount = approach(globalFrostAmount, target, speed);
        refreshLeafRenderIfNeeded(client, previousFrostAmount);
    }

    public float frostAt(ClientWorld world, BlockPos pos, double biomeWeight, double variation) {
        if (!config.enabled) {
            return 0.0F;
        }
        double frost = globalFrostAmount * biomeWeight * variation;
        return (float) MathHelper.clamp(frost, 0.0, config.maxFrostAmount);
    }

    public float globalFrostAmount() {
        return (float) globalFrostAmount;
    }

    private boolean shouldAccumulate(ClientWorld world) {
        return world.isRaining();
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

    private void refreshLeafRenderIfNeeded(MinecraftClient client, double previousFrostAmount) {
        if (client.player == null || client.worldRenderer == null) {
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

        BlockPos center = client.player.getBlockPos();
        int horizontalRadius = 64;
        int verticalRadius = 40;
        client.worldRenderer.scheduleBlockRenders(
                center.getX() - horizontalRadius,
                center.getY() - verticalRadius,
                center.getZ() - horizontalRadius,
                center.getX() + horizontalRadius,
                center.getY() + verticalRadius,
                center.getZ() + horizontalRadius
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

    private void clearVisualStateIfWorldChanged(ClientWorld world) {
        if (cachedWorld == world) {
            return;
        }
        cachedWorld = world;
        SnowyLeafColorRegistry.clearVisualCache();
        lastRenderRefreshBucket = -1;
    }
}
