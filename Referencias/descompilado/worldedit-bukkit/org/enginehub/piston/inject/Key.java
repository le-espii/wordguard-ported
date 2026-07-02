/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.reflect.TypeToken
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.inject;

import com.google.auto.value.AutoValue;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.enginehub.piston.inject.AnnotationWrapper;
import org.enginehub.piston.inject.Annotations;
import org.enginehub.piston.inject.AutoValue_Key;
import org.enginehub.piston.inject.InjectAnnotation;
import org.enginehub.piston.inject.InstanceAnnotationWrapper;
import org.enginehub.piston.inject.NullAnnotationWrapper;
import org.enginehub.piston.inject.TypeAnnotationWrapper;

@AutoValue
public abstract class Key<T> {
    public static <T> Key<T> of(Class<T> clazz) {
        return Key.of(TypeToken.of(clazz));
    }

    public static <T> Key<T> of(TypeToken<T> typeToken) {
        return Key.of(typeToken, NullAnnotationWrapper.getInstance());
    }

    public static <T> Key<T> of(Class<T> clazz, @Nullable Annotation annotation) {
        return Key.of(TypeToken.of(clazz), annotation);
    }

    public static <T> Key<T> of(Class<T> clazz, @Nullable Class<? extends Annotation> annotation) {
        return Key.of(TypeToken.of(clazz), annotation);
    }

    public static <T> Key<T> of(TypeToken<T> typeToken, @Nullable Annotation annotation) {
        return Key.of(typeToken, Key.strategyFor(annotation));
    }

    public static <T> Key<T> of(TypeToken<T> typeToken, @Nullable Class<? extends Annotation> annotation) {
        return Key.of(typeToken, Key.strategyFor(annotation));
    }

    private static <T> Key<T> of(TypeToken<T> typeToken, AnnotationWrapper annotationWrapper) {
        return new AutoValue_Key(typeToken.wrap(), annotationWrapper);
    }

    private static AnnotationWrapper strategyFor(@Nullable Annotation annotation) {
        if (annotation == null) {
            return NullAnnotationWrapper.getInstance();
        }
        Key.validateAnnotationType(annotation.annotationType());
        if (annotation.annotationType().getDeclaredMethods().length == 0) {
            return TypeAnnotationWrapper.from(annotation.annotationType());
        }
        return InstanceAnnotationWrapper.from(annotation);
    }

    private static AnnotationWrapper strategyFor(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NullAnnotationWrapper.getInstance();
        }
        Key.validateAnnotationType(annotationType);
        if (annotationType.getDeclaredMethods().length > 0 && Stream.of(annotationType.getDeclaredMethods()).map(Method::getDefaultValue).allMatch(Objects::nonNull)) {
            return Key.strategyFor(Annotations.allDefaultsAnnotation(annotationType));
        }
        return TypeAnnotationWrapper.from(annotationType);
    }

    private static void validateAnnotationType(Class<? extends Annotation> annotationType) {
        Retention retention = annotationType.getAnnotation(Retention.class);
        Preconditions.checkArgument((retention != null && retention.value() == RetentionPolicy.RUNTIME ? 1 : 0) != 0, (String)"Annotation type %s is not retained at runtime.", annotationType);
        InjectAnnotation injectAnnotation = annotationType.getAnnotation(InjectAnnotation.class);
        Preconditions.checkArgument((injectAnnotation != null ? 1 : 0) != 0, (String)"Annotation type %s is not an inject annotation", annotationType);
    }

    public abstract TypeToken<T> getTypeToken();

    abstract AnnotationWrapper getAnnotationWrapper();

    @Nullable
    public final Annotation getAnnotation() {
        return this.getAnnotationWrapper().getAnnotation();
    }

    @Nullable
    public final Class<? extends Annotation> getAnnotationType() {
        return this.getAnnotationWrapper().getAnnotationType();
    }

    public final String toString() {
        return MoreObjects.toStringHelper(Key.class).add("type", this.getTypeToken()).add("annotationWrapper", (Object)this.getAnnotationWrapper()).toString();
    }
}

