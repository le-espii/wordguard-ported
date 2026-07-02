/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.meta.TypeQualifierDefault
 */
package org.enginehub.piston.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

@Documented
@Nonnull
@TypeQualifierDefault(value={ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface NonnullByDefault {
}

