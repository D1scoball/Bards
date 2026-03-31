package com.bards.mixin;

import com.bards.effect.BardsEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WardensPaeanMixin {

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void wardensPaean$interceptEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.getWorld().isClient()) return;

        if (effect.getEffectType().value().isBeneficial() && entity.hasStatusEffect(BardsEffects.HARMFUL_WARDENS_PAEAN.entry)) {
            entity.removeStatusEffect(BardsEffects.HARMFUL_WARDENS_PAEAN.entry);
            cir.setReturnValue(false);
            return;
        }

        if (!effect.getEffectType().value().isBeneficial() && entity.hasStatusEffect(BardsEffects.BENEFICIAL_WARDENS_PAEAN.entry)) {
            entity.removeStatusEffect(BardsEffects.BENEFICIAL_WARDENS_PAEAN.entry);
            cir.setReturnValue(false);
        }
    }
}
