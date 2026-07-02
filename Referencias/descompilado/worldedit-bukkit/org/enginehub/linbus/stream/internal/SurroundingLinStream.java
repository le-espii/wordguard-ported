/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream.internal;

import java.io.IOException;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class SurroundingLinStream
implements LinStream {
    private @Nullable LinToken prefix;
    private @Nullable LinStream stream;
    private @Nullable LinToken suffix;

    public SurroundingLinStream(@Nullable LinToken prefix, LinStream stream, @Nullable LinToken suffix) {
        this.prefix = prefix;
        this.stream = stream;
        this.suffix = suffix;
    }

    @Override
    public @Nullable LinToken nextOrNull() throws IOException {
        LinToken token;
        if (this.prefix != null) {
            LinToken token2 = this.prefix;
            this.prefix = null;
            return token2;
        }
        LinToken linToken = token = this.stream == null ? null : this.stream.nextOrNull();
        if (token == null) {
            this.stream = null;
            token = this.suffix;
            this.suffix = null;
        }
        return token;
    }
}

