/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.jnbt;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTUtils;
import com.sk89q.jnbt.Tag;
import java.util.Collection;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

@Deprecated
public class ListTagBuilder<V, LT extends LinTag<V>> {
    private final LinListTag.Builder<LT> builder;

    ListTagBuilder(Class<? extends Tag<V, LT>> type) {
        Preconditions.checkNotNull(type);
        this.builder = LinListTag.builder(LinTagType.fromId(LinTagId.fromId(NBTUtils.getTypeCode(type))));
    }

    public ListTagBuilder<V, LT> add(Tag<V, LT> value) {
        Preconditions.checkNotNull(value);
        this.builder.add(value.toLinTag());
        return this;
    }

    public ListTagBuilder<V, LT> addAll(Collection<? extends Tag<V, LT>> value) {
        Preconditions.checkNotNull(value);
        for (Tag<V, LT> v : value) {
            this.add(v);
        }
        return this;
    }

    public ListTag<V, LT> build() {
        return new ListTag(this.builder.build());
    }

    public static <V, LT extends LinTag<V>> ListTagBuilder<V, LT> create(Class<? extends Tag<V, LT>> type) {
        return new ListTagBuilder<V, LT>(type);
    }

    @SafeVarargs
    public static <V, LT extends LinTag<V>> ListTagBuilder<V, LT> createWith(Tag<V, LT> ... entries) {
        Preconditions.checkNotNull(entries);
        if (entries.length == 0) {
            throw new IllegalArgumentException("This method needs an array of at least one entry");
        }
        Class<?> type = entries[0].getClass();
        ListTagBuilder<V, LT> builder = new ListTagBuilder<V, LT>(type);
        for (Tag<V, LT> entry : entries) {
            if (!type.isInstance(entry)) {
                throw new IllegalArgumentException("An array of different tag types was provided");
            }
            builder.add(entry);
        }
        return builder;
    }
}

