/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinByteTag;

@Deprecated
public final class ByteTag
extends Tag<Byte, LinByteTag> {
    public ByteTag(byte value) {
        this(LinByteTag.of(value));
    }

    public ByteTag(LinByteTag tag) {
        super(tag);
    }

    @Override
    public Byte getValue() {
        return (Byte)super.getValue();
    }
}

