/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree;

import java.nio.IntBuffer;
import java.util.Arrays;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jspecify.annotations.Nullable;

public final class LinIntArrayTag
extends LinTag<int[]> {
    private final int[] value;

    public static LinIntArrayTag of(int ... value) {
        return new LinIntArrayTag((int[])value.clone());
    }

    private LinIntArrayTag(int[] value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinIntArrayTag> type() {
        return LinTagType.intArrayTag();
    }

    @Override
    public int[] value() {
        return (int[])this.value.clone();
    }

    public IntBuffer view() {
        return IntBuffer.wrap(this.value).asReadOnlyBuffer();
    }

    @Override
    public LinStream linStream() {
        return new SurroundingLinStream(new LinToken.IntArrayStart(this.value.length), new LinStream(){
            private static final int BUFFER_SIZE = 4096;
            private int i = 0;

            @Override
            public @Nullable LinToken nextOrNull() {
                if (this.i >= LinIntArrayTag.this.value.length) {
                    return null;
                }
                int length = Math.min(4096, LinIntArrayTag.this.value.length - this.i);
                IntBuffer buffer = IntBuffer.wrap(LinIntArrayTag.this.value, this.i, length).asReadOnlyBuffer();
                this.i += length;
                return new LinToken.IntArrayContent(buffer);
            }
        }, new LinToken.IntArrayEnd());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LinIntArrayTag that = (LinIntArrayTag)o;
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

