/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.lifecycle;

import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.util.lifecycle.LifecycledCallbackHandler;
import com.sk89q.worldedit.util.lifecycle.Token;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

class FlatMapLifecycled<T, U>
implements Lifecycled<U> {
    private final LifecycledCallbackHandler<U> events = new LifecycledCallbackHandler(this);
    private Lifecycled<U> mapped;
    private Token<FlatMapLifecycled<T, U>> mappedToken;
    @Nullable
    private U value;

    FlatMapLifecycled(Lifecycled<T> upstream, Function<T, Lifecycled<U>> mapper) {
        upstream.events().onInvalidated(this, (this$, up) -> {
            boolean fire = this$.value != null;
            this$.value = null;
            this$.mappedToken = null;
            this$.mapped = null;
            if (fire) {
                this$.events.fireInvalidated();
            }
        });
        upstream.events().onNewValue(this, (this$, up) -> {
            this$.mapped = (Lifecycled)mapper.apply(up.valueOrThrow());
            this$.mappedToken = new Token<FlatMapLifecycled>((FlatMapLifecycled)this$);
            this.mapped.events().onInvalidated(this$.mappedToken, (token, mapped$) -> {
                boolean fire = ((FlatMapLifecycled)token.inner).value != null;
                ((FlatMapLifecycled)token.inner).value = null;
                if (fire) {
                    this$.events.fireInvalidated();
                }
            });
            this.mapped.events().onNewValue(this$.mappedToken, (token, mapped$) -> {
                Object newValue = mapped$.valueOrThrow();
                boolean fire = ((FlatMapLifecycled)token.inner).value != newValue;
                ((FlatMapLifecycled)token.inner).value = newValue;
                if (fire) {
                    this$.events.fireOnNewValue();
                }
            });
        });
    }

    @Override
    public Optional<U> value() {
        return Optional.ofNullable(this.value);
    }

    @Override
    public boolean isValid() {
        return this.value != null;
    }

    @Override
    public Lifecycled.Events<U> events() {
        return this.events;
    }
}

