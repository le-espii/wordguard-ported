/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.inject.Key;

enum EmptyArgumentConverterAccess implements ArgumentConverterAccess
{
    INSTANCE;


    @Override
    public Set<Key<?>> keySet() {
        return Collections.emptySet();
    }

    @Override
    public <T> Optional<ArgumentConverter<T>> getConverter(Key<T> key) {
        return Optional.empty();
    }
}

