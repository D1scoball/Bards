package com.bards.client;

import com.bards.BardsMod;
import com.bards.client.armor.CustomArmorRenderer;
import com.bards.item.Armors;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.spell_engine.api.item.armor.Armor;

import java.util.function.Supplier;

public class BardClient {
    public static void init() {

        registerArmorRenderer(Armors.entertainerArmorSet.armorSet(), CustomArmorRenderer::entertainer_armor);
        registerArmorRenderer(Armors.troubadourArmorSet.armorSet(), CustomArmorRenderer::troubadour_armor);
        registerArmorRenderer(Armors.netheriteTroubadourArmorSet.armorSet(), CustomArmorRenderer::netherite_troubadour_armor);
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || BardsMod.tweaksConfig.value.ignore_items_required_mods) {
            registerArmorRenderer(Armors.storytellerArmorSet.armorSet(), CustomArmorRenderer::storyteller_armor);
        }
    }
    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
