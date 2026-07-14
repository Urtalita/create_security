package org.portality.create_security.blocks.inscriber;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.portality.create_security.Index.CSMenus;
import org.portality.create_security.Index.Index;

import java.util.Arrays;

public class InscriberMenu extends MenuBase<InscriberBE> {

    private FilterSlot InkSlot;
    private FilterSlot CardSlot;

    private Slot typeSlot1;
    private Slot typeSlot2;
    private Slot typeSlot3;

    public InscriberMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public InscriberMenu(MenuType<?> type, int id, Inventory inv, InscriberBE be) {
        super(type, id, inv, be);
        BlockEntityBehaviour.get(be, AnimatedContainerBehaviour.TYPE)
                .startOpen(player);
    }

    @Override
    protected InscriberBE createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof InscriberBE ibe)
            return ibe;
        return null;
    }

    public static InscriberMenu create(int id, Inventory inv, InscriberBE be) {
        return new InscriberMenu(CSMenus.PSE.get(), id, inv, be);
    }


    @Override
    protected void initAndReadInventory(InscriberBE contentHolder) {

    }

    @Override
    protected void addSlots() {
        int invX = 9;
        int invY = 109;

        int xShift = 167;
        int yShift = 148;

        SmartInventory inv = contentHolder.inventory;

        InkSlot = new FilterSlot(inv, 0, 177 - xShift, 174 - yShift, new Item[]{Items.INK_SAC});
        this.addSlot(InkSlot);

        CardSlot = new FilterSlot(inv, 1, 177 - xShift, 192 - yShift, new Item[]{Index.BLANK_CARD.asItem(), Index.BLANK_TICKET.asItem()});
        this.addSlot(CardSlot);

        typeSlot1 = new SlotItemHandler(inv, 2, 217 - xShift, 189 - yShift);
        typeSlot2 = new SlotItemHandler(inv, 3, 235 - xShift, 189 - yShift);
        typeSlot3 = new SlotItemHandler(inv, 4, 253 - xShift, 189 - yShift);

        this.addSlot(typeSlot1);
        this.addSlot(typeSlot2);
        this.addSlot(typeSlot3);

        this.addPlayerSlots(invX, invY);
    }

    @Override
    protected void saveData(InscriberBE contentHolder) {

    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        //Create mod implementation based on chest logic

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        // we need to copy the stack here since it may be modified by moveItemStackTo, but the
        // stack may be taken directly from a SlotItemHandler, which just defers to an IItemHandler.
        // modifying the original stack would violate the class's contract and cause problems.
        ItemStack stack = slot.getItem().copy();
        // we return the stack that was moved out of the slot, so make a copy of that now too.
        ItemStack moved = stack.copy();

        int size = 5;
        if (index < size) {
            // move into player inventory
            if (!this.moveItemStackTo(stack, size, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // move into port inventory
            if (!this.moveItemStackTo(stack, 0, size, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            // setByPlayer instead of just setChanged, since we made a copy
            // setByPlayer instead of set because, I don't know, that's what the other branch does
            slot.setByPlayer(stack.copy());
        }

        return moved;
    }

    public boolean hasResources(){
        return InkSlot.hasFilter() && CardSlot.hasFilter();
    }

    public void start(){
        ItemStack ink = InkSlot.getItem();
        ink.shrink(1);
        InkSlot.set(ink);

        ItemStack card = CardSlot.getItem();
        card.shrink(1);
        CardSlot.set(card);
    }

    private static class FilterSlot extends SlotItemHandler{
        Item[] filters;

        public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item[] filters) {
            super(itemHandler, index, xPosition, yPosition);
            this.filters = filters;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            boolean isValid = super.mayPlace(stack);
            boolean isFilter = hasFilter(stack);
            return isValid && isFilter;
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            super.setByPlayer(stack);
        }

        public boolean hasFilter(){
            return hasFilter(getItem());
        }

        public boolean hasFilter(ItemStack stack){
            for(Item item : filters){
                if(item == stack.getItem()) return true;
            }
            return false;
        }
    }
}
