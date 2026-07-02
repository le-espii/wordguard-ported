/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinFloatTag;

@Deprecated
public final class FloatTag
extends Tag<Float, LinFloatTag> {
    public FloatTag(float value) {
        this(LinFloatTag.of(value));
    }

    public FloatTag(LinFloatTag tag) {
        super(tag);
    }

    @Override
    public Float getValue() {
        return (Float)super.getValue();
    }
}

