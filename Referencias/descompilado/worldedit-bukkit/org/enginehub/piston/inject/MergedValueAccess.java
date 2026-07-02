/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.inject;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public final class MergedValueAccess
implements InjectedValueAccess {
    private final ImmutableList<InjectedValueAccess> delegates;

    public static MergedValueAccess of(InjectedValueAccess ... delegates) {
        return MergedValueAccess.of((Collection<? extends InjectedValueAccess>)ImmutableList.copyOf((Object[])delegates));
    }

    public static MergedValueAccess of(Collection<? extends InjectedValueAccess> delegates) {
        return new MergedValueAccess(delegates);
    }

    private MergedValueAccess(Collection<? extends InjectedValueAccess> delegates) {
        this.delegates = ImmutableList.copyOf(delegates);
    }

    @Override
    public <T> Optional<T> injectedValue(Key<T> key, InjectedValueAccess context) {
        return this.delegates.stream().map(access -> access.injectedValue(key, context)).filter(Optional::isPresent).map(Optional::get).findFirst();
    }
}

