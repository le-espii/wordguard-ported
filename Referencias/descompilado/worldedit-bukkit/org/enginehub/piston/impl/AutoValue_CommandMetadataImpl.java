/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.enginehub.piston.impl.CommandMetadataImpl;

final class AutoValue_CommandMetadataImpl
extends CommandMetadataImpl {
    private final String calledName;
    private final ImmutableList<String> arguments;

    private AutoValue_CommandMetadataImpl(String calledName, ImmutableList<String> arguments) {
        this.calledName = calledName;
        this.arguments = arguments;
    }

    @Override
    public String getCalledName() {
        return this.calledName;
    }

    @Override
    public ImmutableList<String> getArguments() {
        return this.arguments;
    }

    public String toString() {
        return "CommandMetadataImpl{calledName=" + this.calledName + ", arguments=" + this.arguments + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandMetadataImpl) {
            CommandMetadataImpl that = (CommandMetadataImpl)o;
            return this.calledName.equals(that.getCalledName()) && this.arguments.equals(that.getArguments());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.calledName.hashCode();
        h$ *= 1000003;
        return h$ ^= this.arguments.hashCode();
    }

    static final class Builder
    implements CommandMetadataImpl.Builder {
        private String calledName;
        private ImmutableList<String> arguments;

        Builder() {
        }

        @Override
        public CommandMetadataImpl.Builder calledName(String calledName) {
            if (calledName == null) {
                throw new NullPointerException("Null calledName");
            }
            this.calledName = calledName;
            return this;
        }

        @Override
        public CommandMetadataImpl.Builder arguments(Collection<String> arguments) {
            this.arguments = ImmutableList.copyOf(arguments);
            return this;
        }

        @Override
        public CommandMetadataImpl build() {
            if (this.calledName == null || this.arguments == null) {
                StringBuilder missing = new StringBuilder();
                if (this.calledName == null) {
                    missing.append(" calledName");
                }
                if (this.arguments == null) {
                    missing.append(" arguments");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandMetadataImpl(this.calledName, this.arguments);
        }
    }
}

