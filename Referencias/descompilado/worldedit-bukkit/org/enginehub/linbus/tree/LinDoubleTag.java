/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinDoubleTag
extends LinNumberTag<Double> {
    private final double value;

    public static LinDoubleTag of(double value) {
        return new LinDoubleTag(value);
    }

    private LinDoubleTag(double value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinDoubleTag> type() {
        return LinTagType.doubleTag();
    }

    @Override
    public Double value() {
        return this.value;
    }

    public double valueAsDouble() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.Double(this.value));
    }
}

