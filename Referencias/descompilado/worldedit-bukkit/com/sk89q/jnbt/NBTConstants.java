/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.ByteTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.DoubleTag;
import com.sk89q.jnbt.EndTag;
import com.sk89q.jnbt.FloatTag;
import com.sk89q.jnbt.IntArrayTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.LongArrayTag;
import com.sk89q.jnbt.LongTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Deprecated
public final class NBTConstants {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final int TYPE_END = 0;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_BYTE_ARRAY = 7;
    public static final int TYPE_STRING = 8;
    public static final int TYPE_LIST = 9;
    public static final int TYPE_COMPOUND = 10;
    public static final int TYPE_INT_ARRAY = 11;
    public static final int TYPE_LONG_ARRAY = 12;

    private NBTConstants() {
    }

    public static Class<? extends Tag<?, ?>> getClassFromType(int id) {
        return switch (id) {
            case 0 -> EndTag.class;
            case 1 -> ByteTag.class;
            case 2 -> ShortTag.class;
            case 3 -> IntTag.class;
            case 4 -> LongTag.class;
            case 5 -> FloatTag.class;
            case 6 -> DoubleTag.class;
            case 7 -> ByteArrayTag.class;
            case 8 -> StringTag.class;
            case 9 -> {
                Class<ListTag> aClass = ListTag.class;
                yield aClass;
            }
            case 10 -> CompoundTag.class;
            case 11 -> IntArrayTag.class;
            case 12 -> LongArrayTag.class;
            default -> throw new IllegalArgumentException("Unknown tag type ID of " + id);
        };
    }
}

