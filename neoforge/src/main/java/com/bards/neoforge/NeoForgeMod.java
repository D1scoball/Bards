package com.bards.neoforge;

import com.bards.worldgen.villages.BardVillagerProfessions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
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
        event.register(RegistryKeys.BLOCK, reg -> {
            BardsMod.registerBlocks();
        });
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
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            BardVillagerProfessions.poiRegistrar = (id, block) -> {
                var states = ImmutableSet.copyOf(block.getStateManager().getStates());
                var poi = new PointOfInterestType(states, 1, 1);
                reg.register(id, poi);
                return poi;
            };
            BardsMod.registerVillagePoi();
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            BardsMod.registerVillageProfessions();
        });
        event.register(RegistryKeys.SCHEDULE, reg -> {
            BardsMod.registerVillageSchedules();
        });
    }
}
