package com.shinobunoinu.shinobu.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.SimpleParticleType;

public class ZzzParticle extends TextureSheetParticle {

    protected ZzzParticle(ClientLevel level, double x, double y, double z,
                          double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.gravity = 0f;
        this.lifetime = 40 + this.random.nextInt(20); // 2~3秒寿命
        this.setSize(0.2F, 0.2F);
        this.hasPhysics = false;

        // 🚫 重点：完全禁用传入的速度，自己指定速度
        this.xd = (random.nextDouble() - 0.5D) * 0.01D;  // 轻微左右飘
        this.yd = 0.002D;  // 缓慢上升
        this.zd = (random.nextDouble() - 0.5D) * 0.01D;
    }

    @Override
    public void tick() {
        super.tick();
        // ✅ 让粒子渐渐淡出
        this.alpha = 1.0F - (float) this.age / this.lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            ZzzParticle particle = new ZzzParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}