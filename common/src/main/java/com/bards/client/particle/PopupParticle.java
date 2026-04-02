package com.bards.client.particle;

import com.bards.content.PopupParticleEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PopupParticle extends SpriteBillboardParticle {
    private static final int POP_UP_TICKS = 8;
    private static final int STAY_TICKS = 40;
    private static final int SHRINK_TICKS = 6;
    private static final float BASE_SCALE = 0.5f;
    private static final float MAX_SCALE = 0.55f;

    private static final ParticleTextureSheet MOB_EFFECTS_SHEET = new ParticleTextureSheet() {
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.setShaderTexture(0, Identifier.ofVanilla("textures/atlas/mob_effects.png"));
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }
    };

    private final int entityId;
    private float currentOffsetY = 0f;

    private PopupParticle(ClientWorld world, double x, double y, double z, PopupParticleEffect effect) {
        super(world, x, y, z);
        this.entityId = effect.entityId;
        this.maxAge = POP_UP_TICKS + STAY_TICKS + SHRINK_TICKS;
        this.scale = 0f;
        this.collidesWithWorld = false;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;

        Optional<RegistryEntry.Reference<StatusEffect>> effectEntry = world.getRegistryManager()
            .get(RegistryKeys.STATUS_EFFECT)
            .getEntry(effect.effectId);
        effectEntry.ifPresent(entry -> {
            var sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(entry);
            if (sprite != null) {
                setSprite(sprite);
            }
        });
        if (this.sprite == null) {
            this.markDead();
        }
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        Entity entity = this.world.getEntityById(entityId);
        if (entity == null) {
            this.markDead();
            return;
        }

        if (this.age < POP_UP_TICKS) {
            float t = (float) this.age / POP_UP_TICKS;
            currentOffsetY = t * 0.5f;
            this.scale = BASE_SCALE * t;
        } else if (this.age < POP_UP_TICKS + STAY_TICKS) {
            float t = (float) (this.age - POP_UP_TICKS) / STAY_TICKS;
            currentOffsetY = 0.5f;
            this.scale = BASE_SCALE + (MAX_SCALE - BASE_SCALE) * t;
        } else {
            float t = (float) (this.age - POP_UP_TICKS - STAY_TICKS) / SHRINK_TICKS;
            currentOffsetY = 0.5f;
            this.scale = MAX_SCALE * (1f - t);
            this.alpha = 1f - t;
        }

        this.x = entity.getX();
        this.y = entity.getY() + entity.getHeight() + 0.3 + currentOffsetY;
        this.z = entity.getZ();

        this.age++;
        if (this.age >= this.maxAge) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return MOB_EFFECTS_SHEET;
    }

    public static class Factory implements ParticleFactory<PopupParticleEffect> {
        @Override
        public Particle createParticle(PopupParticleEffect effect, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
            return new PopupParticle(world, x, y, z, effect);
        }
    }
}
