package ru.creepypasta.nightmare;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import ru.creepypasta.nightmare.entity.StalkerEntity;

public final class ModEntities {
    public static final RegistryKey<EntityType<?>> STALKER_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE,
            CreepypastaMod.id("stalker")
    );

    public static final EntityType<StalkerEntity> STALKER = Registry.register(
            Registries.ENTITY_TYPE,
            STALKER_KEY.getValue(),
            EntityType.Builder.create(StalkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6F, 1.95F)
                    .eyeHeight(1.75F)
                    .maxTrackingRange(96)
                    .trackingTickInterval(1)
                    .build(STALKER_KEY)
    );

    private ModEntities() {
    }

    public static void init() {
        // Static registration hook.
    }
}
