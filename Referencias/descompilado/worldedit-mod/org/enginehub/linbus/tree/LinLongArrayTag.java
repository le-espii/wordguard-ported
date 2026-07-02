/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree;

import java.nio.LongBuffer;
import java.util.Arrays;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jspecify.annotations.Nullable;

public final class LinLongArrayTag
extends LinTag<long[]> {
    private final long[] value;

    public static LinLongArrayTag of(long ... value) {
        return new LinLongArrayTag((long[])value.clone());
    }

    private LinLongArrayTag(long[] value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinLongArrayTag> type() {
        return LinTagType.longArrayTag();
    }

    @Override
    public long[] value() {
        return (long[])this.value.clone();
    }

    public LongBuffer view() {
        return LongBuffer.wrap(this.value).asReadOnlyBuffer();
    }

    @Override
    public LinStream linStream() {
        return new SurroundingLinStream(new LinToken.LongArrayStart(this.value.length), new LinStream(){
            private static final int BUFFER_SIZE = 4096;
            private int i = 0;

            @Override
            public @Nullable LinToken nextOrNull() {
                if (this.i >= LinLongArrayTag.this.value.length) {
                    return null;
                }
                int length = Math.min(4096, LinLongArrayTag.this.value.length - this.i);
                LongBuffer buffer = LongBuffer.wrap(LinLongArrayTag.this.value, this.i, length).asReadOnlyBuffer();
                this.i += length;
                return new LinToken.LongArrayContent(buffer);
            }
        }, new LinToken.LongArrayEnd());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LinLongArrayTag that = (LinLongArrayTag)o;
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

