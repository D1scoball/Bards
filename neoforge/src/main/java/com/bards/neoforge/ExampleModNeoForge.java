package com.bards.neoforge;

import net.neoforged.fml.common.Mod;

import com.bards.BardsMod;

@Mod(BardsMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        BardsMod.init();
    }
}
