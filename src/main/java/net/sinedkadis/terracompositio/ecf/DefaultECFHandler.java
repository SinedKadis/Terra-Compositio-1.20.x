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
    protected ECFNetworkMember attachedMember;
    @Getter
    protected int index = 0;
    @Getter
    protected int ECF;
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

    public DefaultECFHandler(ECFNetworkMember attachedMember) {
        this.attachedMember = attachedMember;
        ECF = 0;
    }

    @Override
    public IEntityInstance getAttachedEntity() {
        return attachedMember.getEntityInstance();
    }

    @Override
    public ECFNetworkMember getAttachedMember() {
        return attachedMember;
    }

    @Override
    public int takeECF(int cfe, TransferAction action) {
        int taken;
        int current = this.getECF();
        if (action.simulate()) {
            taken = Mth.clamp(cfe, 0, current);
        } else
            taken = cfe;
        if (action.execute()) {
            int toSet = Math.max(current - taken, 0);
            this.setECF(toSet);

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
        int current = this.getECF();
        int maxECF = this.getMaxECF();
        if (action.simulate()) {
            int pMax = maxECF - current - this.getQueued();
            added = Mth.clamp(cfe, 0, pMax);
        }
        if (action.execute()) {
            int toSet = Math.min(current + added, maxECF);
            this.setECF(toSet);
            if (getAttachedEntity() instanceof ECFNetworkMember member)
                member.scheduleMemberUpdate();
            onContentsChanged();
        }
        return Math.max(added, 0);
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
        nbt.putInt("ECF", this.getECF());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        int toSet = tag.getInt("ECF");
        this.setECF(toSet);
    }

    public int getMaxECF() {
        return Math.max(this.maxECF, this.getECF());
    }

    public DefaultECFHandler setMaxECF(int max) {
        this.maxECF = max;
        return this;
    }
}
