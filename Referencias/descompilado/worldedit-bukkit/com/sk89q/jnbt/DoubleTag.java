/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinDoubleTag;

@Deprecated
public final class DoubleTag
extends Tag<Double, LinDoubleTag> {
    public DoubleTag(double value) {
        this(LinDoubleTag.of(value));
    }

    public DoubleTag(LinDoubleTag tag) {
        super(tag);
    }

    @Override
    public Double getValue() {
        return (Double)super.getValue();
    }
}

