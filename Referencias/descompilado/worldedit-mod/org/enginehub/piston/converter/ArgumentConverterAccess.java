/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import java.util.Optional;
import java.util.Set;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.EmptyArgumentConverterAccess;
import org.enginehub.piston.inject.Key;

public interface ArgumentConverterAccess {
    public static final ArgumentConverterAccess EMPTY = EmptyArgumentConverterAccess.INSTANCE;

    public Set<Key<?>> keySet();

    public <T> Optional<ArgumentConverter<T>> getConverter(Key<T> var1);
}

