/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.impl.AutoValue_CommandMetadataImpl;

@AutoValue
abstract class CommandMetadataImpl
implements CommandMetadata {
    static Builder builder() {
        return new AutoValue_CommandMetadataImpl.Builder();
    }

    CommandMetadataImpl() {
    }

    @Override
    public abstract String getCalledName();

    @Override
    public abstract ImmutableList<String> getArguments();

    @AutoValue.Builder
    static interface Builder {
        public Builder calledName(String var1);

        public Builder arguments(Collection<String> var1);

        public CommandMetadataImpl build();
    }
}

