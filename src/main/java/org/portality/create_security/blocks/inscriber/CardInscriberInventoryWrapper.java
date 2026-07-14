package org.portality.create_security.blocks.inscriber;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.portality.create_security.items.BlankCardItem;

public class CardInscriberInventoryWrapper extends ItemHandlerWrapper {
    private final InscriberBE ibe;

    public CardInscriberInventoryWrapper(IItemHandlerModifiable wrapped, InscriberBE ibe) {
        super(wrapped);
        this.ibe = ibe;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack preview = super.extractItem(slot, 64, true);

        if (slot >= 2)
            return ItemStack.EMPTY;

        return simulate ? preview : super.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot >= 2)
            return stack;

        if(slot == 0){
            if(stack.getItem() != Items.INK_SAC) return stack;
        }

        if(slot == 1){
            if(!(stack.getItem() instanceof BlankCardItem)) return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }
}
