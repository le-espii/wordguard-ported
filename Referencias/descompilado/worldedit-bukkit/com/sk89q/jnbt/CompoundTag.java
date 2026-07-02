/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.sk89q.jnbt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sk89q.jnbt.CompoundTagBuilder;
import com.sk89q.jnbt.LinBusConverter;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

@Deprecated
public final class CompoundTag
extends Tag<Object, LinCompoundTag> {
    public CompoundTag(Map<String, Tag<?, ?>> value) {
        this(LinCompoundTag.of(Maps.transformValues(value, Tag::toLinTag)));
    }

    public CompoundTag(LinCompoundTag tag) {
        super(tag);
    }

    public boolean containsKey(String key) {
        return ((LinCompoundTag)this.linTag).value().containsKey(key);
    }

    @Override
    public Map<String, Tag<?, ?>> getValue() {
        return ImmutableMap.copyOf((Map)Maps.transformValues((Map)((LinCompoundTag)this.linTag).value(), tag -> LinBusConverter.toJnbtTag(tag)));
    }

    public CompoundTag setValue(Map<String, Tag<?, ?>> value) {
        return new CompoundTag(value);
    }

    public CompoundTagBuilder createBuilder() {
        return new CompoundTagBuilder((LinCompoundTag)this.linTag);
    }

    public byte[] getByteArray(String key) {
        LinByteArrayTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.byteArrayTag());
        return tag == null ? new byte[]{} : tag.value();
    }

    public byte getByte(String key) {
        LinByteTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.byteTag());
        return tag == null ? (byte)0 : tag.value();
    }

    public double getDouble(String key) {
        LinDoubleTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.doubleTag());
        return tag == null ? 0.0 : tag.value();
    }

    public double asDouble(String key) {
        LinTag tag = (LinTag)((LinCompoundTag)this.linTag).value().get(key);
        if (tag instanceof LinNumberTag) {
            LinNumberTag numberTag = (LinNumberTag)tag;
            Number value = (Number)numberTag.value();
            return value.doubleValue();
        }
        return 0.0;
    }

    public float getFloat(String key) {
        LinFloatTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.floatTag());
        return tag == null ? 0.0f : tag.value().floatValue();
    }

    public int[] getIntArray(String key) {
        LinIntArrayTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.intArrayTag());
        return tag == null ? new int[]{} : tag.value();
    }

    public int getInt(String key) {
        LinIntTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.intTag());
        return tag == null ? 0 : tag.value();
    }

    public int asInt(String key) {
        LinTag tag = (LinTag)((LinCompoundTag)this.linTag).value().get(key);
        if (tag instanceof LinNumberTag) {
            LinNumberTag numberTag = (LinNumberTag)tag;
            Number value = (Number)numberTag.value();
            return value.intValue();
        }
        return 0;
    }

    public List<? extends Tag<?, ?>> getList(String key) {
        return this.getListTag(key).getValue();
    }

    public <EV, E extends LinTag<EV>> ListTag<EV, E> getListTag(String key) {
        LinListTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.listTag());
        if (tag == null) {
            LinTagType<LinEndTag> endGenerically = LinTagType.endTag();
            return new ListTag(LinListTag.empty(endGenerically));
        }
        return new ListTag(tag);
    }

    public <T extends Tag<?, ?>> List<T> getList(String key, Class<T> listType) {
        ListTag listTag = this.getListTag(key);
        if (listTag.getType().equals(listType)) {
            return listTag.getValue();
        }
        return Collections.emptyList();
    }

    public long[] getLongArray(String key) {
        LinLongArrayTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.longArrayTag());
        return tag == null ? new long[]{} : tag.value();
    }

    public long getLong(String key) {
        LinLongTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.longTag());
        return tag == null ? 0L : tag.value();
    }

    public long asLong(String key) {
        LinTag tag = (LinTag)((LinCompoundTag)this.linTag).value().get(key);
        if (tag instanceof LinNumberTag) {
            LinNumberTag numberTag = (LinNumberTag)tag;
            Number value = (Number)numberTag.value();
            return value.longValue();
        }
        return 0L;
    }

    public short getShort(String key) {
        LinShortTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.shortTag());
        return tag == null ? (short)0 : tag.value();
    }

    public String getString(String key) {
        LinStringTag tag = ((LinCompoundTag)this.linTag).findTag(key, LinTagType.stringTag());
        return tag == null ? "" : tag.value();
    }
}

