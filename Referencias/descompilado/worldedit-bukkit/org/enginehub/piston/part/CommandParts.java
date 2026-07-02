/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.part;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.NoArgCommandFlag;

public class CommandParts {
    public static NoArgCommandFlag.Builder flag(char flag, Component description) {
        return NoArgCommandFlag.builder(flag, description);
    }

    public static CommandArgument.Builder arg(TranslatableComponent name, Component description) {
        return CommandArgument.builder(name, description);
    }

    private CommandParts() {
    }
}

