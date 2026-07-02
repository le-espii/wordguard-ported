/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.part;

import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.part.CommandPart;

public interface ArgConsumingCommandPart
extends CommandPart {
    public TranslatableComponent getArgumentName();
}

