package com.bards.neoforge;

import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import com.bards.BardsMod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(BardsMod.MOD_ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        BardsMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
    }
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            BardsMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            BardsMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            BardsMod.registerEffects();
        });
        event.register(RegistryKeys.PARTICLE_TYPE, reg -> {
            BardsMod.registerParticles();
        });
    }
}
