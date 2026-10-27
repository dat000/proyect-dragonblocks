package com.devdat.dragonblocks.capability;

import com.devdat.dragonblocks.DragonBlocks;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerStatsRegisterHandler {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerStats.class);
    }
}