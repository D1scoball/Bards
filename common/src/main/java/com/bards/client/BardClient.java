package com.bards.client;

import com.bards.BardsMod;
import com.bards.client.armor.CustomArmorRenderer;
import com.bards.client.effect.ArmysPaeonCircleRenderer;
import com.bards.content.BardsSpells;
import com.bards.effect.BardsEffects;
import com.bards.item.Armors;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.client.gui.SpellTooltip;

import java.util.function.Supplier;

public class BardClient {
    public static void init() {
        for (var spell: BardsSpells.entries) {
            if (spell.mutator() != null) {
                SpellTooltip.addDescriptionMutator(spell.id(), spell.mutator());
            }
        }

        registerArmorRenderer(Armors.entertainerArmorSet.armorSet(), CustomArmorRenderer::entertainer_armor);
        registerArmorRenderer(Armors.troubadourArmorSet.armorSet(), CustomArmorRenderer::troubadour_armor);
        registerArmorRenderer(Armors.netheriteTroubadourArmorSet.armorSet(), CustomArmorRenderer::netherite_troubadour_armor);
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || BardsMod.tweaksConfig.value.ignore_items_required_mods) {
            registerArmorRenderer(Armors.storytellerArmorSet.armorSet(), CustomArmorRenderer::storyteller_armor);
        }
        registerEffectRenderers();
        CustomModelStatusEffect.register(BardsEffects.ARMYS_PAEON_STASH.effect, new ArmysPaeonCircleRenderer());
    }
    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
    private static void registerEffectRenderers() {
        /*
        CustomParticleStatusEffect.register(
                BardsEffects.ARMYS_PAEON_STASH.effect,
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
         */
    }
}
