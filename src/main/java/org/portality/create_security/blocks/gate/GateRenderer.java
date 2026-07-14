package org.portality.create_security.blocks.gate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.portality.create_security.Index.CSPartalModels;
import org.portality.create_security.blocks.inscriber.InscriberBlock;

public class GateRenderer extends SmartBlockEntityRenderer<GateBE> {
    public GateRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(GateBE be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        PartialModel gate = CSPartalModels.GATE;
        SuperByteBuffer superBuffer = CachedBuffers.partial(gate, be.getBlockState());

        Direction facing = be.getBlockState().getValue(GateBlock.FACING).getOpposite();
        superBuffer.rotateCenteredDegrees(-facing.toYRot(), Direction.Axis.Y);

        superBuffer.rotateAround(Axis.ZN.rotationDegrees(be.getGateRotation(partialTicks)), 3/16f, 14/16f, 0);

        superBuffer.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
