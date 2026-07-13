package net.sinedkadis.terracompositio.ecf;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.registries.TCDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
@MethodsReturnNonnullByDefault
public class ECFItemWrapper implements IECFHandler {
    @Getter
    @Setter
    protected int queued = 0;
    @NotNull
    @Getter
    protected ItemStack container;

    public ECFItemWrapper(@NotNull ItemStack container) {
        this.container = container;
    }

    @Override
    public String toString() {
        return "CFEItemWrapper{" +
                "container=" + container +
                ",\n CFE=" + getECF() +
                ",\n Max CFE=" + getMaxECF() +
                ",\n queued=" + queued +
                '}';
    }

    @Override
    public void clear() {
        setECF(0);
        queued = 0;
    }

    @Override
    public int getECF() {
        Integer ecf = container.get(TCDataComponents.STORED_ECF);
        if (ecf == null) return 0;
        return ecf;
    }

    @Override
    public IECFHandler setIndex(int index) {
        return this;
    }

    @Override
    public IECFHandler setOffset(Function<Vec3, Vec3> offset) {
        return this;
    }

    @Override
    public void setECF(int ecf) {
        container.set(TCDataComponents.STORED_ECF,ecf);
    }

    @Override
    public int takeECF(int cfe, boolean simulate) {
        if (cfe == 0) return 0;
        int cfe1 = this.getECF();
        int toTake = Math.min(cfe, cfe1);
        if (!simulate) {
            this.setECF(cfe1 - toTake);
        }
        return toTake;
    }

    @Override
    public int sendECF(ECFNetworkMember target, int cfe, float speed) {
        return 0;
    }

    @Override
    public int addECF(int cfe, boolean simulate) {
        int toAdd = Math.min(this.getFreeSpace(),cfe);
        if (!simulate) {
            int cfe1 = this.getECF();
            this.setECF(cfe1 + toAdd);
        }
        return toAdd;
    }

    @Override
    public int getMaxECF() {
        Integer i = container.get(TCDataComponents.MAX_ECF);
        if (i == null) return 8;
        return i;
    }

    @Override
    public IECFHandler setMaxECF(int max) {
        container.set(TCDataComponents.MAX_ECF,Math.max(0,max));
        return this;
    }

    @Override
    public void writeToNBT(HolderLookup.Provider provider, CompoundTag pTag) {

    }

    @Override
    public void readFromNBT(HolderLookup.Provider provider, CompoundTag pTag) {

    }

    @Override
    public int getECFWithQueue() {
        return getECF() + getQueued();
    }

    @Override
    public boolean isEmpty() {
        return getECFWithQueue() <= 0;
    }

    @Override
    public int getFreeSpace() {
        return this.getMaxECF() - this.getECFWithQueue();
    }

    @Override
    public IEntityInstance getAttachedEntity() {
        return null;
    }

    @Override
    public Function<Vec3, Vec3> getOffset() {
        return t->t;
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
