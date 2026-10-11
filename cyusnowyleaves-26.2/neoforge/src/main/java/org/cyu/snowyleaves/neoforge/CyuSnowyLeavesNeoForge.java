package org.cyu.snowyleaves.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.cyu.snowyleaves.neoforge.color.SnowyLeafColorRegistry;
import org.cyu.snowyleaves.neoforge.color.SnowyLeafItemColorRegistry;
import org.cyu.snowyleaves.neoforge.config.SnowyLeavesConfig;
import org.cyu.snowyleaves.neoforge.frost.FrostController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CyuSnowyLeavesNeoForge.MOD_ID)
public final class CyuSnowyLeavesNeoForge {
    public static final String MOD_ID = "cyusnowyleaves";
    public static final Logger LOGGER = LoggerFactory.getLogger("CyuSnowyLeaves");

    public CyuSnowyLeavesNeoForge(IEventBus modEventBus, ModContainer container) {
        if (!FMLEnvironment.getDist().isClient()) {
            LOGGER.warn("CyuSnowyLeaves is a client-only mod and is disabled on dedicated servers.");
            return;
        }
        ClientInitializer.init(modEventBus);
    }

    private static final class ClientInitializer {
        private static void init(IEventBus modEventBus) {
            SnowyLeavesConfig config = SnowyLeavesConfig.load();
            FrostController frostController = new FrostController(config);

            modEventBus.addListener(SnowyLeafColorRegistry.createRegisterListener(config, frostController));
            SnowyLeafItemColorRegistry.register(modEventBus);
            NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> frostController.tick());

            LOGGER.info("CyuSnowyLeaves initialized successfully.");
        }
    }
}
