package net.sinedkadis.terracompositio.registries;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.sinedkadis.terracompositio.TerraCompositio;
import net.sinedkadis.terracompositio.api.TerraCompositioAPI;
import net.sinedkadis.terracompositio.api.networks.ecf.ECFNetworkMember;
import net.sinedkadis.terracompositio.api.networks.ecf.IECFHandler;

import java.util.Arrays;

@EventBusSubscriber(modid = TerraCompositio.MOD_ID)
public class TCCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("terracompositio")
                        .requires(source -> source.hasPermission(2)) // op only
                        .then(
                                Commands.literal("print-ecf-data")
                                        .executes(TCCommands::printCFEData)
                        )
                        .then(
                                Commands.argument("print-ecf-data", EntityArgument.entity()))
                                        .executes(ctx -> {
                                            Entity entity = EntityArgument
                                                    .getEntity(ctx,"ecf network member entity");
                                            return TCCommands.printCFEData(ctx,entity);
                                        })
                        .then(
                                Commands.literal("clear-ecf-data")
                                        .executes(TCCommands::clearECFData)
                        )
                        .then(
                                Commands.argument("clear-ecf-data", EntityArgument.entity()))
                        .executes(ctx -> {
                            Entity entity = EntityArgument
                                    .getEntity(ctx,"ecf network member entity");
                            return TCCommands.clearECFData(ctx, entity);
                        })
                        .then(
                                Commands.literal("clear-all-queues")
                                        .executes(TCCommands::clearAllQueues)
                        )
                        );
    }

    private static int clearAllQueues(CommandContext<CommandSourceStack> ctx) {
        TerraCompositioAPI.instance().getECFNetworkInstance().getAllECFNetworkMembers(ctx.getSource().getLevel()).stream()
                .map(ECFNetworkMember::getMainHandler)
                .forEach(iEcfHandler -> iEcfHandler.setQueued(0));
        return 0;
    }

    private static int clearECFData(CommandContext<CommandSourceStack> ctx, Entity... entities) {
        CommandSourceStack source = ctx.getSource();
        ECFNetworkMember memberEntity;
        if (Arrays.stream(entities).toList().isEmpty()) {
            ServerPlayer player = source.getPlayer();
            if (player == null) {
                source.sendFailure(Component.literal("No entities were provided"));
                return 0;
            }
            memberEntity = (ECFNetworkMember) player;
        } else {
            Entity entity = entities[0];
            if (entity instanceof ECFNetworkMember memberEntity1)
                memberEntity = memberEntity1;
            else {
                source.sendFailure(Component.literal("Entity has to be ECFNetworkMemberEntity"));
                return 0;
            }
        }
        IECFHandler mainHandler = memberEntity.getMainHandler();
        mainHandler.clear();

        NonNullSupplier<Exception> exception = Exception::new;
        ((LivingEntity) memberEntity.getEntityInstance()).getArmorSlots().forEach(itemStack -> {
            try {
                IECFHandler capability = itemStack.getCapability(TCCapabilities.ECF);
                if (capability == null) throw exception.get();
            } catch (Exception ignored) {

            }
        });

        source.sendSuccess(() ->
                        Component.literal("Data cleared"),
                true);
        return Command.SINGLE_SUCCESS;
    }

    private static int printCFEData(CommandContext<CommandSourceStack> ctx, Entity... entities) {
        CommandSourceStack source = ctx.getSource();
        ECFNetworkMember memberEntity;
        if (Arrays.stream(entities).toList().isEmpty()) {
            ServerPlayer player = source.getPlayer();
            if (player == null) {
                source.sendFailure(Component.literal("No entities were provided"));
                return 0;
            }
            memberEntity = (ECFNetworkMember) player;
        } else {
            Entity entity = entities[0];
            if (entity instanceof ECFNetworkMember memberEntity1)
                memberEntity = memberEntity1;
            else {
                source.sendFailure(Component.literal("Entity has to be ECFNetworkMemberEntity"));
                return 0;
            }
        }

        StringBuilder message = new StringBuilder();

        IECFHandler mainHandler = memberEntity.getMainHandler();
        message.append(mainHandler.toString()).append("\n\n");

        source.sendSuccess(() ->
                        Component.literal(message.toString()),
                true);
        return Command.SINGLE_SUCCESS;
    }
}