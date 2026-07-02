/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.part;

import com.sk89q.worldedit.util.formatting.text.Component;
import org.enginehub.piston.CommandParameters;

public interface CommandPart {
    default public boolean in(CommandParameters parameters) {
        return parameters.has(this);
    }

    public Component getTextRepresentation();

    public Component getDescription();

    public boolean isRequired();
}

