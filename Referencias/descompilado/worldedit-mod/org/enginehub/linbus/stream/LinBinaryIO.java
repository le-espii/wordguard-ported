/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import org.enginehub.linbus.common.IOFunction;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinReadOptions;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.stream.exception.NbtWriteException;
import org.enginehub.linbus.stream.impl.LinNbtReader;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class LinBinaryIO {
    public static LinStream read(DataInput input) {
        return LinBinaryIO.read(input, LinReadOptions.builder().build());
    }

    public static LinStream read(DataInput input, LinReadOptions options) {
        return new LinNbtReader(input, options);
    }

    public static <R> R readUsing(DataInput input, IOFunction<? super LinStream, ? extends R> transform) throws IOException {
        return transform.apply(LinBinaryIO.read(input));
    }

    public static <R> R readUsing(DataInput input, LinReadOptions options, IOFunction<? super LinStream, ? extends R> transform) throws IOException {
        return transform.apply(LinBinaryIO.read(input, options));
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void write(DataOutput output, LinStreamable tokens) throws IOException {
        LinToken token;
        LinStream tokenStream = tokens.linStream().calculateOptionalInfo();
        boolean seenFirstName = false;
        Optional<LinTagId> nextName = null;
        block39: while (true) {
            LinToken linToken;
            if ((token = tokenStream.nextOrNull()) == null) {
                return;
            }
            if (!seenFirstName) {
                if (!(token instanceof LinToken.Name)) {
                    throw new NbtWriteException("Expected first token to be a name");
                }
                seenFirstName = true;
            }
            Objects.requireNonNull(token);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{LinToken.Name.class, LinToken.ByteArrayStart.class, LinToken.ByteArrayContent.class, LinToken.ByteArrayEnd.class, LinToken.Byte.class, LinToken.CompoundStart.class, LinToken.CompoundEnd.class, LinToken.Double.class, LinToken.Float.class, LinToken.IntArrayStart.class, LinToken.IntArrayContent.class, LinToken.IntArrayEnd.class, LinToken.Int.class, LinToken.ListStart.class, LinToken.ListEnd.class, LinToken.LongArrayStart.class, LinToken.LongArrayContent.class, LinToken.LongArrayEnd.class, LinToken.Long.class, LinToken.Short.class, LinToken.String.class}, (Object)linToken, (int)n)) {
                case 0: {
                    Optional<LinTagId> optional;
                    LinToken.Name name = (LinToken.Name)linToken;
                    try {
                        Optional<LinTagId> name2 = optional = name.name();
                        Optional<LinTagId> id = optional = name.id();
                        nextName = name2;
                        continue block39;
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                }
                case 1: {
                    Object size;
                    Object object;
                    Optional<LinTagId> optional = (LinToken.ByteArrayStart)linToken;
                    {
                        size = object = ((LinToken.ByteArrayStart)((Object)optional)).size();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.BYTE_ARRAY, nextName);
                    nextName = null;
                    output.writeInt(size.orElseThrow());
                    continue block39;
                }
                case 2: {
                    ByteBuffer buffer;
                    Object object = (LinToken.ByteArrayContent)linToken;
                    {
                        ByteBuffer byteBuffer;
                        buffer = byteBuffer = ((LinToken.ByteArrayContent)object).buffer();
                    }
                    byte[] copy = new byte[buffer.remaining()];
                    buffer.get(copy);
                    output.write(copy);
                    continue block39;
                }
                case 3: {
                    LinToken.ByteArrayEnd byteArrayEnd = (LinToken.ByteArrayEnd)linToken;
                    continue block39;
                }
                case 4: {
                    byte value;
                    LinToken.Byte byteVal = (LinToken.Byte)linToken;
                    {
                        byte by;
                        value = by = byteVal.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.BYTE, nextName);
                    nextName = null;
                    output.writeByte(value);
                    continue block39;
                }
                case 5: {
                    LinToken.CompoundStart compoundStart = (LinToken.CompoundStart)linToken;
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.COMPOUND, nextName);
                    nextName = null;
                    continue block39;
                }
                case 6: {
                    LinToken.CompoundEnd compoundEnd = (LinToken.CompoundEnd)linToken;
                    output.writeByte(LinTagId.END.id());
                    continue block39;
                }
                case 7: {
                    double value;
                    LinToken.Double double_ = (LinToken.Double)linToken;
                    {
                        double d;
                        value = d = double_.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.DOUBLE, nextName);
                    nextName = null;
                    output.writeDouble(value);
                    continue block39;
                }
                case 8: {
                    float value;
                    LinToken.Float float_ = (LinToken.Float)linToken;
                    {
                        float f;
                        value = f = float_.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.FLOAT, nextName);
                    nextName = null;
                    output.writeFloat(value);
                    continue block39;
                }
                case 9: {
                    Object size;
                    Object object;
                    LinToken.IntArrayStart intArrayStart = (LinToken.IntArrayStart)linToken;
                    {
                        size = object = intArrayStart.size();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.INT_ARRAY, nextName);
                    nextName = null;
                    output.writeInt(size.orElseThrow());
                    continue block39;
                }
                case 10: {
                    IntBuffer buffer;
                    Object object = (LinToken.IntArrayContent)linToken;
                    {
                        IntBuffer intBuffer;
                        buffer = intBuffer = ((LinToken.IntArrayContent)object).buffer();
                    }
                    while (true) {
                        if (!buffer.hasRemaining()) continue block39;
                        output.writeInt(buffer.get());
                    }
                }
                case 11: {
                    LinToken.IntArrayEnd intArrayEnd = (LinToken.IntArrayEnd)linToken;
                    continue block39;
                }
                case 12: {
                    int value;
                    LinToken.Int intVal = (LinToken.Int)linToken;
                    {
                        int n2;
                        value = n2 = intVal.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.INT, nextName);
                    nextName = null;
                    output.writeInt(value);
                    continue block39;
                }
                case 13: {
                    Object elementId;
                    OptionalInt size;
                    LinToken.ListStart listStart = (LinToken.ListStart)linToken;
                    {
                        Object object = listStart.size();
                        size = object;
                        elementId = object = listStart.elementId();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.LIST, nextName);
                    nextName = null;
                    output.writeByte(((LinTagId)((Object)elementId.orElseThrow())).id());
                    output.writeInt(size.orElseThrow());
                    continue block39;
                }
                case 14: {
                    LinToken.ListEnd listEnd = (LinToken.ListEnd)linToken;
                    continue block39;
                }
                case 15: {
                    Object size;
                    Object object;
                    LinToken.LongArrayStart longArrayStart = (LinToken.LongArrayStart)linToken;
                    {
                        size = object = longArrayStart.size();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.LONG_ARRAY, nextName);
                    nextName = null;
                    output.writeInt(size.orElseThrow());
                    continue block39;
                }
                case 16: {
                    LongBuffer buffer;
                    Object object = (LinToken.LongArrayContent)linToken;
                    {
                        LongBuffer longBuffer;
                        buffer = longBuffer = ((LinToken.LongArrayContent)object).buffer();
                    }
                    while (true) {
                        if (!buffer.hasRemaining()) continue block39;
                        output.writeLong(buffer.get());
                    }
                }
                case 17: {
                    LinToken.LongArrayEnd longArrayEnd = (LinToken.LongArrayEnd)linToken;
                    continue block39;
                }
                case 18: {
                    long value;
                    LinToken.Long longVal = (LinToken.Long)linToken;
                    {
                        long l;
                        value = l = longVal.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.LONG, nextName);
                    nextName = null;
                    output.writeLong(value);
                    continue block39;
                }
                case 19: {
                    short value;
                    LinToken.Short shortVal = (LinToken.Short)linToken;
                    {
                        short s;
                        value = s = shortVal.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.SHORT, nextName);
                    nextName = null;
                    output.writeShort(value);
                    continue block39;
                }
                case 20: {
                    String value;
                    LinToken.String string2 = (LinToken.String)linToken;
                    {
                        String string;
                        value = string = string2.value();
                    }
                    LinBinaryIO.writeIdAndNameIfNeeded(output, LinTagId.STRING, nextName);
                    nextName = null;
                    output.writeUTF(value);
                    continue block39;
                }
            }
            break;
        }
        throw new NbtWriteException("Unknown token: " + String.valueOf(token));
    }

    private static void writeIdAndNameIfNeeded(DataOutput output, LinTagId id, @Nullable String name) throws IOException {
        if (name != null) {
            output.writeByte(id.id());
            output.writeUTF(name);
        }
    }

    private LinBinaryIO() {
    }
}

