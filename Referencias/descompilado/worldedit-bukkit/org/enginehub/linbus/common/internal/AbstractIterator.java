/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.enginehub.linbus.common.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

public abstract class AbstractIterator<T>
implements Iterator<T> {
    private boolean needNext = true;
    private boolean end;
    private T next;

    protected abstract @Nullable T computeNext();

    protected final @Nullable T end() {
        this.end = true;
        return null;
    }

    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        T next = this.next;
        this.needNext = true;
        this.next = null;
        return next;
    }

    @Override
    public final boolean hasNext() {
        if (!this.end && this.needNext) {
            this.needNext = false;
            this.next = this.computeNext();
        }
        return !this.end;
    }

    @Override
    public final void forEachRemaining(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (this.end) {
            return;
        }
        if (!this.needNext) {
            action.accept(this.next);
            this.next = null;
        }
        while (true) {
            T next = this.computeNext();
            if (this.end) break;
            action.accept(next);
        }
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException("remove");
    }
}

