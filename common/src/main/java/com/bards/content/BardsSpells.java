package com.bards.content;

import com.bards.effect.BardsEffects;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
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
        spell.range = 5;
        spell.tier = 1;
        spell.group = GROUP_PRIMARY;

        return new Entry(id, spell, title, description, null);
    }
    public static final Entry troubadours_minuet = add(troubadours_minuet());
    private static Entry troubadours_minuet() {
        var id = Identifier.of(MOD_ID, "troubadours_minuet");
        var title = "Troubadour's Minuet";
        var description = "";
        var stashEffect = BardsEffects.TROUBADOURS_MINUET;
        var buffEffect = BardsEffects.TROUBADOURS_MINUET_BUFF;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 5;
        spell.tier = 2;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 0.5F;
        spell.active.cast.animation = "spell_engine:one_handed_area_charge";
        spell.active.cast.sound =  new Sound("");

        var stashTrigger = SpellBuilder.Triggers.effectTick(stashEffect.id.toString());
        SpellBuilder.Deliver.stash(spell, stashEffect.id.toString(), 8.0F, List.of(stashTrigger));
        spell.deliver.stash_effect.consume = 0;

        var impact = SpellBuilder.Impacts.heal(0.3F);
        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),10,1,5);
        spell.impacts = List.of(impact, buff);
        var areaImpact = new Spell.AreaImpact();
        areaImpact.radius = 5F;
        areaImpact.area.include_caster = true;
        spell.area_impact = areaImpact;

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, null);
    }
    public static final Entry encore = add(encore());
    private static Entry encore() {
        var id = Identifier.of(MOD_ID, "encore");
        var title = "Encore";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 12;
        spell.tier = 3;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 0.5F;
        spell.active.cast.animation = "spell_engine:one_handed_area_charge";
        spell.active.cast.sound =  new Sound("");
        //spell.active.cast.particles = new ParticleBatch[] {};

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        spell.release = new Spell.Release();
        spell.release.animation = "spell_engine:one_handed_area_release";
        spell.release.sound = new Sound("");

        var damage = SpellBuilder.Impacts.damage(0.6F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        30, 0.2F, 0.7F)
                        .color(Color.HOLY.toRGBA())
        };
        //damage.sound = new Sound("");

        Spell.Impact cooldown = new Spell.Impact();
        cooldown.action = new Spell.Impact.Action();
        cooldown.action.type = net.spell_engine.api.spell.Spell.Impact.Action.Type.COOLDOWN;
        cooldown.action.cooldown = new Spell.Impact.Action.Cooldown();
        cooldown.action.cooldown.actives = new Spell.Impact.Action.Cooldown.Modify();
        cooldown.action.cooldown.actives.duration_multiplier = 0.8F;

        spell.impacts = List.of(damage, cooldown);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, null);
    }
    public static final Entry armys_paeon = add(armys_paeon());
    private static Entry armys_paeon() {
        var id = Identifier.of(MOD_ID, "armys_paeon");
        var title = "Army's Paeon";
        var description = "";
        var effect = BardsEffects.ARMYS_PAEON;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 0;
        spell.tier = 4;

        /*
        spell.release.animation = "spell_engine:dual_handed_weapon_charge";
        spell.release.sound = new Sound("");
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        15, 0.15F, 0.2F)
                        .preSpawnTravel(7)
                        .invert()
                        .color(Color.WHITE.toRGBA())
        };

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        var stashMeleeTrigger = new Spell.Trigger();
        stashMeleeTrigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        stashMeleeTrigger.target_override = Spell.Trigger.TargetSelector.AOE_SOURCE;

        spell.deliver.stash_effect.triggers = List.of(stashMeleeTrigger);


        spell.impacts = List.of();

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);
         */

        return new Entry(id, spell, title, description, null);
    }



}
