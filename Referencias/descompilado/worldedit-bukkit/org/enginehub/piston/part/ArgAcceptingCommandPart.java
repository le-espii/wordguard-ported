/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.part;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.CommandValue;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.ArgConsumingCommandPart;

public interface ArgAcceptingCommandPart
extends ArgConsumingCommandPart {
    default public CommandValue value(CommandParameters parameters) {
        return parameters.valueOf(this);
    }

    public ImmutableSet<Key<?>> getTypes();

    public ImmutableList<String> getDefaults();
}

