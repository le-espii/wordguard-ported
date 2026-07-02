/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jspecify.annotations.Nullable;

public final class LinByteArrayTag
extends LinTag<byte[]> {
    private final byte[] value;

    public static LinByteArrayTag of(byte ... value) {
        return new LinByteArrayTag((byte[])value.clone());
    }

    private LinByteArrayTag(byte[] value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinByteArrayTag> type() {
        return LinTagType.byteArrayTag();
    }

    @Override
    public byte[] value() {
        return (byte[])this.value.clone();
    }

    public ByteBuffer view() {
        return ByteBuffer.wrap(this.value).asReadOnlyBuffer();
    }

    @Override
    public LinStream linStream() {
        return new SurroundingLinStream(new LinToken.ByteArrayStart(this.value.length), new LinStream(){
            private static final int BUFFER_SIZE = 4096;
            private int i = 0;

            @Override
            public @Nullable LinToken nextOrNull() {
                if (this.i >= LinByteArrayTag.this.value.length) {
                    return null;
                }
                int length = Math.min(4096, LinByteArrayTag.this.value.length - this.i);
                ByteBuffer buffer = ByteBuffer.wrap(LinByteArrayTag.this.value, this.i, length).asReadOnlyBuffer();
                this.i += length;
                return new LinToken.ByteArrayContent(buffer);
            }
        }, new LinToken.ByteArrayEnd());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LinByteArrayTag that = (LinByteArrayTag)o;
        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.value);
        return result;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + Arrays.toString(this.value);
    }
}

