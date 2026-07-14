package org.portality.create_security.network;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.portality.create_security.blocks.inscriber.InscriberBE;

public record InscriberStartUpdate(BlockPos posInWorld) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, InscriberStartUpdate> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            InscriberStartUpdate::posInWorld,
            InscriberStartUpdate::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.INSCRIBER_START_UPDATE;
    }

    @Override
    public void handle(ServerPlayer player) {
        BlockEntity be = player.level().getBlockEntity(posInWorld());
        if(be instanceof InscriberBE inscriberBE){
            inscriberBE.start();
            inscriberBE.sendData();
        }
    }
}
