package com.bards.content;

import com.bards.effect.BardsEffects;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.bards.BardsMod.MOD_ID;

public class BardsSpells {
    public enum Book { BARD}
    public enum WeaponGroup { LUTE, LYRE }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator,
                        @Nullable List<WeaponGroup> weaponGroups,
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null,List.of(), null);
        }
        public Entry mutator(SpellTooltip.DescriptionMutator mutator) {
            return new Entry(id, spell, title, description, mutator,weaponGroups ,book);
        }
        public Entry weaponGroup(WeaponGroup weaponGroup) {
            var newGroups = new ArrayList<>(weaponGroups != null ? weaponGroups : List.of());
            newGroups.add(weaponGroup);
            return new Entry(id, spell, title, description, mutator, newGroups, book);
        }
        public Entry book(Book book) {
            return new Entry(id, spell, title, description, mutator, weaponGroups,book);
        }
    }

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

    private static ParticleBatch musicParticles(Float particleCount) {
        return new ParticleBatch(
                "more_rpg_classes:music_note",
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                particleCount, 0.4F, 0.5F);
    }
    private static ParticleBatch musicImpactParticles(Float particleCount) {
        return new ParticleBatch(
                "more_rpg_classes:music_note",
                ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                particleCount, 0.6F, 0.8F);
    }

    public static final Entry troubadours_minuet = add(troubadours_minuet());
    private static Entry troubadours_minuet() {
        var id = Identifier.of(MOD_ID, "troubadours_minuet");
        var title = "Troubadour's Minuet";
        var description = "Song that deals {damage} damage to enemies, heals by {heal} and reduces incoming damage by {bonus} for allies, can be stacked {amplifier_cap} times.";
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
        var spellColor = BRIGHT_GREEN.toRGBA();

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 5.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.channel_ticks = 10;
        spell.active.cast.animation = new PlayerAnimation("spell_engine:one_handed_area_charge");
        spell.active.cast.sound =  new Sound(BardsSounds.troubadours_minuet.id());
        spell.active.cast.particles = new ParticleBatch[] {
                musicParticles(0.5F).color(spellColor).extent(2.0F)
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 1.5F;
        spell.target.area.include_caster = true;

        var damage = SpellBuilder.Impacts.damage(0.65F);
        damage.particles = new ParticleBatch[]{
        new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                SpellEngineParticles.MagicParticles.Shape.SPELL,
                SpellEngineParticles.MagicParticles.Motion.BURST
                ).id().toString(),
                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                10, 0.5F, 0.5F)
                .color(spellColor),};
        damage.sound = new Sound(BardsSounds.bard_impact.id());

        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),10,1,3);
        buff.school = SpellSchools.HEALING;
        buff.action.status_effect.amplifier_cap_power_multiplier = 0.1F;
        buff.action.status_effect.refresh_duration = false;
        buff.particles = new ParticleBatch[]{
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };

        var heal  = SpellBuilder.Impacts.heal(0.2F);
        heal.school = SpellSchools.HEALING;
        heal.particles = new ParticleBatch[]{
                new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.HEAL,
                        SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                        5, 0.1F, 0.3F).extent(1.0F)
                        .color(spellColor),
        };
        //heal.sound = new Sound();

        spell.impacts = List.of(damage, buff,heal);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).mutator(mutator).weaponGroup(WeaponGroup.LUTE);
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
        var spellColor = Color.ARCANE.toRGBA();

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 4.0F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.animation = new PlayerAnimation("spell_engine:one_handed_area_charge");
        spell.active.cast.sound = new Sound(BardsSounds.magical_ballad.id());
        spell.active.cast.channel_ticks = 4;
        spell.active.cast.particles = new ParticleBatch[]{
                musicParticles(0.5F).color(spellColor).extent(2.0F)
        };

        spell.release = new Spell.Release();
        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.5F;

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 0F;
        projectile.perks.pierce = 3;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "bards_rpg:spell_projectile/magical_ballad";
        projectile.client_data.model.scale = 2.0F;
        projectile.client_data.model.rotate_degrees_per_tick = 0F;
        projectile.client_data.model.rotate_degrees_offset = 0F;
        projectile.client_data.model.light_emission = LightEmission.RADIATE;
        projectile.hitbox = new Spell.ProjectileData.HitBox(0.6F, 0.8F);
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.75F, 1.0F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.1F, 0.25F).color(Color.ARCANE.toRGBA()),
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };
        damage.sound = new Sound(BardsSounds.bard_impact.id());

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
                        15, 0.1F, 0.25F).color(Color.ARCANE.toRGBA()),
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };
        buff.sound = new Sound(BardsSounds.bard_buff.id());

        spell.impacts = List.of(damage, buff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).mutator(mutator).book(Book.BARD);
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
        var spellColor = CYAN.toRGBA();

        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.25F;
        spell.active.cast.movement_speed = 1.5F;
        spell.active.cast.animation = new PlayerAnimation("spell_engine:one_handed_area_charge");
        spell.active.cast.sound =  new Sound(BardsSounds.encore_channel.id());

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;
        spell.target.area.include_caster = true;

        spell.release = new Spell.Release();
        spell.release.animation = new PlayerAnimation("spell_engine:one_handed_area_release");
        spell.release.particles = new ParticleBatch[]{
                musicParticles(10F).color(spellColor).extent(2.0F)
        };

        var damage = SpellBuilder.Impacts.damage(0.6F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.BURST
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        30, 0.2F, 0.7F)
                        .color(CYAN.toRGBA()),
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };
        damage.sound = new Sound(BardsSounds.bard_impact.id());

        Spell.Impact cooldown = new Spell.Impact();
        cooldown.action = new Spell.Impact.Action();
        cooldown.action.type = Spell.Impact.Action.Type.COOLDOWN;
        cooldown.action.cooldown = new Spell.Impact.Action.Cooldown();
        cooldown.action.cooldown.actives = new Spell.Impact.Action.Cooldown.Modify();
        cooldown.action.cooldown.actives.duration_multiplier = 0.8F;
        cooldown.particles = new ParticleBatch[] {
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };
        cooldown.sound = new Sound(BardsSounds.encore_cooldown_impact.id());

        spell.impacts = List.of(damage, cooldown);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).book(Book.BARD);
    }
    public static final Entry armys_paeon = add(armys_paeon());
    private static Entry armys_paeon() {
        var id = Identifier.of(MOD_ID, "armys_paeon");
        var title = "Army's Paeon";
        var description = "Buff nearby allies for {effect_duration} sec, enhance their strength if you damage enemies. The effect can be stacked {amplifier_cap} times.";
        var stashEffect = BardsEffects.ARMYS_PAEON_STASH;
        var buffEffect = BardsEffects.ARMYS_PAEON;

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 5;
        spell.tier = 4;
        var spellColor = GOLD.toRGBA();

        spell.release.animation = new PlayerAnimation("spell_engine:one_handed_area_release");
        spell.release.particles = new ParticleBatch[]{
                musicParticles(10F).color(spellColor).extent(2.0F)
        };
        spell.release.sound = new Sound(BardsSounds.armys_paeon_release.id());

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = stashEffect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        spell.deliver.stash_effect.triggers = List.of(armiesPaeonMelee(), armiesPaeonRanged(), armiesPaeonSpell());

        var buff = SpellBuilder.Impacts.effectAdd(buffEffect.id.toString(),8,1,3);
        buff.sound = new Sound(BardsSounds.armys_paeon_impact.id());
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
                        .color(GOLD.toRGBA()),
                musicImpactParticles(0.5F).extent(1.5F).color(spellColor)
        };
        buff.sound = new Sound(BardsSounds.armys_paeon_buff.id());

        spell.impacts = List.of(buff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).book(Book.BARD);
    }
    public static final Entry crescendo = add(crescendo());
    private static Entry crescendo() {
        var id = Identifier.of(MOD_ID, "crescendo");
        var title = "Crescendo";
        var debuffEffect = BardsEffects.CRESCENDO;
        var description = "Strikes an irresistible chord, stunning any enemy it passes through, dealing {damage} damage." +
                "Also increases incoming damage by {bonus} per stack.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = debuffEffect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };

        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 30;
        spell.tier = 4;
        var spellColor = GOLD.toRGBA();

        spell.learn = new Spell.Learn();

        spell.release = new Spell.Release();
        spell.release.particles = new ParticleBatch[]{
                musicParticles(4.0F).color(spellColor).extent(2.0F)
        };
        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 2.0F;

        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 0F;
        projectile.perks.pierce = 3;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "bards_rpg:spell_projectile/crescendo";
        projectile.client_data.model.scale = 1.5F;
        projectile.client_data.model.rotate_degrees_per_tick = 0F;
        projectile.client_data.model.rotate_degrees_offset = 0F;
        projectile.client_data.model.light_emission = LightEmission.GLOW;
        projectile.hitbox = new Spell.ProjectileData.HitBox(0.6F, 0.8F);
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(1.0F, 0.0F);
        damage.particles = new ParticleBatch[] {
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor),
        };
        //CHANGE SOUND
        damage.sound = new Sound(BardsSounds.bard_impact.id());

        var debuff = SpellBuilder.Impacts.effectAdd(BardsEffects.BALLAD.id.toString(),8,1,3);
        debuff.school = SpellSchools.HEALING;
        debuff.action.status_effect.amplifier_cap_power_multiplier = 0.15F;
        debuff.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        15, 0.1F, 0.25F).color(Color.ARCANE.toRGBA()),
                musicImpactParticles(0.5F).extent(0.5F).color(spellColor)
        };
        //CHANGE SOUND
        debuff.sound = new Sound(BardsSounds.bard_buff.id());

        spell.impacts = List.of(damage, debuff);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).mutator(mutator).book(Book.BARD);
    }
    /// HELPER IMPACTS
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
        var spellColor = GOLD.toRGBA();

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
                musicImpactParticles(0.5F).extent(0.25F).color(spellColor)
        };

        spell.impacts = List.of(custom);

        SpellBuilder.Cost.cooldown(spell, 1);

        return new Entry(id, spell, title, description);
    }
    /// MODIFIERS
    public static final Entry improved_encore= add(improved_encore());
    private static Entry improved_encore() {
        var id = Identifier.of(MOD_ID, "improved_encore");
        var title = "Improved Encore";
        var description = "Increases the range of Encore by {range_add}";
        var spell = new Spell();
        spell.school = SpellSchools.ARCANE;
        spell.range = 0;
        spell.tier = 1;

        spell.type = Spell.Type.MODIFIER;

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.show_header = false;
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, false);
        spell.tooltip.description.color = "gray";
        spell.tooltip.description.show_in_compact = true;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "bards_rpg:encore";
        modifier.range_add = 2.0F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description);
    }
    /// PASSIVE SPELLS
    public static Entry spellthief = add(spellthief());
    private static Entry spellthief() {
        var id = Identifier.of(MOD_ID, "spellthief");
        var title = "Spellthief";
        var description = "On spell impact: {trigger_chance} chance to steal beneficial status effects and to cast a random spell from the damaged entity.";
        var spell = SpellBuilder.createSpellPassive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 7F;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.chance = 0.2F;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.include_caster = true;
        spell.target.area.angle_degrees = 360.0F;

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "bards_rpg:spellthief_impact";
        spell.impacts = List.of(custom);

        SpellBuilder.Cost.cooldown(spell,5.0F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description);
    }
    public static Entry melody_of_the_meteor = add(melody_of_the_meteor());
    private static Entry melody_of_the_meteor() {
        var id = Identifier.of(MOD_ID, "melody_of_the_meteor");
        var title = "Melody of the Meteor";
        var description = "On spell impact: {trigger_chance} to spawn ruby meteorites above the target, dealing {damage} damage.";
        var spell = SpellBuilder.createSpellPassive();
        spell.school = SpellSchools.ARCANE;
        spell.range = 10F;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.chance = 0.3F;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        spell.deliver.type = Spell.Delivery.Type.METEOR;
        spell.deliver.meteor = new Spell.Delivery.Meteor();
        spell.deliver.meteor.launch_height = 7;
        spell.deliver.meteor.launch_radius = 4;
        spell.deliver.meteor.launch_properties.velocity = 0.9F;
        spell.deliver.meteor.launch_properties.extra_launch_count = 2;
        spell.deliver.meteor.launch_properties.extra_launch_delay = 3;


        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.light_level = 12;
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(Color.RED.toRGBA())
        };
        projectile.client_data.model = new Spell.ProjectileModel();
        projectile.client_data.model.model_id = "bards_rpg:spell_projectile/melody_of_the_meteor";
        projectile.client_data.model.scale = 2F;
        spell.deliver.meteor.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.3F, 0.3F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.ARCANE,
                                SpellEngineParticles.MagicParticles.Motion.BURST).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        null, 20, 0.2F, 0.7F, 0.0F, 0F)
                        .color(Color.RED.toRGBA())
        };
        //damage.sound = new Sound("");
        spell.impacts = List.of(damage);

        SpellBuilder.Cost.cooldown(spell,5.0F);

        return new Entry(id, spell, title, description);
    }
    public static Entry song_of_sun_and_moon = add(song_of_sun_and_moon());
    private static Entry song_of_sun_and_moon() {
        var id = Identifier.of(MOD_ID, "song_of_sun_and_moon");
        var title = "Song of Sun and Moon";
        var description = "On effect applied: {trigger_chance} chance to heal your ally by {heal} hearts.";
        var spell = SpellBuilder.createSpellPassive();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.chance = 0.25F;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.STATUS_EFFECT.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var heal = new Spell.Impact();
        heal.action = new Spell.Impact.Action();
        heal.action.type = Spell.Impact.Action.Type.HEAL;
        heal.action.heal = new Spell.Impact.Action.Heal();
        heal.action.heal.spell_power_coefficient = 0.25F;
        heal.particles = new ParticleBatch[]{
                new ParticleBatch( SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        20, 0.1F, 0.1F)
                        .color(Color.WHITE.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.HOLY,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE
                        ).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.25F)
                        .color(GOLD.toRGBA())
        };
        heal.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_IMPACT_3.id().toString());
        spell.impacts = List.of(heal);

        SpellBuilder.Cost.cooldown(spell,5.0F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description);
    }
}
