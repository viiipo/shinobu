package com.shinobunoinu.shinobu.entity.goal;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.EnumSet;

public class ShinobuAutoAttackGoal extends MeleeAttackGoal {
    private final ShinobuEntity mob;

    @Override
    public boolean canUse() {
        return !mob.hasWeapon() && super.canUse();
    }

    public ShinobuAutoAttackGoal(ShinobuEntity shinobu, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(shinobu, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = shinobu;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double p_25558_) {
        double d0 = this.getAttackReachSqr(target);
        if (p_25558_ <= d0 && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            if (this.mob.getSpinAttackAnimTimer() == 0) {
                this.mob.setAutoAttackAnimTimer(this.mob.AUTO_ATTACK_ANIMATION_DURATION);
            }
            this.mob.doHurtTarget(target);
        }
    }

    @Override
    public void stop() {
        this.mob.setAutoAttackAnimTimer(0);
        super.stop();
    }
}
