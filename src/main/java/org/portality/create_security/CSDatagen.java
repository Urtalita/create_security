package org.portality.create_security;

import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.portality.create_security.ponder.CSPonderPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CSDatagen {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

    }

    public static void addExtraRegistrateData() {
        CreateSecurity.registrate().addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            providePonderLang(langConsumer);
        });
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin(new CSPonderPlugin());

        PonderIndex.getLangAccess().provideLang(CreateSecurity.MODID, consumer);
    }
}
