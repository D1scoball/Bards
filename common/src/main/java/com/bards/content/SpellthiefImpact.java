package com.bards.content;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.container.SpellContainerSource;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;

import java.util.List;

import static com.bards.BardsMod.MOD_ID;

public class SpellthiefImpact implements SpellHandlers.CustomImpact {

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
                .filter(effect -> effect.getEffectType().value().getCategory() == StatusEffectCategory.BENEFICIAL)
                .toList();

        for (StatusEffectInstance effect : beneficialEffects) {
            caster.addStatusEffect(new StatusEffectInstance(effect));
            livingTarget.removeStatusEffect(effect.getEffectType());
        }

        if (livingTarget instanceof PlayerEntity targetPlayer) {
            List<RegistryEntry<Spell>> spells = SpellContainerSource.activeSpellsOf(targetPlayer);
            if (!spells.isEmpty()) {
                RegistryEntry<Spell> stolenSpellEntry = spells.get(targetPlayer.getRandom().nextInt(spells.size()));
                Spell stolenSpell = stolenSpellEntry.value();

                RegistryEntry<Spell> spellthiefSpellEntry = SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, "spellthief")).get();
                Spell spellthiefSpell = spellthiefSpellEntry.value();
                SpellPower.Result power = SpellPower.getSpellPower(spellthiefSpell.school, caster);
                if (!caster.getWorld().isClient() && target != null){
                    ParticleHelper.sendBatches(caster, stolenSpell.release.particles);
                    ParticleHelper.sendBatches(caster, stolenSpell.release.particles_scaled_with_ranged);
                    for(Entity targetEntity : TargetHelper.targetsFromArea(caster, spellthiefSpell.range, spellthiefSpell.target.area, e -> e != caster)) {
                        SpellHelper.performImpacts(caster.getWorld(), caster, targetEntity, caster, stolenSpellEntry,
                                stolenSpell.impacts, new SpellHelper.ImpactContext().power(power).position(caster.getPos()));
                        ParticleHelper.sendBatches(targetEntity, stolenSpell.impacts.get(0).particles);
                    }
                }
            }
        }

        return new SpellHandlers.ImpactResult(true, false);
    }
}
