/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package org.enginehub.piston.exception;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;

public class ConditionFailedException
extends CommandException {
    public ConditionFailedException(ImmutableList<Command> commands) {
        super(TextComponent.of("Condition failed: " + ((Command)Iterables.getLast(commands)).getCondition()), commands);
    }

    public Command.Condition getCondition() {
        return ((Command)Iterables.getLast((Iterable)this.commands)).getCondition();
    }
}

