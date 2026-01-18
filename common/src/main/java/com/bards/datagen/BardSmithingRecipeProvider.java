package com.bards.datagen;

import com.bards.item.Weapons;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static com.bards.BardsMod.MOD_ID;

public class BardSmithingRecipeProvider extends FabricRecipeProvider {

    public BardSmithingRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Smithing Recipes (" + MOD_ID + ")";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // ==========================================
        // NETHERITE UPGRADES
        // ==========================================
        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.ofItems(Weapons.diamond_rapier.item()),
                        Ingredient.ofItems(Items.NETHERITE_INGOT),
                        RecipeCategory.COMBAT,
                        Weapons.netherite_rapier.item())
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "netherite_rapier_smithing"));
        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.ofItems(Weapons.diamond_lute.item()),
                        Ingredient.ofItems(Items.NETHERITE_INGOT),
                        RecipeCategory.COMBAT,
                        Weapons.netherite_lute.item())
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "netherite_lute_smithing"));
        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.ofItems(Weapons.diamond_lyre.item()),
                        Ingredient.ofItems(Items.NETHERITE_INGOT),
                        RecipeCategory.COMBAT,
                        Weapons.netherite_lyre.item())
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "netherite_lyre_smithing"));
    }
}
