package com.devdat.dragonblocks.command;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.PlayerStatsProvider;
import com.devdat.dragonblocks.capability.Race;
import com.devdat.dragonblocks.core.event.PlayerSyncHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// /dragonblocks race <vanilla|humano|saiyajin|namekiano> [jugadores]
@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RaceCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dragonblocks")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("race")
                        .then(Commands.argument("race", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(Race.values()).map(Race::getName), builder))
                                .executes(ctx -> setRace(ctx, List.of(ctx.getSource().getPlayerOrException())))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> setRace(ctx, EntityArgument.getPlayers(ctx, "targets")))))));
    }

    private static int setRace(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "race");
        Race race = Race.byName(name);
        if (race == null) {
            ctx.getSource().sendFailure(Component.literal("§cRaza desconocida: " + name));
            return 0;
        }

        for (ServerPlayer player : targets) {
            player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(stats -> stats.setRace(race.getId()));
            PlayerSyncHandler.syncRace(player);
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "§a[Raza] " + player.getName().getString() + " ahora es: " + race.getName()), true);
        }
        return targets.size();
    }
}
