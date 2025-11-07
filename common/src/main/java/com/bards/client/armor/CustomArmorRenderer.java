package com.bards.client.armor;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;

import static com.bards.BardsMod.MOD_ID;

public class CustomArmorRenderer extends AzArmorRenderer {

    public static CustomArmorRenderer entertainer_armor() {
        return new CustomArmorRenderer("entertainer_armor", "entertainer_armor");
    }
    public static CustomArmorRenderer troubadour_armor() {
        return new CustomArmorRenderer("troubadour_armor", "troubadour_armor");
    }
    public static CustomArmorRenderer netherite_troubadour_armor() {
        return new CustomArmorRenderer("troubadour_armor", "netherite_troubadour_armor");
    }
    public static CustomArmorRenderer storyteller_armor() {
        return new CustomArmorRenderer("storyteller_armor", "storyteller_armor");
    }

    public CustomArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png")
        ).build());
    }
}