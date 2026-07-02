/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.format.snbt.impl.reader;

import java.lang.runtime.SwitchBootstraps;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.enginehub.linbus.format.snbt.impl.reader.SnbtToken;
import org.enginehub.linbus.format.snbt.impl.reader.SnbtTokenWithMetadata;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class LinSnbtReader
implements LinStream {
    private static final int BUFFER_SIZE = 4096;
    private final Iterator<? extends SnbtTokenWithMetadata> input;
    private final Deque<State> stateStack;
    private final Deque<LinToken> tokenQueue;
    private final Deque<SnbtTokenWithMetadata> readAgainStack;
    private int charIndex;

    public LinSnbtReader(Iterator<? extends SnbtTokenWithMetadata> input) {
        this.input = input;
        this.stateStack = new ArrayDeque<State>(List.of((Object)new State.ReadValue(true)));
        this.tokenQueue = new ArrayDeque<LinToken>();
        this.readAgainStack = new ArrayDeque<SnbtTokenWithMetadata>();
    }

    private SnbtTokenWithMetadata read() {
        SnbtTokenWithMetadata token = this.readAgainStack.pollFirst();
        if (token != null) {
            return token;
        }
        if (!this.input.hasNext()) {
            throw new NbtParseException(this.errorPrefix() + "Unexpected end of input");
        }
        SnbtTokenWithMetadata next = this.input.next();
        this.charIndex = next.charIndex();
        return next;
    }

    private String errorPrefix() {
        return "At character index " + this.charIndex + ": ";
    }

    private NbtParseException unexpectedTokenError(SnbtToken token) {
        return new NbtParseException(this.errorPrefix() + "Unexpected token: " + String.valueOf(token));
    }

    private NbtParseException unexpectedTokenSpecificError(SnbtToken token, String expected) {
        return new NbtParseException(this.errorPrefix() + "Unexpected token: " + String.valueOf(token) + ", expected " + expected);
    }

    @Override
    public @Nullable LinToken nextOrNull() {
        LinToken token = this.tokenQueue.pollFirst();
        while (token == null) {
            State state = this.stateStack.peekLast();
            if (state == null) {
                return null;
            }
            this.fillTokenStack(state);
            token = this.tokenQueue.pollFirst();
        }
        return token;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void fillTokenStack(State state) {
        State state2 = state;
        Objects.requireNonNull(state2);
        State state3 = state2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{State.ReadValue.class, State.InCompound.class, State.CompoundEntryName.class, State.InList.class, State.InByteArray.class, State.InIntArray.class, State.InLongArray.class}, (Object)state3, (int)n)) {
            case 0: {
                State.ReadValue readValue = (State.ReadValue)state3;
                try {
                    boolean bl;
                    boolean mustBeCompound = bl = readValue.mustBeCompound();
                    this.readValue(mustBeCompound);
                    return;
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                State.InCompound inCompound = (State.InCompound)state3;
                this.advanceCompound();
                return;
            }
            case 2: {
                State.CompoundEntryName compoundEntryName = (State.CompoundEntryName)state3;
                this.readName();
                return;
            }
            case 3: {
                State.InList inList = (State.InList)state3;
                this.advanceList();
                return;
            }
            case 4: {
                State.InByteArray inByteArray = (State.InByteArray)state3;
                this.advanceArray(LinToken.Byte.class, ByteBuffer::allocate, (buffer, t) -> buffer.put(t.value()), buffer -> new LinToken.ByteArrayContent(((ByteBuffer)buffer.flip()).asReadOnlyBuffer()), LinToken.ByteArrayEnd::new);
                return;
            }
            case 5: {
                State.InIntArray inIntArray = (State.InIntArray)state3;
                this.advanceArray(LinToken.Int.class, IntBuffer::allocate, (buffer, t) -> buffer.put(t.value()), buffer -> new LinToken.IntArrayContent(((IntBuffer)buffer.flip()).asReadOnlyBuffer()), LinToken.IntArrayEnd::new);
                return;
            }
            case 6: {
                State.InLongArray inLongArray = (State.InLongArray)state3;
                this.advanceArray(LinToken.Long.class, LongBuffer::allocate, (buffer, t) -> buffer.put(t.value()), buffer -> new LinToken.LongArrayContent(((LongBuffer)buffer.flip()).asReadOnlyBuffer()), LinToken.LongArrayEnd::new);
                return;
            }
        }
        throw new IllegalStateException(this.errorPrefix() + "Unknown state: " + String.valueOf(state));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void readValue(boolean mustBeCompound) {
        this.stateStack.removeLast();
        SnbtToken token = this.read().token();
        if (token instanceof SnbtToken.CompoundStart) {
            this.stateStack.addLast(new State.InCompound());
            this.stateStack.addLast(new State.CompoundEntryName());
            this.tokenQueue.addLast(new LinToken.CompoundStart());
            return;
        }
        if (mustBeCompound) {
            throw this.unexpectedTokenSpecificError(token, SnbtToken.CompoundStart.INSTANCE.toString());
        }
        SnbtToken snbtToken = token;
        Objects.requireNonNull(snbtToken);
        SnbtToken snbtToken2 = snbtToken;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SnbtToken.ListLikeStart.class, SnbtToken.Text.class}, (Object)snbtToken2, (int)n)) {
            case 0: {
                SnbtToken.ListLikeStart listLikeStart = (SnbtToken.ListLikeStart)snbtToken2;
                this.prepareListLike();
                return;
            }
            case 1: {
                LinToken linToken;
                SnbtToken.Text text = (SnbtToken.Text)snbtToken2;
                try {
                    String string;
                    boolean bl;
                    boolean quoted = bl = text.quoted();
                    String content = string = text.content();
                    linToken = quoted ? new LinToken.String(content) : this.getTokenFor(content);
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                LinToken linToken2 = linToken;
                this.tokenQueue.addLast(linToken2);
                return;
            }
        }
        throw this.unexpectedTokenError(token);
    }

    private void advanceCompound() {
        SnbtToken token;
        SnbtToken snbtToken = token = this.read().token();
        Objects.requireNonNull(snbtToken);
        SnbtToken snbtToken2 = snbtToken;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SnbtToken.CompoundEnd.class, SnbtToken.Separator.class}, (Object)snbtToken2, (int)n)) {
            case 0: {
                SnbtToken.CompoundEnd compoundEnd = (SnbtToken.CompoundEnd)snbtToken2;
                this.stateStack.removeLast();
                this.tokenQueue.addLast(new LinToken.CompoundEnd());
                break;
            }
            case 1: {
                SnbtToken.Separator separator = (SnbtToken.Separator)snbtToken2;
                this.stateStack.addLast(new State.CompoundEntryName());
                break;
            }
            default: {
                throw this.unexpectedTokenError(token);
            }
        }
    }

    private void readName() {
        this.stateStack.removeLast();
        SnbtToken token = this.read().token();
        if (!(token instanceof SnbtToken.Text)) {
            throw this.unexpectedTokenSpecificError(token, "Text");
        }
        SnbtToken.Text text = (SnbtToken.Text)token;
        token = this.read().token();
        if (!(token instanceof SnbtToken.EntrySeparator)) {
            throw this.unexpectedTokenSpecificError(token, SnbtToken.EntrySeparator.INSTANCE.toString());
        }
        this.stateStack.addLast(new State.ReadValue(false));
        this.tokenQueue.addLast(new LinToken.Name(text.content()));
    }

    private void advanceList() {
        SnbtToken token;
        SnbtToken snbtToken = token = this.read().token();
        Objects.requireNonNull(snbtToken);
        SnbtToken snbtToken2 = snbtToken;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SnbtToken.ListLikeEnd.class, SnbtToken.Separator.class}, (Object)snbtToken2, (int)n)) {
            case 0: {
                SnbtToken.ListLikeEnd listLikeEnd = (SnbtToken.ListLikeEnd)snbtToken2;
                this.stateStack.removeLast();
                this.tokenQueue.addLast(new LinToken.ListEnd());
                break;
            }
            case 1: {
                SnbtToken.Separator separator = (SnbtToken.Separator)snbtToken2;
                this.stateStack.addLast(new State.ReadValue(false));
                break;
            }
            default: {
                throw this.unexpectedTokenError(token);
            }
        }
    }

    private <T extends Buffer, L extends LinToken> void advanceArray(Class<L> tagType, IntFunction<T> allocator, BiConsumer<T, L> putter, Function<T, LinToken> contentProducer, Supplier<LinToken> endProducer) {
        boolean isEnd = false;
        Buffer buffer = (Buffer)allocator.apply(4096);
        while (buffer.hasRemaining()) {
            SnbtToken token = this.read().token();
            if (!(token instanceof SnbtToken.Text)) {
                throw this.unexpectedTokenSpecificError(token, "Text");
            }
            SnbtToken.Text text = (SnbtToken.Text)token;
            LinToken nextValue = this.getTokenFor(text.content());
            if (!tagType.isInstance(nextValue)) {
                throw new NbtParseException(this.errorPrefix() + "Expected " + tagType.getSimpleName() + " token, got " + String.valueOf(nextValue));
            }
            putter.accept(buffer, (LinToken)tagType.cast(nextValue));
            token = this.read().token();
            if (token instanceof SnbtToken.ListLikeEnd) {
                isEnd = true;
                break;
            }
            if (token instanceof SnbtToken.Separator) continue;
            throw this.unexpectedTokenError(token);
        }
        this.tokenQueue.addLast(contentProducer.apply(buffer));
        if (isEnd) {
            this.stateStack.removeLast();
            this.tokenQueue.addLast(endProducer.get());
        }
    }

    private void prepareListLike() {
        SnbtToken.Text text;
        int initialCharIndex = this.charIndex;
        SnbtTokenWithMetadata typing = this.read();
        SnbtToken snbtToken = typing.token();
        if (snbtToken instanceof SnbtToken.Text && !(text = (SnbtToken.Text)snbtToken).quoted() && text.content().length() == 1) {
            SnbtTokenWithMetadata separatorCheck = this.read();
            if (separatorCheck.token() instanceof SnbtToken.ListTypeSeparator) {
                switch (text.content().charAt(0)) {
                    case 'B': {
                        this.stateStack.addLast(new State.InByteArray());
                        this.tokenQueue.addLast(new LinToken.ByteArrayStart());
                        break;
                    }
                    case 'I': {
                        this.stateStack.addLast(new State.InIntArray());
                        this.tokenQueue.addLast(new LinToken.IntArrayStart());
                        break;
                    }
                    case 'L': {
                        this.stateStack.addLast(new State.InLongArray());
                        this.tokenQueue.addLast(new LinToken.LongArrayStart());
                        break;
                    }
                    default: {
                        throw new NbtParseException(this.errorPrefix() + "Invalid array type: " + text.content());
                    }
                }
                return;
            }
            this.readAgainStack.addFirst(separatorCheck);
        }
        this.readAgainStack.addFirst(typing);
        this.charIndex = initialCharIndex;
        this.stateStack.addLast(new State.InList());
        this.stateStack.addLast(new State.ReadValue(false));
        this.tokenQueue.addLast(new LinToken.ListStart());
    }

    private LinToken getTokenFor(String valueString) {
        char last = valueString.charAt(valueString.length() - 1);
        return switch (last) {
            case 'B', 'b' -> {
                try {
                    LinToken.Byte var3_3 = new LinToken.Byte(Byte.parseByte(valueString.substring(0, valueString.length() - 1)));
                    yield var3_3;
                }
                catch (NumberFormatException e) {
                    LinToken.String var3_4 = new LinToken.String(valueString);
                    yield var3_4;
                }
            }
            case 'L', 'l' -> {
                try {
                    LinToken.Long var3_5 = new LinToken.Long(Long.parseLong(valueString.substring(0, valueString.length() - 1)));
                    yield var3_5;
                }
                catch (NumberFormatException e) {
                    LinToken.String var3_6 = new LinToken.String(valueString);
                    yield var3_6;
                }
            }
            case 'S', 's' -> {
                try {
                    LinToken.Short var3_7 = new LinToken.Short(Short.parseShort(valueString.substring(0, valueString.length() - 1)));
                    yield var3_7;
                }
                catch (NumberFormatException e) {
                    LinToken.String var3_8 = new LinToken.String(valueString);
                    yield var3_8;
                }
            }
            case 'F', 'f' -> {
                try {
                    LinToken.Float var3_9 = new LinToken.Float(Float.parseFloat(valueString.substring(0, valueString.length() - 1)));
                    yield var3_9;
                }
                catch (NumberFormatException e) {
                    LinToken.String var3_10 = new LinToken.String(valueString);
                    yield var3_10;
                }
            }
            case 'D', 'd' -> {
                try {
                    LinToken.Double var3_11 = new LinToken.Double(Double.parseDouble(valueString.substring(0, valueString.length() - 1)));
                    yield var3_11;
                }
                catch (NumberFormatException e) {
                    LinToken.String var3_12 = new LinToken.String(valueString);
                    yield var3_12;
                }
            }
            default -> {
                try {
                    LinToken.Int var3_13;
                    yield var3_13 = new LinToken.Int(Integer.parseInt(valueString));
                }
                catch (NumberFormatException e) {
                    try {
                        LinToken.Double var3_14 = new LinToken.Double(Double.parseDouble(valueString));
                        yield var3_14;
                    }
                    catch (NumberFormatException e) {
                        boolean isTrue = valueString.equalsIgnoreCase("true");
                        if (isTrue || valueString.equalsIgnoreCase("false")) {
                            LinToken.Byte var3_15 = new LinToken.Byte((byte)(isTrue ? 1 : 0));
                            yield var3_15;
                        }
                        LinToken.String var3_16 = new LinToken.String(valueString);
                        yield var3_16;
                    }
                }
            }
        };
    }

    private static sealed interface State {

        public record ReadValue(boolean mustBeCompound) implements State
        {
        }

        public record InLongArray() implements State
        {
        }

        public record InIntArray() implements State
        {
        }

        public record InByteArray() implements State
        {
        }

        public record InList() implements State
        {
        }

        public record CompoundEntryName() implements State
        {
        }

        public record InCompound() implements State
        {
        }
    }

    private static interface ArrayAdvancer<T extends Buffer, L extends LinToken> {
        public Class<L> tagType();

        public T allocate();

        public void put(T var1, L var2);

        public LinToken produceContent(T var1);

        public LinToken produceEnd();
    }
}

