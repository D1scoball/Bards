package com.bards.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

public class PopupParticleEffect implements ParticleEffect {
    private final ParticleType<PopupParticleEffect> type;
    public final Identifier effectId;
    public final int entityId;

    public PopupParticleEffect(ParticleType<PopupParticleEffect> type, Identifier effectId, int entityId) {
        this.type = type;
        this.effectId = effectId;
        this.entityId = entityId;
    }

    @Override
    public ParticleType<PopupParticleEffect> getType() {
        return type;
    }

    public static MapCodec<PopupParticleEffect> createCodec(ParticleType<PopupParticleEffect> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("effect").forGetter(e -> e.effectId),
            Codec.INT.fieldOf("entity").forGetter(e -> e.entityId)
        ).apply(instance, (effectId, entityId) -> new PopupParticleEffect(type, effectId, entityId)));
    }

    public static PacketCodec<RegistryByteBuf, PopupParticleEffect> createPacketCodec(ParticleType<PopupParticleEffect> type) {
        return PacketCodec.tuple(
            Identifier.PACKET_CODEC, e -> e.effectId,
            PacketCodecs.VAR_INT, e -> e.entityId,
            (effectId, entityId) -> new PopupParticleEffect(type, effectId, entityId)
        );
    }
}
