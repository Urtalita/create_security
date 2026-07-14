package org.portality.create_security.blocks.gate;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.portality.create_security.Index.Index;
import org.portality.create_security.blocks.reader.ReaderBlock;
import org.portality.create_security.items.CardItem;

import java.util.List;

public class GateBE extends SmartBlockEntity {
    private float gateRotation;
    private float prevGateRotation;
    public boolean isGateOpen = false;
    public SmartInventory inventory;
    private int openTicks = -1;
    private int savedTier;

    public GateBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new SmartInventory(3, this, (slot, stack) -> {
            return true;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public float getGateRotation(float pt){
        int multiplayer = 90;
        return Mth.lerp(pt, Mth.sin(prevGateRotation * Mth.HALF_PI) * multiplayer, Mth.sin(gateRotation * Mth.HALF_PI) * multiplayer);
    }

    @Override
    public void tick() {
        super.tick();

        if(openTicks >= 0){
            openTicks--;
        }
        if(openTicks < 0 && isGateOpen){
            isGateOpen = false;
        }

        prevGateRotation = gateRotation;
        if(isGateOpen){
            gateRotation = Math.min(gateRotation + 0.1f, 1);
            if(prevGateRotation == 0 && prevGateRotation != gateRotation){
                AllSoundEvents.WRENCH_ROTATE.playOnServer(level, getBlockPos());
            }
        } else {
            gateRotation = Math.max(gateRotation - 0.1f, 0);
            if(gateRotation == 0 && prevGateRotation != gateRotation){
                AllSoundEvents.FROGPORT_CLOSE.playOnServer(level, getBlockPos());
            }
        }

        if(level.getGameTime() % 3 != 0) return;
        if(openTicks >= 0) return;

        List<ItemEntity> neigbourEntityies = level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(0.5f));
        for(ItemEntity entity : neigbourEntityies){
            if(entity.getItem().getItem() instanceof CardItem){
                ItemStack stack = entity.getItem();

                if(isSameCode(stack, inventory, savedTier)) {
                    isGateOpen = true;
                    openTicks = 40;

                    AllSoundEvents.CONFIRM_2.playOnServer(level, getBlockPos());
                    if(stack.getItem() == Index.TICKET.asItem()){
                        stack.shrink(1);
                        entity.setItem(stack);
                    }

                    notifyUpdate();
                    sendData();
                }
            }
        }
    }
    public ItemInteractionResult use(Player player) {
        ItemStack itemInHand = player.getItemInHand(player.getUsedItemHand());
        if(!(itemInHand.getItem() instanceof CardItem)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if(inventory.isEmpty()){
            inventory.setStackInSlot(0, CardItem.getFirstFilter(itemInHand));
            inventory.setStackInSlot(1, CardItem.getSecondFilter(itemInHand));
            inventory.setStackInSlot(2, CardItem.getThirdFilter(itemInHand));
            savedTier = CardItem.getTier(itemInHand);

            if(player instanceof ServerPlayer serverPlayer){
                serverPlayer.sendSystemMessage(Component.literal("Card saved"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }

        if(isSameCode(itemInHand, inventory, savedTier)){
            isGateOpen = !isGateOpen;
            level.setBlockAndUpdate(worldPosition, Index.GATE.getDefaultState()
                    .setValue(GateBlock.FACING,
                            getBlockState().getValue(GateBlock.FACING)));
            openTicks = 40;

            if(itemInHand.getItem() == Index.TICKET.asItem()) itemInHand.shrink(1);
        } else {
            AllSoundEvents.DENY.playOnServer(level, getBlockPos());
        }
        return ItemInteractionResult.SUCCESS;
    }

    static public boolean isSameCode(ItemStack stack, SmartInventory inventory, int savedTier){
        ItemStack first = CardItem.getFirstFilter(stack);
        ItemStack second = CardItem.getSecondFilter(stack);
        ItemStack third = CardItem.getThirdFilter(stack);

        if(CardItem.getTier(stack) < savedTier) return false;
        if(first.getItem() != inventory.getItem(0).getItem()) return false;
        if(second.getItem() != inventory.getItem(1).getItem()) return false;
        if(third.getItem() != inventory.getItem(2).getItem()) return false;
        return true;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));

        isGateOpen = tag.getBoolean("isGateOpen");
        gateRotation = tag.getFloat("gateRotation");
        openTicks = tag.getInt("openTicks");
        savedTier = tag.getInt("tier");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));

        tag.putBoolean("isGateOpen", isGateOpen);
        tag.putFloat("gateRotation", gateRotation);
        tag.putInt("openTicks", openTicks);
        tag.putInt("tier", savedTier);
    }
}
