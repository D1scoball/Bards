package com.bards.worldgen.villages;

import com.bards.content.BardsSounds;
import com.bards.effect.BardsEffects;
import com.bards.item.Weapons;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

import java.util.List;

public class LuthierSongs {

    public record Song(
            String name,
            Item heldInstrument,
            float noteHue,
            RegistryEntry<StatusEffect> effect,
            int effectAmplifier,
            RegistryEntry<SoundEvent> sound,
            int soundDurationTicks
    ) {}

    public static final List<Song> SONGS = List.of(
            new Song(BardsEffects.TROUBADOURS_MINUET.title, Weapons.wooden_lute.item(), 0.0f, BardsEffects.TROUBADOURS_MINUET.entry, 0, BardsSounds.troubadours_minuet.entry(), 100)
    );
}
