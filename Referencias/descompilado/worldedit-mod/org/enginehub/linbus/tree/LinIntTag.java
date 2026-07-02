/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinIntTag
extends LinNumberTag<Integer> {
    private final int value;

    public static LinIntTag of(int value) {
        return new LinIntTag(value);
    }

    private LinIntTag(int value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinIntTag> type() {
        return LinTagType.intTag();
    }

    @Override
    public Integer value() {
        return this.value;
    }

    public int valueAsInt() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.Int(this.value));
    }
}

