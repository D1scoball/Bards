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

    private static Spell.Impact.TargetModifier extraCritAgainstUndead() {
        var modifier = createImpactModifier("#minecraft:undead");
        var powerModifier = new Spell.Impact.Modifier();
        powerModifier.critical_chance_bonus = 1F;
        modifier.modifier = powerModifier;
        return modifier;
    }

    public static final Entry troubadours_minuet = add(troubadours_minuet());
    private static Entry troubadours_minuet() {
        var id = Identifier.of(MOD_ID, "troubadours_minuet");
        var title = "Troubadour's Minuet";
        var description = "";
        var buffEffect = BardsEffects.TROUBADOURS_MINUET;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 5;
        spell.tier = 1;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 5.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.channel_ticks = 10;
        spell.active.cast.animation = "spell_engine:one_handed_area_charge";
        spell.active.cast.sound =  new Sound("");

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 1.5F;

        var impact = SpellBuilder.Impacts.damage(0.5F);
        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),10,1,3);
        spell.impacts = List.of(impact, buff);
        var areaImpact = new Spell.AreaImpact();
        areaImpact.radius = 5F;
        areaImpact.area.include_caster = true;
        spell.area_impact = areaImpact;

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, null);
    }

    public static final Entry magical_ballad = add(magical_ballad());
    private static Entry magical_ballad() {
        var id = Identifier.of(MOD_ID, "magical_ballad");
        var title = "Magical Ballad";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.HEALING;
        spell.range = 20;
        spell.tier = 2;

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 4.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.animation = "spell_engine:one_handed_projectile_charge";
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
        spell.deliver.projectile.launch_properties.velocity = 1F;
        spell.deliver.projectile.launch_properties.sound = new Sound("");

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "bards_rpg:projectile/magical_ballad";
        projectile.client_data.model.scale = 3.0F;
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
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.15F;

        spell.impacts = List.of(damage, buff);

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
        spell.active.cast.movement_speed = 1.5F;
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
        spell.school = SpellSchools.HEALING;
        spell.range = 5;
        spell.tier = 4;

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

        var stashTrigger = SpellBuilder.Triggers.effectTick(stashEffect.id.toString());
        SpellBuilder.Deliver.stash(spell, stashEffect.id.toString(), 8.0F, List.of(stashTrigger));
        spell.deliver.stash_effect.consume = 0;

        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),8,1,3);
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.15F;
        spell.impacts = List.of(buff);
        var areaImpact = new Spell.AreaImpact();
        areaImpact.radius = 5F;
        areaImpact.area.include_caster = true;
        spell.area_impact = areaImpact;

        var damage = SpellBuilder.Impacts.damage(0.75F, 1.0F);

        spell.impacts = List.of(buff, damage);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description, null);
    }



}
