package org.portality.create_security;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.portality.create_security.blocks.inscriber.InscriberBE;

@EventBusSubscriber
public class CommonEvents {
    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        InscriberBE.registerCapabilities(event);
    }
}
