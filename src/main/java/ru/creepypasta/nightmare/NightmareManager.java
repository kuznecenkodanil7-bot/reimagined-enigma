package ru.creepypasta.nightmare;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import ru.creepypasta.nightmare.entity.StalkerEntity;

import java.util.List;
import java.util.Set;

public final class NightmareManager {
    public static final RegistryKey<World> NIGHTMARE_WORLD = RegistryKey.of(
            RegistryKeys.WORLD,
            CreepypastaMod.id("nightmare")
    );

    private static final List<String> GLITCH_LINES = List.of(
            "§4[ERROR] §k000000 §r§cНЕ СМОТРИ НАЗАД",
            "§8<system> §7chunk_§k██§7 corrupted",
            "§cон уже рядом",
            "§0§k████████████████",
            "§4[null] §fвыхода нет",
            "§7Ваш мир сделал вдох."
    );

    private NightmareManager() {
    }

    public static void tickServer(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }

                if (world.getRegistryKey().equals(NIGHTMARE_WORLD)) {
                    tickNightmarePlayer(player, world);
                } else {
                    tickOverworldHorror(player, world);
                }
            }
        }
    }

    private static void tickOverworldHorror(ServerPlayerEntity player, ServerWorld world) {
        if (player.age % 40 != 0) {
            return;
        }

        if (world.random.nextFloat() < 0.22F) {
            sendGlitchChat(player);
        }
        if (world.random.nextFloat() < 0.16F) {
            strikeLightningNear(player, world);
        }
        if (world.random.nextFloat() < 0.30F) {
            deleteAndCorruptBlocks(player, world);
        }
        if (world.random.nextFloat() < 0.18F) {
            spawnStalkerNear(player, world, false);
        }
    }

    private static void tickNightmarePlayer(ServerPlayerEntity player, ServerWorld world) {
        if (player.age % 20 == 0) {
            world.setWeather(0, 600, true, true);
        }

        if (player.age % 60 == 0) {
            ensureNightmareArena(world);
            ensurePortalGuard(world);
            sendGlitchChat(player);
        }

        if (world.getBlockState(player.getBlockPos()).isOf(ModBlocks.NIGHTMARE_PORTAL)
                || world.getBlockState(player.getBlockPos().down()).isOf(ModBlocks.NIGHTMARE_PORTAL)) {
            returnToOverworld(player);
        }
    }

    public static void teleportToNightmare(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerWorld nightmare = server.getWorld(NIGHTMARE_WORLD);
        if (nightmare == null) {
            player.sendMessage(Text.literal("§4[creepypasta] Nightmare dimension не загружен."), false);
            return;
        }

        ensureNightmareArena(nightmare);
        ensurePortalGuard(nightmare);
        player.sendMessage(Text.literal("§4ОНО КОСНУЛОСЬ ТЕБЯ."), false);
        nightmare.playSound(null, BlockPos.ofFloored(0, 72, 0), SoundEvents.ENTITY_WARDEN_ROAR, SoundCategory.HOSTILE, 2.0F, 0.6F);
        player.teleport(nightmare, 0.5D, 72.0D, 0.5D, Set.of(), player.getYaw(), player.getPitch(), true);
    }

    private static void returnToOverworld(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerWorld overworld = server.getOverworld();
        BlockPos spawn = overworld.getSpawnPos();
        player.sendMessage(Text.literal("§7Ты выбрался. Пока."), false);
        player.teleport(overworld, spawn.getX() + 0.5D, spawn.getY() + 1.0D, spawn.getZ() + 0.5D, Set.of(), player.getYaw(), player.getPitch(), true);
    }

    private static void ensureNightmareArena(ServerWorld world) {
        BlockPos center = new BlockPos(0, 70, 0);

        for (int x = -14; x <= 14; x++) {
            for (int z = -14; z <= 14; z++) {
                BlockPos floor = center.add(x, 0, z);
                world.setBlockState(floor, Blocks.DEEPSLATE.getDefaultState(), Block.NOTIFY_ALL);
                if (Math.abs(x) == 14 || Math.abs(z) == 14) {
                    world.setBlockState(floor.up(), Blocks.BLACKSTONE.getDefaultState(), Block.NOTIFY_ALL);
                    world.setBlockState(floor.up(2), Blocks.CRYING_OBSIDIAN.getDefaultState(), Block.NOTIFY_ALL);
                } else if (world.random.nextInt(18) == 0) {
                    world.setBlockState(floor.up(), Blocks.SCULK.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        BlockPos portal = new BlockPos(11, 71, 0);
        for (int y = 0; y <= 2; y++) {
            world.setBlockState(portal.up(y), ModBlocks.NIGHTMARE_PORTAL.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    private static void ensurePortalGuard(ServerWorld world) {
        Box box = Box.of(new Vec3d(11.0D, 72.0D, 0.0D), 12.0D, 8.0D, 12.0D);
        boolean hasGuard = !world.getEntitiesByType(ModEntities.STALKER, box, entity -> entity.isAlive()).isEmpty();
        if (!hasGuard) {
            StalkerEntity guard = ModEntities.STALKER.create(world, SpawnReason.EVENT);
            if (guard != null) {
                guard.refreshPositionAndAngles(8.5D, 72.0D, 0.5D, 0.0F, 0.0F);
                guard.setPersistent();
                world.spawnEntity(guard);
            }
        }
    }

    private static void spawnStalkerNear(ServerPlayerEntity player, ServerWorld world, boolean force) {
        Box checkBox = Box.of(player.getPos(), 96.0D, 64.0D, 96.0D);
        if (!force && !world.getEntitiesByType(ModEntities.STALKER, checkBox, entity -> entity.isAlive()).isEmpty()) {
            return;
        }

        int dx = world.random.nextBetween(-18, 18);
        int dz = world.random.nextBetween(-18, 18);
        if (Math.abs(dx) < 8) dx += dx < 0 ? -8 : 8;
        if (Math.abs(dz) < 8) dz += dz < 0 ? -8 : 8;

        int x = player.getBlockX() + dx;
        int z = player.getBlockZ() + dz;
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);

        StalkerEntity stalker = ModEntities.STALKER.create(world, SpawnReason.EVENT);
        if (stalker != null) {
            stalker.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            stalker.setPersistent();
            stalker.setTarget(player);
            world.spawnEntity(stalker);
            world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_STARE, SoundCategory.HOSTILE, 1.6F, 0.55F);
        }
    }

    private static void sendGlitchChat(ServerPlayerEntity player) {
        String line = GLITCH_LINES.get(player.getWorld().random.nextInt(GLITCH_LINES.size()));
        player.sendMessage(Text.literal(line), false);
    }

    private static void strikeLightningNear(ServerPlayerEntity player, ServerWorld world) {
        BlockPos pos = player.getBlockPos().add(world.random.nextBetween(-6, 6), 0, world.random.nextBetween(-6, 6));
        pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.TRIGGERED);
        if (lightning != null) {
            Vec3d strike = Vec3d.ofBottomCenter(pos);
            lightning.refreshPositionAfterTeleport(strike.x, strike.y, strike.z);
            world.spawnEntity(lightning);
        }
    }

    private static void deleteAndCorruptBlocks(ServerPlayerEntity player, ServerWorld world) {
        BlockPos origin = player.getBlockPos();
        for (int i = 0; i < 9; i++) {
            BlockPos pos = origin.add(
                    world.random.nextBetween(-5, 5),
                    world.random.nextBetween(-2, 3),
                    world.random.nextBetween(-5, 5)
            );

            if (!world.isChunkLoaded(pos)) {
                continue;
            }

            if (world.getBlockState(pos).isAir() || world.getBlockState(pos).getHardness(world, pos) < 0.0F) {
                continue;
            }

            if (world.random.nextBoolean()) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            } else {
                world.setBlockState(pos, world.random.nextBoolean()
                        ? Blocks.SCULK.getDefaultState()
                        : Blocks.BLACKSTONE.getDefaultState(), Block.NOTIFY_ALL);
            }
        }

        world.playSound(null, origin, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.HOSTILE, 1.3F, 0.7F);
    }
}
