/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class CommandUtils {
    private CommandUtils() {
    }

    public static String replaceColorMacros(String str) {
        str = str.replace("`r", "&c");
        str = str.replace("`R", "&4");
        str = str.replace("`y", "&e");
        str = str.replace("`Y", "&6");
        str = str.replace("`g", "&a");
        str = str.replace("`G", "&2");
        str = str.replace("`c", "&b");
        str = str.replace("`C", "&3");
        str = str.replace("`b", "&9");
        str = str.replace("`B", "&1");
        str = str.replace("`p", "&d");
        str = str.replace("`P", "&5");
        str = str.replace("`0", "&0");
        str = str.replace("`1", "&8");
        str = str.replace("`2", "&7");
        str = str.replace("`w", "&F");
        str = str.replace("`k", "&k");
        str = str.replace("`l", "&l");
        str = str.replace("`m", "&m");
        str = str.replace("`n", "&n");
        str = str.replace("`o", "&o");
        str = str.replace("`x", "&r");
        str = Arrays.stream(str.split("\n")).map(line -> {
            TextComponent comp = LegacyComponentSerializer.INSTANCE.deserialize(line, '&');
            return LegacyComponentSerializer.INSTANCE.serialize((Component)comp);
        }).collect(Collectors.joining("\n"));
        return str;
    }

    public static String getOwnerName(@Nullable Object owner) {
        if (owner == null) {
            return "?";
        }
        if (owner instanceof Actor) {
            return ((Actor)owner).getName();
        }
        return "?";
    }

    public static Function<String, ?> messageFunction(Actor sender) {
        return s -> {
            sender.printRaw(s);
            return null;
        };
    }

    public static Function<TextComponent, ?> messageComponentFunction(Actor sender) {
        return s -> {
            sender.print((Component)s);
            return null;
        };
    }
}

