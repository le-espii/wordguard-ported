/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinByteArrayTag;

@Deprecated
public final class ByteArrayTag
extends Tag<byte[], LinByteArrayTag> {
    public ByteArrayTag(byte[] value) {
        this(LinByteArrayTag.of(value));
    }

    public ByteArrayTag(LinByteArrayTag tag) {
        super(tag);
    }

    @Override
    public byte[] getValue() {
        return (byte[])super.getValue();
    }
}

