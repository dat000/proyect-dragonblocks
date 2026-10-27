package com.devdat.dragonblocks.capability;

import com.devdat.dragonblocks.DragonBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerStatsAttachHandler {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerStatsProvider.PLAYER_STATS).isPresent()) {
                event.addCapability(new ResourceLocation(DragonBlocks.MODID, "player_stats"), new PlayerStatsProvider());
            }
        }
    }
}