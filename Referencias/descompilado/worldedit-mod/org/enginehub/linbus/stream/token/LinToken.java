/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.stream.token;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Optional;
import java.util.OptionalInt;
import org.enginehub.linbus.common.LinTagId;

public sealed interface LinToken {
    public boolean isSimpleValue();

    default public Optional<LinTagId> tagId() {
        return Optional.empty();
    }

    public record String(java.lang.String value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.STRING);
        }
    }

    public record Short(short value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.SHORT);
        }
    }

    public record Long(long value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.LONG);
        }
    }

    public record LongArrayEnd() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record LongArrayContent(LongBuffer buffer) implements LinToken
    {
        public LongArrayContent {
            if (!buffer.isReadOnly()) {
                throw new IllegalArgumentException("buffer must be read-only");
            }
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record LongArrayStart(OptionalInt size) implements LinToken
    {
        public LongArrayStart() {
            this(OptionalInt.empty());
        }

        public LongArrayStart(int size) {
            this(OptionalInt.of(size));
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.LONG_ARRAY);
        }
    }

    public record ListEnd() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record ListStart(OptionalInt size, Optional<LinTagId> elementId) implements LinToken
    {
        public ListStart() {
            this(OptionalInt.empty(), Optional.empty());
        }

        public ListStart(int size, LinTagId elementId) {
            this(OptionalInt.of(size), Optional.of(elementId));
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.LIST);
        }
    }

    public record Int(int value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.INT);
        }
    }

    public record IntArrayEnd() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record IntArrayContent(IntBuffer buffer) implements LinToken
    {
        public IntArrayContent {
            if (!buffer.isReadOnly()) {
                throw new IllegalArgumentException("buffer must be read-only");
            }
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record IntArrayStart(OptionalInt size) implements LinToken
    {
        public IntArrayStart() {
            this(OptionalInt.empty());
        }

        public IntArrayStart(int size) {
            this(OptionalInt.of(size));
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.INT_ARRAY);
        }
    }

    public record Float(float value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.FLOAT);
        }
    }

    public record Double(double value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.DOUBLE);
        }
    }

    public record CompoundEnd() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record CompoundStart() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.COMPOUND);
        }
    }

    public record Byte(byte value) implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.BYTE);
        }
    }

    public record ByteArrayEnd() implements LinToken
    {
        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record ByteArrayContent(ByteBuffer buffer) implements LinToken
    {
        public ByteArrayContent {
            if (!buffer.isReadOnly()) {
                throw new IllegalArgumentException("buffer must be read-only");
            }
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }

    public record ByteArrayStart(OptionalInt size) implements LinToken
    {
        public ByteArrayStart() {
            this(OptionalInt.empty());
        }

        public ByteArrayStart(int size) {
            this(OptionalInt.of(size));
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }

        @Override
        public Optional<LinTagId> tagId() {
            return Optional.of(LinTagId.BYTE_ARRAY);
        }
    }

    public record Name(java.lang.String name, Optional<LinTagId> id) implements LinToken
    {
        public Name(java.lang.String name) {
            this(name, Optional.empty());
        }

        public Name(java.lang.String name, LinTagId id) {
            this(name, Optional.of(id));
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }
}

