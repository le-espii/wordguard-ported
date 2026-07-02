/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Optional;
import org.enginehub.linbus.common.internal.AbstractIterator;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.stream.impl.OptionalInfoCalculator;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public interface LinStream
extends LinStreamable {
    public static LinStream of() {
        return () -> null;
    }

    public static LinStream of(final LinToken token) {
        return new LinStream(){
            private @Nullable LinToken nextToken;
            {
                this.nextToken = token;
            }

            @Override
            public @Nullable LinToken nextOrNull() {
                LinToken token2 = this.nextToken;
                this.nextToken = null;
                return token2;
            }
        };
    }

    public static LinStream of(final LinToken ... tokens) {
        return new LinStream(){
            private int index = 0;

            @Override
            public @Nullable LinToken nextOrNull() {
                if (this.index >= tokens.length) {
                    return null;
                }
                LinToken token = tokens[this.index];
                ++this.index;
                return token;
            }
        };
    }

    public @Nullable LinToken nextOrNull() throws IOException;

    default public Optional<LinToken> next() throws IOException {
        return Optional.ofNullable(this.nextOrNull());
    }

    default public LinStream calculateOptionalInfo() {
        return new OptionalInfoCalculator(this);
    }

    @Override
    default public LinStream linStream() {
        return this;
    }

    default public Iterator<LinToken> asIterator() {
        return new AbstractIterator<LinToken>(){

            @Override
            protected @Nullable LinToken computeNext() {
                try {
                    LinToken linToken = LinStream.this.nextOrNull();
                    return linToken == null ? (LinToken)this.end() : linToken;
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }
}

