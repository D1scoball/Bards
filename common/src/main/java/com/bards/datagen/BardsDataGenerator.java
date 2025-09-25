package com.bards.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BardsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // SoundGen needs support for sounds with multiple file entries "paladins:plate_equip_1","paladins:plate_equip_2","paladins:plate_equip_3"
        // pack.addProvider(SoundGen::new);
        //pack.addProvider(SpellGen::new);
    }
}
