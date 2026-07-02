/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.enginehub.piston.inject.InjectAnnotation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
@InjectAnnotation
public @interface Offset {
    public static final String FORWARD = "forward";
    public static final String ZERO = "0,0,0";
}

