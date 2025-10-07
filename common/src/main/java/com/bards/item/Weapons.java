package com.bards.item;

import com.bards.BardsMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_engine.api.item.weapon.SpellSwordItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import static com.bards.BardsMod.MOD_ID;

public class Weapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType category) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, category);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static final String AETHER = "aether";
    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String LNE = "loot_n_explore";
    private static final String ARSENAL = "arsenal";
    // Rapiers
    private static final float rapier_attack_speed = 0;
    private static Weapon.Entry rapier(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, rapier_attack_speed), Equipment.WeaponType.SWORD);
    }

    public static final Weapon.Entry iron_rapier = rapier("iron_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 8.3F)
            .translatedName("Iron Rapier")
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry golden_rapier = rapier("golden_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 5.2F)
            .translatedName("Golden Rapier")
            .loot(Equipment.LootProperties.of("golden_rapier"));
    public static final Weapon.Entry diamond_rapier = rapier("diamond_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 9.9F)
            .translatedName("Diamond Rapier")
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_rapier = rapier("netherite_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 11.5F)
            .translatedName("Netherite Rapier")
            .loot(Equipment.LootProperties.of(3));

    /// REGISTRY
    private static final float rapier_t5_attack_damage = 9;
    public static void register(Map<String, WeaponConfig> configs) {
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            rapier("ruby_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Ruby Rapier")
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            rapier("aeternium_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Aeternium Rapier")
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER)) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            rapier("aether_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Valkyrie Rapier")
                    .loot(Equipment.LootProperties.of("aether"));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(LNE)) {
            /// TO DO
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(ARSENAL)) {
            rapier("unique_rapier_1", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), rapier_t5_attack_damage)
                    .translatedName("Singing Blade")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
        }

        Weapon.register(configs, entries, Group.KEY);
    }
}
