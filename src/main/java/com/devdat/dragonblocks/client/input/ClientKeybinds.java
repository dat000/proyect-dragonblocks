package com.devdat.dragonblocks.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "dragonblocks", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientKeybinds {
    public static final String CATEGORY_DRAGONBLOCKS = "key.categories.dragonblocks";

    // Asignamos por defecto el botón central del mouse (GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
    public static final KeyMapping LOCK_KEY = new KeyMapping(
            "key.dragonblocks.lock_on",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            CATEGORY_DRAGONBLOCKS
    );

    // NUEVA: Tecla para cargar Ki (por defecto la 'C')
    public static final KeyMapping CHARGE_KI_KEY = new KeyMapping(
            "key.dragonblocks.charge_ki",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY_DRAGONBLOCKS
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(LOCK_KEY);
        event.register(CHARGE_KI_KEY);
    }
}