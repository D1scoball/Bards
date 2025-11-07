package com.bards.item;

import com.bards.BardsMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.bards.BardsMod.MOD_ID;

public class Armors {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, int tier,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, Armor.ItemSettingsTweaker settings) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier),
                settings
        );
        entries.add(entry);
        return entry;
    }

    public static RegistryEntry<ArmorMaterial> material(
            String name, int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
            int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {

        var material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(MOD_ID, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(MOD_ID, name), material);
    }
    private static final Supplier<Ingredient> BARD_INGREDIENTS = () -> Ingredient.ofItems(
            Items.WHITE_WOOL
    );
    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            itemSettings
                    .component(SpellDataComponents.EQUIPMENT_SET, equipmentSetId)
                    .component(DataComponentTypes.RARITY, Rarity.RARE);
        });
    }
    public static RegistryEntry<ArmorMaterial> entertainers_garb = material(
            "entertainers_garb",
            1, 3, 2, 1,
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, () -> { return Ingredient.fromTag(ItemTags.WOOL); });

    public static RegistryEntry<ArmorMaterial> troubadours_garb = material(
            "troubadours_garb",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, () -> { return Ingredient.fromTag(ItemTags.WOOL); });

    public static RegistryEntry<ArmorMaterial> netherite_troubadours_garb = material(
            "netherite_troubadours_garb",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> storytellers_garb = material(
            "storytellers_garb",
            1, 3, 2, 1,
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static final Armor.Entry entertainerArmorSet = create(
            entertainers_garb,
            Identifier.of(MOD_ID, "entertainer_garb"),
            10,
            1,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(entertainers_garb.value().getProtection(ArmorItem.Type.HELMET)),
                    new ArmorSetConfig.Piece(entertainers_garb.value().getProtection(ArmorItem.Type.CHESTPLATE)),
                    new ArmorSetConfig.Piece(entertainers_garb.value().getProtection(ArmorItem.Type.LEGGINGS)),
                    new ArmorSetConfig.Piece(entertainers_garb.value().getProtection(ArmorItem.Type.BOOTS))
            ),
            commonSettings(null))
            .translatedName("Entertainer Hat", "Entertainer Garb", "Entertainer Trousers", "Entertainer Boots");
    public static final Armor.Entry troubadourArmorSet = create(
            troubadours_garb,
            Identifier.of(MOD_ID, "troubadour_garb"),
            20,
            2,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(troubadours_garb.value().getProtection(ArmorItem.Type.HELMET)),
                    new ArmorSetConfig.Piece(troubadours_garb.value().getProtection(ArmorItem.Type.CHESTPLATE)),
                    new ArmorSetConfig.Piece(troubadours_garb.value().getProtection(ArmorItem.Type.LEGGINGS)),
                    new ArmorSetConfig.Piece(troubadours_garb.value().getProtection(ArmorItem.Type.BOOTS))
            ),
            commonSettings(null))
            .translatedName("Troubadour Hat", "Troubadour Garb", "Troubadour Trousers", "Troubadour Boots");
    public static final Armor.Entry netheriteTroubadourArmorSet = create(
            netherite_troubadours_garb,
            Identifier.of(MOD_ID, "netherite_troubadour_garb"),
            30,
            3,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(netherite_troubadours_garb.value().getProtection(ArmorItem.Type.HELMET)),
                    new ArmorSetConfig.Piece(netherite_troubadours_garb.value().getProtection(ArmorItem.Type.CHESTPLATE)),
                    new ArmorSetConfig.Piece(netherite_troubadours_garb.value().getProtection(ArmorItem.Type.LEGGINGS)),
                    new ArmorSetConfig.Piece(netherite_troubadours_garb.value().getProtection(ArmorItem.Type.BOOTS))
            ),
            commonSettings(null))
            .translatedName("Netherite Troubadour Hat", "Netherite Troubadour Garb", "Netherite Troubadour Trousers", "Netherite Troubadour Boots");

    public static Armor.Entry storytellerArmorSet;
    public static Identifier storyteller_passive = Identifier.of(MOD_ID, "storyteller");

    public static void register(Map<String, ArmorSetConfig> configs) {
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || BardsMod.tweaksConfig.value.ignore_items_required_mods) {
            storytellerArmorSet = create(
                    storytellers_garb,
                    Identifier.of(MOD_ID, "storyteller_garb"),
                    40,
                    5,
                    Armor.CustomItem::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(storytellers_garb.value().getProtection(ArmorItem.Type.HELMET)),
                            new ArmorSetConfig.Piece(storytellers_garb.value().getProtection(ArmorItem.Type.CHESTPLATE)),
                            new ArmorSetConfig.Piece(storytellers_garb.value().getProtection(ArmorItem.Type.LEGGINGS)),
                            new ArmorSetConfig.Piece(storytellers_garb.value().getProtection(ArmorItem.Type.BOOTS))
                    ),
                    commonSettings(null))
                    .translatedName("Storyteller Hat", "Storyteller Tunic", "Storyteller Trousers", "Storyteller Boots");
        }
        Armor.register(configs, entries, Group.KEY);
    }
}
