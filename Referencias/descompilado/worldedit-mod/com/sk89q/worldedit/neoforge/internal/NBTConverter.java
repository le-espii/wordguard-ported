/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.ByteArrayTag
 *  net.minecraft.nbt.ByteTag
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.DoubleTag
 *  net.minecraft.nbt.EndTag
 *  net.minecraft.nbt.FloatTag
 *  net.minecraft.nbt.IntArrayTag
 *  net.minecraft.nbt.IntTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.LongArrayTag
 *  net.minecraft.nbt.LongTag
 *  net.minecraft.nbt.ShortTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 */
package com.sk89q.worldedit.neoforge.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinEndTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongArrayTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public final class NBTConverter {
    private NBTConverter() {
    }

    public static Tag toNative(LinTag<?> tag) {
        if (tag instanceof LinIntArrayTag) {
            LinIntArrayTag t = (LinIntArrayTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinListTag) {
            LinListTag t = (LinListTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinLongTag) {
            LinLongTag t = (LinLongTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinLongArrayTag) {
            LinLongArrayTag t = (LinLongArrayTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinStringTag) {
            LinStringTag t = (LinStringTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinIntTag) {
            LinIntTag t = (LinIntTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinByteTag) {
            LinByteTag t = (LinByteTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinByteArrayTag) {
            LinByteArrayTag t = (LinByteArrayTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinCompoundTag) {
            LinCompoundTag t = (LinCompoundTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinFloatTag) {
            LinFloatTag t = (LinFloatTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinShortTag) {
            LinShortTag t = (LinShortTag)tag;
            return NBTConverter.toNative(t);
        }
        if (tag instanceof LinDoubleTag) {
            LinDoubleTag t = (LinDoubleTag)tag;
            return NBTConverter.toNative(t);
        }
        throw new IllegalArgumentException("Can't convert tag of type " + tag.getClass().getCanonicalName());
    }

    public static IntArrayTag toNative(LinIntArrayTag tag) {
        int[] value = tag.value();
        return new IntArrayTag(Arrays.copyOf(value, value.length));
    }

    public static ListTag toNative(LinListTag<?> tag) {
        ListTag list = new ListTag();
        Iterator iterator = tag.value().iterator();
        while (iterator.hasNext()) {
            LinTag child = (LinTag)iterator.next();
            list.add((Object)NBTConverter.toNative(child));
        }
        return list;
    }

    public static LongTag toNative(LinLongTag tag) {
        return LongTag.valueOf((long)tag.value());
    }

    public static LongArrayTag toNative(LinLongArrayTag tag) {
        return new LongArrayTag((long[])tag.value().clone());
    }

    public static StringTag toNative(LinStringTag tag) {
        return StringTag.valueOf((String)tag.value());
    }

    public static IntTag toNative(LinIntTag tag) {
        return IntTag.valueOf((int)tag.value());
    }

    public static ByteTag toNative(LinByteTag tag) {
        return ByteTag.valueOf((byte)tag.value());
    }

    public static ByteArrayTag toNative(LinByteArrayTag tag) {
        return new ByteArrayTag((byte[])tag.value().clone());
    }

    public static CompoundTag toNative(LinCompoundTag tag) {
        CompoundTag compound = new CompoundTag();
        tag.value().forEach((key, value) -> compound.put(key, NBTConverter.toNative(value)));
        return compound;
    }

    public static FloatTag toNative(LinFloatTag tag) {
        return FloatTag.valueOf((float)tag.value().floatValue());
    }

    public static ShortTag toNative(LinShortTag tag) {
        return ShortTag.valueOf((short)tag.value());
    }

    public static DoubleTag toNative(LinDoubleTag tag) {
        return DoubleTag.valueOf((double)tag.value());
    }

    public static LinTag<?> fromNative(Tag other) {
        if (other instanceof IntArrayTag) {
            return NBTConverter.fromNative((IntArrayTag)other);
        }
        if (other instanceof ListTag) {
            return NBTConverter.fromNative((ListTag)other);
        }
        if (other instanceof EndTag) {
            return NBTConverter.fromNative((EndTag)other);
        }
        if (other instanceof LongTag) {
            return NBTConverter.fromNative((LongTag)other);
        }
        if (other instanceof LongArrayTag) {
            return NBTConverter.fromNative((LongArrayTag)other);
        }
        if (other instanceof StringTag) {
            return NBTConverter.fromNative((StringTag)other);
        }
        if (other instanceof IntTag) {
            return NBTConverter.fromNative((IntTag)other);
        }
        if (other instanceof ByteTag) {
            return NBTConverter.fromNative((ByteTag)other);
        }
        if (other instanceof ByteArrayTag) {
            return NBTConverter.fromNative((ByteArrayTag)other);
        }
        if (other instanceof CompoundTag) {
            return NBTConverter.fromNative((CompoundTag)other);
        }
        if (other instanceof FloatTag) {
            return NBTConverter.fromNative((FloatTag)other);
        }
        if (other instanceof ShortTag) {
            return NBTConverter.fromNative((ShortTag)other);
        }
        if (other instanceof DoubleTag) {
            return NBTConverter.fromNative((DoubleTag)other);
        }
        throw new IllegalArgumentException("Can't convert other of type " + other.getClass().getCanonicalName());
    }

    public static LinIntArrayTag fromNative(IntArrayTag other) {
        int[] value = other.getAsIntArray();
        return LinIntArrayTag.of(Arrays.copyOf(value, value.length));
    }

    public static LinListTag<?> fromNative(ListTag other) {
        LinListTag.Builder<LinTag<?>> list = LinListTag.builder(LinTagType.fromId(LinTagId.fromId(other.getElementType())));
        for (Tag tag : other) {
            list.add(NBTConverter.fromNative(tag));
        }
        return list.build();
    }

    public static LinEndTag fromNative(EndTag other) {
        return LinEndTag.instance();
    }

    public static LinLongTag fromNative(LongTag other) {
        return LinLongTag.of(other.getAsLong());
    }

    public static LinLongArrayTag fromNative(LongArrayTag other) {
        return LinLongArrayTag.of((long[])other.getAsLongArray().clone());
    }

    public static LinStringTag fromNative(StringTag other) {
        return LinStringTag.of(other.getAsString());
    }

    public static LinIntTag fromNative(IntTag other) {
        return LinIntTag.of(other.getAsInt());
    }

    public static LinByteTag fromNative(ByteTag other) {
        return LinByteTag.of(other.getAsByte());
    }

    public static LinByteArrayTag fromNative(ByteArrayTag other) {
        return LinByteArrayTag.of((byte[])other.getAsByteArray().clone());
    }

    public static LinCompoundTag fromNative(CompoundTag other) {
        Set tags = other.getAllKeys();
        LinCompoundTag.Builder builder = LinCompoundTag.builder();
        for (String tagName : tags) {
            builder.put(tagName, NBTConverter.fromNative(other.get(tagName)));
        }
        return builder.build();
    }

    public static LinFloatTag fromNative(FloatTag other) {
        return LinFloatTag.of(other.getAsFloat());
    }

    public static LinShortTag fromNative(ShortTag other) {
        return LinShortTag.of(other.getAsShort());
    }

    public static LinDoubleTag fromNative(DoubleTag other) {
        return LinDoubleTag.of(other.getAsDouble());
    }
}

