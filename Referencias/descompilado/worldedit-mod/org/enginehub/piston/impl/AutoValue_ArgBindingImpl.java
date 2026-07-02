/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.enginehub.piston.impl.ArgBindingImpl;
import org.enginehub.piston.part.CommandPart;

final class AutoValue_ArgBindingImpl
extends ArgBindingImpl {
    private final String input;
    private final ImmutableMap<CommandPart, Boolean> partsMap;

    private AutoValue_ArgBindingImpl(String input, ImmutableMap<CommandPart, Boolean> partsMap) {
        this.input = input;
        this.partsMap = partsMap;
    }

    @Override
    public String getInput() {
        return this.input;
    }

    @Override
    ImmutableMap<CommandPart, Boolean> getPartsMap() {
        return this.partsMap;
    }

    public String toString() {
        return "ArgBindingImpl{input=" + this.input + ", partsMap=" + this.partsMap + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArgBindingImpl) {
            ArgBindingImpl that = (ArgBindingImpl)o;
            return this.input.equals(that.getInput()) && this.partsMap.equals(that.getPartsMap());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.input.hashCode();
        h$ *= 1000003;
        return h$ ^= this.partsMap.hashCode();
    }

    static final class Builder
    implements ArgBindingImpl.Builder {
        private String input;
        private ImmutableMap<CommandPart, Boolean> partsMap;

        Builder() {
        }

        @Override
        public ArgBindingImpl.Builder input(String input) {
            if (input == null) {
                throw new NullPointerException("Null input");
            }
            this.input = input;
            return this;
        }

        @Override
        public ArgBindingImpl.Builder partsMap(Map<CommandPart, Boolean> partsMap) {
            this.partsMap = ImmutableMap.copyOf(partsMap);
            return this;
        }

        @Override
        public ArgBindingImpl build() {
            if (this.input == null || this.partsMap == null) {
                StringBuilder missing = new StringBuilder();
                if (this.input == null) {
                    missing.append(" input");
                }
                if (this.partsMap == null) {
                    missing.append(" partsMap");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_ArgBindingImpl(this.input, this.partsMap);
        }
    }
}

