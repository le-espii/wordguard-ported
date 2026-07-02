/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.part;

import org.enginehub.piston.part.CommandPart;

public interface CommandFlag
extends CommandPart {
    public char getName();

    @Override
    default public boolean isRequired() {
        return false;
    }
}

