/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import java.util.Iterator;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.inject.Key;

public interface ArgumentConverterStore
extends ArgumentConverterAccess {
    public <T> void registerConverter(Key<T> var1, ArgumentConverter<T> var2);

    default public void registerConverters(ArgumentConverterAccess converters) {
        Iterator<Key<?>> iterator = converters.keySet().iterator();
        while (iterator.hasNext()) {
            Key<?> key;
            Key<?> typeSafeKey = key = iterator.next();
            ArgumentConverter<?> converter = converters.getConverter(key).orElseThrow(IllegalStateException::new);
            this.registerConverter(typeSafeKey, converter);
        }
    }
}

