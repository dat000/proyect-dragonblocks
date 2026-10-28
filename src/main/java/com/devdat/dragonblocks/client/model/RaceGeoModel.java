package com.devdat.dragonblocks.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

// Modelo GeckoLib de una raza: rutas al .geo.json, la textura y el .animation.json
public class RaceGeoModel extends GeoModel<RaceAnimatable> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public RaceGeoModel(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    public ResourceLocation getModelResource(RaceAnimatable animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(RaceAnimatable animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(RaceAnimatable animatable) {
        return animation;
    }
}
