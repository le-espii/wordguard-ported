/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.tree;

import java.util.Objects;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinEndTag;
import org.enginehub.linbus.tree.LinIntArrayTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinLongArrayTag;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTagType;
import org.enginehub.linbus.tree.ToLinTag;
import org.jspecify.annotations.Nullable;

public abstract sealed class LinTag<T>
implements ToLinTag<LinTag<T>>,
LinStreamable
permits LinByteArrayTag, LinCompoundTag, LinEndTag, LinIntArrayTag, LinListTag, LinLongArrayTag, LinNumberTag, LinStringTag {
    protected LinTag() {
    }

    public abstract LinTagType<? extends LinTag<T>> type();

    public abstract T value();

    @Override
    public final LinTag<T> toLinTag() {
        return this;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LinTag that = (LinTag)o;
        return Objects.equals(this.value(), that.value());
    }

    public int hashCode() {
        return this.value().hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + String.valueOf(this.value()) + "]";
    }
}

