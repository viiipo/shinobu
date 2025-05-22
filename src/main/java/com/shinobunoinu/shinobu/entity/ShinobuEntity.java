// 文件位置：src/main/java/com/shinobu/entity/ShinobuPetEntity.java
package com.shinobunoinu.shinobu.entity;

import com.shinobunoinu.shinobu.registry.ItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;

public class ShinobuEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 正确属性注册方法
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
    @Override

    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // 不需要繁殖功能，直接返回null
        return null;
    }

    public ShinobuEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    // AI行为
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    }

    // 驯服逻辑（使用甜甜圈）
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (item.getItem() == ItemRegistry.DONUT.get() && !isTame()) {
            if (!player.isCreative()) item.shrink(1);
            this.tame(player);
            this.setOrderedToSit(true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    // 动画控制器
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::animateMain));
    }

    private PlayState animateMain(AnimationState<ShinobuEntity> event) {
        if (this.isDeadOrDying()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("die"));
        } else if (this.isAggressive()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("attack"));
        } else if (this.isInSittingPose()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("sit"));
        } else if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay(
                this.isSprinting() ? "run" : "walk"
            ));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}