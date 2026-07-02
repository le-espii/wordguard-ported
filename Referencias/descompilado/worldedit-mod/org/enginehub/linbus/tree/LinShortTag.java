/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinShortTag
extends LinNumberTag<Short> {
    private final short value;

    public static LinShortTag of(short value) {
        return new LinShortTag(value);
    }

    private LinShortTag(short value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinShortTag> type() {
        return LinTagType.shortTag();
    }

    @Override
    public Short value() {
        return this.value;
    }

    public short valueAsShort() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.Short(this.value));
    }
}

