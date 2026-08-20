package org.portality.create_security.Index;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.portality.create_security.CreateSecurity;
import net.createmod.catnip.theme.Color;

public enum GuiTextures implements ScreenElement, TextureSheetSegment {
    INSCRIBER_MAIN("inscriber", 256, 256),
    LOCK_LIGHT("button/locklight",  18, 6),

    T1("tier/tier1", 18, 24),
    T2("tier/tier2", 18, 24),
    T3("tier/tier3", 18, 24),
    T4("tier/tier4", 18, 24),
    T5("tier/tier5", 18, 24),

    RIGHT("button/right", 12, 18),
    RIGHT_HOVERED("button/right",0, 18, 12, 18),
    RIGHT_PRESSED("button/right",0, 36, 12, 18),

    LEFT("button/left", 12, 18),
    LEFT_HOVERED("button/left", 0, 18, 12, 18),
    LEFT_PRESSED("button/left", 0, 36, 12, 18),

    ADD_RESOURCES("helper/add_ressources", 179, 21),
    ADD_ROTATION("helper/add_rotation", 179, 21),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;

    GuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    GuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateSecurity.MODID, location, startX, startY, width, height);
    }

    GuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
