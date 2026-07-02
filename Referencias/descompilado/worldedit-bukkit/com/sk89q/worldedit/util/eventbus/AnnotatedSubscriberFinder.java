/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 */
package com.sk89q.worldedit.util.eventbus;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.MethodHandleEventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.util.eventbus.SubscriberFindingStrategy;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

class AnnotatedSubscriberFinder
implements SubscriberFindingStrategy {
    AnnotatedSubscriberFinder() {
    }

    @Override
    public Multimap<Class<?>, EventHandler> findAllSubscribers(Object listener) {
        HashMultimap methodsInListener = HashMultimap.create();
        for (Class<?> clazz = listener.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getMethods()) {
                MethodHandle handle;
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                method.setAccessible(true);
                if (annotation == null) continue;
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("Method " + String.valueOf(method) + " has @Subscribe annotation, but requires " + parameterTypes.length + " arguments. Event handler methods must require a single argument.");
                }
                Class<?> eventType = parameterTypes[0];
                try {
                    handle = MethodHandles.publicLookup().unreflect(method);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Method " + String.valueOf(method) + " failed to unreflect.", e);
                }
                MethodHandleEventHandler handler = new MethodHandleEventHandler(annotation.priority(), listener, handle, method.getName());
                methodsInListener.put(eventType, (Object)handler);
            }
        }
        return methodsInListener;
    }
}

