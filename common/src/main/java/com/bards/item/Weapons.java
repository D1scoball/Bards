package com.bards.item;

import com.bards.BardsMod;
import com.bards.content.BardsSpells;
import net.fabric_extras.ranged_weapon.api.CustomCrossbow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
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
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.bards.BardsMod.MOD_ID;

public class Weapons {
    public static final ArrayList<Weapon.Entry> meleeEntries = new ArrayList<>();
    private static Weapon.Entry meleeEntry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType category) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, category);
        if (entry.isRequiredModInstalled()) {
            meleeEntries.add(entry);
            entry.loot(Equipment.LootProperties.of(""));
        }
        return entry;
    }
    public static final ArrayList<RangedEntry> rangedEntries = new ArrayList<>();
    public interface RangedFactory {
        Item create(Item.Settings settings, RangedConfig config, Supplier<Ingredient> repairIngredientSupplier);
    }

    public static final class RangedEntry {
        private final Identifier id;
        private final RangedFactory factory;
        private final RangedConfig defaults;
        private final Supplier<Ingredient> repairIngredientSupplier;
        private final int durability;
        public List<Identifier> spells = null;
        private String translatedName = null;

        public Item item;

        public Equipment.LootProperties lootProperties = Equipment.LootProperties.EMPTY;
        public Equipment.WeaponType weaponType = Equipment.WeaponType.SHORT_BOW;

        public RangedEntry(Identifier id, RangedFactory factory, RangedConfig defaults, Supplier<Ingredient> repairIngredientSupplier, int durability) {
            this.id = id;
            this.factory = factory;
            this.defaults = defaults;
            this.repairIngredientSupplier = repairIngredientSupplier;
            this.durability = durability;
        }

        public Identifier id() {
            return id;
        }

        public Item create(Item.Settings settings, RangedConfig config) {
            this.item = factory.create(
                    settings.maxDamage(durability),
                    config,
                    repairIngredientSupplier
            );
            return this.item;
        }

        public Item item() {
            return item;
        }

        public RangedEntry weaponType(Equipment.WeaponType weaponType) {
            this.weaponType = weaponType;
            return this;
        }

        public RangedEntry spell(Identifier spellId) {
            spells = List.of(spellId);
            return this;
        }

        public RangedEntry translatedName(String translatedName) {
            this.translatedName = translatedName;
            return this;
        }

        public String translatedName() {
            return translatedName;
        }
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
    private static final float rapier_attack_speed = -2.0F;
    private static Weapon.Entry rapier(String name, Weapon.CustomMaterial material, float damage) {
        return meleeEntry(name, material, SpellSwordItem::new, new WeaponConfig(damage, rapier_attack_speed), Equipment.WeaponType.SWORD)
                .spellContainer(SpellContainers.forMeleeWeapon().withSpellId(BardsSpells.puncture.id()));
    }
    private static final float lute_attack_speed = -3.0F;
    private static Weapon.Entry lute(String name, Weapon.CustomMaterial material, float damage) {
        return meleeEntry(name, material, StaffItem::new, new WeaponConfig(damage, lute_attack_speed), Equipment.WeaponType.DAMAGE_STAFF)
                .spellContainer(SpellContainers.forMagicWeapon()).withSpellChoices("bards_rpg:weapon/lute");
    }
    private static Weapon.Entry special_lute(String name, Weapon.CustomMaterial material, float damage) {
        return meleeEntry(name, material, StaffItem::new, new WeaponConfig(damage, lute_attack_speed), Equipment.WeaponType.DAMAGE_STAFF)
                .spellContainer(SpellContainers.forMagicWeapon());
    }
    private static final float lyre_attack_speed = -2.2F;
    private static final float lyre_attack_damage = 3.0F;
    private static Weapon.Entry lyre(String name, Weapon.CustomMaterial material) {
        return meleeEntry(name, material, StaffItem::new, new WeaponConfig(lyre_attack_damage, lyre_attack_speed), Equipment.WeaponType.HEALING_STAFF)
                .spellContainer(SpellContainers.forMagicWeapon()).withSpellChoices("bards_rpg:weapon/lyre");
    }
    private static Weapon.Entry special_lyre(String name, Weapon.CustomMaterial material) {
        return meleeEntry(name, material, StaffItem::new, new WeaponConfig(lyre_attack_damage, lyre_attack_speed), Equipment.WeaponType.HEALING_STAFF)
                .spellContainer(SpellContainers.forMagicWeapon());
    }
    /// RAPIERS
    public static final Weapon.Entry golden_rapier = rapier("golden_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 2.1F)
            .translatedName("Golden Rapier")
            .loot(Equipment.LootProperties.of("golden"));
    public static final Weapon.Entry iron_rapier = rapier("iron_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 3.6F)
            .translatedName("Iron Rapier")
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry diamond_rapier = rapier("diamond_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 4.4F)
            .translatedName("Diamond Rapier")
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_rapier = rapier("netherite_rapier",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 5.9F)
            .translatedName("Netherite Rapier")
            .loot(Equipment.LootProperties.of(3));
    /// LUTES
    private static final float T1_LUTE_POWER = 3.5F;
    private static final float T2_LUTE_POWER = 4F;
    private static final float T3_LUTE_POWER = 4.5F;
    private static final float T4_LUTE_POWER = 5F;

    public static final Weapon.Entry wooden_lute = lute("wooden_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 4.0F)
            .translatedName("Wooden Lute")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T1_LUTE_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T1_LUTE_POWER))
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry diamond_lute = lute("diamond_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 6.0F)
            .translatedName("Diamond Lute")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T2_LUTE_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T2_LUTE_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_lute = lute("netherite_lute",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 8.0F)
            .translatedName("Netherite Lute")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T3_LUTE_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T3_LUTE_POWER))
            .loot(Equipment.LootProperties.of(3));
    /// LYRES
    private static final float LYRE_ARCANE_POWER_MULTIPLIER = 0.5F;
    private static final float T1_LYRE_ARCANE_POWER = (Math.round((T1_LUTE_POWER * LYRE_ARCANE_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T2_LYRE_ARCANE_POWER = (Math.round((T2_LUTE_POWER * LYRE_ARCANE_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T3_LYRE_ARCANE_POWER = (Math.round((T3_LUTE_POWER * LYRE_ARCANE_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T4_LYRE_ARCANE_POWER = (Math.round((T4_LUTE_POWER * LYRE_ARCANE_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float LYRE_HEALING_POWER_MULTIPLIER = 1.5F;
    private static final float T1_LYRE_HEALING_POWER = (Math.round((T1_LUTE_POWER * LYRE_HEALING_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T2_LYRE_HEALING_POWER = (Math.round((T2_LUTE_POWER * LYRE_HEALING_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T3_LYRE_HEALING_POWER = (Math.round((T3_LUTE_POWER * LYRE_HEALING_POWER_MULTIPLIER)*10) / 10.0F);
    private static final float T4_LYRE_HEALING_POWER = (Math.round((T4_LUTE_POWER * LYRE_HEALING_POWER_MULTIPLIER)*10) / 10.0F);
    public static final Weapon.Entry golden_lyre = lyre("golden_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .translatedName("Golden Lyre")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T1_LYRE_ARCANE_POWER ))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T1_LYRE_HEALING_POWER))
            .loot(Equipment.LootProperties.of("golden"));
    public static final Weapon.Entry diamond_lyre = lyre("diamond_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)))
            .translatedName("Diamond Lyre")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T2_LYRE_ARCANE_POWER ))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T2_LYRE_HEALING_POWER))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_lyre = lyre("netherite_lyre",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .translatedName("Netherite Lyre")
            .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T3_LYRE_ARCANE_POWER ))
            .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T3_LYRE_HEALING_POWER))
            .loot(Equipment.LootProperties.of(3));
    ///HARP CROSSBOW
    private static RangedEntry harpCrossbow(String name, int durability, Supplier<Ingredient> repairIngredientSupplier, RangedConfig defaults) {
        var entry = new RangedEntry(Identifier.of(MOD_ID, name), HarpCrossbowItem::new, defaults, repairIngredientSupplier, durability);
        rangedEntries.add(entry);
        return entry;
    }
    private static final float pullTime_rapidCrossbow = 0;
    private static final float pullTime_heavyCrossbow = 1.75F - 1F;
    private static final float velocity_rapidCrossbow = 0F;
    private static final float velocity_heavyCrossbow = 0.5F;
    
    public static float rapid_crossbow_damage = 10.5F;
    public static float heavy_crossbow_damage = 17.0F;

    /// REGISTRY
    private static final float rapier_t5_attack_damage = 6.7F;
    private static final float lute_t5_attack_damage = 10;
    public static void register(Map<String, WeaponConfig> configs) {
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            rapier("ruby_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Ruby Rapier")
                    .loot(Equipment.LootProperties.of(4));
            lute("ruby_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lute_t5_attack_damage)
                    .translatedName("Ruby Lute")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LUTE_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LUTE_POWER))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            rapier("aeternium_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Aeternium Rapier")
                    .loot(Equipment.LootProperties.of(4));
            lyre("aeternium_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .translatedName("Aeternium Lyre")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LYRE_ARCANE_POWER ))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LYRE_HEALING_POWER))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            rapier("aether_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), rapier_t5_attack_damage)
                    .translatedName("Valkyrie Rapier")
                    .loot(Equipment.LootProperties.of("aether"));
            lute("aether_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), lute_t5_attack_damage)
                    .translatedName("Angelic Lute")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LUTE_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LUTE_POWER))
                    .loot(Equipment.LootProperties.of("aether"));
            lyre("aether_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .translatedName("Valkyrie Lyre")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LYRE_ARCANE_POWER ))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LYRE_HEALING_POWER))
                    .loot(Equipment.LootProperties.of("aether"));
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(LNE) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            rapier("ender_dragon_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), rapier_t5_attack_damage)
                    .translatedName("Dragon's Rapier")
                    .withAdditionalSpell("loot_n_explore:dragonclaw")
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("elder_guardian_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)), rapier_t5_attack_damage)
                    .translatedName("Coral Rapier")
                    .withAdditionalSpell("loot_n_explore:waterbomb")
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("wither_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BONE)), rapier_t5_attack_damage)
                    .translatedName("Withered Rapier")
                    .withAdditionalSpell("loot_n_explore:wither_pulse")
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            rapier("glacial_rapier", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BLUE_ICE)), rapier_t5_attack_damage)
                    .translatedName("Glacial Rapier")
                    .withAdditionalSpell("loot_n_explore:avalanche")
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("ender_dragon_lute", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)), lute_t5_attack_damage)
                    .translatedName("Dragon Lute")
                    .withSpellChoices("bards_rpg:weapon/dragon_lyre")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LUTE_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LUTE_POWER))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("elder_guardian_lyre", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)))
                    .translatedName("Siren's Lyre")
                    .loot(Equipment.LootProperties.of(5))
                    .withSpellChoices("bards_rpg:weapon/ocean_lyre")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LYRE_ARCANE_POWER ))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LYRE_HEALING_POWER))
                    .rarity = Rarity.RARE;
        }
        if (BardsMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(ARSENAL) || FabricLoader.getInstance().isDevelopmentEnvironment()) {
            rapier("unique_rapier_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)), rapier_t5_attack_damage)
                    .translatedName("Singing Blade")
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("unique_lute_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.REDSTONE_BLOCK)), lute_t5_attack_damage)
                    .translatedName("Lute of Ruby Verdict")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LUTE_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LUTE_POWER))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lute("unique_lute_1", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)), lute_t5_attack_damage)
                    .translatedName("Spellthief's Lute")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LUTE_POWER))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LUTE_POWER))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("unique_lyre_0", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)))
                    .translatedName("Lyre of Apollo")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LYRE_ARCANE_POWER ))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LYRE_HEALING_POWER))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            lyre("unique_lyre_1", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.GOLD_BLOCK)))
                    .translatedName("Lyre of Antecael")
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, T4_LYRE_ARCANE_POWER ))
                    .attribute(AttributeModifier.bonus(SpellSchools.HEALING.id, T4_LYRE_HEALING_POWER))
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
        }

        Weapon.register(configs, meleeEntries, Group.KEY);
    }
}
