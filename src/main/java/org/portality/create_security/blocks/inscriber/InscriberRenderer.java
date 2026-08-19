package org.portality.create_security.blocks.inscriber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.portality.create_security.Index.CSPartalModels;
import org.portality.create_security.Index.Index;
import org.portality.create_security.items.BlankCardItem;

public class InscriberRenderer extends KineticBlockEntityRenderer<InscriberBE> {
    public InscriberRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }



    @Override
    protected void renderSafe(InscriberBE be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = getRenderedBlockState(be);
        RenderType type = getRenderType(be, state);
        renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(type), light);

        Direction facing = be.getBlockState().getValue(InscriberBlock.HORIZONTAL_FACING);

        PartialModel top = CSPartalModels.CAP;
        SuperByteBuffer superBuffer = CachedBuffers.partial(top, be.getBlockState());

        superBuffer.rotateCenteredDegrees(90, Direction.Axis.Y);
        superBuffer.rotateCenteredDegrees(-facing.toYRot(), Direction.Axis.Y);
        superBuffer.translate(0, 1/16f, 2/16f);

        Axis axis = Axis.XN;
        Vec3 offset = new Vec3(0, 12/16f, 0);
        float capOffset = 14/16f;

        offset = new Vec3(0, offset.y, capOffset);

        superBuffer.rotateAround(axis.rotationDegrees(-be.getCapRotation(partialTicks)), offset.toVector3f());

        superBuffer.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));

        PartialModel card = CSPartalModels.CARD;
        PartialModel ticket = CSPartalModels.TICKET;

        if(!(be.inventory.getItem(1).getItem() instanceof BlankCardItem)) return;
        if(be.inventory.getItem(1).getItem() == Index.BLANK_TICKET.asItem()) card = ticket;

        SuperByteBuffer superCard = CachedBuffers.partial(card, be.getBlockState());
        superCard.rotateCenteredDegrees(-facing.toYRot(), Direction.Axis.Y);
        superCard.translate(0, 0, -1/16f);
        superCard.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(InscriberBE be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, state
                .getValue(InscriberBlock.HORIZONTAL_FACING).getOpposite()
                .getOpposite());
    }
}
