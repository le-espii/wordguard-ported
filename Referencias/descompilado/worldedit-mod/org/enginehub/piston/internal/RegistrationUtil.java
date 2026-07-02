/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.inject.Key;

public class RegistrationUtil {
    public static <T> T requireOptional(Key<T> type, String name, Optional<T> optional) {
        return optional.orElseThrow(() -> new IllegalStateException("No injected value for " + name + " (type " + type + ")"));
    }

    public static Method getCommandMethod(Class<?> registrationClass, String methodName, Class ... parameterTypes) {
        try {
            return registrationClass.getDeclaredMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Missing command method: " + methodName);
        }
    }

    public static void listenersBeforeCall(List<CommandCallListener> listeners, Method commandMethod, CommandParameters parameters) {
        for (CommandCallListener listener : listeners) {
            listener.beforeCall(commandMethod, parameters);
        }
    }

    public static void listenersAfterCall(List<CommandCallListener> listeners, Method commandMethod, CommandParameters parameters) {
        for (CommandCallListener listener : listeners) {
            listener.afterCall(commandMethod, parameters);
        }
    }

    public static void listenersAfterThrow(List<CommandCallListener> listeners, Method commandMethod, CommandParameters parameters, Throwable error) {
        for (CommandCallListener listener : listeners) {
            listener.afterThrow(commandMethod, parameters, error);
        }
    }

    private RegistrationUtil() {
        throw new RuntimeException();
    }
}

