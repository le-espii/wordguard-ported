/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinFloatTag
extends LinNumberTag<Float> {
    private final float value;

    public static LinFloatTag of(float value) {
        return new LinFloatTag(value);
    }

    private LinFloatTag(float value) {
        this.value = value;
    }

    @Override
    public LinTagType<LinFloatTag> type() {
        return LinTagType.floatTag();
    }

    @Override
    public Float value() {
        return Float.valueOf(this.value);
    }

    public float valueAsFloat() {
        return this.value;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of((LinToken)new LinToken.Float(this.value));
    }
}

