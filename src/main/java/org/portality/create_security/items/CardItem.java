package org.portality.create_security.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.portality.create_security.Index.Index;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CardItem extends Item {
    public CardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int tier = getTier(stack);

        MutableComponent component = Component.translatable("create_security.tier").withStyle(ChatFormatting.GRAY);
        component.append(Component.literal(String.valueOf(tier)).withStyle(ChatFormatting.AQUA));
        component.append(Component.literal("     "));
        tooltipComponents.add(component);

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new CardTooltipComponent(
                getFirstFilter(stack),
                getSecondFilter(stack),
                getThirdFilter(stack),
                getUUID(stack)
                )
        );
    }

    public static class CardTooltipComponent implements TooltipComponent {
        public final ItemStack firstFilter;
        public final ItemStack secondFilter;
        public final ItemStack thirdFilter;
        public final UUID id;

        public CardTooltipComponent(ItemStack firstFilter, ItemStack secondFilter, ItemStack thirdFilter, UUID id) {
            this.firstFilter = firstFilter;
            this.secondFilter = secondFilter;
            this.thirdFilter = thirdFilter;
            this.id = id;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class CardSlotRenderer implements ClientTooltipComponent {
        private final CardTooltipComponent component;

        public CardSlotRenderer(CardTooltipComponent component) {
            this.component = component;
        }

        @Override
        public int getHeight() {
            if(Minecraft.getInstance().player == null) return 1;
            if(hasMagnifyingGlass(Minecraft.getInstance().player)){
                return 20;
            }
            return 1;
        }

        @Override
        public int getWidth(net.minecraft.client.gui.Font font) {
            return 18;
        }

        @Override
        public void renderImage(net.minecraft.client.gui.Font Font, int x, int y, GuiGraphics GuiGraphics) {
            if((!Minecraft.getInstance().player.getUUID().equals(component.id)) && component.id != null) return;
            if(Minecraft.getInstance().player == null) return;
            if(hasMagnifyingGlass(Minecraft.getInstance().player)){
                GuiGraphics.renderItem(component.firstFilter, x, y + 1);
                GuiGraphics.renderItem(component.secondFilter, x + getWidth(Font), y + 1);
                GuiGraphics.renderItem(component.thirdFilter, x  + getWidth(Font) * 2, y + 1);
            }
        }
    }

    static boolean hasMagnifyingGlass(Player player){
        for (ItemStack stack : player.getInventory().items){
            if(Index.MAGNIFYING_GLASS.isIn(stack)) return true;
        }
        return false;
    }

    public static int getTier(ItemStack stack){
        return stack.getOrDefault(Index.CARD_TIER, 1);
    }

    public static UUID getUUID(ItemStack stack){
        return stack.getOrDefault(Index.PLAYER_ID, null);
    }

    public static ItemStack getFirstFilter(ItemStack stack){
        ItemStack filter = getContainerStack(stack, 0);
        if(filter == ItemStack.EMPTY) filter = new ItemStack(Blocks.BARRIER.asItem());
        return filter;
    }

    public static ItemStack getSecondFilter(ItemStack stack){
        ItemStack filter = getContainerStack(stack, 1);
        if(filter == ItemStack.EMPTY) filter = new ItemStack(Blocks.BARRIER.asItem());
        return filter;
    }

    public static ItemStack getThirdFilter(ItemStack stack){
        ItemStack filter = getContainerStack(stack, 2);
        if(filter == ItemStack.EMPTY) filter = new ItemStack(Blocks.BARRIER.asItem());
        return filter;
    }

    public static ItemStack getContainerStack(ItemStack stack, int index) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        if (contents.equals(ItemContainerContents.EMPTY) || contents.getSlots() == 0) {
            return ItemStack.EMPTY;
        }

        if(index >= contents.getSlots()){
            return ItemStack.EMPTY;
        }

        return contents.getStackInSlot(index);
    }

    public static void setFilters(ItemStack stack, ItemStack first, ItemStack second, ItemStack third) {
        ItemContainerContents contents = ItemContainerContents.fromItems(List.of(first, second, third));
        stack.set(DataComponents.CONTAINER, contents);
    }
}
