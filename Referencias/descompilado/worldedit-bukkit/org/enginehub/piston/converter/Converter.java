/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.inject.InjectedValueAccess;

@FunctionalInterface
public interface Converter<T> {
    public ConversionResult<T> convert(String var1, InjectedValueAccess var2);
}

