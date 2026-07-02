/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NonAbstractForCompatibility {
    public String delegateName();

    public Class<?>[] delegateParams();
}

