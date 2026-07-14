package org.portality.create_security.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.portality.create_security.Create_security;

public class CSPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Create_security.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CSPonders.register(helper);
    }
}
