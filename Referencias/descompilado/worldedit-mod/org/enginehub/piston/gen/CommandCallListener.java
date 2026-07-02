/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.gen;

import java.lang.reflect.Method;
import org.enginehub.piston.CommandParameters;

public interface CommandCallListener {
    default public void beforeCall(Method commandMethod, CommandParameters parameters) {
    }

    default public void afterCall(Method commandMethod, CommandParameters parameters) {
    }

    default public void afterThrow(Method commandMethod, CommandParameters parameters, Throwable error) {
    }
}

