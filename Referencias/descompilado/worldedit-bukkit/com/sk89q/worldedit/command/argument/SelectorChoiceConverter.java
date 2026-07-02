/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nonnull
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.sk89q.worldedit.command.argument.SelectorChoice;
import com.sk89q.worldedit.command.argument.SelectorChoiceList;
import com.sk89q.worldedit.command.argument.SelectorChoiceOrList;
import java.util.Locale;
import javax.annotation.Nonnull;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.MultiKeyConverter;
import org.enginehub.piston.inject.Key;

public class SelectorChoiceConverter {
    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(SelectorChoice.class), MultiKeyConverter.from(SelectorChoiceConverter.getBasicItems()));
        commandManager.registerConverter(Key.of(SelectorChoiceOrList.class), SelectorChoiceConverter.orListConverter());
    }

    private static ArgumentConverter<SelectorChoiceOrList> orListConverter() {
        LinkedHashMultimap items = LinkedHashMultimap.create(SelectorChoiceConverter.getBasicItems());
        items.put((Object)SelectorChoiceList.INSTANCE, (Object)"list");
        return MultiKeyConverter.from(ImmutableSetMultimap.copyOf((Multimap)items), SelectorChoiceList.INSTANCE);
    }

    @Nonnull
    private static SetMultimap<SelectorChoice, String> getBasicItems() {
        LinkedHashMultimap items = LinkedHashMultimap.create();
        for (SelectorChoice item : SelectorChoice.values()) {
            items.put((Object)item, (Object)item.name().toLowerCase(Locale.ROOT));
        }
        items.put((Object)SelectorChoice.CONVEX, (Object)"hull");
        items.put((Object)SelectorChoice.CONVEX, (Object)"polyhedron");
        return items;
    }
}

