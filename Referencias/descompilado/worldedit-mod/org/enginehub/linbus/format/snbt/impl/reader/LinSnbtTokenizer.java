/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.format.snbt.impl.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import org.enginehub.linbus.common.internal.AbstractIterator;
import org.enginehub.linbus.format.snbt.impl.Elusion;
import org.enginehub.linbus.format.snbt.impl.reader.SnbtToken;
import org.enginehub.linbus.format.snbt.impl.reader.SnbtTokenWithMetadata;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.jspecify.annotations.Nullable;

public class LinSnbtTokenizer
extends AbstractIterator<SnbtTokenWithMetadata> {
    private final Reader input;
    private int charIndex = -1;
    private boolean eatAllWhitespaceAfter;

    public LinSnbtTokenizer(Reader input) {
        this.input = input.markSupported() ? input : new BufferedReader(input);
    }

    private String errorPrefix() {
        return "At character index " + this.charIndex + ": ";
    }

    private void skipWhitespace() throws IOException {
        block1: {
            int next;
            do {
                this.input.mark(1);
                next = this.input.read();
                if (next == -1) break block1;
                ++this.charIndex;
            } while (Character.isWhitespace(next));
            this.input.reset();
            --this.charIndex;
        }
    }

    @Override
    protected @Nullable SnbtTokenWithMetadata computeNext() {
        try {
            this.skipWhitespace();
            this.input.mark(1);
            int next = this.input.read();
            if (next == -1) {
                return (SnbtTokenWithMetadata)((Object)this.end());
            }
            ++this.charIndex;
            return switch (next) {
                case 123 -> new SnbtTokenWithMetadata(SnbtToken.CompoundStart.INSTANCE, this.charIndex);
                case 125 -> new SnbtTokenWithMetadata(SnbtToken.CompoundEnd.INSTANCE, this.charIndex);
                case 91 -> new SnbtTokenWithMetadata(SnbtToken.ListLikeStart.INSTANCE, this.charIndex);
                case 93 -> new SnbtTokenWithMetadata(SnbtToken.ListLikeEnd.INSTANCE, this.charIndex);
                case 58 -> new SnbtTokenWithMetadata(SnbtToken.EntrySeparator.INSTANCE, this.charIndex);
                case 59 -> new SnbtTokenWithMetadata(SnbtToken.ListTypeSeparator.INSTANCE, this.charIndex);
                case 44 -> new SnbtTokenWithMetadata(SnbtToken.Separator.INSTANCE, this.charIndex);
                case 34, 39 -> {
                    int initialCharIndex = this.charIndex;
                    yield new SnbtTokenWithMetadata(new SnbtToken.Text(true, this.readQuotedText((char)next)), initialCharIndex);
                }
                default -> {
                    this.input.reset();
                    --this.charIndex;
                    yield this.readSimpleValue();
                }
            };
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private SnbtTokenWithMetadata readSimpleValue() throws IOException {
        int initialCharIndex = this.charIndex + 1;
        StringBuilder builder = new StringBuilder();
        boolean wasWhitespace = false;
        while (true) {
            this.input.mark(1);
            int next = this.input.read();
            if (next == -1) break;
            ++this.charIndex;
            if (next == 44 || next == 58 || next == 59 || next == 125 || next == 93) {
                this.input.reset();
                --this.charIndex;
                break;
            }
            if (Character.isWhitespace(next)) {
                wasWhitespace = true;
                continue;
            }
            if (!Elusion.isSafeCharacter((char)next)) {
                throw new NbtParseException(this.errorPrefix() + "Unexpected character: " + (char)next);
            }
            if (wasWhitespace) {
                throw new NbtParseException(this.errorPrefix() + "Found non-terminator after whitespace");
            }
            builder.append((char)next);
        }
        return new SnbtTokenWithMetadata(new SnbtToken.Text(false, builder.toString()), initialCharIndex);
    }

    private String readQuotedText(char quoteChar) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        while (true) {
            int c;
            if ((c = this.input.read()) == -1) {
                throw new NbtParseException(this.errorPrefix() + "Unexpected end of input in quoted value");
            }
            ++this.charIndex;
            if (!escaped) {
                if (c == quoteChar) {
                    return sb.toString();
                }
                if (c == 92) {
                    escaped = true;
                    continue;
                }
            } else {
                if (c != quoteChar && c != 92) {
                    throw new NbtParseException(this.errorPrefix() + "Invalid escape: \\" + (char)c);
                }
                escaped = false;
            }
            sb.append((char)c);
        }
    }
}

