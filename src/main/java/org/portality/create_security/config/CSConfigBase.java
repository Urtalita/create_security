package org.portality.create_security.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class CSConfigBase extends ConfigBase {
    public abstract class CIDLXConfigBase extends ConfigBase {
        @Override public void registerAll(final ModConfigSpec.Builder builder) { super.registerAll(builder); }
    }
}
