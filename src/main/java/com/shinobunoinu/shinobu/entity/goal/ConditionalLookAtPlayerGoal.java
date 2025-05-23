package com.shinobunoinu.shinobu.entity.goal;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class ConditionalLookAtPlayerGoal extends LookAtPlayerGoal {

    private final ShinobuEntity shinobu;

    public ConditionalLookAtPlayerGoal(ShinobuEntity shinobu, Class<? extends LivingEntity> targetType, float range) {
        super(shinobu, targetType, range);
        this.shinobu = shinobu;
    }

    @Override
    public boolean canUse() {
        return !shinobu.isLying() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !shinobu.isLying() && super.canContinueToUse();
    }
}
