/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.stream.internal;

import java.io.IOException;
import java.util.Iterator;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.stream.token.LinToken;
import org.jspecify.annotations.Nullable;

public class FlatteningLinStream
implements LinStream {
    private final Iterator<? extends LinStreamable> streamables;
    private @Nullable LinStream current;

    public FlatteningLinStream(Iterator<? extends LinStreamable> streamables) {
        this.streamables = streamables;
    }

    @Override
    public @Nullable LinToken nextOrNull() throws IOException {
        LinToken token;
        while (true) {
            if (this.current == null) {
                if (!this.streamables.hasNext()) {
                    return null;
                }
                this.current = this.streamables.next().linStream();
            }
            if ((token = this.current.nextOrNull()) != null) break;
            this.current = null;
        }
        return token;
    }
}

