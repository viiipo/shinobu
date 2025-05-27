package com.shinobunoinu.shinobu.entity.goal;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ShinobuSpinAttackGoal extends MeleeAttackGoal {
    private final ShinobuEntity mob;
    private final float applyKnockback = 1;
    private final float range;

    public ShinobuSpinAttackGoal(ShinobuEntity shinobu, double speedModifier, boolean followingTargetEvenIfNotSeen, float range) {
        super(shinobu, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = shinobu;
        this.range = range;
    }

    @Override
    public boolean canUse() {
        return mob.hasWeapon() && super.canUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double p_25558_) {
        // 优先检查目标是否进入攻击范围
        double targetDistanceSqr = mob.distanceToSqr(target);
        boolean targetInRange = targetDistanceSqr <= range * range;
        // 仅在目标进入范围时触发动画
        if (targetInRange && this.mob.getSpinAttackAnimTimer() == 0) {
            this.mob.setSpinAttackAnimTimer(this.mob.SPIN_ATTACK_ANIMATION_DURATION);
        }
        AABB attackArea = new AABB(
                mob.getX() - range, mob.getY() - 1, mob.getZ() - range,
                mob.getX() + range, mob.getY() + 2, mob.getZ() + range
        );
        List<LivingEntity> entitiesHit = mob.level().getEntitiesOfClass(LivingEntity.class, attackArea);
        float damage = (float) mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        for (LivingEntity entityHit : entitiesHit) {
            if (entityHit instanceof ShinobuEntity || entityHit == this.mob.getOwner()) continue;
            double dx = entityHit.getX() - mob.getX();
            double dz = entityHit.getZ() - mob.getZ();
            double distanceSqr = dx * dx + dz * dz;
            if (distanceSqr <= range * range) {
                if (mob.getSpinAttackAnimTimer() == 55 || mob.getSpinAttackAnimTimer() == 50 || mob.getSpinAttackAnimTimer() == 45 || mob.getSpinAttackAnimTimer() == 30 || mob.getSpinAttackAnimTimer() == 15 || mob.getSpinAttackAnimTimer() == 10 || mob.getSpinAttackAnimTimer() == 5) {
                    entityHit.hurt(mob.damageSources().mobAttack(mob), damage);
                    if (entityHit.isBlocking()) {
                        entityHit.getUseItem().hurtAndBreak(400, entityHit,
                                player -> player.broadcastBreakEvent(entityHit.getUsedItemHand()));
                    }
                    // 施加方向性击退
                    double magnitude = Math.sqrt(distanceSqr);
                    if (magnitude != 0) {
                        double knockback = 0.5 * applyKnockback;
                        entityHit.setDeltaMovement(
                                dx / magnitude * knockback,
                                entityHit.getDeltaMovement().y + 0.3, // 轻微向上击飞
                                dz / magnitude * knockback
                        );
                    }
                }
            }
        }
    }

    @Override
    public void stop(){
        this.mob.setSpinAttackAnimTimer(0);
        super.stop();
    }
}
