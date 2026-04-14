package com.bards.worldgen.villages;

import com.bards.block.BardBlocks;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.BiFunction;

import static com.bards.BardsMod.MOD_ID;

public class BardVillagerProfessions {

    public static BiFunction<Identifier, Block, PointOfInterestType> poiRegistrar;

    public static final RegistryKey<PointOfInterestType> LUTHIER_POI_KEY = registerKey("luthier");
    public static PointOfInterestType LUTHIER_POI;
    public static VillagerProfession LUTHIER;

    public static void registerPoiTypes() {
        LUTHIER_POI = registerPoi("luthier", BardBlocks.MUSIC_STAND.block());
    }

    public static void registerProfessions() {
        LUTHIER = registerProfession("luthier", LUTHIER_POI_KEY);
    }

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> poiKey) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(MOD_ID, name),
                new VillagerProfession(name,
                        entry -> entry.matchesKey(poiKey),
                        entry -> entry.matchesKey(poiKey),
                        ImmutableSet.of(), ImmutableSet.of(),
                        SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER));
    }

    private static PointOfInterestType registerPoi(String name, Block block) {
        var id = Identifier.of(MOD_ID, name);
        if (poiRegistrar != null) {
            return poiRegistrar.apply(id, block);
        }
        return PointOfInterestHelper.register(id, 1, 1, block);
    }

    public static RegistryKey<PointOfInterestType> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(MOD_ID, name));
    }
}
