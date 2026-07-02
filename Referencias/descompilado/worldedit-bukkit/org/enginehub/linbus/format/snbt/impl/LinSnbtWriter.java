/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package org.enginehub.linbus.format.snbt.impl;

import java.io.IOException;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.OptionalInt;
import org.enginehub.linbus.format.snbt.impl.Elusion;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.exception.NbtWriteException;
import org.enginehub.linbus.stream.token.LinToken;

public class LinSnbtWriter {
    private final ArrayDeque<WriteState> stateStack = new ArrayDeque();

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void write(Appendable output, LinStream tokens) throws IOException {
        LinToken token;
        block36: while (true) {
            LinToken linToken;
            WriteState state = this.stateStack.peekLast();
            token = tokens.nextOrNull();
            if (token == null) {
                return;
            }
            Objects.requireNonNull(token);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{LinToken.Name.class, LinToken.ByteArrayStart.class, LinToken.ByteArrayContent.class, LinToken.ByteArrayEnd.class, LinToken.Byte.class, LinToken.CompoundStart.class, LinToken.CompoundEnd.class, LinToken.Double.class, LinToken.Float.class, LinToken.IntArrayStart.class, LinToken.IntArrayContent.class, LinToken.IntArrayEnd.class, LinToken.Int.class, LinToken.ListStart.class, LinToken.ListEnd.class, LinToken.LongArrayStart.class, LinToken.LongArrayContent.class, LinToken.LongArrayEnd.class, LinToken.Long.class, LinToken.Short.class, LinToken.String.class}, (Object)linToken, (int)n)) {
                case 0: {
                    String name2;
                    LinToken.Name name = (LinToken.Name)linToken;
                    try {
                        Object object = name.name();
                        name2 = object;
                        Object id = object = name.id();
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    if (!(state instanceof WriteState.Compound)) throw new NbtWriteException("Names can only appear inside compounds");
                    WriteState.Compound compound = (WriteState.Compound)state;
                    if (compound.hasPrevious) {
                        output.append(',');
                        this.replaceLast(new WriteState.Compound(false));
                    }
                    output.append(Elusion.escapeIfNeeded(name2)).append(':');
                    continue block36;
                }
                case 1: {
                    LinToken.ByteArrayStart byteArrayStart = (LinToken.ByteArrayStart)linToken;
                    output.append("[B;");
                    continue block36;
                }
                case 2: {
                    ByteBuffer buffer;
                    LinToken.ByteArrayContent byteArrayContent = (LinToken.ByteArrayContent)linToken;
                    {
                        ByteBuffer byteBuffer;
                        buffer = byteBuffer = byteArrayContent.buffer();
                    }
                    if (state instanceof WriteState.WritingArray) {
                        output.append(',');
                    } else {
                        this.stateStack.addLast(new WriteState.WritingArray());
                    }
                    while (true) {
                        if (!buffer.hasRemaining()) continue block36;
                        output.append(String.valueOf(buffer.get())).append('B');
                        if (!buffer.hasRemaining()) continue;
                        output.append(',');
                    }
                }
                case 3: {
                    LinToken.ByteArrayEnd byteArrayEnd = (LinToken.ByteArrayEnd)linToken;
                    if (state instanceof WriteState.WritingArray) {
                        this.stateStack.removeLast();
                    }
                    output.append(']');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 4: {
                    byte value;
                    LinToken.Byte byteVal = (LinToken.Byte)linToken;
                    {
                        byte by;
                        value = by = byteVal.value();
                    }
                    output.append(String.valueOf(value)).append('B');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 5: {
                    LinToken.CompoundStart compoundStart = (LinToken.CompoundStart)linToken;
                    output.append('{');
                    this.stateStack.addLast(new WriteState.Compound(false));
                    continue block36;
                }
                case 6: {
                    LinToken.CompoundEnd compoundEnd = (LinToken.CompoundEnd)linToken;
                    output.append('}');
                    this.stateStack.removeLast();
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 7: {
                    double value;
                    LinToken.Double double_ = (LinToken.Double)linToken;
                    {
                        double d;
                        value = d = double_.value();
                    }
                    output.append(String.valueOf(value)).append('D');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 8: {
                    float value;
                    LinToken.Float float_ = (LinToken.Float)linToken;
                    {
                        float f;
                        value = f = float_.value();
                    }
                    output.append(String.valueOf(value)).append('F');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 9: {
                    LinToken.IntArrayStart intArrayStart = (LinToken.IntArrayStart)linToken;
                    output.append("[I;");
                    continue block36;
                }
                case 10: {
                    IntBuffer buffer;
                    LinToken.IntArrayContent intArrayContent = (LinToken.IntArrayContent)linToken;
                    {
                        IntBuffer intBuffer;
                        buffer = intBuffer = intArrayContent.buffer();
                    }
                    if (state instanceof WriteState.WritingArray) {
                        output.append(',');
                    } else {
                        this.stateStack.addLast(new WriteState.WritingArray());
                    }
                    while (true) {
                        if (!buffer.hasRemaining()) continue block36;
                        output.append(String.valueOf(buffer.get()));
                        if (!buffer.hasRemaining()) continue;
                        output.append(',');
                    }
                }
                case 11: {
                    LinToken.IntArrayEnd intArrayEnd = (LinToken.IntArrayEnd)linToken;
                    if (state instanceof WriteState.WritingArray) {
                        this.stateStack.removeLast();
                    }
                    output.append(']');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 12: {
                    int value;
                    LinToken.Int intVal = (LinToken.Int)linToken;
                    {
                        int n2;
                        value = n2 = intVal.value();
                    }
                    output.append(String.valueOf(value));
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 13: {
                    OptionalInt size;
                    LinToken.ListStart listStart = (LinToken.ListStart)linToken;
                    {
                        Object object = listStart.size();
                        size = object;
                        Object elementId = object = listStart.elementId();
                    }
                    output.append('[');
                    this.stateStack.addLast(new WriteState.List(size.orElseThrow()));
                    continue block36;
                }
                case 14: {
                    LinToken.ListEnd listEnd = (LinToken.ListEnd)linToken;
                    output.append(']');
                    this.stateStack.removeLast();
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 15: {
                    LinToken.LongArrayStart longArrayStart = (LinToken.LongArrayStart)linToken;
                    output.append("[L;");
                    continue block36;
                }
                case 16: {
                    LongBuffer buffer;
                    LinToken.LongArrayContent longArrayContent = (LinToken.LongArrayContent)linToken;
                    {
                        LongBuffer longBuffer;
                        buffer = longBuffer = longArrayContent.buffer();
                    }
                    if (state instanceof WriteState.WritingArray) {
                        output.append(',');
                    } else {
                        this.stateStack.addLast(new WriteState.WritingArray());
                    }
                    while (true) {
                        if (!buffer.hasRemaining()) continue block36;
                        output.append(String.valueOf(buffer.get())).append('L');
                        if (!buffer.hasRemaining()) continue;
                        output.append(',');
                    }
                }
                case 17: {
                    LinToken.LongArrayEnd longArrayEnd = (LinToken.LongArrayEnd)linToken;
                    if (state instanceof WriteState.WritingArray) {
                        this.stateStack.removeLast();
                    }
                    output.append(']');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 18: {
                    long value;
                    LinToken.Long longVal = (LinToken.Long)linToken;
                    {
                        long l;
                        value = l = longVal.value();
                    }
                    output.append(String.valueOf(value)).append('L');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 19: {
                    short value;
                    LinToken.Short shortVal = (LinToken.Short)linToken;
                    {
                        short s;
                        value = s = shortVal.value();
                    }
                    output.append(String.valueOf(value)).append('S');
                    this.handleValueEnd(output);
                    continue block36;
                }
                case 20: {
                    String value;
                    LinToken.String string2 = (LinToken.String)linToken;
                    {
                        String string;
                        value = string = string2.value();
                    }
                    output.append(Elusion.escapeIfNeeded(value));
                    this.handleValueEnd(output);
                    continue block36;
                }
            }
            break;
        }
        throw new NbtWriteException("Unknown token: " + String.valueOf(token));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void handleValueEnd(Appendable output) throws IOException {
        WriteState state;
        WriteState writeState = state = this.stateStack.pollLast();
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{WriteState.List.class, WriteState.Compound.class}, (Object)writeState, (int)n)) {
            case -1: {
                return;
            }
            case 0: {
                WriteState.List list = (WriteState.List)writeState;
                try {
                    int n2;
                    int remainingValues = n2 = list.remainingValues();
                    this.stateStack.addLast(new WriteState.List(remainingValues - 1));
                    if (remainingValues - 1 <= 0) return;
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                output.append(',');
                return;
            }
            case 1: {
                WriteState.Compound compound = (WriteState.Compound)writeState;
                this.stateStack.addLast(new WriteState.Compound(true));
                return;
            }
        }
        throw new NbtWriteException("Unexpected state: " + String.valueOf(state));
    }

    private void replaceLast(WriteState state) {
        this.stateStack.removeLast();
        this.stateStack.addLast(state);
    }

    private static sealed interface WriteState {

        public record WritingArray() implements WriteState
        {
        }

        public record Compound(boolean hasPrevious) implements WriteState
        {
        }

        public record List(int remainingValues) implements WriteState
        {
        }
    }
}

