package com.bards.datagen;

import com.bards.item.Armors;
import com.google.gson.JsonObject;
import com.bards.item.Weapons;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static com.bards.BardsMod.MOD_ID;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        Armors.entries.forEach(entry -> {
            for (var piece: entry.armorSet().pieces()) {
                itemModelGenerator.register((Item) piece, Models.GENERATED);
            }
        });
        for (var entry : Weapons.entries) {
            Item item = entry.item();
            if (item == null) continue;

            Identifier itemId = Registries.ITEM.getId(item);
            String name = itemId.getPath();

            if (name.contains("rapier")) {
                generateRapierModel(itemModelGenerator, itemId, name);
            } else if (name.contains("lute")) {
                generateLuteInventoryModel(itemModelGenerator, itemId, name);
                if(name.contains("wooden") || name.contains("diamond") || name.contains("netherite") || name.contains("ruby")|| name.contains("aether")){
                    generateLuteOverworldModel(itemModelGenerator, itemId, name);
                }
            } else if (name.contains("lyre")) {
                generateLyreInventoryModel(itemModelGenerator, itemId, name);
                if(name.contains("golden") || name.contains("diamond") || name.contains("netherite") || name.contains("aeternium")){
                    generateLyreOverworldModel(itemModelGenerator, itemId, name);
                }
            }
        }
    }


    private void generateRapierModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name);

        JsonObject json = new JsonObject();
        json.addProperty("parent", MOD_ID + ":item/rapier_model");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/" + name);
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }

    private void generateLuteInventoryModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name);

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/" + name + "_inventory");
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }

    private void generateLuteOverworldModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name + "_model");

        JsonObject json = new JsonObject();
        json.addProperty("parent", MOD_ID + ":item/generic_lute");

        JsonObject textures = new JsonObject();
        textures.addProperty("4", MOD_ID + ":item/" + name);
        textures.addProperty("particle", MOD_ID + ":item/" + name);
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }

    private void generateLyreInventoryModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name);

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/" + name + "_inventory");
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }

    private void generateLyreOverworldModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name + "_model");

        JsonObject json = new JsonObject();
        json.addProperty("parent", MOD_ID + ":item/generic_lyre");

        JsonObject textures = new JsonObject();
        textures.addProperty("0", MOD_ID + ":item/" + name);
        textures.addProperty("particle", MOD_ID + ":item/" + name);
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }
}
