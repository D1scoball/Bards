package com.bards.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import static com.bards.BardsMod.MOD_ID;

public class Group {
    public static Identifier ID = Identifier.of(MOD_ID, "general");
    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), ID);
    public static String translationKey = "itemGroup." + ID.getNamespace() + "." + ID.getPath();
    public static ItemGroup BARDS;
}