/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston;

import com.google.common.collect.ImmutableSet;
import org.enginehub.piston.part.CommandPart;

public interface ArgBinding {
    public String getInput();

    default public boolean isExactMatch(CommandPart part) {
        return true;
    }

    public ImmutableSet<CommandPart> getParts();
}

