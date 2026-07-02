/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream.impl;

import java.io.IOException;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.OptionalInt;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.stream.impl.ValueCounter;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class OptionalInfoCalculator
implements LinStream {
    private final LinStream original;
    private @Nullable Deque<LinToken> tokenBuffer;

    public OptionalInfoCalculator(LinStream original) {
        this.original = original;
    }

    @Override
    public @Nullable LinToken nextOrNull() throws IOException {
        LinToken next;
        if (this.tokenBuffer != null) {
            next = this.tokenBuffer.pollFirst();
            if (next != null) {
                return next;
            }
            this.tokenBuffer = null;
        }
        if ((next = this.original.nextOrNull()) == null) {
            return null;
        }
        TokenAndBuffer tokenAndBuffer = this.fillIfNeeded(next);
        if (tokenAndBuffer.buffer != null && !tokenAndBuffer.buffer.isEmpty()) {
            this.tokenBuffer = tokenAndBuffer.buffer;
        }
        return tokenAndBuffer.token;
    }

    @Override
    public LinStream calculateOptionalInfo() {
        return this;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private TokenAndBuffer fillIfNeeded(LinToken token) throws IOException {
        TokenAndBuffer tokenAndBuffer;
        LinToken linToken = token;
        Objects.requireNonNull(linToken);
        LinToken linToken2 = linToken;
        int n = 0;
        block11: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{LinToken.ListStart.class, LinToken.ByteArrayStart.class, LinToken.IntArrayStart.class, LinToken.LongArrayStart.class, LinToken.Name.class}, (Object)linToken2, (int)n)) {
                case 0: {
                    LinToken.ListStart listStart = (LinToken.ListStart)linToken2;
                    if (!listStart.size().isEmpty() && !listStart.elementId().isEmpty()) {
                        n = 1;
                        continue block11;
                    }
                    tokenAndBuffer = this.getFilled(new ListStartFill(listStart));
                    return tokenAndBuffer;
                }
                case 1: {
                    LinToken.ByteArrayStart byteArrayStart = (LinToken.ByteArrayStart)linToken2;
                    if (byteArrayStart.size().isPresent()) {
                        tokenAndBuffer = new TokenAndBuffer(token, null);
                        return tokenAndBuffer;
                    }
                    tokenAndBuffer = this.getFilled(new ByteArrayStartFill());
                    return tokenAndBuffer;
                }
                case 2: {
                    Object object2;
                    LinToken.IntArrayStart intArrayStart = (LinToken.IntArrayStart)linToken2;
                    try {
                        Object size = object2 = intArrayStart.size();
                        if (((OptionalInt)size).isPresent()) {
                            tokenAndBuffer = new TokenAndBuffer(token, null);
                            return tokenAndBuffer;
                        }
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    tokenAndBuffer = this.getFilled(new IntArrayStartFill());
                    return tokenAndBuffer;
                }
                case 3: {
                    Object size;
                    Object object;
                    Object object2 = (LinToken.LongArrayStart)linToken2;
                    {
                        size = object = ((LinToken.LongArrayStart)object2).size();
                    }
                    if (size.isEmpty()) {
                        tokenAndBuffer = this.getFilled(new LongArrayStartFill());
                        return tokenAndBuffer;
                    }
                    n = 4;
                    continue block11;
                }
                case 4: {
                    Object id;
                    String name;
                    Object object = (LinToken.Name)linToken2;
                    {
                        Object object3 = ((LinToken.Name)object).name();
                        name = object3;
                        id = object3 = ((LinToken.Name)object).id();
                    }
                    if (id.isEmpty()) {
                        tokenAndBuffer = this.getFilled(new NameFill(name));
                        return tokenAndBuffer;
                    }
                    n = 5;
                    continue block11;
                }
            }
            break;
        }
        tokenAndBuffer = new TokenAndBuffer(token, null);
        return tokenAndBuffer;
    }

    private TokenAndBuffer getFilled(OptionalFill fill) throws IOException {
        LinToken filled;
        ArrayDeque<LinToken> buffer = new ArrayDeque<LinToken>();
        ArrayDeque<LinToken> consumedTokenStack = new ArrayDeque<LinToken>();
        while (true) {
            LinToken next;
            if ((next = (LinToken)buffer.pollFirst()) == null) {
                LinToken originalNext = this.original.nextOrNull();
                if (originalNext == null) {
                    throw new NbtParseException("Optional value not filled by the end of token stream");
                }
                TokenAndBuffer tokenAndBuffer = this.fillIfNeeded(originalNext);
                buffer.add(tokenAndBuffer.token);
                consumedTokenStack.add(tokenAndBuffer.token);
                if (tokenAndBuffer.buffer == null) continue;
                buffer.addAll(tokenAndBuffer.buffer);
                consumedTokenStack.addAll(tokenAndBuffer.buffer);
                continue;
            }
            filled = fill.tryFill(next);
            if (filled != null) break;
        }
        return new TokenAndBuffer(filled, consumedTokenStack);
    }

    private record TokenAndBuffer(LinToken token, @Nullable Deque<LinToken> buffer) {
    }

    private static final class ListStartFill
    implements OptionalFill {
        private final int knownSize;
        private final @Nullable ValueCounter counter;
        private @Nullable LinTagId elementId;

        public ListStartFill(LinToken.ListStart listStart) {
            if (listStart.size().isPresent()) {
                this.knownSize = listStart.size().getAsInt();
                this.counter = null;
            } else {
                this.knownSize = -1;
                this.counter = new ValueCounter();
            }
            this.elementId = listStart.elementId().orElse(null);
        }

        @Override
        public @Nullable LinToken tryFill(LinToken token) {
            if (this.counter == null || !this.counter.isNested()) {
                LinTagId elementId = this.elementId;
                if (elementId == null) {
                    elementId = token instanceof LinToken.ListEnd ? LinTagId.END : token.tagId().orElseThrow(() -> new NbtParseException("Token doesn't represent a tag directly: " + String.valueOf(token)));
                    this.elementId = elementId;
                }
                if (this.counter == null) {
                    return new LinToken.ListStart(this.knownSize, elementId);
                }
                if (token instanceof LinToken.ListEnd) {
                    return new LinToken.ListStart(this.counter.count(), elementId);
                }
            }
            this.counter.add(token);
            return null;
        }
    }

    private static interface OptionalFill {
        public @Nullable LinToken tryFill(LinToken var1);
    }

    private static final class ByteArrayStartFill
    implements OptionalFill {
        private int size;

        private ByteArrayStartFill() {
        }

        @Override
        public @Nullable LinToken tryFill(LinToken token) {
            if (token instanceof LinToken.ByteArrayEnd) {
                return new LinToken.ByteArrayStart(this.size);
            }
            if (token instanceof LinToken.ByteArrayContent) {
                LinToken.ByteArrayContent content = (LinToken.ByteArrayContent)token;
                this.size += content.buffer().remaining();
            } else {
                throw new NbtParseException("Unexpected token: " + String.valueOf(token));
            }
            return null;
        }
    }

    private static final class IntArrayStartFill
    implements OptionalFill {
        private int size;

        private IntArrayStartFill() {
        }

        @Override
        public @Nullable LinToken tryFill(LinToken token) {
            if (token instanceof LinToken.IntArrayEnd) {
                return new LinToken.IntArrayStart(this.size);
            }
            if (token instanceof LinToken.IntArrayContent) {
                LinToken.IntArrayContent content = (LinToken.IntArrayContent)token;
                this.size += content.buffer().remaining();
            } else {
                throw new NbtParseException("Unexpected token: " + String.valueOf(token));
            }
            return null;
        }
    }

    private static final class LongArrayStartFill
    implements OptionalFill {
        private int size;

        private LongArrayStartFill() {
        }

        @Override
        public @Nullable LinToken tryFill(LinToken token) {
            if (token instanceof LinToken.LongArrayEnd) {
                return new LinToken.LongArrayStart(this.size);
            }
            if (token instanceof LinToken.LongArrayContent) {
                LinToken.LongArrayContent content = (LinToken.LongArrayContent)token;
                this.size += content.buffer().remaining();
            } else {
                throw new NbtParseException("Unexpected token: " + String.valueOf(token));
            }
            return null;
        }
    }

    private record NameFill(String name) implements OptionalFill
    {
        @Override
        public LinToken tryFill(LinToken token) {
            return new LinToken.Name(this.name, token.tagId().orElseThrow(() -> new NbtParseException("Token doesn't represent a tag directly: " + String.valueOf(token))));
        }
    }
}

