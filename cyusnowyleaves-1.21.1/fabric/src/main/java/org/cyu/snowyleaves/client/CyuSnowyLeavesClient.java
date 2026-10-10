package org.cyu.snowyleaves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.cyu.snowyleaves.client.color.SnowyLeafColorRegistry;
import org.cyu.snowyleaves.client.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.client.debug.SnowyLeavesDebugHud;
import org.cyu.snowyleaves.client.frost.FrostController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CyuSnowyLeavesClient implements ClientModInitializer {
    public static final String MOD_ID = "cyusnowyleaves";
    public static final Logger LOGGER = LoggerFactory.getLogger("CyuSnowyLeaves");

    @Override
    public void onInitializeClient() {
        SnowyLeavesConfig config = SnowyLeavesConfig.load();
        FrostController frostController = new FrostController(config);

        SnowyLeafColorRegistry.register(config, frostController);
        SnowyLeavesDebugHud.register(config, frostController);
        ClientTickEvents.END_CLIENT_TICK.register(frostController::tick);

        LOGGER.info("CyuSnowyLeaves initialized successfully.");
    }
}
