/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.inject;

import java.lang.annotation.Annotation;
import javax.annotation.Nullable;

interface AnnotationWrapper {
    @Nullable
    public Annotation getAnnotation();

    @Nullable
    public Class<? extends Annotation> getAnnotationType();
}

