package org.portality.create_security.blocks;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SmartEncryptedInventory extends SmartInventory {

    public SmartEncryptedInventory(int slots, SyncedBlockEntity be, BiPredicate<Integer, ItemStack> isValid, ArrayList<Integer> encryptedSlots) {
        this(slots, be, 64, false, isValid, encryptedSlots);
    }

    public SmartEncryptedInventory(int slots, SyncedBlockEntity be, int stackSize, boolean stackNonStackables, BiPredicate<Integer, ItemStack> isValid, ArrayList<Integer> encryptedSlots) {
        super(new SyncedEncryptedStackHandler(slots, be, stackNonStackables, stackSize, isValid, encryptedSlots), stackSize, stackNonStackables);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        super.deserializeNBT(registries, nbt);
    }

    public static class SyncedEncryptedStackHandler extends SyncedStackHandler {
        private final ArrayList<Integer> encryptedSlots;
        private static final String encryptedKey = "nice_try_stack_is_encrypted";
        private static final byte byteShift = 100;

        public SyncedEncryptedStackHandler(int slots, SyncedBlockEntity be, boolean stackNonStackables,
                                           int stackSize, BiPredicate<Integer, ItemStack> isValid, ArrayList<Integer> encryptedSlots) {
            super(slots, be, stackNonStackables, stackSize, isValid);
            this.encryptedSlots = encryptedSlots;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag nbt = new CompoundTag();
            ListTag nbtTagList = new ListTag();

            for (int i = 0; i < this.stacks.size(); ++i) {
                ItemStack stack = this.stacks.get(i);
                if (stack.isEmpty()) {
                    continue;
                }

                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);

                if (encryptedSlots.contains(i)) {
                    serializeEncryptedStack(registries, itemTag, stack);
                } else {
                    net.minecraft.nbt.Tag savedItemTag = stack.saveOptional(registries);

                    if (savedItemTag instanceof CompoundTag compoundTag) {
                        compoundTag.putInt("Slot", i);
                        nbtTagList.add(compoundTag);
                    }
                }
                nbtTagList.add(itemTag);
            }

            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", this.stacks.size());
            return nbt;
        }

        public static void serializeEncryptedStack(HolderLookup.Provider provider, CompoundTag itemTag, ItemStack stack) {
            ByteBuf buffer = Unpooled.buffer();
            RegistryFriendlyByteBuf friendlyBuf = new RegistryFriendlyByteBuf(buffer, (RegistryAccess) provider);
            ItemStack.STREAM_CODEC.encode(friendlyBuf, stack);

            byte[] bytes = new byte[friendlyBuf.readableBytes()];
            friendlyBuf.readBytes(bytes);

            bytes = encryptByteArray(bytes);
            itemTag.putByteArray(encryptedKey, bytes);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
            ListTag tagList = nbt.getList("Items", 10);

            for (int i = 0; i < this.stacks.size(); i++) {
                this.stacks.set(i, ItemStack.EMPTY);
            }

            for (int i = 0; i < tagList.size(); ++i) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");

                if (slot >= 0 && slot < this.stacks.size()) {
                    if (encryptedSlots.contains(slot)) {
                        this.stacks.set(slot, deserializeEncryptedStack(provider, itemTags));
                    } else {
                        ItemStack.parse(provider, itemTags).ifPresent((stack) -> this.stacks.set(slot, stack));
                    }
                }
            }

            this.onLoad();
        }

        public static ItemStack deserializeEncryptedStack(HolderLookup.Provider provider, CompoundTag itemTags) {
            if (itemTags.contains(encryptedKey, 7)) { // 7 = ByteArray tag type
                byte[] bytes = decryptByteArray(itemTags.getByteArray(encryptedKey));

                ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
                RegistryFriendlyByteBuf friendlyBuf = new RegistryFriendlyByteBuf(buffer, (RegistryAccess) provider);
                try {
                    ItemStack stack = ItemStack.STREAM_CODEC.decode(friendlyBuf);
                    return stack;
                } catch (Exception e) {
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.EMPTY;
        }

        private static byte[] encryptByteArray(byte[] notEncrypted){
            byte[] output = new byte[notEncrypted.length];
            for(int i = 0; i < notEncrypted.length; i++){
                byte b = notEncrypted[i];
                b += byteShift;
                output[i] = b;
            }
            return output;
        }

        private static byte[] decryptByteArray(byte[] encrypted){
            byte[] output = new byte[encrypted.length];
            for(int i = 0; i < encrypted.length; i++){
                byte b = encrypted[i];
                b -= byteShift;
                output[i] = b;
            }
            return output;
        }
    }
}
