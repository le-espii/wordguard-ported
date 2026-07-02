/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.reflect.TypeToken
 */
package org.enginehub.piston.inject;

import com.google.common.reflect.TypeToken;
import org.enginehub.piston.inject.AnnotationWrapper;
import org.enginehub.piston.inject.Key;

final class AutoValue_Key<T>
extends Key<T> {
    private final TypeToken<T> typeToken;
    private final AnnotationWrapper annotationWrapper;

    AutoValue_Key(TypeToken<T> typeToken, AnnotationWrapper annotationWrapper) {
        if (typeToken == null) {
            throw new NullPointerException("Null typeToken");
        }
        this.typeToken = typeToken;
        if (annotationWrapper == null) {
            throw new NullPointerException("Null annotationWrapper");
        }
        this.annotationWrapper = annotationWrapper;
    }

    @Override
    public TypeToken<T> getTypeToken() {
        return this.typeToken;
    }

    @Override
    AnnotationWrapper getAnnotationWrapper() {
        return this.annotationWrapper;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Key) {
            Key that = (Key)o;
            return this.typeToken.equals(that.getTypeToken()) && this.annotationWrapper.equals(that.getAnnotationWrapper());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.typeToken.hashCode();
        h$ *= 1000003;
        return h$ ^= this.annotationWrapper.hashCode();
    }
}

