package com.bards.datagen;

import com.bards.item.Weapons;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static com.bards.BardsMod.MOD_ID;

public class BardRecipeProvider extends FabricRecipeProvider {

    private static Item getOrFallback(Identifier id, Item fallback) {
        var item = Registries.ITEM.get(id);
        return item != null && item != Items.AIR ? item : fallback;
    }

    public BardRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Crafting Recipes (" + MOD_ID + ")";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // ==========================================
        // RAPIERS
        // ==========================================
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.iron_rapier.item())
                .pattern("  W")
                .pattern(" W ")
                .pattern("RW ")
                .input('W', Items.IRON_INGOT)
                .input('R', Items.LEATHER)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "iron_rapier"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.golden_rapier.item())
                .pattern("  W")
                .pattern(" W ")
                .pattern("RW ")
                .input('W', Items.GOLD_INGOT)
                .input('R', Items.LEATHER)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "golden_rapier"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.diamond_rapier.item())
                .pattern("  W")
                .pattern(" W ")
                .pattern("RW ")
                .input('W', Items.DIAMOND)
                .input('R', Items.LEATHER)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, Identifier.of(MOD_ID, "diamond_rapier"));

        // ==========================================
        // LUTES
        // ==========================================
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.wooden_lute.item())
                .pattern(" WR")
                .pattern("WRW")
                .pattern("WW ")
                .input('W', Items.BIRCH_PLANKS)
                .input('R', Items.STRING)
                .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                .offerTo(exporter, Identifier.of(MOD_ID, "wooden_lute"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.diamond_lute.item())
                .pattern(" DR")
                .pattern("DRD")
                .pattern("DD ")
                .input('D', Items.DIAMOND)
                .input('R', Items.STRING)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, Identifier.of(MOD_ID, "diamond_lute"));

        // ==========================================
        // LYRES
        // ==========================================
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.golden_lyre.item())
                .pattern("WRW")
                .pattern("WRW")
                .pattern(" W ")
                .input('W', Items.GOLD_INGOT)
                .input('R', Items.STRING)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "golden_lyre"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Weapons.diamond_lyre.item())
                .pattern("DRD")
                .pattern("DRD")
                .pattern(" D ")
                .input('D', Items.DIAMOND)
                .input('R', Items.STRING)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, Identifier.of(MOD_ID, "diamond_lyre"));

        // MISC
        var bardBook = getOrFallback(Identifier.of(MOD_ID, "bard_spell_book"), Items.WRITTEN_BOOK);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, bardBook)
                .input(Items.STRING)
                .input(Items.GOLD_NUGGET)
                .input(Items.BOOK)
                .input(Items.LAPIS_LAZULI)
                .criterion(hasItem(Items.NOTE_BLOCK), conditionsFromItem(Items.NOTE_BLOCK))
                .offerTo(exporter);
    }
}
