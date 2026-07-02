/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.internal.annotation.MultiDirection;
import java.io.Serializable;

final class AutoAnnotation_AbstractDirectionConverter_multiDirection
implements MultiDirection,
Serializable {
    private static final long serialVersionUID = 231310279470082624L;
    private final boolean includeDiagonals;

    AutoAnnotation_AbstractDirectionConverter_multiDirection(boolean includeDiagonals) {
        this.includeDiagonals = includeDiagonals;
    }

    public Class<? extends MultiDirection> annotationType() {
        return MultiDirection.class;
    }

    @Override
    public boolean includeDiagonals() {
        return this.includeDiagonals;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("@com.sk89q.worldedit.internal.annotation.MultiDirection(");
        sb.append("includeDiagonals=");
        sb.append(this.includeDiagonals);
        return sb.append(')').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MultiDirection) {
            MultiDirection that = (MultiDirection)o;
            return this.includeDiagonals == that.includeDiagonals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0x5949F7EA ^ (this.includeDiagonals ? 1231 : 1237);
    }
}

