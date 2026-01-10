package com.bards.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
import net.spell_engine.api.effect.CustomStatusEffect;
import net.spell_engine.api.effect.Effects;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.effect.TickingStatusEffect;
import net.spell_engine.api.entity.SpellEngineAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.bards.BardsMod.MOD_ID;

public class BardsEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static Effects.Entry BALLAD = add(new Effects.Entry(Identifier.of(MOD_ID, "ballad"),
            "Ballad",
            "",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x9999ff),
            new EffectConfig(
                    List.of(
                    )
            )
    ));

    public static Effects.Entry TROUBADOURS_MINUET = add(new Effects.Entry(Identifier.of(MOD_ID, "troubadours_minuet"),
            "Troubadours Minuet",
            "",
            new TickingStatusEffect(StatusEffectCategory.BENEFICIAL, 0x9999ff).interval(30),
            new EffectConfig(
                    List.of(
                    )
            )
    ));
    public static Effects.Entry TROUBADOURS_MINUET_BUFF = add(new Effects.Entry(Identifier.of(MOD_ID, "troubadours_minuet_buff"),
            "Troubadours Minuet",
            "",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x9999ff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellEngineAttributes.DAMAGE_TAKEN.id.toString(),
                                    -0.05F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    SpellEngineAttributes.HEALING_TAKEN.id.toString(),
                                    0.05F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry ARMYS_PAEON = add(new Effects.Entry(Identifier.of(MOD_ID, "armys_paeon"),
            "Army's Paeon",
            "",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x9999ff),
            new EffectConfig(
                    List.of(
                    )
            )
    ));


    public static void register(ConfigFile.Effects config) {
        for (var entry : entries) {
            Synchronized.configure(entry.effect, true);
        }
        Effects.register(entries, config.effects);

    }
}
