/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongArrayTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jspecify.annotations.Nullable;

public class LinTagReader {
    public static LinRootEntry readRoot(LinStream tokens) throws IOException {
        LinToken linToken = (tokens = tokens.calculateOptionalInfo()).nextOrNull();
        if (!(linToken instanceof LinToken.Name)) {
            throw new NbtParseException("Expected root name");
        }
        LinToken.Name name = (LinToken.Name)linToken;
        if (name.id().orElseThrow() != LinTagId.COMPOUND) {
            throw new NbtParseException("Expected compound tag for root tag");
        }
        LinCompoundTag tag = LinTagReader.readCompound(tokens);
        return new LinRootEntry(name.name(), tag);
    }

    public static LinCompoundTag readCompound(LinStream tokens) throws IOException {
        LinToken token;
        if (!((tokens = tokens.calculateOptionalInfo()).nextOrNull() instanceof LinToken.CompoundStart)) {
            throw new NbtParseException("Expected compound start");
        }
        LinCompoundTag.Builder builder = LinCompoundTag.builder();
        while ((token = tokens.nextOrNull()) != null) {
            if (token instanceof LinToken.CompoundEnd) {
                return builder.build();
            }
            if (!(token instanceof LinToken.Name)) {
                throw new NbtParseException("Expected name, got " + String.valueOf(token));
            }
            LinToken.Name name = (LinToken.Name)token;
            Object value = LinTagReader.readValue(tokens, name.id().map(LinTagType::fromId).orElse(null));
            builder.put(name.name(), (LinTag<?>)value);
        }
        throw new NbtParseException("Expected compound end");
    }

    private static LinByteArrayTag readByteArray(LinStream tokens) throws IOException {
        LinToken token;
        LinToken linToken = tokens.nextOrNull();
        if (!(linToken instanceof LinToken.ByteArrayStart)) {
            throw new NbtParseException("Expected byte array start");
        }
        LinToken.ByteArrayStart start = (LinToken.ByteArrayStart)linToken;
        ByteBuffer buffer = ByteBuffer.allocate(start.size().orElseThrow());
        while ((token = tokens.nextOrNull()) != null) {
            if (token instanceof LinToken.ByteArrayEnd) {
                if (buffer.hasRemaining()) {
                    throw new NbtParseException("Not all bytes received");
                }
                return LinByteArrayTag.of(buffer.array());
            }
            if (!(token instanceof LinToken.ByteArrayContent)) {
                throw new NbtParseException("Expected byte array content, got " + String.valueOf(token));
            }
            LinToken.ByteArrayContent content = (LinToken.ByteArrayContent)token;
            buffer.put(content.buffer());
        }
        throw new NbtParseException("Expected byte array end");
    }

    private static LinIntArrayTag readIntArray(LinStream tokens) throws IOException {
        LinToken token;
        LinToken linToken = tokens.nextOrNull();
        if (!(linToken instanceof LinToken.IntArrayStart)) {
            throw new NbtParseException("Expected int array start");
        }
        LinToken.IntArrayStart start = (LinToken.IntArrayStart)linToken;
        IntBuffer buffer = IntBuffer.allocate(start.size().orElseThrow());
        while ((token = tokens.nextOrNull()) != null) {
            if (token instanceof LinToken.IntArrayEnd) {
                if (buffer.hasRemaining()) {
                    throw new NbtParseException("Not all ints received");
                }
                return LinIntArrayTag.of(buffer.array());
            }
            if (!(token instanceof LinToken.IntArrayContent)) {
                throw new NbtParseException("Expected int array content, got " + String.valueOf(token));
            }
            LinToken.IntArrayContent content = (LinToken.IntArrayContent)token;
            buffer.put(content.buffer());
        }
        throw new NbtParseException("Expected int array end");
    }

    private static LinLongArrayTag readLongArray(LinStream tokens) throws IOException {
        LinToken token;
        LinToken linToken = tokens.nextOrNull();
        if (!(linToken instanceof LinToken.LongArrayStart)) {
            throw new NbtParseException("Expected long array start");
        }
        LinToken.LongArrayStart start = (LinToken.LongArrayStart)linToken;
        LongBuffer buffer = LongBuffer.allocate(start.size().orElseThrow());
        while ((token = tokens.nextOrNull()) != null) {
            if (token instanceof LinToken.LongArrayEnd) {
                if (buffer.hasRemaining()) {
                    throw new NbtParseException("Not all longs received");
                }
                return LinLongArrayTag.of(buffer.array());
            }
            if (!(token instanceof LinToken.LongArrayContent)) {
                throw new NbtParseException("Expected long array content, got " + String.valueOf(token));
            }
            LinToken.LongArrayContent content = (LinToken.LongArrayContent)token;
            buffer.put(content.buffer());
        }
        throw new NbtParseException("Expected long array end");
    }

    private static <T extends LinTag<?>> LinListTag<T> readList(LinStream tokens) throws IOException {
        LinToken linToken = tokens.nextOrNull();
        if (!(linToken instanceof LinToken.ListStart)) {
            throw new NbtParseException("Expected list start");
        }
        LinToken.ListStart start = (LinToken.ListStart)linToken;
        LinTagType elementType = LinTagType.fromId((LinTagId)((Object)start.elementId().orElseThrow()));
        LinListTag.Builder builder = LinListTag.builder(elementType);
        for (int i = 0; i < start.size().orElseThrow(); ++i) {
            Object tag = LinTagReader.readValue(tokens, elementType);
            builder.add(tag);
        }
        if (!(tokens.nextOrNull() instanceof LinToken.ListEnd)) {
            throw new NbtParseException("Expected list end");
        }
        return builder.build();
    }

    private static <T extends LinTag<?>> T readValue(LinStream tokens, @Nullable LinTagType<T> id) throws IOException {
        if (id == null) {
            LinToken next = tokens.nextOrNull();
            if (next == null) {
                throw new NbtParseException("Expected value, got end of stream");
            }
            id = LinTagType.fromId(next.tagId().orElseThrow(() -> new NbtParseException("Expected value, got " + String.valueOf(next))));
            tokens = new SurroundingLinStream(next, tokens, null);
        }
        return id.cast(switch (id.id()) {
            default -> throw new MatchException(null, null);
            case LinTagId.BYTE_ARRAY -> LinTagReader.readByteArray(tokens);
            case LinTagId.BYTE -> LinByteTag.of(((LinToken.Byte)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.COMPOUND -> LinTagReader.readCompound(tokens);
            case LinTagId.DOUBLE -> LinDoubleTag.of(((LinToken.Double)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.END -> throw new NbtParseException("Unexpected END id");
            case LinTagId.FLOAT -> LinFloatTag.of(((LinToken.Float)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.INT_ARRAY -> LinTagReader.readIntArray(tokens);
            case LinTagId.INT -> LinIntTag.of(((LinToken.Int)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.LIST -> LinTagReader.readList(tokens);
            case LinTagId.LONG_ARRAY -> LinTagReader.readLongArray(tokens);
            case LinTagId.LONG -> LinLongTag.of(((LinToken.Long)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.SHORT -> LinShortTag.of(((LinToken.Short)LinTagReader.requireNextToken(tokens)).value());
            case LinTagId.STRING -> LinStringTag.of(((LinToken.String)LinTagReader.requireNextToken(tokens)).value());
        });
    }

    private static LinToken requireNextToken(LinStream tokens) throws IOException {
        LinToken linToken = tokens.nextOrNull();
        if (linToken == null) {
            throw new NbtParseException("Unexpected end of stream");
        }
        return linToken;
    }

    private LinTagReader() {
    }
}

