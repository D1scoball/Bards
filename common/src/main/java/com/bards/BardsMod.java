package com.bards;

import com.bards.config.Default;
import com.bards.config.TweaksConfig;
import com.bards.effect.BardsEffects;
import com.bards.item.Armors;
import com.bards.item.BardBooks;
import com.bards.item.Group;
import com.bards.item.Weapons;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;

public final class BardsMod {
    public static final String MOD_ID = "bards_rpg";
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment", Default.itemConfig)
            .builder()
            .setDirectory(MOD_ID)
            .sanitize(true)
            .build();

    public static ConfigManager<ConfigFile.Effects> effectsConfig = new ConfigManager<>
            ("effects", new ConfigFile.Effects())
            .builder()
            .setDirectory(MOD_ID)
            .sanitize(true)
            .build();

    public static ConfigManager<StructurePoolConfig> villageConfig = new ConfigManager<>
            ("villages", Default.villageConfig)
            .builder()
            .setDirectory(MOD_ID)
            .sanitize(true)
            .build();

    public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
            ("tweaks", new TweaksConfig())
            .builder()
            .setDirectory(MOD_ID)
            .sanitize(true)
            .build();

    public static void init() {
        itemConfig.refresh();
        effectsConfig.refresh();
        villageConfig.refresh();
        tweaksConfig.refresh();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            tweaksConfig.value.ignore_items_required_mods = true;
        }
    }

    public static void registerItems() {
        Group.BARDS = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Armors.troubadourArmorSet.armorSet().head.asItem()))
                .displayName(Text.translatable("itemGroup.bards_rpg.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.BARDS);
        BardBooks.register();
        Armors.register(itemConfig.value.armor_sets);
        Weapons.register(itemConfig.value.weapons);
        itemConfig.save();
    }

    public static void registerEffects() {
        BardsEffects.register(effectsConfig.value);
        effectsConfig.save();
    }
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
