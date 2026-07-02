/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.inject;

import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import org.enginehub.piston.inject.AnnotationWrapper;

final class NullAnnotationWrapper
implements AnnotationWrapper {
    private static final NullAnnotationWrapper INSTANCE = new NullAnnotationWrapper();

    static NullAnnotationWrapper getInstance() {
        return INSTANCE;
    }

    private NullAnnotationWrapper() {
    }

    @Override
    @Nullable
    public Annotation getAnnotation() {
        return null;
    }

    @Override
    @Nullable
    public Class<? extends Annotation> getAnnotationType() {
        return null;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

