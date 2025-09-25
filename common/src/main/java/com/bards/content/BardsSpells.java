package com.bards.content;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

import static com.bards.BardsMod.MOD_ID;

public class BardsSpells {

    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) { }
    public static final List<Entry> entries = new ArrayList<>();
    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Spell.Impact.TargetModifier createImpactModifier(String entityType) {
        var condition = new Spell.TargetCondition();
        condition.entity_type = entityType;
        var modifier = new Spell.Impact.TargetModifier();
        modifier.conditions = List.of(condition);
        return modifier;
    }

    private static void impactDeniedForMechanical(Spell.Impact impact) {
        var modifier = createImpactModifier("#spell_engine:mechanical");
        modifier.execute = TriState.DENY;
        impact.target_modifiers = List.of(modifier);
    }
    private static Spell.Impact.TargetModifier extraDamageAgainstUndead() {
        var modifier = createImpactModifier("#minecraft:undead");
        var powerModifier = new Spell.Impact.Modifier();
        powerModifier.power_multiplier = 0.5F;
        modifier.modifier = powerModifier;
        return modifier;
    }
    private static final String GROUP_PRIMARY = "primary";

    private static Spell.Impact.TargetModifier extraCritAgainstUndead() {
        var modifier = createImpactModifier("#minecraft:undead");
        var powerModifier = new Spell.Impact.Modifier();
        powerModifier.critical_chance_bonus = 1F;
        modifier.modifier = powerModifier;
        return modifier;
    }

    public static final Entry ballad = add(ballad());
    private static Entry ballad() {
        var id = Identifier.of(MOD_ID, "ballad");
        var title = "Ballad";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 0;
        spell.tier = 1;
        spell.group = GROUP_PRIMARY;


        return new Entry(id, spell, title, description, null);
    }
    public static final Entry troubadours_minuet = add(troubadours_minuet());
    private static Entry troubadours_minuet() {
        var id = Identifier.of(MOD_ID, "troubadours_minuet");
        var title = "Troubadour's Minuet";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 0;
        spell.tier = 2;


        return new Entry(id, spell, title, description, null);
    }
    public static final Entry encore = add(encore());
    private static Entry encore() {
        var id = Identifier.of(MOD_ID, "encore");
        var title = "Encore";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 0;
        spell.tier = 3;


        return new Entry(id, spell, title, description, null);
    }
    public static final Entry armys_paeon = add(armys_paeon());
    private static Entry armys_paeon() {
        var id = Identifier.of(MOD_ID, "armys_paeon");
        var title = "Army's Paeon";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 0;
        spell.tier = 4;


        return new Entry(id, spell, title, description, null);
    }



}
