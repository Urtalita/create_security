package org.portality.create_security.blocks.inscriber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ConfigurableButton extends AbstractSimiWidget {
    protected ScreenElement icon;
    protected ScreenElement iconDown;
    protected ScreenElement iconHovered;

    protected Color hoverColour = new Color(1, 1, 1, 0.5f);

    protected ConfigurableButton(int x, int y,  int w, int h, ScreenElement icon, ScreenElement iconDown, ScreenElement iconHovered) {
        super(x, y, w, h);
        this.icon = icon;
        this.iconDown = iconDown;
        this.iconHovered = iconHovered;
    }

    @Override
    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;

            ScreenElement button = !active ? AllGuiTextures.BUTTON_DISABLED
                    : isHovered && AllKeys.isMouseButtonDown(0) ? iconHovered
                    : isHovered ? iconDown
                    : icon;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawBg(graphics, button);
        }
    }

    protected void drawBg(GuiGraphics graphics, ScreenElement button) {
        button.render(graphics, getX(), getY());
    }

    public void setToolTip(Component text) {
        toolTip.clear();
        toolTip.add(text);
    }

    public void setIcon(ScreenElement icon) {
        this.icon = icon;
    }
}
