/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Joiner$MapJoiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.inject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

class Annotations {
    private static final Joiner.MapJoiner JOINER = Joiner.on((String)", ").withKeyValueSeparator("=");
    private static final Map<MethodKey, AnnoMethod> ANNOTATION_METHODS = ImmutableMap.of((Object)MethodKey.of(Class.class, "annotationType", new Class[0]), (type, members, args) -> type, (Object)MethodKey.of(Boolean.TYPE, "equals", Object.class), (type, members, args) -> {
        if (!type.isInstance(args[0])) {
            return false;
        }
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (Objects.equals(method.invoke(args[0], new Object[0]), members.get(name))) continue;
            return false;
        }
        return true;
    }, (Object)MethodKey.of(Integer.TYPE, "hashCode", new Class[0]), (type, members, args) -> {
        int result = 0;
        for (String name : members.keySet()) {
            Object value = members.get(name);
            result += 127 * name.hashCode() ^ Objects.hash(value) - 31;
        }
        return result;
    }, (Object)MethodKey.of(String.class, "toString", new Class[0]), (type, members, args) -> {
        StringBuilder output = new StringBuilder("@").append(type.getName()).append('(');
        JOINER.appendTo(output, members.entrySet().stream().map(e -> Maps.immutableEntry((Object)((String)e.getKey()), (Object)Annotations.valueToString(e.getValue()))).iterator());
        return output.append(')').toString();
    });
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    Annotations() {
    }

    private static String valueToString(@Nullable Object value) {
        if (value == null) {
            return "null";
        }
        if (value.getClass().isArray()) {
            return Arrays.deepToString((Object[])value);
        }
        return value.toString();
    }

    static Annotation allDefaultsAnnotation(Class<? extends Annotation> annotationType) {
        Map<String, Object> members = Stream.of(annotationType.getDeclaredMethods()).collect(Collectors.toMap(Method::getName, Method::getDefaultValue));
        return (Annotation)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, (proxy, method, args) -> {
            AnnoMethod call = ANNOTATION_METHODS.get(MethodKey.from(method));
            if (call != null) {
                return call.invoke(annotationType, members, args);
            }
            Object value = members.get(method.getName());
            if (value != null) {
                return value;
            }
            throw new IllegalStateException("Unknown method on " + annotationType + ": " + method);
        });
    }

    private static final class MethodKey {
        private final String name;
        private final ImmutableList<Class<?>> signature;

        static MethodKey from(Method method) {
            return MethodKey.of(method.getReturnType(), method.getName(), method.getParameterTypes());
        }

        static MethodKey of(Class<?> rtype, String name, Class<?> ... ptypes) {
            return new MethodKey(name, (List<Class<?>>)ImmutableList.builder().add(rtype).add((Object[])ptypes).build());
        }

        private MethodKey(String name, List<Class<?>> signature) {
            this.name = name;
            this.signature = ImmutableList.copyOf(signature);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MethodKey methodKey = (MethodKey)o;
            return this.name.equals(methodKey.name) && this.signature.equals(methodKey.signature);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.signature);
        }
    }

    @FunctionalInterface
    private static interface AnnoMethod {
        public Object invoke(Class<? extends Annotation> var1, Map<String, Object> var2, Object[] var3) throws Exception;
    }
}

