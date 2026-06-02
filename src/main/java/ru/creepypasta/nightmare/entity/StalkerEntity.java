package ru.creepypasta.nightmare.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import ru.creepypasta.nightmare.NightmareManager;

public final class StalkerEntity extends HostileEntity {
    public StalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
    }

    public static DefaultAttributeContainer.Builder createStalkerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 200.0D)
.add(EntityAttributes.ATTACK_DAMAGE, 1.0D)
.add(EntityAttributes.MOVEMENT_SPEED, 0.38D)
.add(EntityAttributes.FOLLOW_RANGE, 96.0D)
.add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0D)
.add(EntityAttributes.STEP_HEIGHT, 1.2D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.35D, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.9D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient || this.age % 5 != 0) {
            return;
        }

        for (PlayerEntity player : this.getWorld().getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer
                    && !serverPlayer.isCreative()
                    && !serverPlayer.isSpectator()
                    && this.squaredDistanceTo(serverPlayer) <= 2.25D) {
                NightmareManager.teleportToNightmare(serverPlayer);
                return;
            }
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean attacked = super.tryAttack(world, target);
        if (!this.getWorld().isClient && target instanceof ServerPlayerEntity serverPlayer) {
            NightmareManager.teleportToNightmare(serverPlayer);
        }
        return attacked;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_SCREAM;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }
}
