package com.bards.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WardensPaeanHarmfulEffect extends StatusEffect {
    public static final Set<UUID> PENDING_REMOVAL = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected WardensPaeanHarmfulEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient()) return;
        for (var instance : new ArrayList<>(entity.getStatusEffects())) {
            if (instance.getEffectType().value().isBeneficial()) {
                entity.removeStatusEffect(instance.getEffectType());
                PENDING_REMOVAL.add(entity.getUuid());
                return;
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (PENDING_REMOVAL.remove(entity.getUuid())) {
            entity.removeStatusEffect(BardsEffects.HARMFUL_WARDENS_PAEAN.entry);
        }
        return true;
    }
}
