package org.cyu.snowyleaves.client.debug;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;
import org.cyu.snowyleaves.client.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.client.frost.FrostController;
import org.cyu.snowyleaves.client.frost.SnowyWeatherRules;

public final class SnowyLeavesDebugHud {
    private SnowyLeavesDebugHud() {
    }

    @SuppressWarnings("deprecation")
    public static void register(SnowyLeavesConfig config, FrostController frostController) {
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context, tickCounter, config, frostController));
    }

    private static void render(
            DrawContext context,
            RenderTickCounter tickCounter,
            SnowyLeavesConfig config,
            FrostController frostController
    ) {
        if (!config.enableDebugHud) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.textRenderer == null) {
            return;
        }

        BlockPos pos = client.player.getBlockPos();
        boolean raining = client.world.isRaining();
        boolean skyVisible = client.world.isSkyVisible(pos.up());
        boolean canSnow = SnowyWeatherRules.canSnowAt(client.world, pos);
        double localWeight = raining ? SnowyWeatherRules.skyWeatherWeight(client.world, pos) : 0.0;

        int x = 8;
        int y = 8;
        int color = config.snowColorRgb();
        context.drawText(client.textRenderer, "CyuSnowyLeaves", x, y, color, true);
        y += 10;
        context.drawText(client.textRenderer, String.format("global frost: %.3f", frostController.globalFrostAmount()), x, y, color, true);
        y += 10;
        context.drawText(client.textRenderer, String.format("local snow weight: %.3f", localWeight), x, y, color, true);
        y += 10;
        context.drawText(client.textRenderer, "weather: " + (raining ? "precipitating" : "clear"), x, y, color, true);
        y += 10;
        context.drawText(client.textRenderer, "snow allowed: " + canSnow + " sky: " + skyVisible, x, y, color, true);
    }
}
