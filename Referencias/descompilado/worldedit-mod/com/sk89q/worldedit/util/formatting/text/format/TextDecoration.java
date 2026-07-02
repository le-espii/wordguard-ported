/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.format;

import com.sk89q.worldedit.util.formatting.text.format.TextFormat;
import com.sk89q.worldedit.util.formatting.text.util.NameMap;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum TextDecoration implements TextFormat
{
    OBFUSCATED("obfuscated"),
    BOLD("bold"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINED("underlined"),
    ITALIC("italic");

    public static final NameMap<TextDecoration> NAMES;
    private final String name;

    private TextDecoration(String name) {
        this.name = name;
    }

    public @NonNull String toString() {
        return this.name;
    }

    @Deprecated
    public static @NonNull Set<TextDecoration> setOf(TextDecoration ... decorations) {
        return ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations);
    }

    static {
        NAMES = NameMap.create(TextDecoration.class, constant -> constant.name);
    }

    public static enum State {
        NOT_SET{

            @Override
            public String toString() {
                return "null";
            }
        }
        ,
        FALSE,
        TRUE;


        public String toString() {
            return this.name().toLowerCase();
        }

        public static @NonNull State byBoolean(boolean flag) {
            return flag ? TRUE : FALSE;
        }

        public static @NonNull State byBoolean(@Nullable Boolean flag) {
            return flag == null ? NOT_SET : State.byBoolean((boolean)flag);
        }
    }
}

