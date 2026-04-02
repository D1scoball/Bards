package com.bards.content;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.more_rpg_classes.entity.ISpellCasterEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.minecraft.registry.Registry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.container.SpellContainerSource;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

public class SpellthiefImpact implements SpellHandlers.CustomImpact {

    private enum DeliveryType { PROJECTILE, CLOUD, METEOR, AREA, DIRECT }

    @Override
    public SpellHandlers.ImpactResult onSpellImpact(
            RegistryEntry<Spell> spell,
            SpellPower.Result powerResult,
            LivingEntity caster,
            Entity target,
            SpellHelper.ImpactContext context
    ) {
        if (!(target instanceof LivingEntity livingTarget)) {
            return new SpellHandlers.ImpactResult(false, false);
        }

        List<StatusEffectInstance> beneficialEffects = livingTarget.getStatusEffects().stream()
                .filter(e -> e.getEffectType().value().getCategory() == StatusEffectCategory.BENEFICIAL)
                .toList();
        for (StatusEffectInstance effect : beneficialEffects) {
            caster.addStatusEffect(new StatusEffectInstance(effect));
            livingTarget.removeStatusEffect(effect.getEffectType());
        }

        if (caster.getWorld().isClient()) {
            return new SpellHandlers.ImpactResult(true, false);
        }

        RegistryEntry<Spell> stolenEntry = null;

        if (livingTarget instanceof PlayerEntity targetPlayer) {
            List<RegistryEntry<Spell>> playerSpells = SpellContainerSource.activeSpellsOf(targetPlayer);
            if (!playerSpells.isEmpty()) {
                stolenEntry = playerSpells.get(caster.getRandom().nextInt(playerSpells.size()));
            }
        } else if (livingTarget instanceof ISpellCasterEntity) {
            Identifier entityTypeId = Registries.ENTITY_TYPE.getId(livingTarget.getType());
            String folderPrefix = "mob/" + entityTypeId.getPath() + "/";
            String namespace = entityTypeId.getNamespace();
            Registry<Spell> spellRegistry = SpellRegistry.from(livingTarget.getWorld());
            List<RegistryEntry<Spell>> mobSpells = new ArrayList<>();
            spellRegistry.streamTags()
                .filter(tag -> tag.id().getNamespace().equals(namespace) && tag.id().getPath().startsWith(folderPrefix))
                .forEach(tag -> spellRegistry.getEntryList(tag).ifPresent(list -> list.forEach(mobSpells::add)));
            if (!mobSpells.isEmpty()) {
                stolenEntry = mobSpells.get(caster.getRandom().nextInt(mobSpells.size()));
            }
        }

        if (stolenEntry == null) {
            return new SpellHandlers.ImpactResult(true, false);
        }

        castStolenSpell(caster, livingTarget, stolenEntry);

        String spellName = formatSpellName(stolenEntry.getKey().get().getValue());
        String entityName = livingTarget.getDisplayName().getString();
        if (caster instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("Stole and casted " + spellName + " from " + entityName), true);
        }

        Identifier effectId = findEffectId(stolenEntry.value());
        if (effectId != null && caster.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                new PopupParticleEffect(BardParticles.SPELL_STOLEN_POPUP, effectId, caster.getId()),
                caster.getX(), caster.getEyeY() + 0.2, caster.getZ(),
                1, 0, 0, 0, 0
            );
        }

        return new SpellHandlers.ImpactResult(true, false);
    }

    private void castStolenSpell(LivingEntity caster, LivingEntity target, RegistryEntry<Spell> spellEntry) {
        Spell stolenSpell = spellEntry.value();
        SpellPower.Result arcane = SpellPower.getSpellPower(SpellSchools.ARCANE, caster);
        SpellPower.Result healing = SpellPower.getSpellPower(SpellSchools.HEALING, caster);
        SpellPower.Result power = arcane.baseValue() >= healing.baseValue() ? arcane : healing;

        if (stolenSpell.release != null) {
            ParticleHelper.sendBatches(caster, stolenSpell.release.particles);
            SoundHelper.playSound(caster.getWorld(), caster, stolenSpell.release.sound);
        }

        switch (deriveDelivery(stolenSpell)) {
            case PROJECTILE -> {
                SpellHelper.ImpactContext ctx = new SpellHelper.ImpactContext()
                        .power(power).position(caster.getEyePos()).target(SpellHelper.focusMode(stolenSpell));
                SpellHelper.shootProjectile(caster.getWorld(), caster, target, spellEntry, ctx, 0);
            }
            case CLOUD -> {
                boolean aimRequired = stolenSpell.target != null && stolenSpell.target.aim != null && stolenSpell.target.aim.required;
                LivingEntity cloudTarget = aimRequired ? target : caster;
                Vec3d pos = cloudTarget.getPos();
                SpellHelper.ImpactContext ctx = new SpellHelper.ImpactContext()
                        .power(power).position(caster.getEyePos()).target(SpellTarget.FocusMode.AREA);
                SpellHelper.placeCloud(caster.getWorld(), caster, cloudTarget, pos, spellEntry, ctx);
            }
            case METEOR -> {
                boolean aimRequired = stolenSpell.target != null && stolenSpell.target.aim != null && stolenSpell.target.aim.required;
                LivingEntity meteorTarget = aimRequired ? target : caster;
                Vec3d pos = meteorTarget.getPos();
                SpellHelper.ImpactContext ctx = new SpellHelper.ImpactContext()
                        .power(power).position(pos).target(SpellTarget.FocusMode.AREA);
                try {
                    SpellHelper.fallProjectile(caster.getWorld(), caster, meteorTarget, pos, spellEntry, ctx);
                } catch (Exception e) {
                    SpellHelper.performImpacts(caster.getWorld(), caster, meteorTarget, caster, spellEntry,
                            stolenSpell.impacts, ctx, false, null);
                }
            }
            case AREA -> {
                SpellHelper.ImpactContext ctx = new SpellHelper.ImpactContext()
                        .power(power).position(caster.getPos()).target(SpellTarget.FocusMode.AREA);
                if (hasSpawnImpact(stolenSpell)) {
                    SpellHelper.ImpactContext spawnCtx = new SpellHelper.ImpactContext()
                            .power(power).position(caster.getPos()).target(SpellTarget.FocusMode.DIRECT);
                    SpellHelper.performImpacts(caster.getWorld(), caster, caster, caster, spellEntry,
                            stolenSpell.impacts, spawnCtx, false, Spell.Impact.Action.Type.SPAWN);
                }
                Spell.Target.Area area = stolenSpell.target != null ? stolenSpell.target.area : null;
                for (Entity t : TargetHelper.targetsFromArea(caster, stolenSpell.range, area, e -> e != caster)) {
                    SpellHelper.performImpacts(caster.getWorld(), caster, t, caster, spellEntry,
                            stolenSpell.impacts, ctx, false, null);
                }
            }
            case DIRECT -> {
                boolean harmfulCustomSpawn = hasHarmfulCustomImpact(stolenSpell);
                Vec3d contextPos = harmfulCustomSpawn ? target.getPos() : caster.getEyePos();
                SpellHelper.ImpactContext ctx = new SpellHelper.ImpactContext()
                        .power(power).position(contextPos).target(SpellHelper.focusMode(stolenSpell));
                if (hasSpawnImpact(stolenSpell)) {
                    Vec3d spawnPos = harmfulCustomSpawn ? target.getPos() : caster.getPos();
                    SpellHelper.ImpactContext spawnCtx = new SpellHelper.ImpactContext()
                            .power(power).position(spawnPos).target(SpellTarget.FocusMode.DIRECT);
                    SpellHelper.performImpacts(caster.getWorld(), caster, caster, caster, spellEntry,
                            stolenSpell.impacts, spawnCtx, false, Spell.Impact.Action.Type.SPAWN);
                }
                SpellHelper.performImpacts(caster.getWorld(), caster, target, caster, spellEntry,
                        stolenSpell.impacts, ctx, false, null);
            }
        }
    }

    private DeliveryType deriveDelivery(Spell spell) {
        if (spell.target != null && spell.target.type == Spell.Target.Type.AREA) {
            return DeliveryType.AREA;
        }
        if (spell.deliver != null) {
            return switch (spell.deliver.type) {
                case PROJECTILE, SHOOT_ARROW -> DeliveryType.PROJECTILE;
                case METEOR -> DeliveryType.METEOR;
                case CLOUD -> DeliveryType.CLOUD;
                default -> DeliveryType.DIRECT;
            };
        }
        return DeliveryType.DIRECT;
    }

    private boolean hasSpawnImpact(Spell spell) {
        if (spell.impacts == null) return false;
        return spell.impacts.stream()
                .anyMatch(i -> i.action != null && i.action.type == Spell.Impact.Action.Type.SPAWN);
    }

    private boolean hasHarmfulCustomImpact(Spell spell) {
        if (spell.impacts == null) return false;
        return spell.impacts.stream()
                .anyMatch(i -> i.action != null && i.action.type == Spell.Impact.Action.Type.CUSTOM
                        && i.action.custom != null && i.action.custom.intent == SpellTarget.Intent.HARMFUL);
    }

    private Identifier findEffectId(Spell spell) {
        if (spell.impacts == null) return null;
        return spell.impacts.stream()
            .filter(i -> i.action != null && i.action.type == Spell.Impact.Action.Type.STATUS_EFFECT && i.action.status_effect != null)
            .map(i -> Identifier.of(i.action.status_effect.effect_id))
            .findFirst()
            .orElse(null);
    }

    private String formatSpellName(Identifier id) {
        String[] parts = id.getPath().split("[_/]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }
}
