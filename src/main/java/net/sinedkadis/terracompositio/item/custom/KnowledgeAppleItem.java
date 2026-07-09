package net.sinedkadis.terracompositio.item.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sinedkadis.terracompositio.api.helpers.PlayerHelper;
import net.sinedkadis.terracompositio.api.networks.AnyNetworkMember;
import net.sinedkadis.terracompositio.block.entity.TCBlockEntity;
import net.sinedkadis.terracompositio.components.attachments.KnowledgeAttachment;
import net.sinedkadis.terracompositio.config.TCClientConfigs;
import net.sinedkadis.terracompositio.registries.TCAttachments;
import net.sinedkadis.terracompositio.registries.TCItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KnowledgeAppleItem extends Item {
    public KnowledgeAppleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player accessor) {
            KnowledgeAttachment data = accessor.getData(TCAttachments.KNOWLEDGE);
            data.setCreationAcknowledged(stack.is(TCItems.APPLE_OF_KNOWLEDGE));
            accessor.setData(TCAttachments.KNOWLEDGE,data);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 60;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        return super.use(level, player, usedHand);
    }

    public static void rangeVisualisation() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !player.isShiftKeyDown()) return;
        if (!player.getData(TCAttachments.KNOWLEDGE).isCreationAcknowledged()) return;

        ClientLevel level = mc.level;
        if (level == null) return;

        if (!TCClientConfigs.APPLE_RANGE_CIRCLE.get()) return;

        HitResult hitResult = mc.hitResult;
        EntityHitResult entityResult = PlayerHelper.getEntityHitResult(mc, player, level);


        int range = 0;
        BlockPos blockpos = BlockPos.ZERO.atY(-64);

        if (entityResult != null) {
            if ((entityResult.getEntity() instanceof AnyNetworkMember networkMember)) {
                range = networkMember.getRange();
                blockpos = networkMember.getEntityInstance().tc$getBlockPos();
            }
        }

        if (range == 0 && hitResult instanceof BlockHitResult result) {
            blockpos = result.getBlockPos();

            BlockEntity be = level.getBlockEntity(blockpos);
            if (be instanceof AnyNetworkMember networkMember) {
                range = networkMember.getRange();
            } else if (be instanceof TCBlockEntity tcBlockEntity) {
                Optional<AnyNetworkMember> any = tcBlockEntity.getBehaviours().stream()
                        .filter(AnyNetworkMember.class::isInstance)
                        .map(AnyNetworkMember.class::cast)
                        .findAny();
                if (any.isPresent()) {
                    range = any.get().getRange();
                }
            }

        }

        if (range > 0) {
            double radius = range - ((Math.sqrt(2)/2) + 0.1f);
            for (int i = 0; i < 360; i += 15) {
                Vec3 vec3 = new Vec3(1, 0, 0);
                Vec3 rotated = vec3.yRot((float) Math.toRadians(i));
                Vec3 scaled = rotated.scale(radius);
                Vec3 relative = scaled.add(Vec3.atCenterOf(blockpos));
                level.addParticle(ParticleTypes.CLOUD,relative.x(),relative.y(),relative.z(),0,0,0);
            }
        }
    }

}
