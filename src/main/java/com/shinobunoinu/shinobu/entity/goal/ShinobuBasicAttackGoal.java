package com.shinobunoinu.shinobu.entity.goal;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.EnumSet;

public class ShinobuBasicAttackGoal extends Goal {
    private final ShinobuEntity mob;
    private LivingEntity target;
    private final double speed = 1.2;
    private final float attackRange = 2.5f;
    private int attackCooldown = 0;

    public ShinobuBasicAttackGoal(ShinobuEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && !mob.hasWeapon();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.target = mob.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSqr = mob.distanceToSqr(target);

        if (distSqr > attackRange * attackRange) {
            mob.getNavigation().moveTo(target, speed); // ✅ 持续追击
        } else {
            mob.getNavigation().stop();

            // 攻击冷却控制
            if (attackCooldown <= 0) {
                mob.setAnimation(ShinobuEntity.ANIMATION_ATTACK);
                mob.setAutoAttackAnimTimer(mob.AUTO_ATTACK_ANIMATION_DURATION);

                float damage = (float) mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                target.hurt(mob.damageSources().mobAttack(mob), damage);

                attackCooldown = 20; // 1秒冷却（20tick）
            } else {
                attackCooldown--;
            }
        }
    }
}
