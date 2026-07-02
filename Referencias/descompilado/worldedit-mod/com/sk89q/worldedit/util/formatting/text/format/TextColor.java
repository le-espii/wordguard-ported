/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.format;

import com.sk89q.worldedit.util.formatting.text.format.TextFormat;
import com.sk89q.worldedit.util.formatting.text.util.NameMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum TextColor implements TextFormat
{
    BLACK("black"),
    DARK_BLUE("dark_blue"),
    DARK_GREEN("dark_green"),
    DARK_AQUA("dark_aqua"),
    DARK_RED("dark_red"),
    DARK_PURPLE("dark_purple"),
    GOLD("gold"),
    GRAY("gray"),
    DARK_GRAY("dark_gray"),
    BLUE("blue"),
    GREEN("green"),
    AQUA("aqua"),
    RED("red"),
    LIGHT_PURPLE("light_purple"),
    YELLOW("yellow"),
    WHITE("white");

    public static final NameMap<TextColor> NAMES;
    private final String name;

    private TextColor(String name) {
        this.name = name;
    }

    public @NonNull String toString() {
        return this.name;
    }

    static {
        NAMES = NameMap.create(TextColor.class, constant -> constant.name);
    }
}

