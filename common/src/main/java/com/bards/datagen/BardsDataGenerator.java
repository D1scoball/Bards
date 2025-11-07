package com.bards.datagen;

import com.bards.content.BardsSpells;
import com.bards.item.Armors;
import com.bards.item.Group;
import com.bards.item.Weapons;
import com.bards.tags.BardTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_engine.api.tags.SpellEngineItemTags;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;
import net.spell_power.api.SpellPowerTags;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BardsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(SpellGen::new);
        pack.addProvider(LangGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
    }
    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: BardsSpells.entries) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }
    public static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(Group.translationKey, "Bards");
            translationBuilder.add("item.bards_rpg.bard_spell_book","Bard's Stories");
            translationBuilder.add("item.bards_rpg.bard_spell_scroll","Bard Ballad");
            Weapons.entries.forEach(entry ->
                    translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
            );
            BardsSpells.entries.forEach(entry -> {
                var id = entry.id();
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".name" , entry.title());
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".description" , entry.description());
            });
            Armors.entries.forEach(entry -> {
                var translations = new LinkedHashMap<String, String>();
                translations.put(((Item)entry.armorSet().head).getTranslationKey(), entry.armorSet().headTranslation);
                translations.put(((Item)entry.armorSet().chest).getTranslationKey(), entry.armorSet().chestTranslation);
                translations.put(((Item)entry.armorSet().legs).getTranslationKey(), entry.armorSet().legsTranslation);
                translations.put(((Item)entry.armorSet().feet).getTranslationKey(), entry.armorSet().feetTranslation);
                for (var armorEntry: translations.entrySet()) {
                    translationBuilder.add(armorEntry.getKey(), armorEntry.getValue());
                }
            });
        }
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public void generateBardWeaponTags(List<Weapon.Entry> weapons, TagKey tagKey) {
            Iterator var2 = weapons.iterator();
            while(var2.hasNext()) {
                Weapon.Entry weapon = (Weapon.Entry)var2.next();
                FabricTagProvider<Item>.FabricTagBuilder tag = this.getOrCreateTagBuilder(tagKey);
                tag.addOptional(weapon.id());
                int tier = weapon.lootProperties().tier();
                if (tier >= 0) {
                    FabricTagProvider<Item>.FabricTagBuilder tierTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootTiers.get(tier, RPGSeriesItemTags.LootCategory.WEAPONS));
                    tierTag.addOptional(weapon.id());
                }
                String lootTheme = weapon.lootProperties().theme();
                if (lootTheme != null && !lootTheme.isEmpty()) {
                    FabricTagProvider<Item>.FabricTagBuilder themeTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootThemes.get(lootTheme));
                    themeTag.addOptional(weapon.id());
                }
            }
        }
        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var armorTagOptions = new RPGSeriesDataGen.ItemTagGenerator.ArmorOptions(true, true);
            generateArmorTags(
                    Armors.entries.stream().toList(),
                    RPGSeriesItemTags.ArmorMetaType.MAGIC,
                    armorTagOptions
            );
            generateWeaponTags(Weapons.entries.stream()
                    .filter(entry -> entry.name().toLowerCase().contains("rapier"))
                    .toList());
            generateBardWeaponTags(
                    Weapons.entries.stream()
                            .filter(entry -> entry.name().toLowerCase().contains("lute"))
                            .toList(),
                    BardTags.LUTES
            );
            generateBardWeaponTags(
                    Weapons.entries.stream()
                            .filter(entry -> entry.name().toLowerCase().contains("lyre"))
                            .toList(),
                    BardTags.LYRES
            );
            var twoModels = getOrCreateTagBuilder(BardTags.TWO_MODEL_INSTRUMENT);
            twoModels.addTag(BardTags.LUTES);
            twoModels.addTag(BardTags.LYRES);

            var spellInfinityTag = getOrCreateTagBuilder(SpellEngineItemTags.ENCHANTABLE_SPELL_INFINITY);
            spellInfinityTag.addTag(BardTags.LUTES);
            spellInfinityTag.addTag(BardTags.LYRES);
            var spellHasteTag = getOrCreateTagBuilder(SpellPowerTags.Items.Enchantable.HASTE);
            spellHasteTag.addTag(BardTags.LUTES);
            spellHasteTag.addTag(BardTags.LYRES);
            var criticalDamageTag  = getOrCreateTagBuilder(SpellPowerTags.Items.Enchantable.CRITICAL_DAMAGE);
            criticalDamageTag.addTag(BardTags.LUTES);
            criticalDamageTag.addTag(BardTags.LYRES);
            var spellPowerTag  = getOrCreateTagBuilder(SpellPowerTags.Items.Enchantable.SPELL_POWER_GENERIC);
            spellPowerTag.addTag(BardTags.LUTES);
            spellPowerTag.addTag(BardTags.LYRES);
            var unbreakingTag = getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE);
            unbreakingTag.addTag(BardTags.LUTES);
            unbreakingTag.addTag(BardTags.LYRES);
        }
    }


}
