/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.sk89q.jnbt;

import javax.annotation.Nonnull;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.ToLinTag;

@Deprecated
public abstract class Tag<V, LT extends LinTag<? extends V>>
implements ToLinTag<LT> {
    protected final LT linTag;

    protected Tag(LT linTag) {
        this.linTag = linTag;
    }

    public V getValue() {
        return (V)((LinTag)this.linTag).value();
    }

    public String toString() {
        return ((LinTag)this.toLinTag()).toString();
    }

    @Override
    @Nonnull
    public LT toLinTag() {
        return this.linTag;
    }
}

