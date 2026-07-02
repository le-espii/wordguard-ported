/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.inject;

import com.google.auto.value.AutoValue;
import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.enginehub.piston.inject.AnnotationWrapper;
import org.enginehub.piston.inject.AutoValue_TypeAnnotationWrapper;

@AutoValue
abstract class TypeAnnotationWrapper
implements AnnotationWrapper {
    static TypeAnnotationWrapper from(Class<? extends Annotation> annotationType) {
        return new AutoValue_TypeAnnotationWrapper(annotationType);
    }

    TypeAnnotationWrapper() {
    }

    @Override
    @Nonnull
    public abstract Class<? extends Annotation> getAnnotationType();

    @Override
    @Nullable
    public final Annotation getAnnotation() {
        return null;
    }
}

