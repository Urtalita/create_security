package org.portality.create_security.blocks.inscriber;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import org.portality.create_security.Index.CSSounds;
import org.portality.create_security.Index.Index;
import org.portality.create_security.blocks.SmartEncryptedInventory;
import org.portality.create_security.items.BlankCardItem;
import org.portality.create_security.items.CardItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InscriberBE extends KineticBlockEntity implements MenuProvider {

    private final CardInscriberInventoryWrapper itemHandler;
    private float capRotation;
    private float prevCapRotation;
    public boolean isCapOpen = false;

    public SmartEncryptedInventory inventory;

    public int selectedTier = 1;
    public boolean lock = false;

    private int multiplyer = 45;

    public int processingTicks = -1;
    private boolean procesingCard = true;

    private static int lenght = 20 * 256;

    public final static int animationLength = 6;

    UUID tempId;

    protected AnimatedContainerBehaviour<InscriberMenu> openTracker;

    public InscriberBE(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);

        inventory = new SmartEncryptedInventory(5, this, (slot, stack) -> {
            if(slot == 0){
                return stack.getItem() == Items.INK_SAC;
            }
            if(slot == 1){
                return stack.getItem() == Index.BLANK_CARD.asItem() || stack.getItem() == Index.BLANK_TICKET.asItem();
            }
            return true;
        }, list);
        itemHandler = new CardInscriberInventoryWrapper(inventory, this);
    }

    private int getLenght(){
        return (int) (lenght / Math.abs(getTheoreticalSpeed()));
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Index.INSCRIBER_BE.get(),
                (be, context) -> be.itemHandler
        );
    }

    public float getCapRotation(float pt){
        return Mth.lerp(pt, Mth.sin(prevCapRotation * Mth.HALF_PI) * multiplyer, Mth.sin(capRotation * Mth.HALF_PI) * multiplyer);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return InscriberMenu.create(pContainerId, pPlayerInventory, this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(openTracker = new AnimatedContainerBehaviour<>(this, InscriberMenu.class));
        openTracker.onOpenChanged(this::onOpenChange);
    }

    void onOpenChange(boolean open){
        isCapOpen = open;
        sendData();
        multiplyer = 45;
    }

    public ItemInteractionResult use(Player player) {
        if (player == null || player.isCrouching())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (player instanceof FakePlayer)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        tempId = player.getUUID();

        player.openMenu(this, worldPosition);
        return ItemInteractionResult.SUCCESS;
    }

    public void start() {
        if(processingTicks >= 0) return;

        if(inventory.getItem(0).getItem() != Items.INK_SAC) return;
        if(!(inventory.getItem(1).getItem() instanceof BlankCardItem)) return;
        processingTicks = 0;

        procesingCard = (inventory.getItem(1).getItem() == Index.BLANK_CARD.asItem());

        inventory.getItem(0).shrink(1);
        inventory.getItem(1).shrink(1);

        sendData();
        notifyUpdate();
    }

    @Override
    public void tick() {
        super.tick();

        if(processingTicks > getLenght()){
            processingTicks = -1;
            isCapOpen = false;

            ItemStack stack = Index.CARD.asStack();
            if(!procesingCard) stack = Index.TICKET.asStack();

            stack.set(Index.CARD_TIER, selectedTier);
            if(!lock) if (tempId != null) stack.set(Index.PLAYER_ID, tempId);
            CardItem.setFilters(stack, inventory.getItem(2), inventory.getItem(3), inventory.getItem(4), level);

            if(hasOutput(stack)){
                sendData();
                return;
            }

            Direction facing = getBlockState().getValue(InscriberBlock.HORIZONTAL_FACING).getClockWise();
            Vec3 movementVector = new Vec3(facing.getNormal().getX(), 1.5f, facing.getNormal().getZ()).scale(0.5);
            ItemEntity itemEntity = new ItemEntity(level,
                    getBlockPos().getX() + 0.5f + movementVector.x,
                    getBlockPos().getY() + 0.5f + movementVector.y,
                    getBlockPos().getZ() + 0.5f + movementVector.z,
                    stack);
            itemEntity.setDeltaMovement(movementVector.scale(0.5f));
            level.addFreshEntity(itemEntity);

            level.playSound(null, getBlockPos(),
                    SoundEvents.DISPENSER_LAUNCH,
                    SoundSource.NEUTRAL, 0.8f, 1F);

            sendData();

            if(level.getBestNeighborSignal(worldPosition) == 0){
                start();
            }
        }
        if(processingTicks >= 0){
            if(Math.abs(getTheoreticalSpeed()) == 0) processingTicks--;
            processingTicks++;
            if(processingTicks > getLenght() - animationLength){
                isCapOpen = true;
                multiplyer = 135;
            }

            sendData();
        }

        if(processingTicks <= 0){
            int signal = level.getBestNeighborSignal(worldPosition);
            if(signal == 0) start();
        }

        prevCapRotation = capRotation;
        if(isCapOpen){
            capRotation = Math.min(capRotation + (1f / animationLength), 1);
            if(prevCapRotation == 0 && prevCapRotation != capRotation){
                AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, getBlockPos());
            }
        } else {
            capRotation = Math.max(capRotation - (1f / animationLength), 0);
            if(capRotation == 0 && prevCapRotation != capRotation){
                AllSoundEvents.FROGPORT_CLOSE.playOnServer(level, getBlockPos());
            }
        }
    }

    @Override
    public float calculateStressApplied() {
        float impact = 4f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        isCapOpen = getSpeed() != 0;
        multiplyer = 45;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public void destroy() {
        super.destroy();
        for (int i = 0; i < inventory.getSlots(); i++)
            drop(inventory.getStackInSlot(i));
    }

    public void drop(ItemStack box) {
        Block.popResource(level, worldPosition, box);
    }

    private boolean hasOutput(ItemStack card){
        for(Direction facing : Direction.values()){
            IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.relative(facing), facing.getOpposite());
            if(cap == null) continue;

            for(int i = 0; i < cap.getSlots(); i++){
                ItemStack leftStack = cap.insertItem(i, card, false);
                if(leftStack == ItemStack.EMPTY) return true;
            }
        }

        return false;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));

        isCapOpen = compound.getBoolean("open");
        selectedTier = compound.getInt("tier");
        lock = compound.getBoolean("lock");
        processingTicks = compound.getInt("PTicks");
        multiplyer = compound.getInt("multiplayer");
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Inventory", inventory.serializeNBT(registries));

        compound.putBoolean("open", isCapOpen);
        compound.putInt("tier", selectedTier);
        compound.putBoolean("lock", lock);
        compound.putInt("PTicks", processingTicks);
        compound.putInt("multiplayer", multiplyer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if(processingTicks % 40 == 1){
            float x = worldPosition.getX() + .5f;
            float y = worldPosition.getY() + .5f;
            float z = worldPosition.getZ() + .5f;

            level.playLocalSound(x, y, z, CSSounds.INSCRIBE.get(), SoundSource.BLOCKS, 0.6f,
                    1f, true);
        }
    }
}
