package org.portality.create_security.blocks.inscriber;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.portality.create_security.Index.GuiTextures;
import org.portality.create_security.network.InscriberClientUpdate;
import org.portality.create_security.network.InscriberStartUpdate;

public class InscriberScreen extends AbstractSimiContainerScreen<InscriberMenu> {
    boolean lock;
    int selectedTier = 1;
    GuiTextures background = GuiTextures.INSCRIBER_MAIN;

    private IconButton tierButton;
    private IconButton confirmButton;
    private IconButton lockButton;
    private ConfigurableButton rightButton;
    private ConfigurableButton leftButton;

    public InscriberScreen(InscriberMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        int x = leftPos;
        int y = topPos;

        if(!lock) GuiTextures.LOCK_LIGHT.render(graphics, x + 9, y + 82 - 6);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        int x = leftPos;
        int y = topPos;

        background.render(graphics, x, y);
        if(!hasSpeed()) GuiTextures.ADD_ROTATION.render(graphics, x, y - GuiTextures.ADD_ROTATION.getHeight());
        else if(!hasResources()) GuiTextures.ADD_RESOURCES.render(graphics, x, y - GuiTextures.ADD_ROTATION.getHeight());
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        tierButton = new IconButton(x + 126, y + 39, 18, 24, GuiTextures.T1);
        tierButton.withCallback(this::setRightButton);
        addRenderableWidget(tierButton);

        confirmButton = new IconButton(x + 153, y + 82, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::confirm);
        addRenderableWidget(confirmButton);

        lockButton = new IconButton(x + 9, y + 82, AllIcons.I_CONFIG_LOCKED);
        lockButton.withCallback(this::lock);
        lockButton.setToolTip(Component.translatable("gui.create_security.inscriber_gui.label_mode_player"));
        addRenderableWidget(lockButton);

        rightButton = new ConfigurableButton(x + 147, y + 40, 12, 18, GuiTextures.RIGHT, GuiTextures.RIGHT_HOVERED, GuiTextures.RIGHT_PRESSED);
        rightButton.withCallback(this::setRightButton);
        addRenderableWidget(rightButton);

        leftButton = new ConfigurableButton(x + 113, y + 40, 12, 18, GuiTextures.LEFT, GuiTextures.LEFT_HOVERED, GuiTextures.LEFT_PRESSED);
        leftButton.withCallback(this::onLeftButton);
        addRenderableWidget(leftButton);

        InscriberBE be = getMenu().contentHolder;
        selectedTier = be.selectedTier;
        onTierChanged();
        lock = be.lock;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int xShift = -168;
        int yShift = -148;

        String lockLabel;

        if(lock) lockLabel = Component.translatable("gui.create_security.inscriber_gui.label_mode_universal").getString();
        else lockLabel = Component.translatable("gui.create_security.inscriber_gui.label_mode_player").getString();
        guiGraphics.drawString(this.font, lockLabel, 203 + xShift, 235 + yShift, -1, false);

        guiGraphics.drawString(this.font, Component.translatable("gui.create_security.inscriber_gui.label_card_inscriber"),
                205 + xShift, 153 + yShift, -9682909, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.create_security.inscriber_gui.label_code"),
                216 + xShift, 177 + yShift, -12500671, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.create_security.inscriber_gui.label_tier"),
                280 + xShift, 177 + yShift, -12500671, false);

        if(!hasSpeed()) guiGraphics.drawString(this.font, Component.translatable("gui.create_security.inscriber_gui.label_no_rotation"),
                197 + xShift, 130 + yShift, -1, false);
        else if(!menu.hasResources())
            guiGraphics.drawString(this.font, Component.translatable("gui.create_security.inscriber_gui.label_add_ressources_to_continue"),
                    209 + xShift, 130 + yShift, -1, false);
    }

    void lock(){
        lock = !lock;
        if(lock) lockButton.setToolTip(Component.translatable("gui.create_security.inscriber_gui.tooltip_ssauniversal_ssr_cards_can_be_rema"));
        else lockButton.setToolTip(Component.translatable("gui.create_security.inscriber_gui.tooltip_sseplayer_ssrencypted_cards_are_on"));
    }

    void confirm(){
        if(hasResources() && hasSpeed()) {
            PacketDistributor.sendToServer(new InscriberStartUpdate(getMenu().contentHolder.getBlockPos()));
            getMenu().start();
        }
        onClose();
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new InscriberClientUpdate(getMenu().contentHolder.getBlockPos(), lock, selectedTier));
    }

    void setRightButton(){
        selectedTier += 1;
        if(selectedTier > 5) selectedTier = 1;
        onTierChanged();
    }

    void onLeftButton(){
        selectedTier -= 1;
        if(selectedTier < 1) selectedTier = 5;
        onTierChanged();
    }

    void onTierChanged(){
        GuiTextures tier = GuiTextures.T1;
        tier = switch (selectedTier) {
            case 2 -> GuiTextures.T2;
            case 3 -> GuiTextures.T3;
            case 4 -> GuiTextures.T4;
            case 5 -> GuiTextures.T5;
            default -> tier;
        };

        tierButton.setIcon(tier);
    }

    boolean hasSpeed(){
        return getMenu().contentHolder.getSpeed() != 0;
    }

    boolean hasResources(){
        return getMenu().hasResources();
    }
}
