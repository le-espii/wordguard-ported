/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import java.util.Comparator;
import java.util.stream.Stream;
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

public final class LinTagType<T extends LinTag<?>> {
    private static final LinTagType<LinEndTag> END_TAG = new LinTagType<LinEndTag>(LinTagId.END, LinEndTag.class);
    private static final LinTagType<LinByteTag> BYTE_TAG = new LinTagType<LinByteTag>(LinTagId.BYTE, LinByteTag.class);
    private static final LinTagType<LinShortTag> SHORT_TAG = new LinTagType<LinShortTag>(LinTagId.SHORT, LinShortTag.class);
    private static final LinTagType<LinIntTag> INT_TAG = new LinTagType<LinIntTag>(LinTagId.INT, LinIntTag.class);
    private static final LinTagType<LinLongTag> LONG_TAG = new LinTagType<LinLongTag>(LinTagId.LONG, LinLongTag.class);
    private static final LinTagType<LinFloatTag> FLOAT_TAG = new LinTagType<LinFloatTag>(LinTagId.FLOAT, LinFloatTag.class);
    private static final LinTagType<LinDoubleTag> DOUBLE_TAG = new LinTagType<LinDoubleTag>(LinTagId.DOUBLE, LinDoubleTag.class);
    private static final LinTagType<LinByteArrayTag> BYTE_ARRAY_TAG = new LinTagType<LinByteArrayTag>(LinTagId.BYTE_ARRAY, LinByteArrayTag.class);
    private static final LinTagType<LinStringTag> STRING_TAG = new LinTagType<LinStringTag>(LinTagId.STRING, LinStringTag.class);
    private static final LinTagType LIST_TAG = new LinTagType<LinListTag>(LinTagId.LIST, LinListTag.class);
    private static final LinTagType<LinCompoundTag> COMPOUND_TAG = new LinTagType<LinCompoundTag>(LinTagId.COMPOUND, LinCompoundTag.class);
    private static final LinTagType<LinIntArrayTag> INT_ARRAY_TAG = new LinTagType<LinIntArrayTag>(LinTagId.INT_ARRAY, LinIntArrayTag.class);
    private static final LinTagType<LinLongArrayTag> LONG_ARRAY_TAG = new LinTagType<LinLongArrayTag>(LinTagId.LONG_ARRAY, LinLongArrayTag.class);
    private static final LinTagType<?>[] LIN_TAG_TYPES = (LinTagType[])Stream.of(END_TAG, BYTE_TAG, SHORT_TAG, INT_TAG, LONG_TAG, FLOAT_TAG, DOUBLE_TAG, BYTE_ARRAY_TAG, STRING_TAG, LIST_TAG, COMPOUND_TAG, INT_ARRAY_TAG, LONG_ARRAY_TAG).sorted(Comparator.comparing(LinTagType::id)).toArray(LinTagType[]::new);
    private final LinTagId id;
    private final Class<T> javaType;

    public static LinTagType<LinEndTag> endTag() {
        return END_TAG;
    }

    public static LinTagType<LinByteTag> byteTag() {
        return BYTE_TAG;
    }

    public static LinTagType<LinShortTag> shortTag() {
        return SHORT_TAG;
    }

    public static LinTagType<LinIntTag> intTag() {
        return INT_TAG;
    }

    public static LinTagType<LinLongTag> longTag() {
        return LONG_TAG;
    }

    public static LinTagType<LinFloatTag> floatTag() {
        return FLOAT_TAG;
    }

    public static LinTagType<LinDoubleTag> doubleTag() {
        return DOUBLE_TAG;
    }

    public static LinTagType<LinByteArrayTag> byteArrayTag() {
        return BYTE_ARRAY_TAG;
    }

    public static LinTagType<LinStringTag> stringTag() {
        return STRING_TAG;
    }

    public static <T extends LinTag<?>> LinTagType<LinListTag<T>> listTag() {
        LinTagType cast = LIST_TAG;
        return cast;
    }

    public static LinTagType<LinCompoundTag> compoundTag() {
        return COMPOUND_TAG;
    }

    public static LinTagType<LinIntArrayTag> intArrayTag() {
        return INT_ARRAY_TAG;
    }

    public static LinTagType<LinLongArrayTag> longArrayTag() {
        return LONG_ARRAY_TAG;
    }

    public static <T extends LinTag<?>> LinTagType<T> fromId(LinTagId id) {
        LinTagType<?> cast = LIN_TAG_TYPES[id.id()];
        return cast;
    }

    private LinTagType(LinTagId id, Class<T> javaType) {
        this.id = id;
        this.javaType = javaType;
    }

    public String name() {
        return this.id.name();
    }

    public LinTagId id() {
        return this.id;
    }

    public T cast(LinTag<?> tag) {
        if (tag.type() != this) {
            throw new IllegalArgumentException("Tag is a " + tag.type().name() + ", not a " + this.name());
        }
        return (T)((LinTag)this.javaType.cast(tag));
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id.toString();
    }
}

