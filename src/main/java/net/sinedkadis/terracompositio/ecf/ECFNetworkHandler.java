package net.sinedkadis.terracompositio.ecf;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sinedkadis.terracompositio.api.IEntityInstance;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.helpers.SentinelHelper;
import net.sinedkadis.terracompositio.api.networks.AnyNetworkMember;
import net.sinedkadis.terracompositio.api.networks.NetworkAction;
import net.sinedkadis.terracompositio.api.networks.TransferAction;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetwork;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;
import net.sinedkadis.terracompositio.block.entity.PathPointerBlockEntity;
import net.sinedkadis.terracompositio.ecf.burst.ECFBurstProjectileEntity;
import net.sinedkadis.terracompositio.entity.custom.ECFCloudEntity;
import net.sinedkadis.terracompositio.events.ECFNetworkEvent;

import java.util.*;
import java.util.function.IntBinaryOperator;

@Mod.EventBusSubscriber(modid = TerraCompositioAPI.MOD_ID)
public class ECFNetworkHandler implements ECFNetwork {
    public static final ECFNetworkHandler INSTANCE = new ECFNetworkHandler();
    private final Map<Level, Set<ECFNetworkMember>> ecfSources = new WeakHashMap<>();


    static Queue<Pair<Long, Runnable>> scheduledDeliveries = new LinkedList<>();

    @SubscribeEvent
    public static void onLevelTickEvent(TickEvent.LevelTickEvent event) {
        long gameTime = event.level.getGameTime();
        if (gameTime % 10 != 0) return;
        Queue<Pair<Long, Runnable>> skipped = new LinkedList<>();
        while (!scheduledDeliveries.isEmpty()) {
            Pair<Long, Runnable> poll = scheduledDeliveries.poll();
            if (poll.getFirst() < gameTime) {
                poll.getSecond().run();
            } else {
                skipped.add(poll);
            }
        }
        scheduledDeliveries.addAll(skipped);
    }

    @Override
    public boolean validateMember(AnyNetworkMember target) {
        if (target == null) return false;

        if (target instanceof PPECFMemberProxy inst) {
            PathPointerBlockEntity proxy = inst.proxy();
            if (proxy == null) return false;
            ECFNetworkMember trueTarget = inst.target();
            if (proxy.parts.contains(PathPointerBlockEntity.PPPart.COLLECTOR)
                    || proxy.parts.contains(PathPointerBlockEntity.PPPart.EXTRACTOR)) {
                if (proxy.getOutputPos() == null) return false;
            }
            target = trueTarget;
        }

        if (target.getEntityInstance() instanceof BlockEntity memberBE) {
            return !memberBE.isRemoved();
        }
        if (target.getEntityInstance() instanceof Entity memberEntity) {
            return !memberEntity.isRemoved();
        }
        return false;
    }

    @Override
    public boolean validateRelation(ECFNetworkMember source, ECFNetworkMember target, IntBinaryOperator distanceOp) {
        if (target.getEntityInstance().equals(source.getEntityInstance())) return false;
        if (!validateMember(source)) return false;
        if (!validateMember(target)) return false;
        if (!(source.getPriority() < target.getPriority())) return false;
        if (target.getEntityInstance().tc$isEntity()
                && source instanceof PathPointerBlockEntity pp
                && !pp.parts.contains(PathPointerBlockEntity.PPPart.INFUSER)) return false;
        if (target.getEntityInstance().tc$isBlock()
                && source instanceof PathPointerBlockEntity pp
                && !pp.parts.contains(PathPointerBlockEntity.PPPart.EMITTER)) return false;


        return source.getEntityInstance().tc$getPosition()
                .closerThan(
                        target.getEntityInstance().tc$getPosition(),
                        distanceOp.applyAsInt(source.getRange(), target.getRange())
                );
    }

    /**
     * Tries to make transfer between two members.
     * If blocks are close enough, just takes ECF from source and adds it to target, else sends it like burst
     *
     * @param target the target member. Used for navigation in sending burst, actually receives {@link IECFHandler#getMainHandler()},
     *               so passing {@link IECFHandler} like argument only make sense when two blocks are close enough
     * @param source the source member. {@link IECFHandler} can be used as argument
     * @param speed  the speed of th burst, 1 means 1 block per tick
     */
    @Override
    public void executeECFTransfer(ECFNetworkMember target,
                                   ECFNetworkMember source,
                                   float speed) {
        if (!validateRelation(source, target, Math::max)) return;


        IECFHandler sourceMainHandler = source.getMainHandler();
        int taken = sourceMainHandler.takeECF(Integer.MAX_VALUE, TransferAction.SIMULATE);
        IECFHandler targetMainHandler = target.getMainHandler();
        int added = targetMainHandler.addECF(taken, TransferAction.SIMULATE);

        if (added > 0) {
            sourceMainHandler.takeECF(added, TransferAction.EXECUTE);
            target.getMainHandler().addToQueue(added);
            int divisions = Mth.log2(added);
            if (divisions <= 0) divisions = 1;
            int[] additions = new int[divisions];
            int toAdd = added / divisions;
            Arrays.fill(additions, toAdd);
            additions[0] += added % divisions;

            Level level = source.getEntityInstance().tc$getLevel();
            MinecraftServer server = level.getServer();
            if (server != null) {

                for (int i = 0; i < additions.length; i++) {
                    int addition = additions[i];

                    scheduledDeliveries.add(new Pair<>((level.getGameTime() + (i * 10L)), () -> sendBurst(sourceMainHandler, target, addition, speed)));
                }
            }
        }
    }

    @Override
    public void sendBurst(IECFHandler source, ECFNetworkMember target, int count, float speed) {
        Level level = target.getEntityInstance().tc$getLevel();

        IECFHandler targetMainHandler = target.getMainHandler();
        if (closeAndAllow(source, targetMainHandler)) {
            targetMainHandler.addECF(count, TransferAction.EXECUTE);
            targetMainHandler.subFromQueue(count);
            return;
        }

        Vec3 offset = Vec3.ZERO;
        if (source.getAttachedEntity() instanceof ECFCloudEntity ecfCloudEntity) {
            offset = ecfCloudEntity.getBurstOffset(target.getMainHandler());
        }

        ECFBurstProjectileEntity entity = ECFBurstProjectileEntity.sendBurst(source, offset, target, count, speed);
        level.addFreshEntity(entity);
    }

    private boolean closeAndAllow(IECFHandler source, IECFHandler target) {
        IEntityInstance sourceAttachedEntity = source.getAttachedEntity();
        if (sourceAttachedEntity instanceof PathPointerBlockEntity) return false;
//        if (sourceAttachedEntity.tc$isEntity()) return false;

        IEntityInstance targetAttachedEntity = target.getAttachedEntity();
        if (targetAttachedEntity instanceof PathPointerBlockEntity) return false;
        if (targetAttachedEntity.tc$isEntity()) return false;

        return sourceAttachedEntity.tc$getPosition().closerThan(targetAttachedEntity.tc$getPosition(), Math.max(
                source.getRange(true),
                target.getRange(true)
        ) + 1);
    }


    @Override
    public Set<ECFNetworkMember> getAllECFNetworkMembers(Level level) {
        return ecfSources.getOrDefault(level, Set.of());
    }

    public void networkMemberUpdated(ECFNetworkMember updated) {
        Level level = updated.getEntityInstance().tc$getLevel();
        Set<ECFNetworkMember> members = Set.copyOf(ecfSources.getOrDefault(level, Set.of()));

        Queue<ECFNetworkMember> queue = new ArrayDeque<>();
        // Защита от зацикливания: храним entity, а не member (прокси могут отличаться)
        Set<IEntityInstance> visitedEntities = new HashSet<>();
        Set<PathPointerBlockEntity> updatedEmitters = new HashSet<>();

        queue.add(updated);
        visitedEntities.add(updated.getEntityInstance());

        while (!queue.isEmpty()) {
            ECFNetworkMember current = queue.poll();
            for (ECFNetworkMember member : members) {
                if (!validateRelation(member, current, Math::max)) continue;

                // PathPointer EMITTER — добавляем входы в очередь
                if (member.getEntityInstance() instanceof PathPointerBlockEntity ppBE
                        && (ppBE.parts.contains(PathPointerBlockEntity.PPPart.EMITTER)
                        || (ppBE.parts.contains(PathPointerBlockEntity.PPPart.INFUSER)))
                        && updatedEmitters.add(ppBE)) { // add() возвращает false если уже есть
                    for (BlockPos inputPos : ppBE.getInputPoses()) {
                        BlockEntity be = level.getBlockEntity(inputPos);
                        if (be instanceof PathPointerBlockEntity inputEntity
                                && visitedEntities.add(IEntityInstance.wrap(inputEntity))) { // защита от петли
                            queue.add(new PPECFMemberProxy(updated, inputEntity));
                        }
                    }
                }

                member.scheduleMemberUpdate(current);
            }
        }
    }

    @Override
    public Set<ECFNetworkMember> getAvailableNetworkTargets(ECFNetworkMember requesterMember) {
        Level level = requesterMember.getEntityInstance().tc$getLevel();
        Set<ECFNetworkMember> members = ecfSources.get(level);
        if (members == null) return Set.of();

        Set<ECFNetworkMember> toReturn = new HashSet<>();
        Queue<ECFNetworkMember> queue = new ArrayDeque<>();
        // Защита от зацикливания по entity-идентичности
        Set<IEntityInstance> visitedPP = new HashSet<>();

        queue.add(requesterMember);
        visitedPP.add(requesterMember.getEntityInstance());

        while (!queue.isEmpty()) {
            ECFNetworkMember current = queue.poll();

            for (ECFNetworkMember member : members) {
                if (!validateRelation(current, member, Math::min)) continue;

                //member is collector or extractor if for entity
                if (member.getEntityInstance() instanceof PathPointerBlockEntity ppBE) {
                    if (ppBE.parts.contains(PathPointerBlockEntity.PPPart.COLLECTOR)
                            || (ppBE.parts.contains(PathPointerBlockEntity.PPPart.EXTRACTOR)
                            && current.getEntityInstance().tc$isEntity())) {
                        if (visitedPP.add(IEntityInstance.wrap(ppBE))) {
                            queue.add(new PPECFMemberProxy(requesterMember, (PathPointerBlockEntity) level.getBlockEntity(ppBE.getOutputPos()), ppBE));
                        }
                    }
                    continue;
                }


                if (current instanceof PPECFMemberProxy inst) {
                    if (!member.getEntityInstance().tc$isEntity() || inst.proxy().parts.contains(PathPointerBlockEntity.PPPart.INFUSER)) {
                        toReturn.add(new PPECFMemberProxy(member, inst.source()));
                    }
                } else if (!member.getEntityInstance().tc$isEntity()) {
                    toReturn.add(member);
                }
            }
        }
        return toReturn;
    }

    @Override
    public boolean isIn(Level level, ECFNetworkMember networkMember) {
        Set<ECFNetworkMember> members = ecfSources.get(level);
        if (members == null) return false;
        IECFHandler mainHandler = networkMember.getMainHandler();
        if (!mainHandler.equals(SentinelHelper.EMPTY_ECF_HANDLER)) {
            for (ECFNetworkMember member : members) {
                if (member.getMainHandler().equals(mainHandler)) return true;
            }
        }
        return members.contains(networkMember);
    }

    private void remove(Level level, ECFNetworkMember thing) {
        Set<ECFNetworkMember> set = ecfSources.get(level);
        if (set == null) return;
        networkMemberUpdated(thing);
        set.remove(thing);
        if (set.isEmpty()) ecfSources.remove(level);
    }

    private void add(Level level, ECFNetworkMember thing) {
        Set<ECFNetworkMember> ECFNetworkMembers = ecfSources.computeIfAbsent(level, k -> new HashSet<>());

        Optional<ECFNetworkMember> any = ECFNetworkMembers.stream()
                .filter(member -> member.getEntityInstance().tc$isBlock())
                .filter(cfeNetworkMember ->
                        cfeNetworkMember.getEntityInstance().tc$getBlockPos().equals(thing.getEntityInstance().tc$getBlockPos()))
                .findAny();

        if (any.isPresent()) return;

        ECFNetworkMembers.add(thing);

        networkMemberUpdated(thing);
    }

    public void onNetworkEvent(ECFNetworkMember source, NetworkAction action) {
        switch (action) {
            case ADD -> add(source.getEntityInstance().tc$getLevel(), source);
            case REMOVE -> remove(source.getEntityInstance().tc$getLevel(), source);
            case UPDATE -> networkMemberUpdated(source);
            default     -> throw new RuntimeException("Unsupported Network action: " + action);
        }
    }
    @Override
    public void fireECFNetworkEvent(ECFNetworkMember source, NetworkAction action) {
        MinecraftForge.EVENT_BUS.post(new ECFNetworkEvent(source,action));
    }

    @Override
    public IECFHandler createDefaultECFHandler(ECFNetworkMember entityInstance) {
        return new DefaultECFHandler(entityInstance);
    }
}
