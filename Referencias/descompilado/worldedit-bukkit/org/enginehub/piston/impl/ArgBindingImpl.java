/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import org.enginehub.piston.ArgBinding;
import org.enginehub.piston.impl.AutoValue_ArgBindingImpl;
import org.enginehub.piston.part.CommandPart;

@AutoValue
abstract class ArgBindingImpl
implements ArgBinding {
    static Builder builder() {
        return new AutoValue_ArgBindingImpl.Builder();
    }

    ArgBindingImpl() {
    }

    @Override
    public abstract String getInput();

    abstract ImmutableMap<CommandPart, Boolean> getPartsMap();

    @Override
    public boolean isExactMatch(CommandPart part) {
        Boolean result = (Boolean)this.getPartsMap().get((Object)part);
        if (result == null) {
            throw new IllegalArgumentException("Part " + part + " is not in the parts map");
        }
        return result;
    }

    @Override
    public final ImmutableSet<CommandPart> getParts() {
        return this.getPartsMap().keySet();
    }

    @AutoValue.Builder
    static interface Builder {
        public Builder input(String var1);

        public Builder partsMap(Map<CommandPart, Boolean> var1);

        public ArgBindingImpl build();
    }
}

