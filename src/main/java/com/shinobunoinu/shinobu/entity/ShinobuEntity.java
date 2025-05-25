package com.shinobunoinu.shinobu.entity;

import com.shinobunoinu.shinobu.entity.goal.ConditionalLookAtPlayerGoal;
import com.shinobunoinu.shinobu.item.ShinobuHatItem;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import com.shinobunoinu.shinobu.registry.ParticleTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class ShinobuEntity extends TamableAnimal implements GeoEntity {
    private final RawAnimation IDLING_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation MOVING_ANIMATION = RawAnimation.begin().then("run", Animation.LoopType.LOOP);
    private final RawAnimation SITTING_ANIMATION = RawAnimation.begin().then("sit", Animation.LoopType.LOOP);
    private final RawAnimation LYING_ANIMATION = RawAnimation.begin().then("lie_down", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_MOVE = 1;
    protected static final byte ANIMATION_SIT = 2;
    protected static final byte ANIMATION_LYING = 3;
    protected static final byte ANIMATION_ATTACK = 4;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER = SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.INT);
    public final int ATTACK_ANIMATION_DURATION = 20;

    public byte getAnimation() {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        entityData.set(ANIMATION, animation);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.entityData.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.entityData.set(ATTACK_ANIM_TIMER, time);
    }

    @Override
    protected void customServerAiStep() {
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimation(ANIMATION_ATTACK);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        } else if (getAnimation() == ANIMATION_ATTACK) {
            setAnimation(ANIMATION_IDLE);
        }
        super.customServerAiStep();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        setAttackAnimTimer(20);
        return super.doHurtTarget(target);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean CLIENT_HAS_HAT = false;

    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LYING =
            SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BOOLEAN);
    private int zzzParticleCooldown = 0;  // 粒子冷却时间

    private int sittingTime = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ShinobuEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }
    private static final EntityDataAccessor<Boolean> DATA_HAS_HAT =
            SynchedEntityData.defineId(ShinobuEntity.class, EntityDataSerializers.BOOLEAN);
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
        this.entityData.define(DATA_HAS_HAT, false); // ✅ 注册帽子同步字段
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        this.entityData.define(ATTACK_ANIM_TIMER, 0);
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
        if (level().isClientSide && isLying()) {
            // 每5个游戏刻（ticks）生成一次粒子
            if (zzzParticleCooldown <= 0) {
                double offsetX = (random.nextDouble() - 0.5D) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5D) * 0.3;
                double x = this.getX() + offsetX;
                double y = this.getY() + 0.8D; // 头顶上
                double z = this.getZ() + offsetZ;

                double xSpeed = 0.0D;
                double ySpeed = 0.0001D; // 上升速度
                double zSpeed = 0.0D;

                // 添加粒子（注意要用 motion 参数）
                level().addParticle(ParticleTypeRegistry.ZZZ_PARTICLE.get(), x, y, z, xSpeed, ySpeed, zSpeed);

                zzzParticleCooldown = 20;  // 每5个ticks减少一个粒子
            } else {
                zzzParticleCooldown--; // 减少计数，冷却时间
            }
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
        boolean isShift = player.isShiftKeyDown();

        if (level().isClientSide) return InteractionResult.SUCCESS;

        // --- 驯服逻辑 ---
        if (!isTame() && itemstack.is(ItemRegistry.DONUT.get())) {
            if (!player.getAbilities().instabuild) itemstack.shrink(1);
            if (random.nextFloat() < 0.7f) {
                tame(player);
                setOwnerUUID(player.getUUID());
                level().broadcastEntityEvent(this, (byte) 7); // ❤️ 粒子
                setSitting(true);
            } else {
                level().broadcastEntityEvent(this, (byte) 6); // ❌ 粒子
            }
            return InteractionResult.CONSUME;
        }

        // --- 仅驯服并由当前玩家控制时才能交互 ---
        if (isTame() && isOwnedBy(player)) {
            // --- 取下帽子：Shift + 空手 ---
            if (isShift && itemstack.isEmpty() && hasHeadArmor()) {
                player.addItem(getHeadArmor().copy());
                setHeadArmor(ItemStack.EMPTY);
                player.displayClientMessage(Component.literal("Shinobu took off her hat."), true);
                return InteractionResult.CONSUME;
            }

            // --- 戴上帽子：手上是帽子，当前没有帽子 ---
            if (!hasHeadArmor() && itemstack.getItem() instanceof ShinobuHatItem) {
                setHeadArmor(itemstack.copyWithCount(1));
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                player.displayClientMessage(Component.literal("Shinobu equipped the hat!"), true);
                return InteractionResult.CONSUME;
            }


            // --- 其他情况：切换坐/站 ---
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
        if(sitting) {
            setAnimation(ANIMATION_SIT);
        } else {
            setAnimation(ANIMATION_IDLE);
        }
        this.entityData.set(DATA_SITTING, sitting);
        if (!sitting) setLying(false); // 站起时取消躺下
    }

    public boolean isSitting() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setLying(boolean lying) {
        if(lying) {
            setAnimation(ANIMATION_LYING);
        }

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
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if(getAnimation() == ANIMATION_ATTACK && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAggressive() && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                state.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }
            if (((getAnimation() == ANIMATION_MOVE || state.isMoving()) && getAttackAnimTimer() <= 0)) {
                state.getController().setAnimation(MOVING_ANIMATION);
                return PlayState.CONTINUE;
            }
            if (getAnimation() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && !state.isMoving()) {
                state.getController().setAnimation(IDLING_ANIMATION);
                return PlayState.CONTINUE;
            }
            if (getAnimation() == ANIMATION_SIT || isSitting()) {
                state.getController().setAnimation(SITTING_ANIMATION);
                return PlayState.CONTINUE;
            }
            if (getAnimation() == ANIMATION_LYING || isLying()) {
                state.getController().setAnimation(SITTING_ANIMATION);
                return PlayState.CONTINUE;
            }
            if (getAnimation() == ANIMATION_IDLE && getAttackAnimTimer() > 0) {
                setAnimation(ANIMATION_ATTACK);
                return PlayState.STOP;
            }
            return PlayState.CONTINUE;
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
    private ItemStack headArmor = ItemStack.EMPTY;

    // ✅ 改为从 entityData 中读取同步状态
    public boolean hasHeadArmor() {
        return this.entityData.get(DATA_HAS_HAT);
    }

    public ItemStack getHeadArmor() {
        return headArmor;
    }

    // ✅ 设置本地字段 + 同步状态给客户端
    public void setHeadArmor(ItemStack stack) {
        this.headArmor = stack;
        this.entityData.set(DATA_HAS_HAT, !stack.isEmpty());
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        // 保存坐下、躺下状态
        tag.putBoolean("Sitting", isSitting());
        tag.putBoolean("Lying", isLying());

        // 保存帽子
        if (!headArmor.isEmpty()) {
            tag.put("HeadArmor", headArmor.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        // 恢复坐下、躺下状态
        setSitting(tag.getBoolean("Sitting"));
        setLying(tag.getBoolean("Lying"));

        // 恢复帽子
        if (tag.contains("HeadArmor", Tag.TAG_COMPOUND)) {
            ItemStack loaded = ItemStack.of(tag.getCompound("HeadArmor"));
            setHeadArmor(loaded);
        } else {
            setHeadArmor(ItemStack.EMPTY); // 防止 NULL
        }
    }}



