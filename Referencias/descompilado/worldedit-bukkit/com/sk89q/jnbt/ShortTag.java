/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinShortTag;

@Deprecated
public final class ShortTag
extends Tag<Short, LinShortTag> {
    public ShortTag(short value) {
        super(LinShortTag.of(value));
    }

    public ShortTag(LinShortTag tag) {
        super(tag);
    }

    @Override
    public Short getValue() {
        return (Short)super.getValue();
    }
}

