/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2479
 *  net.minecraft.class_2481
 *  net.minecraft.class_2487
 *  net.minecraft.class_2489
 *  net.minecraft.class_2491
 *  net.minecraft.class_2494
 *  net.minecraft.class_2495
 *  net.minecraft.class_2497
 *  net.minecraft.class_2499
 *  net.minecraft.class_2501
 *  net.minecraft.class_2503
 *  net.minecraft.class_2516
 *  net.minecraft.class_2519
 *  net.minecraft.class_2520
 */
package com.sk89q.worldedit.fabric.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.class_2479;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2491;
import net.minecraft.class_2494;
import net.minecraft.class_2495;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2501;
import net.minecraft.class_2503;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
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

    public static class_2520 toNative(LinTag<?> tag) {
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

    public static class_2495 toNative(LinIntArrayTag tag) {
        int[] value = tag.value();
        return new class_2495(Arrays.copyOf(value, value.length));
    }

    public static class_2499 toNative(LinListTag<?> tag) {
        class_2499 list = new class_2499();
        Iterator iterator = tag.value().iterator();
        while (iterator.hasNext()) {
            LinTag child = (LinTag)iterator.next();
            list.add((Object)NBTConverter.toNative(child));
        }
        return list;
    }

    public static class_2503 toNative(LinLongTag tag) {
        return class_2503.method_23251((long)tag.value());
    }

    public static class_2501 toNative(LinLongArrayTag tag) {
        return new class_2501((long[])tag.value().clone());
    }

    public static class_2519 toNative(LinStringTag tag) {
        return class_2519.method_23256((String)tag.value());
    }

    public static class_2497 toNative(LinIntTag tag) {
        return class_2497.method_23247((int)tag.value());
    }

    public static class_2481 toNative(LinByteTag tag) {
        return class_2481.method_23233((byte)tag.value());
    }

    public static class_2479 toNative(LinByteArrayTag tag) {
        return new class_2479((byte[])tag.value().clone());
    }

    public static class_2487 toNative(LinCompoundTag tag) {
        class_2487 compound = new class_2487();
        tag.value().forEach((key, value) -> compound.method_10566(key, NBTConverter.toNative(value)));
        return compound;
    }

    public static class_2494 toNative(LinFloatTag tag) {
        return class_2494.method_23244((float)tag.value().floatValue());
    }

    public static class_2516 toNative(LinShortTag tag) {
        return class_2516.method_23254((short)tag.value());
    }

    public static class_2489 toNative(LinDoubleTag tag) {
        return class_2489.method_23241((double)tag.value());
    }

    public static LinTag<?> fromNative(class_2520 other) {
        if (other instanceof class_2495) {
            return NBTConverter.fromNative((class_2495)other);
        }
        if (other instanceof class_2499) {
            return NBTConverter.fromNative((class_2499)other);
        }
        if (other instanceof class_2491) {
            return NBTConverter.fromNative((class_2491)other);
        }
        if (other instanceof class_2503) {
            return NBTConverter.fromNative((class_2503)other);
        }
        if (other instanceof class_2501) {
            return NBTConverter.fromNative((class_2501)other);
        }
        if (other instanceof class_2519) {
            return NBTConverter.fromNative((class_2519)other);
        }
        if (other instanceof class_2497) {
            return NBTConverter.fromNative((class_2497)other);
        }
        if (other instanceof class_2481) {
            return NBTConverter.fromNative((class_2481)other);
        }
        if (other instanceof class_2479) {
            return NBTConverter.fromNative((class_2479)other);
        }
        if (other instanceof class_2487) {
            return NBTConverter.fromNative((class_2487)other);
        }
        if (other instanceof class_2494) {
            return NBTConverter.fromNative((class_2494)other);
        }
        if (other instanceof class_2516) {
            return NBTConverter.fromNative((class_2516)other);
        }
        if (other instanceof class_2489) {
            return NBTConverter.fromNative((class_2489)other);
        }
        throw new IllegalArgumentException("Can't convert other of type " + other.getClass().getCanonicalName());
    }

    public static LinIntArrayTag fromNative(class_2495 other) {
        int[] value = other.method_10588();
        return LinIntArrayTag.of(Arrays.copyOf(value, value.length));
    }

    public static LinListTag<?> fromNative(class_2499 other) {
        LinListTag.Builder<LinTag<?>> list = LinListTag.builder(LinTagType.fromId(LinTagId.fromId(other.method_10601())));
        for (class_2520 tag : other) {
            list.add(NBTConverter.fromNative(tag));
        }
        return list.build();
    }

    public static LinEndTag fromNative(class_2491 other) {
        return LinEndTag.instance();
    }

    public static LinLongTag fromNative(class_2503 other) {
        return LinLongTag.of(other.method_10699());
    }

    public static LinLongArrayTag fromNative(class_2501 other) {
        return LinLongArrayTag.of((long[])other.method_10615().clone());
    }

    public static LinStringTag fromNative(class_2519 other) {
        return LinStringTag.of(other.method_10714());
    }

    public static LinIntTag fromNative(class_2497 other) {
        return LinIntTag.of(other.method_10701());
    }

    public static LinByteTag fromNative(class_2481 other) {
        return LinByteTag.of(other.method_10698());
    }

    public static LinByteArrayTag fromNative(class_2479 other) {
        return LinByteArrayTag.of((byte[])other.method_10521().clone());
    }

    public static LinCompoundTag fromNative(class_2487 other) {
        Set tags = other.method_10541();
        LinCompoundTag.Builder builder = LinCompoundTag.builder();
        for (String tagName : tags) {
            builder.put(tagName, NBTConverter.fromNative(other.method_10580(tagName)));
        }
        return builder.build();
    }

    public static LinFloatTag fromNative(class_2494 other) {
        return LinFloatTag.of(other.method_10700());
    }

    public static LinShortTag fromNative(class_2516 other) {
        return LinShortTag.of(other.method_10696());
    }

    public static LinDoubleTag fromNative(class_2489 other) {
        return LinDoubleTag.of(other.method_10697());
    }
}

