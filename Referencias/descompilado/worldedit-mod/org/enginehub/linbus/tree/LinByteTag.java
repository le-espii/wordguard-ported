/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinByteTag
extends LinNumberTag<Byte> {
    private final byte value;

    public static LinByteTag fromInt(int value) {
        return LinByteTag.of((byte)value);
    }

    public static LinByteTag of(byte value) {
        return new LinByteTag(value);
    }

    private LinByteTag(byte value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinByteTag> type() {
        return LinTagType.byteTag();
    }

    @Override
    public Byte value() {
        return this.value;
    }

    public byte valueAsByte() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.Byte(this.value));
    }
}

