package org.portality.create_security.network;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.portality.create_security.blocks.inscriber.InscriberBE;

public record InscriberClientUpdate(BlockPos posInWorld, boolean lock, int tier) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, InscriberClientUpdate> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            InscriberClientUpdate::posInWorld,
            ByteBufCodecs.BOOL,
            InscriberClientUpdate::lock,
            ByteBufCodecs.INT,
            InscriberClientUpdate::tier,
            InscriberClientUpdate::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.INSCRIBER_CLIENT_UPDATE;
    }

    @Override
    public void handle(ServerPlayer player) {
        BlockEntity be = player.level().getBlockEntity(posInWorld());
        if(be instanceof InscriberBE inscriberBE){
            inscriberBE.selectedTier = tier();
            inscriberBE.lock = lock();
            inscriberBE.sendData();
        }
    }
}
