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
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.weapon.StaffItem;
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
    private static final float lute_attack_speed = -2.0F;
    private static Weapon.Entry lute(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, StaffItem::new, new WeaponConfig(damage, lute_attack_speed), Equipment.WeaponType.DAMAGE_STAFF);
    }
    private static final float lyre_attack_speed = 0;
    private static Weapon.Entry lyre(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, StaffItem::new, new WeaponConfig(damage, lyre_attack_speed), Equipment.WeaponType.HEALING_STAFF);
    }

    /// RAPIERS
    public static final Weapon.Entry iron_rapier = rapier("iron_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 8.3F)
            .translatedName("Iron Rapier")
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry golden_rapier = rapier("golden_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 5.2F)
            .translatedName("Golden Rapier")
            .loot(Equipment.LootProperties.of("golden"));
    public static final Weapon.Entry diamond_rapier = rapier("diamond_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 9.9F)
            .translatedName("Diamond Rapier")
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_rapier = rapier("netherite_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 11.5F)
            .translatedName("Netherite Rapier")
            .loot(Equipment.LootProperties.of(3));
    /// LUTES
    public static final Weapon.Entry wooden_lute = lute("wooden_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 8.3F)
            .translatedName("Wooden Lute")
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry diamond_lute = lute("diamond_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 8.3F)
            .translatedName("Diamond Lute")
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_lute = lute("netherite_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 11.5F)
            .translatedName("Netherite Lute")
            .loot(Equipment.LootProperties.of(3));
    /// LYRES
    public static final Weapon.Entry golden_lyre = lyre("golden_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 8.3F)
            .translatedName("Golden Lyre")
            .loot(Equipment.LootProperties.of("golden"));
    public static final Weapon.Entry diamond_lyre = lyre("diamond_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 8.3F)
            .translatedName("Diamond Lyre")
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_lyre = lyre("netherite_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 11.5F)
            .translatedName("Netherite Lyre")
            .loot(Equipment.LootProperties.of(3));


    /// REGISTRY
    private static final float rapier_t5_attack_damage = 9;
    private static final float lute_t5_attack_damage = 9;
    private static final float lyre_t5_attack_damage = 9;
    public static void register(Map<String, WeaponConfig> configs) {
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            rapier("ruby_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Ruby Rapier")
                    .loot(Equipment.LootProperties.of(4));
            lute("ruby_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lute_t5_attack_damage)
                    .translatedName("Ruby Lute")
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            rapier("aeternium_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Aeternium Rapier")
                    .loot(Equipment.LootProperties.of(4));
            lyre("aeternium_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lyre_t5_attack_damage)
                    .translatedName("Aeternium Lyre")
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            rapier("aether_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Valkyrie Rapier")
                    .loot(Equipment.LootProperties.of("aether"));
            lute("aether_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lute_t5_attack_damage)
                    .translatedName("Angelic Lute")
                    .loot(Equipment.LootProperties.of("aether"));
            lyre("aether_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lyre_t5_attack_damage)
                    .translatedName("Valkyrie Lyre")
                    .loot(Equipment.LootProperties.of("aether"));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(LNE) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            rapier("ender_dragon_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), rapier_t5_attack_damage)
                    .translatedName("Dragon's Rapier")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("elder_guardian_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)), rapier_t5_attack_damage)
                    .translatedName("Coral Rapier")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("wither_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BONE)), rapier_t5_attack_damage)
                    .translatedName("Withered Rapier")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("glacial_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BLUE_ICE)), rapier_t5_attack_damage)
                    .translatedName("Glacial Rapier")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("ender_dragon_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), lute_t5_attack_damage)
                    .translatedName("Dragon Lute")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("elder_guardian_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)), lyre_t5_attack_damage)
                    .translatedName("Siren's Lyre")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(ARSENAL) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            rapier("unique_rapier_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), rapier_t5_attack_damage)
                    .translatedName("Singing Blade")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("unique_lute_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.REDSTONE_BLOCK)), lute_t5_attack_damage)
                    .translatedName("Lute of Ruby Verdict")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("unique_lute_1", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)), lute_t5_attack_damage)
                    .translatedName("Spellthief's Lute")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("unique_lyre_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), lyre_t5_attack_damage)
                    .translatedName("Lyre of Apollo")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("unique_lyre_1", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), lyre_t5_attack_damage)
                    .translatedName("Lyre of Antecael")
                    .spell(Identifier.of("arsenal:radiance_melee"))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
        }

        Weapon.register(configs, entries, Group.KEY);
    }
}
