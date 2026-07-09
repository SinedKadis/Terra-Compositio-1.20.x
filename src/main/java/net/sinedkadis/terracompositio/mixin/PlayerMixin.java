package net.sinedkadis.terracompositio.mixin;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.registries.TCCapabilities;
import net.sinedkadis.terracompositio.block.custom.ECFBoardBlock;
import net.sinedkadis.terracompositio.config.TCInnerConfig;
import net.sinedkadis.terracompositio.util.IEntityInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Player.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class PlayerMixin extends LivingEntity implements ECFNetworkMember {
    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    boolean scheduleUpdate = false;

    @Override
    public IEntityInstance getEntityInstance() {
        return IEntityInstance.wrap(this);
    }

    @Override
    public void updateIfScheduled() {
        if (scheduleUpdate){
            scheduleUpdate = false;
            onECFNetworkMemberUpdate();
        }
    }

    @Override
    public void scheduleMemberUpdate() {
        scheduleUpdate = true;
    }

    @Override
    public int getRange() {
        return 10;
    }

    @Override
    public int getPriority() {
        return TCInnerConfig.DEFAULT_CONSUMER_PRIORITY;
    }

    @Override
    public IECFHandler getMainHandler() {
        return Optional.ofNullable(this.getCapability(TCCapabilities.ECF_HANDLER_ENTITY)).orElse(SentinelHelper.EMPTY_ECF_HANDLER);
    }

    @Unique
    private boolean technetium$fakeSneak = false;


    @Inject(
            method = "isStayingOnGroundSurface()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void technetium$isOnGround(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        boolean found = false;
        for (BlockPos pos : BlockPos.betweenClosed(self.blockPosition().offset(-1, -1, -1), self.blockPosition().offset(1, -1, 1))) {
            if ((self.level().getBlockState(pos).getBlock() instanceof ECFBoardBlock)) {
                found = true;
                break;
            }
        }
        if (!found) return;

        technetium$fakeSneak = true;
        cir.setReturnValue(true);
    }

    @Inject(
            method = "maybeBackOffFromEdge",
            at = @At("RETURN")
    )
    private void technetium$edgeSafeTail(Vec3 movement, MoverType mover, CallbackInfoReturnable<Vec3> cir) {
        if (technetium$fakeSneak) {
            this.setShiftKeyDown(false);
            technetium$fakeSneak = false;
        }
    }
}
