/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.jnbt;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinLongArrayTag;

@Deprecated
public class LongArrayTag
extends Tag<long[], LinLongArrayTag> {
    public LongArrayTag(long[] value) {
        this(LinLongArrayTag.of((long[])Preconditions.checkNotNull((Object)value)));
    }

    public LongArrayTag(LinLongArrayTag tag) {
        super(tag);
    }

    @Override
    public long[] getValue() {
        return (long[])super.getValue();
    }
}

