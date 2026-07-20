package net.sinedkadis.terracompositio.network.payloads;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.helpers.WorldHelper;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.registries.TCBlocks;
import net.sinedkadis.terracompositio.registries.TCCapabilities;
import net.sinedkadis.terracompositio.util.helpers.ParticleHelperInternal;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record C2SBoardSyncPayload(BlockPos pos, boolean place, int ecfToTake,
                                  boolean waterlogged) implements CustomPacketPayload {
    public static final Type<C2SBoardSyncPayload> TYPE =
            new Type<>(TerraCompositio.modLoc("board_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SBoardSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SBoardSyncPayload::pos,
                    ByteBufCodecs.BOOL, C2SBoardSyncPayload::place,
                    ByteBufCodecs.VAR_INT, C2SBoardSyncPayload::ecfToTake,
                    ByteBufCodecs.BOOL, C2SBoardSyncPayload::waterlogged,
                    C2SBoardSyncPayload::new
            );

    public static void handle(C2SBoardSyncPayload payload, IPayloadContext context) {

        ServerPlayer player = ((ServerPlayer) context.player());
        Level level = player.level();

        BlockPos pos = payload.pos();
        BlockPos pPos = new BlockPos(pos);
        WorldHelper.destroyBlockNoUpdate(level, pPos, player);
        if (payload.place()) {
            level.setBlock(pPos,
                    TCBlocks.ECF_BOARD.get().defaultBlockState()
                            .setValue(BlockStateProperties.WATERLOGGED,
                                    payload.waterlogged()),
                    3);
            Optional.ofNullable(player.getItemBySlot(EquipmentSlot.FEET).getCapability(TCCapabilities.ECF_HANDLER_ITEM))
                    .orElse(SentinelHelper.EMPTY_ECF_HANDLER)
                    .takeECF(payload.ecfToTake(), TransferAction.EXECUTE);
            ParticleHelperInternal.spawnParticlesIn(level, pPos);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
