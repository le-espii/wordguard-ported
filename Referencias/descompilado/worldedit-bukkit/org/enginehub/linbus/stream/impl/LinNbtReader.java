/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinReadOptions;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class LinNbtReader
implements LinStream {
    private static final byte TWO_BYTE_NULL_START = -64;
    public static final byte TWO_BYTE_NULL_END = -128;
    private static final int TOP_5_BITS = 248;
    private static final int FOUR_BYTE_START = 240;
    private static final byte THREE_BYTE_SURROGATE_START = -19;
    private static final int TOP_3_BITS = 224;
    private static final int THREE_BYTE_SURROGATE_CONTINUATION = 160;
    private final DataInput input;
    private final Deque<State> stateStack;
    private StringEncoding stringEncoding;
    private @Nullable NormalUtf8Decoder decoder;

    private static StringEncoding getGuaranteedStringEncoding(ByteBuffer bytes) {
        boolean sawTwoByteNullStart = false;
        boolean sawThreeByteSurrogateStart = false;
        for (int i = 0; i < bytes.remaining(); ++i) {
            byte b = bytes.get(i);
            if (b == -64) {
                sawTwoByteNullStart = true;
            } else if (sawTwoByteNullStart) {
                if (b == -128) {
                    return StringEncoding.MODIFIED_UTF_8;
                }
                sawTwoByteNullStart = false;
            }
            if ((b & 0xF8) == 240) {
                return StringEncoding.NORMAL_UTF_8;
            }
            if (b == -19) {
                sawThreeByteSurrogateStart = true;
                continue;
            }
            if (!sawThreeByteSurrogateStart) continue;
            if ((b & 0xE0) == 160) {
                return StringEncoding.MODIFIED_UTF_8;
            }
            sawThreeByteSurrogateStart = false;
        }
        return StringEncoding.UNKNOWN;
    }

    public LinNbtReader(DataInput input, LinReadOptions options) {
        this.input = input;
        this.stateStack = new ArrayDeque<State>(List.of((Object)new State.Initial()));
        this.stringEncoding = options.allowNormalUtf8Encoding() ? StringEncoding.UNKNOWN : StringEncoding.MODIFIED_UTF_8;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @Override
    public @Nullable LinToken nextOrNull() throws IOException {
        var2_2 = state = this.stateStack.pollLast();
        var3_4 = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{State.Initial.class, State.CompoundStart.class, State.CompoundEntryName.class, State.ReadValue.class, State.ReadByteArray.class, State.ReadIntArray.class, State.ReadLongArray.class, State.ListEntry.class}, (Object)var2_2, (int)var3_4)) {
            default: {
                throw new MatchException(null, null);
            }
            case -1: {
                v0 /* !! */  = null;
                break;
            }
            case 0: {
                initial = (State.Initial)var2_2;
                if (this.input.readUnsignedByte() != LinTagId.COMPOUND.id()) {
                    throw new NbtParseException("NBT stream does not start with a compound tag");
                }
                this.stateStack.addLast(new State.CompoundStart());
                v0 /* !! */  = new LinToken.Name(this.readUtf(), LinTagId.COMPOUND);
                break;
            }
            case 1: {
                compoundStart = (State.CompoundStart)var2_2;
                this.stateStack.addLast(new State.CompoundEntryName());
                v0 /* !! */  = new LinToken.CompoundStart();
                break;
            }
            case 2: {
                compoundEntryName = (State.CompoundEntryName)var2_2;
                id = LinTagId.fromId(this.input.readUnsignedByte());
                if (id == LinTagId.END) {
                    v0 /* !! */  = new LinToken.CompoundEnd();
                    break;
                }
                this.stateStack.addLast(new State.CompoundEntryName());
                this.stateStack.addLast(new State.ReadValue(id));
                v0 /* !! */  = new LinToken.Name(this.readUtf(), id);
                break;
            }
            case 3: {
                var7_9 = (State.ReadValue)var2_2;
                id = var9_10 = var7_9.id();
                v0 /* !! */  = this.handleReadValue(id);
                break;
            }
            case 4: {
                var9_11 = (State.ReadByteArray)var2_2;
                remaining = var11_13 = var9_11.remaining();
                if (remaining != 0) ** GOTO lbl44
                v0 /* !! */  = new LinToken.ByteArrayEnd();
                break;
lbl44:
                // 1 sources

                buffer = ByteBuffer.allocate(Math.min(8192, remaining));
                this.input.readFully(buffer.array(), buffer.position(), buffer.remaining());
                this.stateStack.addLast(new State.ReadByteArray(remaining - buffer.remaining()));
                v0 /* !! */  = new LinToken.ByteArrayContent(buffer.asReadOnlyBuffer());
                break;
            }
            case 5: {
                var11_15 = (State.ReadIntArray)var2_2;
                remaining = var13_17 = var11_15.remaining();
                if (remaining != 0) ** GOTO lbl56
                v0 /* !! */  = new LinToken.IntArrayEnd();
                break;
lbl56:
                // 1 sources

                buffer = ByteBuffer.allocate(Math.min(8192, remaining * 4));
                this.input.readFully(buffer.array(), buffer.position(), buffer.remaining());
                this.stateStack.addLast(new State.ReadIntArray(remaining - buffer.remaining() / 4));
                v0 /* !! */  = new LinToken.IntArrayContent(buffer.asIntBuffer().asReadOnlyBuffer());
                break;
            }
            case 6: {
                var13_19 = (State.ReadLongArray)var2_2;
                remaining = var15_21 = var13_19.remaining();
                if (remaining != 0) ** GOTO lbl68
                v0 /* !! */  = new LinToken.LongArrayEnd();
                break;
lbl68:
                // 1 sources

                buffer = ByteBuffer.allocate(Math.min(8192, remaining * 8));
                this.input.readFully(buffer.array(), buffer.position(), buffer.remaining());
                this.stateStack.addLast(new State.ReadLongArray(remaining - buffer.remaining() / 8));
                v0 /* !! */  = new LinToken.LongArrayContent(buffer.asLongBuffer().asReadOnlyBuffer());
                break;
            }
            case 7: {
                var15_23 = (State.ListEntry)var2_2;
                remaining = var18_25 = var15_23.remaining();
                elementId = var18_26 = var15_23.elementId();
                if (remaining == 0) {
                    v0 /* !! */  = new LinToken.ListEnd();
                    break;
                }
                this.stateStack.addLast(new State.ListEntry(remaining - 1, elementId));
                v0 /* !! */  = this.handleReadValue(elementId);
            }
        }
        return v0 /* !! */ ;
        catch (Throwable var2_3) {
            throw new MatchException(var2_3.toString(), var2_3);
        }
    }

    private LinToken handleReadValue(LinTagId id) throws IOException {
        return switch (id) {
            default -> throw new MatchException(null, null);
            case LinTagId.BYTE -> new LinToken.Byte(this.input.readByte());
            case LinTagId.SHORT -> new LinToken.Short(this.input.readShort());
            case LinTagId.INT -> new LinToken.Int(this.input.readInt());
            case LinTagId.LONG -> new LinToken.Long(this.input.readLong());
            case LinTagId.FLOAT -> new LinToken.Float(this.input.readFloat());
            case LinTagId.DOUBLE -> new LinToken.Double(this.input.readDouble());
            case LinTagId.BYTE_ARRAY -> {
                int size = this.input.readInt();
                this.stateStack.addLast(new State.ReadByteArray(size));
                yield new LinToken.ByteArrayStart(size);
            }
            case LinTagId.STRING -> new LinToken.String(this.readUtf());
            case LinTagId.LIST -> {
                LinTagId elementId = LinTagId.fromId(this.input.readUnsignedByte());
                int size = this.input.readInt();
                this.stateStack.addLast(new State.ListEntry(size, elementId));
                yield new LinToken.ListStart(size, elementId);
            }
            case LinTagId.COMPOUND -> {
                this.stateStack.addLast(new State.CompoundEntryName());
                yield new LinToken.CompoundStart();
            }
            case LinTagId.INT_ARRAY -> {
                int size = this.input.readInt();
                this.stateStack.addLast(new State.ReadIntArray(size));
                yield new LinToken.IntArrayStart(size);
            }
            case LinTagId.LONG_ARRAY -> {
                int size = this.input.readInt();
                this.stateStack.addLast(new State.ReadLongArray(size));
                yield new LinToken.LongArrayStart(size);
            }
            case LinTagId.END -> throw new NbtParseException("Invalid id: " + String.valueOf((Object)id));
        };
    }

    private NormalUtf8Decoder getNormalUtf8Decoder() {
        NormalUtf8Decoder decoder = this.decoder;
        if (decoder == null) {
            this.decoder = decoder = new NormalUtf8Decoder();
        }
        return decoder;
    }

    private String readUtf() throws IOException {
        return switch (this.stringEncoding.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.input.readUTF();
            case 1 -> {
                int length = this.input.readUnsignedShort();
                NormalUtf8Decoder decoder = this.getNormalUtf8Decoder();
                decoder.fill(this.input, length);
                yield decoder.decode();
            }
            case 2 -> {
                int length = this.input.readUnsignedShort();
                NormalUtf8Decoder decoder = this.getNormalUtf8Decoder();
                decoder.fill(this.input, length);
                StringEncoding knownEncoding = LinNbtReader.getGuaranteedStringEncoding(decoder.sourceBuffer);
                switch (knownEncoding.ordinal()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 0: {
                        this.stringEncoding = knownEncoding;
                        byte[] withLength = new byte[length + 2];
                        withLength[0] = (byte)(length >> 8);
                        withLength[1] = (byte)length;
                        System.arraycopy(decoder.sourceBuffer.array(), 0, withLength, 2, length);
                        yield new DataInputStream(new ByteArrayInputStream(withLength)).readUTF();
                    }
                    case 1: {
                        this.stringEncoding = knownEncoding;
                        yield decoder.decode();
                    }
                    case 2: 
                }
                yield decoder.decode();
            }
        };
    }

    private static enum StringEncoding {
        MODIFIED_UTF_8,
        NORMAL_UTF_8,
        UNKNOWN;

    }

    private static sealed interface State {

        public record ReadLongArray(int remaining) implements State
        {
        }

        public record ReadIntArray(int remaining) implements State
        {
        }

        public record ReadByteArray(int remaining) implements State
        {
        }

        public record ReadValue(LinTagId id) implements State
        {
        }

        public record ListEntry(int remaining, LinTagId elementId) implements State
        {
        }

        public record CompoundEntryName() implements State
        {
        }

        public record CompoundStart() implements State
        {
        }

        public record Initial() implements State
        {
        }
    }

    private static final class NormalUtf8Decoder {
        private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        private ByteBuffer sourceBuffer = ByteBuffer.allocate(128);
        private CharBuffer decodeBuffer = CharBuffer.allocate(128);

        private NormalUtf8Decoder() {
        }

        void fill(DataInput input, int length) throws IOException {
            this.ensureSourceBufferCapacity(length);
            input.readFully(this.sourceBuffer.array(), 0, length);
            this.sourceBuffer.limit(length);
        }

        private void ensureSourceBufferCapacity(int requiredCapacity) {
            if (this.sourceBuffer.capacity() < requiredCapacity) {
                this.sourceBuffer = ByteBuffer.allocate(requiredCapacity);
            } else {
                this.sourceBuffer.clear();
            }
        }

        private void ensureCharBufferCapacity(int requiredCapacity) {
            if (this.decodeBuffer.capacity() < requiredCapacity) {
                this.decodeBuffer = CharBuffer.allocate(requiredCapacity);
            } else {
                this.decodeBuffer.clear();
            }
        }

        public String decode() throws CharacterCodingException {
            int n = (int)((float)this.sourceBuffer.remaining() * this.decoder.averageCharsPerByte());
            this.ensureCharBufferCapacity(n);
            if (n == 0 && this.sourceBuffer.remaining() == 0) {
                return "";
            }
            this.decoder.reset();
            while (true) {
                CoderResult cr;
                CoderResult coderResult = cr = this.sourceBuffer.hasRemaining() ? this.decoder.decode(this.sourceBuffer, this.decodeBuffer, true) : CoderResult.UNDERFLOW;
                if (cr.isUnderflow()) {
                    cr = this.decoder.flush(this.decodeBuffer);
                }
                if (cr.isUnderflow()) break;
                if (cr.isOverflow()) {
                    n += n / 2 + 1;
                    CharBuffer o = CharBuffer.allocate(n);
                    this.decodeBuffer.flip();
                    o.put(this.decodeBuffer);
                    this.decodeBuffer = o;
                    continue;
                }
                cr.throwException();
            }
            this.decodeBuffer.flip();
            return this.decodeBuffer.toString();
        }
    }
}

