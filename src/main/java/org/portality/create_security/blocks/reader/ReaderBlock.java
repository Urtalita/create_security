package org.portality.create_security.blocks.reader;

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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.portality.create_security.HitboxHelper;
import org.portality.create_security.Index.Index;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<ReaderBE> {
    public static final MapCodec<ReaderBlock> CODEC = simpleCodec(ReaderBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ReaderBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(POWERED, false)
                ;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<ReaderBE> getBlockEntityClass() {
        return ReaderBE.class;
    }

    @Override
    public BlockEntityType<? extends ReaderBE> getBlockEntityType() {
        return Index.READER_BE.get();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, b -> b.use(player).result());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack p_316304_, BlockState p_316362_, Level level, BlockPos pos, Player player, InteractionHand p_316595_, BlockHitResult p_316140_) {
        return onBlockEntityUseItemOn(level, pos, b -> b.use(player));
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        AtomicBoolean hasCard = new AtomicBoolean(false);
        withBlockEntityDo(level, pos, b -> hasCard.set(b.hasRedstoneSignal));
        if(hasCard.get()) return 15;

        return 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        AtomicBoolean hasCard = new AtomicBoolean(false);
        withBlockEntityDo(level, pos, b -> hasCard.set(b.hasRedstoneSignal));
        if(hasCard.get()) return 15;

        return 0;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if(facing.getAxis() == Direction.Axis.Z) facing = facing.getOpposite();
        VoxelShape shape = HitboxHelper.calculateDierectionalVoxelShape(facing, new Vec3(0, 0, 9), new Vec3(16, 16, 16));
        VoxelShape shape2 = HitboxHelper.calculateDierectionalVoxelShape(facing, new Vec3(0, 0, 4), new Vec3(16, 11, 9));
        VoxelShape another = HitboxHelper.calculateDierectionalVoxelShape(facing, new Vec3(0, 0, 0), new Vec3(16, 1, 16));
        VoxelShape side = HitboxHelper.calculateDierectionalVoxelShape(facing, new Vec3(0, 0, 0), new Vec3(2, 16, 16));
        VoxelShape side2 = HitboxHelper.calculateDierectionalVoxelShape(facing, new Vec3(14, 0, 0), new Vec3(16, 16, 16));

        shape = Shapes.or(shape, another);
        shape = Shapes.or(shape, side);
        shape = Shapes.or(shape, side2);
        shape = Shapes.or(shape, shape2);

        return shape;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }
}
