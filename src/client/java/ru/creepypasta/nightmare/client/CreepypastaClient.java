package ru.creepypasta.nightmare.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.creepypasta.nightmare.CreepypastaMod;
import ru.creepypasta.nightmare.ModEntities;

import java.util.Random;

public final class CreepypastaClient implements ClientModInitializer {
    private static final Identifier SCREAMER = CreepypastaMod.id("textures/gui/screamer.png");
    private static final Random RANDOM = new Random();
    private static int screamerTicks = 0;
    private static int jitter = 0;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.STALKER, StalkerEntityRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) {
                screamerTicks = 0;
                return;
            }

            if (screamerTicks > 0) {
                screamerTicks--;
                jitter = RANDOM.nextInt(17) - 8;
                return;
            }

            // Frequent local screamers: about once every 12-25 seconds.
            if (RANDOM.nextInt(280) == 0) {
                screamerTicks = 24 + RANDOM.nextInt(14);
                client.player.playSound(SoundEvents.ENTITY_GHAST_SCREAM, 1.0F, 0.45F + RANDOM.nextFloat() * 0.3F);
            }
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            if (screamerTicks <= 0) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            context.fill(0, 0, width, height, 0xCC000000);
            context.drawTexture(RenderLayer::getGuiTextured, SCREAMER, jitter, -jitter, 0.0F, 0.0F, width, height, 256, 256);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("НЕ ОБОРАЧИВАЙСЯ"), width / 2 + jitter, height / 2 - 8 - jitter, 0xFFFF1111);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("RUN"), width / 2 - jitter, height / 2 + 12 + jitter, 0xFFFFFFFF);
        });
    }
}
