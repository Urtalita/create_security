package org.portality.create_security;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(value = CreateSecurity.MODID, dist = Dist.CLIENT)
public class CreateSecurityClient {
    public CreateSecurityClient(IEventBus modEventBus) {
        modEventBus.addListener(CreateSecurityClient::onLoadComplete);
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateSecurity.MODID)
                .orElseThrow(() -> new IllegalStateException("Create: Security Program mod container missing on LoadComplete"));
        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateSecurity.MODID);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }
}
