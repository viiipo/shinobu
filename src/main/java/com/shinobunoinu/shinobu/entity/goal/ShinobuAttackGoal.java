package com.shinobunoinu.shinobu.entity.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class ShinobuAttackGoal extends TargetGoal {
    public final TamableAnimal tameAnimal;
    private final Mode mode;
    private LivingEntity cachedTarget;
    private int lastEventTimestamp;

    // 定义两种工作模式
    public enum Mode {
        DEFEND_OWNER,    // 主人被攻击时触发
        ASSIST_OWNER     // 主人攻击他人时触发
    }

    public ShinobuAttackGoal(TamableAnimal tameAnimal, Mode mode) {
        super(tameAnimal, false);
        this.tameAnimal = tameAnimal;
        this.mode = mode;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        // 基础条件检查
        if (!tameAnimal.isTame() || tameAnimal.isOrderedToSit()) {
            return false;
        }

        LivingEntity owner = tameAnimal.getOwner();
        if (owner == null) {
            return false;
        }

        // 根据模式获取目标
        switch (mode) {
            case DEFEND_OWNER:
                this.cachedTarget = owner.getLastHurtByMob();
                this.lastEventTimestamp = owner.getLastHurtByMobTimestamp();
                break;
            case ASSIST_OWNER:
                this.cachedTarget = owner.getLastHurtMob();
                this.lastEventTimestamp = owner.getLastHurtMobTimestamp();
                break;
            default:
                return false;
        }

        // 目标有效性验证
        return cachedTarget != null
                && cachedTarget.isAlive()
                && lastEventTimestamp != tameAnimal.getNoActionTime()
                && canAttack(cachedTarget, TargetingConditions.DEFAULT)
                && tameAnimal.wantsToAttack(cachedTarget, owner);
    }

    @Override
    public void start() {
        this.mob.setTarget(cachedTarget);
        // 同步时间戳防止重复触发
        tameAnimal.setNoActionTime(lastEventTimestamp);
        super.start();
    }

    @Override
    public void stop() {
        this.cachedTarget = null;
        super.stop();
    }
}