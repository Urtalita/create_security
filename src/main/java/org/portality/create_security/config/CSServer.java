package org.portality.create_security.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CSServer extends CSConfigBase {
    public final CSStress stressValues = nested(0, CSStress::new, Comments.stress);

    @Override
    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
    }
}
