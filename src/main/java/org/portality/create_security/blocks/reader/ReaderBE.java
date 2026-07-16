package org.portality.create_security.blocks.reader;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.portality.create_security.Index.Index;
import org.portality.create_security.blocks.SmartEncryptedInventory;
import org.portality.create_security.blocks.inscriber.InscriberBlock;
import org.portality.create_security.items.CardItem;

import java.util.ArrayList;
import java.util.List;

import static org.portality.create_security.blocks.gate.GateBE.isSameCode;

public class ReaderBE extends SmartBlockEntity {
    public SmartEncryptedInventory inventory;
    private int openTicks = -1;
    public boolean hasRedstoneSignal = false;
    protected IItemHandler itemHandler;
    private int savedTier;

    public ReaderBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);

        inventory = new SmartEncryptedInventory(3, this, (slot, stack) -> {
            return true;
        }, list);

        itemHandler = new CardReaderInventoryWrapper(inventory, this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();

        if(openTicks >= 0){
            openTicks--;
        }

        if(openTicks < 0 && hasRedstoneSignal){
            hasRedstoneSignal = false;
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            level.updateNeighborsAt(worldPosition.relative(getBlockState().getValue(ReaderBlock.FACING)), getBlockState().getBlock());
            level.setBlockAndUpdate(worldPosition, Index.READER.getDefaultState()
                    .setValue(ReaderBlock.FACING, getBlockState().getValue(ReaderBlock.FACING))
                    .setValue(ReaderBlock.POWERED, false)
            );
            notifyUpdate();
            sendData();
        }

        if(level.getGameTime() % 3 != 0) return;
        if(openTicks >= 0) return;

        List<ItemEntity> neigbourEntityies = level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(0.5f));
        for(ItemEntity entity : neigbourEntityies){
            if(entity.getItem().getItem() instanceof CardItem){
                ItemStack stack = entity.getItem();

                if(isSameCode(stack, inventory, level, savedTier)) {
                    hasRedstoneSignal = true;
                    openTicks = 40;
                    level.setBlockAndUpdate(worldPosition, Index.READER.getDefaultState()
                            .setValue(ReaderBlock.FACING, getBlockState().getValue(ReaderBlock.FACING))
                            .setValue(ReaderBlock.POWERED, true)
                    );
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
                    level.updateNeighborsAt(worldPosition.relative(getBlockState().getValue(ReaderBlock.FACING)), getBlockState().getBlock());

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
            inventory.setStackInSlot(0, CardItem.getFirstFilter(itemInHand, level));
            inventory.setStackInSlot(1, CardItem.getSecondFilter(itemInHand, level));
            inventory.setStackInSlot(2, CardItem.getThirdFilter(itemInHand, level));
            savedTier = CardItem.getTier(itemInHand);

            if(player instanceof ServerPlayer serverPlayer){
                serverPlayer.sendSystemMessage(Component.literal("Card saved"), true);
            }

            AllSoundEvents.CONFIRM.playOnServer(level, getBlockPos());
            return ItemInteractionResult.SUCCESS;
        }

        if(isSameCode(itemInHand, inventory,level , savedTier)){
            hasRedstoneSignal = true;
            openTicks = 40;
            level.setBlockAndUpdate(worldPosition, Index.READER.getDefaultState()
                    .setValue(ReaderBlock.FACING, getBlockState().getValue(ReaderBlock.FACING))
                    .setValue(ReaderBlock.POWERED, true)
            );
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            level.updateNeighborsAt(worldPosition.relative(getBlockState().getValue(ReaderBlock.FACING)), getBlockState().getBlock());

            AllSoundEvents.CONFIRM_2.playOnServer(level, getBlockPos());

            if(itemInHand.getItem() == Index.TICKET.asItem()) itemInHand.shrink(1);

            notifyUpdate();
            sendData();
        } else {
            AllSoundEvents.DENY.playOnServer(level, getBlockPos());
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        hasRedstoneSignal = tag.getBoolean("isGateOpen");
        openTicks = tag.getInt("openTicks");
        savedTier = tag.getInt("tier");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putBoolean("isGateOpen", hasRedstoneSignal);
        tag.putInt("openTicks", openTicks);
        tag.putInt("tier", savedTier);
    }
}
