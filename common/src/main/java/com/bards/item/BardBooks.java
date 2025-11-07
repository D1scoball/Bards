package com.bards.item;

import net.minecraft.util.Identifier;
import net.spell_engine.api.item.SpellBooks;

import java.util.List;

import static com.bards.BardsMod.MOD_ID;

public class BardBooks {
    public static void register() {
        var books = List.of("bard");
        for (var name: books) {
            SpellBooks.createAndRegister(Identifier.of(MOD_ID, name), Group.KEY);
        }
    }
}
