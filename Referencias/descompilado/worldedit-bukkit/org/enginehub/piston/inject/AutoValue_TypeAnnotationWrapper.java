/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.enginehub.piston.inject;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import org.enginehub.piston.inject.TypeAnnotationWrapper;

final class AutoValue_TypeAnnotationWrapper
extends TypeAnnotationWrapper {
    private final Class<? extends Annotation> annotationType;

    AutoValue_TypeAnnotationWrapper(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("Null annotationType");
        }
        this.annotationType = annotationType;
    }

    @Override
    @Nonnull
    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    public String toString() {
        return "TypeAnnotationWrapper{annotationType=" + this.annotationType + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TypeAnnotationWrapper) {
            TypeAnnotationWrapper that = (TypeAnnotationWrapper)o;
            return this.annotationType.equals(that.getAnnotationType());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        return h$ ^= this.annotationType.hashCode();
    }
}

