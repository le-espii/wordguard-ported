/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.enginehub.piston.inject;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import org.enginehub.piston.inject.InstanceAnnotationWrapper;

final class AutoValue_InstanceAnnotationWrapper
extends InstanceAnnotationWrapper {
    private final Annotation annotation;

    AutoValue_InstanceAnnotationWrapper(Annotation annotation) {
        if (annotation == null) {
            throw new NullPointerException("Null annotation");
        }
        this.annotation = annotation;
    }

    @Override
    @Nonnull
    public Annotation getAnnotation() {
        return this.annotation;
    }

    public String toString() {
        return "InstanceAnnotationWrapper{annotation=" + this.annotation + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof InstanceAnnotationWrapper) {
            InstanceAnnotationWrapper that = (InstanceAnnotationWrapper)o;
            return this.annotation.equals(that.getAnnotation());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        return h$ ^= this.annotation.hashCode();
    }
}

