package com.bards.worldgen.villages;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.bards.BardsMod.MOD_ID;

public class BardVillagerTrades {

    public static final Schedule LUTHIER_SCHEDULE = new Schedule();

    public static void registerSchedule() {
        new ScheduleBuilder(LUTHIER_SCHEDULE)
                .withActivity(10,    Activity.IDLE)
                .withActivity(2000,  Activity.WORK)
                .withActivity(9000,  Activity.MEET)
                .withActivity(11000, Activity.IDLE)
                .withActivity(12000, Activity.WORK)
                .withActivity(14000, Activity.REST)
                .build();
        Registry.register(Registries.SCHEDULE, Identifier.of(MOD_ID, "luthier"), LUTHIER_SCHEDULE);
    }

    public static void registerTrades() {
        // TODO: Add Luthier villager trades here
    }
}
