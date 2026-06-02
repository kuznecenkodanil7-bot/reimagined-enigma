package ru.creepypasta.nightmare;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModBlocks {
    public static final RegistryKey<Block> NIGHTMARE_PORTAL_KEY = RegistryKey.of(
            RegistryKeys.BLOCK,
            CreepypastaMod.id("nightmare_portal")
    );

    public static final Block NIGHTMARE_PORTAL = Registry.register(
            Registries.BLOCK,
            NIGHTMARE_PORTAL_KEY.getValue(),
            new Block(AbstractBlock.Settings.create()
                    .registryKey(NIGHTMARE_PORTAL_KEY)
                    .mapColor(MapColor.BLACK)
                    .strength(-1.0F, 3600000.0F)
                    .luminance(state -> 15)
                    .noCollision()
                    .nonOpaque())
    );

    private ModBlocks() {
    }

    public static void init() {
        // Static registration hook.
    }
}
