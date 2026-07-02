/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  javax.annotation.Nonnull
 */
package org.enginehub.piston.inject;

import com.google.auto.value.AutoValue;
import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import org.enginehub.piston.inject.AnnotationWrapper;
import org.enginehub.piston.inject.AutoValue_InstanceAnnotationWrapper;

@AutoValue
abstract class InstanceAnnotationWrapper
implements AnnotationWrapper {
    static InstanceAnnotationWrapper from(Annotation annotationInstance) {
        return new AutoValue_InstanceAnnotationWrapper(annotationInstance);
    }

    InstanceAnnotationWrapper() {
    }

    @Override
    @Nonnull
    public abstract Annotation getAnnotation();

    @Override
    public final Class<? extends Annotation> getAnnotationType() {
        return this.getAnnotation().annotationType();
    }
}

