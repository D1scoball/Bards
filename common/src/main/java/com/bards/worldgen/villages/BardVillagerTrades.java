package com.bards.worldgen.villages;

import com.bards.item.Armors;
import com.bards.item.Weapons;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

import java.util.LinkedHashMap;
import java.util.List;

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
        VillagerProfession profession = BardVillagerProfessions.LUTHIER;

        LinkedHashMap<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();
        trades.put(1, List.of(
                new TradeOffers.BuyItemFactory(Items.STRING, 8, 12, 4, 5),
                new TradeOffers.SellItemFactory(Items.ARROW, 2, 8, 128, 3, 0.01f)
        ));
        trades.put(2, List.of(
                new TradeOffers.BuyItemFactory(Items.GOLD_INGOT, 12, 12, 5, 8),
                new TradeOffers.SellItemFactory(Weapons.wooden_lute.item(), 12, 1, 12, 10),
                new TradeOffers.SellItemFactory(Weapons.harp_crossbow.item(), 18, 1, 12, 10),
                new TradeOffers.SellItemFactory(Armors.entertainerArmorSet.armorSet().head, 15, 1, 12, 13)
        ));
        trades.put(3, List.of(
                new TradeOffers.SellItemFactory(Weapons.iron_rapier.item(), 14, 1, 12, 15),
                new TradeOffers.SellItemFactory(Weapons.golden_lyre.item(), 18, 1, 12, 15),
                new TradeOffers.SellItemFactory(Armors.entertainerArmorSet.armorSet().feet, 15, 1, 12, 15),
                new TradeOffers.SellItemFactory(Armors.entertainerArmorSet.armorSet().legs, 15, 1, 12, 15)
        ));
        trades.put(4, List.of(
                new TradeOffers.SellItemFactory(Armors.entertainerArmorSet.armorSet().chest, 15, 1, 12, 15),
                new TradeOffers.SellItemFactory(Items.RABBIT_HIDE, 15, 1, 12, 5)
        ));

        for (var entry: trades.entrySet()) {
            TradeOfferHelper.registerVillagerOffers(profession, entry.getKey(), factories -> {
                factories.addAll(entry.getValue());
            });
        }


        TradeOfferHelper.registerVillagerOffers(profession, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_lute.item(),
                    30,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_lyre.item(),
                    30,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_rapier.item(),
                    40,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_harp_crossbow.item(),
                    40,
                    3,
                    30,
                    0F).create(entity, random)
            ));
        });
    }
}
