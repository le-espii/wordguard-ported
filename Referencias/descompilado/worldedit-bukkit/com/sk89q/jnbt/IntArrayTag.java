/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.jnbt;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinIntArrayTag;

@Deprecated
public final class IntArrayTag
extends Tag<int[], LinIntArrayTag> {
    public IntArrayTag(int[] value) {
        this(LinIntArrayTag.of((int[])Preconditions.checkNotNull((Object)value)));
    }

    public IntArrayTag(LinIntArrayTag tag) {
        super(tag);
    }

    @Override
    public int[] getValue() {
        return (int[])super.getValue();
    }
}

