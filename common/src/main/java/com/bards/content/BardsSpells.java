package com.bards.content;

import com.bards.effect.BardsEffects;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.target.SpellTarget;
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

    public static Spell.Trigger armiesPaeonMelee() {
        Spell.Trigger trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        return trigger;
    }
    public static Spell.Trigger armiesPaeonRanged() {
        Spell.Trigger trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        return trigger;
    }
    public static Spell.Trigger armiesPaeonSpell() {
        Spell.Trigger trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        return trigger;
    }
    public static final Color GOLD = Color.from(0xffd700);
    public static final Color CYAN = Color.from(0x00ffff);
    public static final Color BRIGHT_GREEN = Color.from(0x8efea1);

    public static final Entry troubadours_minuet = add(troubadours_minuet());
    private static Entry troubadours_minuet() {
        var id = Identifier.of(MOD_ID, "troubadours_minuet");
        var title = "Troubadour's Minuet";
        var description = "Song that deals {damage} damage to enemies and reduces incoming damage by {bonus}, can be stacked {amplifier_cap} times.";
        var buffEffect = BardsEffects.TROUBADOURS_MINUET;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = buffEffect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 5;
        spell.tier = 1;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 5.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.channel_ticks = 10;
        spell.active.cast.animation = "bards_rpg:lute_channel";
        spell.active.cast.sound =  new Sound("");

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 1.5F;

        var impact = SpellBuilder.Impacts.damage(0.5F);
        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),10,1,3);
        buff.school = SpellSchools.HEALING;
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.1F;
        buff.particles = new ParticleBatch[]{
                new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPELL,
                        SpellEngineParticles.MagicParticles.Motion.BURST
                ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        20, 0.3F, 0.5F)
                        .color(BRIGHT_GREEN.toRGBA()),
        };
        spell.impacts = List.of(impact, buff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, mutator);
    }

    public static final Entry magical_ballad = add(magical_ballad());
    private static Entry magical_ballad() {
        var id = Identifier.of(MOD_ID, "magical_ballad");
        var title = "Magical Ballad";
        var buffEffect = BardsEffects.BALLAD;
        var description = "Launch magical ballads, piercing thru {pierce} targets, dealing {damage} damage " +
                "and increase offensive stats for allies by {bonus}.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = buffEffect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 20;
        spell.tier = 2;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 4.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.animation = "bards_rpg:lute_channel";
        spell.active.cast.sound = new Sound("");
        spell.active.cast.channel_ticks = 20;
        spell.active.cast.particles = new ParticleBatch[] {
                new ParticleBatch(
                "more_rpg_classes:rainbow_music_note_1",
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                1, 0.05F, 0.1F)
        };

        spell.release = new Spell.Release();
        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;
        spell.deliver.projectile.launch_properties.sound = new Sound("");

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 0F;
        projectile.perks.pierce = 3;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "bards_rpg:projectile/magical_ballad";
        projectile.client_data.model.scale = 2.0F;
        projectile.client_data.model.rotate_degrees_per_tick = 0F;
        projectile.client_data.model.rotate_degrees_offset = 0F;
        projectile.client_data.model.light_emission = LightEmission.RADIATE;
        projectile.hitbox = new Spell.ProjectileData.HitBox(1.8F, 1.8F);
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.75F, 1.0F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.1F, 0.25F).color(Color.ARCANE.toRGBA())
        };
        damage.sound = new Sound("");

        var buff = SpellBuilder.Impacts.effectAdd(BardsEffects.BALLAD.id.toString(),8,1,3);
        buff.school = SpellSchools.HEALING;
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.15F;
        buff.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        15, 0.1F, 0.25F).color(Color.ARCANE.toRGBA())
        };

        spell.impacts = List.of(damage, buff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, mutator);
    }

    public static final Entry encore = add(encore());
    private static Entry encore() {
        var id = Identifier.of(MOD_ID, "encore");
        var title = "Encore";
        var description = "Deals {damage} damage to nearby targets and motivates allies by slightly reducing active spells cooldowns.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 12;
        spell.tier = 3;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 0.5F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.animation = "bards_rpg:lute_channel";
        spell.active.cast.sound =  new Sound("");
        //spell.active.cast.particles = new ParticleBatch[] {};

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        spell.release = new Spell.Release();
        spell.release.animation = "bards_rpg:lute_release";
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
                        .color(CYAN.toRGBA())
        };
        //damage.sound = new Sound("");

        Spell.Impact cooldown = new Spell.Impact();
        cooldown.action = new Spell.Impact.Action();
        cooldown.action.type = Spell.Impact.Action.Type.COOLDOWN;
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
        var stashEffect = BardsEffects.ARMYS_PAEON_STASH;
        var buffEffect = BardsEffects.ARMYS_PAEON;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 5;
        spell.tier = 4;

        spell.release.animation = "bards_rpg:lute_release";
        spell.release.sound = new Sound("");
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        15, 0.15F, 0.2F)
                        .preSpawnTravel(7)
                        .invert()
                        .color(GOLD.toRGBA())
        };

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = stashEffect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = List.of(armiesPaeonMelee(), armiesPaeonRanged(), armiesPaeonSpell());

        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),8,1,3);
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.15F;
        buff.school = SpellSchools.HEALING;
        buff.particles = new ParticleBatch[]{
                new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        30, 0.2F, 0.2F)
                        .color(GOLD.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.area_circle_1.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                        1, 0.3F, 0.3F)
                        .followEntity(true)
                        .scale(1.0F)
                        .maxAge(0.6F)
                        .color(GOLD.toRGBA())
        };

        spell.impacts = List.of(buff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, null);
    }
    public static final Entry armys_paeon_impact = add(armys_paeon_impact());
    private static Entry armys_paeon_impact() {
        var id = Identifier.of(MOD_ID, "armys_paeon_impact");
        var title = "Army's Paeon";
        var description = "";
        var stashEffect = BardsEffects.ARMYS_PAEON;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 0;
        spell.tier = 1;

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = stashEffect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = List.of(SpellBuilder.Triggers.meleeAttackImpact(), SpellBuilder.Triggers.arrowHit(),
                SpellBuilder.Triggers.spellHit(1.0F,null));

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "bards_rpg:armies_paeon_impact";
        custom.particles = new ParticleBatch[]{
                new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPELL,
                        SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        20, 0.3F, 0.5F)
                        .color(GOLD.toRGBA()),
        };

        spell.impacts = List.of(custom);

        SpellBuilder.Cost.cooldown(spell, 1);

        return new Entry(id, spell, title, description, null);
    }



}
