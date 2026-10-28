package com.devdat.dragonblocks.client.renderer;

import com.devdat.dragonblocks.DragonBlocks;
import com.devdat.dragonblocks.capability.Race;
import com.devdat.dragonblocks.client.model.RaceGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;

// Un renderer GeckoLib por raza. Se reconstruyen en cada recarga de recursos (AddLayers).
@Mod.EventBusSubscriber(modid = DragonBlocks.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RaceRenderers {
    private static final Map<Race, RacePlayerRenderer> RENDERERS = new EnumMap<>(Race.class);

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityRendererProvider.Context context = event.getContext();
        RENDERERS.clear();

        // Saiyajin y Namekiano usan el modelo humano hasta que tengan sus propios archivos:
        // para cambiarlos, basta con apuntar a sus rutas aquí.
        RENDERERS.put(Race.HUMAN, new RacePlayerRenderer(context, humanModel()));
        RENDERERS.put(Race.SAIYAN, new RacePlayerRenderer(context, humanModel()));
        RENDERERS.put(Race.NAMEKIAN, new RacePlayerRenderer(context, humanModel()));
    }

    private static RaceGeoModel humanModel() {
        return new RaceGeoModel(
                asset("geo/human_adult_model.geo.json"),
                asset("textures/entity/steve.png"),
                asset("animations/human_adult_animations.animation.json"));
    }

    private static ResourceLocation asset(String path) {
        return new ResourceLocation(DragonBlocks.MODID, path);
    }

    // null = sin modelo custom (Vanilla)
    public static RacePlayerRenderer get(Race race) {
        return RENDERERS.get(race);
    }
}
