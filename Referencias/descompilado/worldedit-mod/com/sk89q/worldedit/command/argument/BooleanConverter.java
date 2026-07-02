/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableSetMultimap;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.MultiKeyConverter;
import org.enginehub.piston.inject.Key;

public class BooleanConverter {
    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(Boolean.class), MultiKeyConverter.builder(ImmutableSetMultimap.builder().putAll((Object)false, (Object[])new String[]{"off", "f", "false", "n", "no"}).putAll((Object)true, (Object[])new String[]{"on", "t", "true", "y", "yes"}).build()).errorMessage(arg -> "Not a boolean value: " + arg).build());
    }

    private BooleanConverter() {
    }
}

