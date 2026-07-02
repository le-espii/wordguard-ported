/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinIntTag;

@Deprecated
public final class IntTag
extends Tag<Integer, LinIntTag> {
    public IntTag(int value) {
        this(LinIntTag.of(value));
    }

    public IntTag(LinIntTag tag) {
        super(tag);
    }

    @Override
    public Integer getValue() {
        return (Integer)super.getValue();
    }
}

