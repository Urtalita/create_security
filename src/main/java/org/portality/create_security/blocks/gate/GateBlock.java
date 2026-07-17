package org.portality.create_security.blocks.gate;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.portality.create_security.HitboxHelper;
import org.portality.create_security.Index.Index;

import java.util.concurrent.atomic.AtomicBoolean;

public class GateBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<GateBE> {
    public static final MapCodec<GateBlock> CODEC = simpleCodec(GateBlock::new);

    public GateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends GateBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());

    }

    @Override
    public Class<GateBE> getBlockEntityClass() {
        return GateBE.class;
    }

    @Override
    public BlockEntityType<? extends GateBE> getBlockEntityType() {
        return Index.GATE_BE.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if(facing.getAxis() == Direction.Axis.Z) facing = facing.getOpposite();
        VoxelShape shape = HitboxHelper.calculateNewDierectionalVoxelShape(facing, new Vec3(12, 0, 0), new Vec3(16, 21, 16));

        AtomicBoolean isOpen = new AtomicBoolean(false);
        withBlockEntityDo(level, pos, b -> {
            isOpen.set(!b.isGateOpen);
        });

        if(isOpen.get()){
            VoxelShape another = HitboxHelper.calculateNewDierectionalVoxelShape(facing, new Vec3(0, 0, 3), new Vec3(16, 21, 5));

            shape = Shapes.or(shape, another);
        }

        return shape;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, b -> b.use(player).result());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack p_316304_, BlockState p_316362_, Level level, BlockPos pos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {
        return onBlockEntityUseItemOn(level, pos, b -> b.use(player));
    }
}
