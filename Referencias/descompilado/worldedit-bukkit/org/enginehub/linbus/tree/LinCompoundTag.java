/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.stream.internal.FlatteningLinStream;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinDoubleTag;
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
import org.enginehub.linbus.tree.impl.LinTagReader;
import org.jspecify.annotations.Nullable;

public final class LinCompoundTag
extends LinTag<Map<String, ? extends LinTag<?>>> {
    private final Map<String, LinTag<?>> value;

    public static LinCompoundTag of(Map<String, ? extends LinTag<?>> value) {
        return new LinCompoundTag(LinCompoundTag.copyImmutable(value), true);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LinCompoundTag readFrom(LinStream tokens) throws IOException {
        return LinTagReader.readCompound(tokens);
    }

    private static Map<String, LinTag<?>> copyImmutable(Map<String, ? extends LinTag<?>> value) {
        return Collections.unmodifiableMap(new LinkedHashMap(value));
    }

    private LinCompoundTag(Map<String, LinTag<?>> value, boolean check) {
        if (check) {
            for (LinTag<?> tag : value.values()) {
                if (tag.type().id() != LinTagId.END) continue;
                throw new IllegalArgumentException("Cannot add END tag to compound tag");
            }
        }
        this.value = Objects.requireNonNull(value, "value is null");
    }

    @Override
    public LinTagType<LinCompoundTag> type() {
        return LinTagType.compoundTag();
    }

    @Override
    public Map<String, LinTag<?>> value() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return new SurroundingLinStream(new LinToken.CompoundStart(), new FlatteningLinStream(new EntryTokenIterator()), new LinToken.CompoundEnd());
    }

    public <T extends LinTag<?>> @Nullable T findTag(String name, LinTagType<T> type) {
        LinTag<?> tag = this.value.get(name);
        return tag != null && type == tag.type() ? (T)type.cast(tag) : null;
    }

    public <T extends LinTag<?>> @Nullable LinListTag<T> findListTag(String name, LinTagType<T> elementType) {
        LinListTag listTag = this.findTag(name, LinTagType.listTag());
        if (listTag == null || listTag.elementType() != elementType) {
            return null;
        }
        LinListTag cast = listTag;
        return cast;
    }

    public <T extends LinTag<?>> T getTag(String name, LinTagType<T> type) {
        LinTag<?> tag = this.value.get(name);
        if (tag == null) {
            throw new NoSuchElementException("No tag under the name '" + name + "' exists");
        }
        if (type != tag.type()) {
            throw new IllegalStateException("Tag under '" + name + "' exists, but is a " + tag.type().name() + " instead of " + type.name());
        }
        return type.cast(tag);
    }

    public <T extends LinTag<?>> LinListTag<T> getListTag(String name, LinTagType<T> elementType) {
        LinListTag listTag = this.getTag(name, LinTagType.listTag());
        if (listTag.elementType() != elementType) {
            throw new IllegalStateException("Tag under '" + name + "' exists, but is a " + listTag.elementType().name() + " list instead of a " + elementType.name() + " list");
        }
        LinListTag cast = listTag;
        return cast;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + String.valueOf(this.value);
    }

    public static final class Builder {
        private final LinkedHashMap<String, LinTag<?>> collector;

        private Builder() {
            this.collector = new LinkedHashMap();
        }

        private Builder(LinCompoundTag base) {
            this.collector = new LinkedHashMap(base.value);
        }

        public Builder put(String name, LinTag<?> value) {
            if (value.type().id() == LinTagId.END) {
                throw new IllegalArgumentException("Cannot add END tag to compound tag");
            }
            this.collector.put(name, value);
            return this;
        }

        public Builder putAll(Map<String, ? extends LinTag<?>> map) {
            map.forEach(this::put);
            return this;
        }

        public Builder remove(String name) {
            this.collector.remove(name);
            return this;
        }

        public Builder putByteArray(String name, byte[] value) {
            return this.put(name, LinByteArrayTag.of(value));
        }

        public Builder putByte(String name, byte value) {
            return this.put(name, LinByteTag.of(value));
        }

        public Builder putCompound(String name, Map<String, ? extends LinTag<?>> value) {
            return this.put(name, new LinCompoundTag(LinCompoundTag.copyImmutable(value), true));
        }

        public Builder putDouble(String name, double value) {
            return this.put(name, LinDoubleTag.of(value));
        }

        public Builder putFloat(String name, float value) {
            return this.put(name, LinFloatTag.of(value));
        }

        public Builder putIntArray(String name, int[] value) {
            return this.put(name, LinIntArrayTag.of(value));
        }

        public Builder putInt(String name, int value) {
            return this.put(name, LinIntTag.of(value));
        }

        public Builder putLongArray(String name, long[] value) {
            return this.put(name, LinLongArrayTag.of(value));
        }

        public Builder putLong(String name, long value) {
            return this.put(name, LinLongTag.of(value));
        }

        public Builder putShort(String name, short value) {
            return this.put(name, LinShortTag.of(value));
        }

        public Builder putString(String name, String value) {
            return this.put(name, LinStringTag.of(value));
        }

        public LinCompoundTag build() {
            return new LinCompoundTag(LinCompoundTag.copyImmutable(this.collector), false);
        }
    }

    private class EntryTokenIterator
    implements Iterator<LinStreamable> {
        private final Iterator<? extends Map.Entry<String, ? extends LinTag<?>>> entryIterator;

        private EntryTokenIterator() {
            this.entryIterator = LinCompoundTag.this.value.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.entryIterator.hasNext();
        }

        @Override
        public LinStreamable next() {
            Map.Entry<String, LinTag<?>> entry = this.entryIterator.next();
            return new SurroundingLinStream(new LinToken.Name(entry.getKey(), entry.getValue().type().id()), entry.getValue().linStream(), null);
        }
    }
}

