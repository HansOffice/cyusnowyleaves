package org.cyu.snowyleaves.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.cyu.snowyleaves.fabric.color.SnowyLeafColorRegistry;
import org.cyu.snowyleaves.fabric.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.fabric.frost.FrostController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CyuSnowyLeavesFabric implements ClientModInitializer {
    public static final String MOD_ID = "cyusnowyleaves";
    public static final Logger LOGGER = LoggerFactory.getLogger("CyuSnowyLeaves");

    @Override
    public void onInitializeClient() {
        SnowyLeavesConfig config = SnowyLeavesConfig.load();
        FrostController frostController = new FrostController(config);

        SnowyLeafColorRegistry.register(config, frostController);
        ClientTickEvents.END_CLIENT_TICK.register(client -> frostController.tick());

        LOGGER.info("CyuSnowyLeaves initialized successfully.");
    }
}
