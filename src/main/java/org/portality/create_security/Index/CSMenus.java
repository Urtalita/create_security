package org.portality.create_security.Index;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.portality.create_security.Create_security;
import org.portality.create_security.blocks.inscriber.InscriberMenu;
import org.portality.create_security.blocks.inscriber.InscriberScreen;

public class CSMenus {
    public static final MenuEntry<InscriberMenu> PSE = register("inscriber", InscriberMenu::new, () -> InscriberScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C>
    register(String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Create_security.CS_REGISTRATE.menu(name, factory, screenFactory).register();
    }

    public static void register() {
    }
}
