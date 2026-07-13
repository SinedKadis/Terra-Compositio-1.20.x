package net.sinedkadis.terracompositio.ecf;


import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.network.TCPackets;
import net.sinedkadis.terracompositio.network.packets.S2CPlayerEcfContainerSync;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@Setter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DefaultECFHandler implements IECFHandler, INBTSerializable<CompoundTag> {
    protected IEntityInstance attachedMember;
    @Getter
    protected int index = 0;
    @Getter
    protected int ECF = 0;
    protected int maxECF = 64;
    @Getter
    protected Function<Vec3, Vec3> offset = t -> t;
    protected int queued = 0;

    @Override
    public String toString() {
        return "CFEContainer{" +
                // "\n attachedMember=" + attachedMember +
                ",\n CFE=" + ECF +
                ",\n maxCFE=" + maxECF +
                ",\n queued=" + queued +
                ",\n index=" + index +
                '}';
    }

    @Override
    public void clear() {
        ECF = 0;
        queued = 0;
    }

    public DefaultECFHandler(IEntityInstance attachedMember) {
        this.attachedMember = attachedMember;
    }

    @Override
    public IEntityInstance getAttachedEntity() {
        return attachedMember;
    }

    @Override
    public int takeECF(int cfe, TransferAction action) {
        int taken = cfe;
        if (action.simulate()) {
            taken = Mth.clamp(cfe, 0, this.getECF());
        }
        if (action.execute()) {
            this.setECF(this.getECF() - taken);

            sendCFEUpdate();
            onContentsChanged();
        }
        return taken;
    }

    @Override
    public IECFHandler setIndex(int index) {
        this.index = index;
        return this;
    }

    public IECFHandler setOffset(Function<Vec3, Vec3> offset) {
        this.offset = offset;
        return this;
    }

    public int addECF(int cfe, TransferAction action) {
        int added = cfe;
        if (action.simulate()) {
            int pMax = getMaxECF() - this.getECF();
            added = Mth.clamp(cfe, 0, pMax);
        }
        if (action.execute()) {
            this.setECF(Math.min(this.getECF() + added, this.getMaxECF()));
            if (getAttachedEntity() instanceof ECFNetworkMember member)
                member.scheduleMemberUpdate();
            onContentsChanged();
        }
        return added;
    }

    protected void sendCFEUpdate() {
        if (getAttachedEntity() instanceof ECFNetworkMember member) {
            TerraCompositioAPI.INSTANCE.getECFNetworkInstance().fireECFNetworkEvent(member, NetworkAction.UPDATE);
            if (getAttachedEntity() instanceof ServerPlayer serverPlayer) {
                TCPackets.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new S2CPlayerEcfContainerSync(this.getECF()));
            }
        }
    }


    protected void onContentsChanged() {
        if (getAttachedEntity() instanceof BlockEntity blockEntity) {
            blockEntity.setChanged();
            Level level = blockEntity.getLevel();
            if (level != null && !level.isClientSide()) {
                BlockState blockState = blockEntity.getBlockState();
                level.sendBlockUpdated(blockEntity.getBlockPos(), blockState, blockState, 3);
            }
        }
    }

    @Override
    public int getECFWithQueue() {
        return this.getECF() + getQueued();
    }

    public void writeToNBT(CompoundTag pTag) {
        pTag.put("cfeContainer_" + getIndex(), this.serializeNBT());
    }

    public void readFromNBT(CompoundTag pTag) {
        CompoundTag tag = pTag.getCompound("cfeContainer_" + getIndex());
        this.deserializeNBT(tag);
    }

    @Override
    public int getQueued() {
        return queued;
    }

    public boolean isEmpty() {
        return !(this.getECF() + getQueued() > 0);
    }

    @Override
    public int getFreeSpace() {
        return getMaxECF() - (this.getECF() + getQueued());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("CFE", this.getECF());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.setECF(tag.getInt("CFE"));
    }

    public int getMaxECF() {
        return Math.max(this.maxECF, this.ECF);
    }

    public DefaultECFHandler setMaxECF(int max) {
        this.maxECF = max;
        return this;
    }
}
