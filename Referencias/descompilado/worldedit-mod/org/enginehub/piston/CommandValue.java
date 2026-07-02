/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package org.enginehub.piston;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import org.enginehub.piston.inject.Key;

public interface CommandValue {
    public ImmutableList<String> asStrings();

    default public String asString() {
        ImmutableList<String> values = this.asStrings();
        Preconditions.checkState((values.size() > 0 ? 1 : 0) != 0, (Object)"No value present");
        Preconditions.checkState((values.size() == 1 ? 1 : 0) != 0, (Object)"Too many values present");
        return (String)values.get(0);
    }

    default public <T> ImmutableList<T> asMultiple(Class<T> key) {
        return this.asMultiple(Key.of(key));
    }

    public <T> ImmutableList<T> asMultiple(Key<T> var1);

    @Nullable
    default public <T> T asSingle(Class<T> key) {
        return this.asSingle(Key.of(key));
    }

    @Nullable
    default public <T> T asSingle(Key<T> key) {
        ImmutableList<T> values = this.asMultiple(key);
        if (values.size() == 0 && this.asStrings().size() > 0) {
            return null;
        }
        Preconditions.checkState((values.size() > 0 ? 1 : 0) != 0, (Object)"No value present");
        Preconditions.checkState((values.size() == 1 ? 1 : 0) != 0, (Object)"Too many values present");
        return (T)values.get(0);
    }
}

