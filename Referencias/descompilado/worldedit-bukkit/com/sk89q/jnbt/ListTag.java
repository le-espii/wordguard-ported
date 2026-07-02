/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.LinBusConverter;
import com.sk89q.jnbt.NBTUtils;
import com.sk89q.jnbt.Tag;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

@Deprecated
public final class ListTag<EV, E extends LinTag<EV>>
extends Tag<Object, LinListTag<E>> {
    public ListTag(Class<? extends Tag<EV, E>> type, List<? extends Tag<EV, E>> value) {
        this(LinListTag.of(LinTagType.fromId(LinTagId.fromId(NBTUtils.getTypeCode(type))), value.stream().map(Tag::toLinTag).toList()));
    }

    public ListTag(LinListTag<E> tag) {
        super(tag);
    }

    public Class<? extends Tag<EV, E>> getType() {
        return NBTUtils.getTypeClass(((LinListTag)this.linTag).elementType().id().id());
    }

    @Override
    public List<? extends Tag<EV, E>> getValue() {
        return ((LinListTag)this.linTag).value().stream().map(LinBusConverter::toJnbtTag).toList();
    }

    public ListTag<EV, E> setValue(List<? extends Tag<EV, E>> list) {
        return new ListTag<EV, E>(this.getType(), list);
    }

    private <T> T accessIfExists(int index, Supplier<T> defaultValue, IntFunction<T> accessor) {
        if (index >= ((LinListTag)this.linTag).value().size()) {
            return defaultValue.get();
        }
        return accessor.apply(index);
    }

    private <T, LT extends LinTag<T>> T extractViaValue(int index, Class<LT> requiredType, Supplier<T> defaultValue) {
        if (index >= ((LinListTag)this.linTag).value().size()) {
            return defaultValue.get();
        }
        Object value = ((LinListTag)this.linTag).get(index);
        if (!requiredType.isInstance(value)) {
            return defaultValue.get();
        }
        return ((LinTag)value).value();
    }

    @Nullable
    public Tag<EV, E> getIfExists(int index) {
        return this.accessIfExists(index, () -> null, i -> LinBusConverter.toJnbtTag(((LinListTag)this.linTag).get(i)));
    }

    public byte[] getByteArray(int index) {
        return this.extractViaValue(index, LinByteArrayTag.class, () -> new byte[0]);
    }

    public byte getByte(int index) {
        return this.extractViaValue(index, LinByteTag.class, () -> (byte)0);
    }

    public double getDouble(int index) {
        return this.extractViaValue(index, LinDoubleTag.class, () -> 0.0);
    }

    public double asDouble(int index) {
        return this.accessIfExists(index, () -> 0.0, i -> {
            Double d;
            Object patt0$temp = ((LinListTag)this.linTag).get(i);
            if (patt0$temp instanceof LinNumberTag) {
                LinNumberTag tag = (LinNumberTag)patt0$temp;
                d = ((Number)tag.value()).doubleValue();
            } else {
                d = 0.0;
            }
            return d;
        });
    }

    public float getFloat(int index) {
        return this.extractViaValue(index, LinFloatTag.class, () -> Float.valueOf(0.0f)).floatValue();
    }

    public int[] getIntArray(int index) {
        return this.extractViaValue(index, LinIntArrayTag.class, () -> new int[0]);
    }

    public int getInt(int index) {
        return this.extractViaValue(index, LinIntTag.class, () -> 0);
    }

    public int asInt(int index) {
        return this.accessIfExists(index, () -> 0, i -> {
            Integer n;
            Object patt0$temp = ((LinListTag)this.linTag).get(i);
            if (patt0$temp instanceof LinNumberTag) {
                LinNumberTag tag = (LinNumberTag)patt0$temp;
                n = ((Number)tag.value()).intValue();
            } else {
                n = 0;
            }
            return n;
        });
    }

    public List<? extends Tag<?, ?>> getList(int index) {
        return this.getListTag(index).getValue();
    }

    public ListTag<?, ?> getListTag(int index) {
        return new ListTag<EV, E>(this.extractViaValue(index, LinListTag.class, () -> LinListTag.empty(LinTagType.endTag())));
    }

    public <T extends Tag<?, ?>> List<T> getList(int index, Class<T> listType) {
        ListTag<?, ?> listTag = this.getListTag(index);
        if (listTag.getType().equals(listType)) {
            return listTag.getValue();
        }
        return Collections.emptyList();
    }

    public long getLong(int index) {
        return this.extractViaValue(index, LinLongTag.class, () -> 0L);
    }

    public long asLong(int index) {
        return this.accessIfExists(index, () -> 0L, i -> {
            Long l;
            Object patt0$temp = ((LinListTag)this.linTag).get(i);
            if (patt0$temp instanceof LinNumberTag) {
                LinNumberTag tag = (LinNumberTag)patt0$temp;
                l = ((Number)tag.value()).longValue();
            } else {
                l = 0L;
            }
            return l;
        });
    }

    public short getShort(int index) {
        return this.extractViaValue(index, LinShortTag.class, () -> (short)0);
    }

    public String getString(int index) {
        return this.extractViaValue(index, LinStringTag.class, () -> "");
    }
}

