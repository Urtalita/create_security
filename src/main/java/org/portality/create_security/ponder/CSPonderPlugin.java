package org.portality.create_security.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.portality.create_security.CreateSecurity;

public class CSPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateSecurity.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CSPonders.register(helper);
    }
}
