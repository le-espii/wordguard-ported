/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinLongTag;

@Deprecated
public final class LongTag
extends Tag<Long, LinLongTag> {
    public LongTag(long value) {
        this(LinLongTag.of(value));
    }

    public LongTag(LinLongTag tag) {
        super(tag);
    }

    @Override
    public Long getValue() {
        return (Long)super.getValue();
    }
}

