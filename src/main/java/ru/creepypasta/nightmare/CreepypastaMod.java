package ru.creepypasta.nightmare;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import ru.creepypasta.nightmare.entity.StalkerEntity;

public final class CreepypastaMod implements ModInitializer {
    public static final String MOD_ID = "creepypasta";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModEntities.init();
        FabricDefaultAttributeRegistry.register(ModEntities.STALKER, StalkerEntity.createStalkerAttributes());
        ServerTickEvents.END_SERVER_TICK.register(NightmareManager::tickServer);
    }
}
