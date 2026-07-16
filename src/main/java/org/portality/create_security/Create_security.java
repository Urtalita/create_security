package org.portality.create_security;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.portality.create_security.Index.CSMenus;
import org.portality.create_security.Index.Index;
import org.portality.create_security.Index.CSPartalModels;
import org.portality.create_security.items.CardItem;
import org.portality.create_security.network.PacketInit;
import org.portality.create_security.ponder.CSPonderPlugin;
import org.slf4j.Logger;

@Mod(Create_security.MODID)
public class Create_security {
    public static final String MODID = "create_security";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("create_security_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(Index.CARD::asStack)
            .title(Component.translatable("creativetab.create_security_tab"))
            .noScrollBar()
            .build());

    public static final CreateRegistrate CS_REGISTRATE =
            CreateRegistrate.create(MODID)
                    .defaultCreativeTab(MAIN_TAB.getKey())
                    .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                    );

    public Create_security(IEventBus modEventBus, ModContainer modContainer) {
        CS_REGISTRATE.registerEventListeners(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);
        Index.register();
        CSPartalModels.register();
        CSMenus.register();
        PacketInit.register();
        Index.registerAllComponents(modEventBus);

        CSDatagen.addExtraRegistrateData();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static CreateRegistrate registrate() {
        return CS_REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            PonderIndex.addPlugin(new CSPonderPlugin());
        }

        @SubscribeEvent
        public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(CardItem.CardTooltipComponent.class, CardItem.CardSlotRenderer::new);
        }
    }
}
