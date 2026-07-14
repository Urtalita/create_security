package org.portality.create_security.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.portality.create_security.Index.Index;

public class CSPonders {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {

        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(Index.GATE).addStoryBoard("gate",
                CSPonderScenes::gate, AllCreatePonderTags.TRAIN_RELATED);

        HELPER.forComponents(Index.READER).addStoryBoard("card_reader",
                CSPonderScenes::reader, AllCreatePonderTags.REDSTONE);

        HELPER.forComponents(Index.INSCRIBER).addStoryBoard("inscriber",
                CSPonderScenes::inscriber, AllCreatePonderTags.REDSTONE);
    }
}
