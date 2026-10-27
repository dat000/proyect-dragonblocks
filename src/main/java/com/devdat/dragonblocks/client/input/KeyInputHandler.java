package com.devdat.dragonblocks.client.input;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.network.ModMessages;
import com.devdat.dragonblocks.network.ChargeKiPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, value = Dist.CLIENT)
public class KeyInputHandler {
    private static boolean wasCharging = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            boolean isCharging = ClientKeybinds.CHARGE_KI_KEY.isDown();
            if (isCharging != wasCharging) {
                wasCharging = isCharging;
                System.out.println("[DEBUG CLIENTE] Tecla C de carga cambiada a: " + isCharging); // <-- Mirar consola de IntelliJ
                ModMessages.sendToServer(new ChargeKiPacket(isCharging));
            }
        }
    }
}