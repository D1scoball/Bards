package com.bards.client;

import com.bards.BardsMod;
import com.bards.client.armor.CustomArmorRenderer;
import com.bards.effect.BardsEffects;
import com.bards.item.Armors;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;

import java.util.function.Supplier;

public class BardClient {
    public static void init() {

        registerArmorRenderer(Armors.entertainerArmorSet.armorSet(), CustomArmorRenderer::entertainer_armor);
        registerArmorRenderer(Armors.troubadourArmorSet.armorSet(), CustomArmorRenderer::troubadour_armor);
        registerArmorRenderer(Armors.netheriteTroubadourArmorSet.armorSet(), CustomArmorRenderer::netherite_troubadour_armor);
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || BardsMod.tweaksConfig.value.ignore_items_required_mods) {
            registerArmorRenderer(Armors.storytellerArmorSet.armorSet(), CustomArmorRenderer::storyteller_armor);
        }
        registerEffectRenderers();
    }
    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
    private static void registerEffectRenderers() {
        CustomParticleStatusEffect.register(
                BardsEffects.TROUBADOURS_MINUET.effect,
                new BuffParticleSpawner(
                        new ParticleBatch(
                                SpellEngineParticles.area_circle_1.id().toString(),
                                ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.FEET,
                                1F, 0.3F, 0.8F)
                                .color(Color.HOLY.toRGBA())
                                .scale(4.5F)
                                .followEntity(true)
                ).withFrequency(30).scaleWithAmplifier(false)
        );
    }
}
