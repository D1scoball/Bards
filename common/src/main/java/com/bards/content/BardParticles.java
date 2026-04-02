package com.bards.content;

import com.bards.BardsMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BardParticles {
    public static ParticleType<PopupParticleEffect> SPELL_STOLEN_POPUP;

    public static void register() {
        SPELL_STOLEN_POPUP = Registry.register(
            Registries.PARTICLE_TYPE,
            BardsMod.id("spell_stolen_popup"),
            new ParticleType<PopupParticleEffect>(false) {
                @Override
                public MapCodec<PopupParticleEffect> getCodec() {
                    return PopupParticleEffect.createCodec(this);
                }
                @Override
                public PacketCodec<? super RegistryByteBuf, PopupParticleEffect> getPacketCodec() {
                    return PopupParticleEffect.createPacketCodec(this);
                }
            }
        );
    }
}
