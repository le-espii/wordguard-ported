/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public final class LinEndTag
extends LinTag<Void> {
    private static final LinEndTag INSTANCE = new LinEndTag();

    public static LinEndTag instance() {
        return INSTANCE;
    }

    private LinEndTag() {
    }

    @Override
    public LinTagType<LinEndTag> type() {
        return LinTagType.endTag();
    }

    @Override
    public Void value() {
        return null;
    }

    @Override
    public LinStream linStream() {
        return LinStream.of();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

