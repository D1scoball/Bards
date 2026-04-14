package com.bards.fabric;

import net.fabricmc.api.ModInitializer;

import com.bards.BardsMod;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BardsMod.init();
        BardsMod.registerBlocks();
        BardsMod.registerEffects();
        BardsMod.registerItems();
        BardsMod.registerSounds();
        BardsMod.registerParticles();
        BardsMod.registerVillagePoi();
        BardsMod.registerVillageProfessions();
        BardsMod.registerVillageSchedules();
        BardsMod.registerVillageTrades();
    }
}
