/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.jnbt;

import com.google.common.base.Preconditions;
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
import com.sk89q.jnbt.NBTConstants;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import java.util.Map;

@Deprecated
public final class NBTUtils {
    private NBTUtils() {
    }

    public static String getTypeName(Class<? extends Tag<?, ?>> clazz) {
        if (clazz.equals(ByteArrayTag.class)) {
            return "TAG_Byte_Array";
        }
        if (clazz.equals(ByteTag.class)) {
            return "TAG_Byte";
        }
        if (clazz.equals(CompoundTag.class)) {
            return "TAG_Compound";
        }
        if (clazz.equals(DoubleTag.class)) {
            return "TAG_Double";
        }
        if (clazz.equals(EndTag.class)) {
            return "TAG_End";
        }
        if (clazz.equals(FloatTag.class)) {
            return "TAG_Float";
        }
        if (clazz.equals(IntTag.class)) {
            return "TAG_Int";
        }
        if (clazz.equals(ListTag.class)) {
            return "TAG_List";
        }
        if (clazz.equals(LongTag.class)) {
            return "TAG_Long";
        }
        if (clazz.equals(ShortTag.class)) {
            return "TAG_Short";
        }
        if (clazz.equals(StringTag.class)) {
            return "TAG_String";
        }
        if (clazz.equals(IntArrayTag.class)) {
            return "TAG_Int_Array";
        }
        if (clazz.equals(LongArrayTag.class)) {
            return "TAG_Long_Array";
        }
        throw new IllegalArgumentException("Invalid tag class (" + clazz.getName() + ").");
    }

    public static int getTypeCode(Class<? extends Tag<?, ?>> clazz) {
        if (clazz == ByteArrayTag.class) {
            return 7;
        }
        if (clazz == ByteTag.class) {
            return 1;
        }
        if (clazz == CompoundTag.class) {
            return 10;
        }
        if (clazz == DoubleTag.class) {
            return 6;
        }
        if (clazz == EndTag.class) {
            return 0;
        }
        if (clazz == FloatTag.class) {
            return 5;
        }
        if (clazz == IntArrayTag.class) {
            return 11;
        }
        if (clazz == IntTag.class) {
            return 3;
        }
        if (clazz.equals(ListTag.class)) {
            return 9;
        }
        if (clazz == LongArrayTag.class) {
            return 12;
        }
        if (clazz == LongTag.class) {
            return 4;
        }
        if (clazz == ShortTag.class) {
            return 2;
        }
        if (clazz == StringTag.class) {
            return 8;
        }
        throw new IllegalArgumentException("Invalid tag class (" + clazz.getName() + ")");
    }

    public static Class<? extends Tag<?, ?>> getTypeClass(int type) {
        return NBTConstants.getClassFromType(type);
    }

    public static Vector3 toVector(ListTag<?, ?> listTag) {
        Preconditions.checkNotNull(listTag);
        return Vector3.at(listTag.asDouble(0), listTag.asDouble(1), listTag.asDouble(2));
    }

    public static <T extends Tag<?, ?>> T getChildTag(Map<String, Tag<?, ?>> items, String key, Class<T> expected) throws InvalidFormatException {
        if (!items.containsKey(key)) {
            throw new InvalidFormatException("Missing a \"" + key + "\" tag");
        }
        Tag<?, ?> tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new InvalidFormatException(key + " tag is not of tag type " + expected.getName());
        }
        return (T)((Tag)expected.cast(tag));
    }
}

