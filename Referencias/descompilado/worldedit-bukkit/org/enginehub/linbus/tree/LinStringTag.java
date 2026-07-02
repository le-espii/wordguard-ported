/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import java.util.Objects;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinStringTag
extends LinTag<String> {
    private final String value;

    public static LinStringTag of(String value) {
        return new LinStringTag(value);
    }

    private LinStringTag(String value) {
        this.value = Objects.requireNonNull(value, "value is null");
    }

    @Override
    public LinTagType<LinStringTag> type() {
        return LinTagType.stringTag();
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.String(this.value));
    }
}

