package org.portality.create_security.blocks.reader;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class CardReaderInventoryWrapper extends ItemHandlerWrapper {
    private final SmartBlockEntity sbe;

    public CardReaderInventoryWrapper(IItemHandlerModifiable wrapped, SmartBlockEntity sbe) {
        super(wrapped);
        this.sbe = sbe;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
