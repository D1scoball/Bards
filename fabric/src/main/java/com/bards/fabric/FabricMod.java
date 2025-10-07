package com.bards.fabric;

import net.fabricmc.api.ModInitializer;

import com.bards.BardsMod;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BardsMod.init();
        BardsMod.registerEffects();
        BardsMod.registerItems();
    }
}
