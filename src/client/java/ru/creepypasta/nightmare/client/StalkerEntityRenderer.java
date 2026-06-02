package ru.creepypasta.nightmare.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import ru.creepypasta.nightmare.CreepypastaMod;
import ru.creepypasta.nightmare.entity.StalkerEntity;

public final class StalkerEntityRenderer extends MobEntityRenderer<StalkerEntity, LivingEntityRenderState, BipedEntityModel<LivingEntityRenderState>> {
    private static final Identifier TEXTURE = CreepypastaMod.id("textures/entity/stalker.png");

    public StalkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.0F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected boolean hasLabel(StalkerEntity entity, double squaredDistanceToCamera) {
        return false;
    }
}
