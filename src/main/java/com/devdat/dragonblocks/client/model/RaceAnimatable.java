package com.devdat.dragonblocks.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

// Animatable "singleton" que reemplaza al jugador. GeckoLib separa el estado de animación
// por jugador usando el id de la entidad (GeoReplacedEntityRenderer#getInstanceId).
public class RaceAnimatable implements GeoReplacedEntity {
    // Nombres tal cual aparecen en el .animation.json
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public EntityType<?> getReplacingEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base", 5, state -> {
            if (!state.isMoving()) return state.setAndContinue(IDLE);

            // isSprinting() ya viene sincronizado por vanilla para los demás jugadores
            Entity entity = state.getData(DataTickets.ENTITY);
            return state.setAndContinue(entity != null && entity.isSprinting() ? RUN : WALK);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
