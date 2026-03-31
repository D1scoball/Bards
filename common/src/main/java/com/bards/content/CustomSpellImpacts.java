package com.bards.content;

import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.event.SpellHandlers;

import static com.bards.BardsMod.MOD_ID;

public class CustomSpellImpacts {
    public static void registerCustomImpacts(){
        SpellHandlers.registerCustomImpact(
                Identifier.of(MOD_ID, "armies_paeon_impact"),
                new ArmiesPaeonImpact()
        );
        SpellHandlers.registerCustomImpact(
                Identifier.of(MOD_ID, "spellthief_impact"),
                new SpellthiefImpact()
        );
    }
}
