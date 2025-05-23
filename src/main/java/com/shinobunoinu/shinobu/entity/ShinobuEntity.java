package com.shinobunoinu.shinobu.entity;

import com.shinobunoinu.shinobu.entity.goal.ConditionalLookAtPlayerGoal;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import com.shinobunoinu.shinobu.registry.ParticleTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class ShinobuEntity extends TamableAnimal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LYING =
            SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BOOLEAN);

    private int sittingTime = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ShinobuEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    // 属性定义
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SITTING, false);
        this.entityData.define(DATA_LYING, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // 坐下后累计时间
        if (isSitting() && !isLying()) {
            sittingTime++;
            if (sittingTime > 600) { // 30秒
                setLying(true);
            }
        } else {
            sittingTime = 0;
        }

        // 客户端才显示粒子
        if (level().isClientSide && isAsleep()) {
            // 随机生成一点浮动效果
            double offsetX = (random.nextDouble() - 0.5D) * 0.3;
            double offsetZ = (random.nextDouble() - 0.5D) * 0.3;
            double x = this.getX() + offsetX;
            double y = this.getY() + 1.2D; // 头顶上
            double z = this.getZ() + offsetZ;

            // 缓缓上升的速度
            double xSpeed = 0.0D;
            double ySpeed = 0.02D; // 上升速度
            double zSpeed = 0.0D;

            // 添加粒子（注意要用 motion 参数）
            level().addParticle(ParticleTypeRegistry.ZZZ_PARTICLE.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
    // 行为目标
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 4.0f, 2.0f, false));
        this.goalSelector.addGoal(4, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new ConditionalLookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    // 与玩家交互（右键）
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (level().isClientSide) return InteractionResult.SUCCESS;

        if (!isTame() && itemstack.is(ItemRegistry.DONUT.get())) {
            if (!player.getAbilities().instabuild) itemstack.shrink(1);
            if (random.nextFloat() < 0.7f) {
                tame(player);
                setOwnerUUID(player.getUUID());
                level().broadcastEntityEvent(this, (byte) 7); // ❤️ 粒子
                setSitting(true);
            } else {
                level().broadcastEntityEvent(this, (byte) 6); // 烟雾
            }
            return InteractionResult.CONSUME;
        }

        if (isTame() && isOwnedBy(player)) {
            // 切换坐下、跟随
            if (isSitting()) {
                setSitting(false);
                player.displayClientMessage(Component.literal("Shinobu is Following"), true);
            } else {
                setSitting(true);
                player.displayClientMessage(Component.literal("Shinobu is Sitting"), true);
            }
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }


    public void setSitting(boolean sitting) {
        super.setOrderedToSit(sitting);
        this.entityData.set(DATA_SITTING, sitting);
        if (!sitting) setLying(false); // 站起时取消躺下
    }

    public boolean isSitting() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setLying(boolean lying) {
        this.entityData.set(DATA_LYING, lying);
    }

    public boolean isLying() {
        return this.entityData.get(DATA_LYING);
    }

    // 繁殖禁用
    @Nullable
    @Override
    public ShinobuEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // Geckolib 动画控制器
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, state -> {
            if (this.isLying()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("lie_down"));
            } else if (this.isSitting()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("sit"));
            } else if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
            } else {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // 不会溺水
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelreader) {
        return true;
    }
    public boolean isAsleep() {
        return this.isLying();
    }
}
