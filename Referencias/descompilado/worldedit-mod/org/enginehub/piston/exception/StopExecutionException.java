/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.exception;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;

public class StopExecutionException
extends CommandException {
    public StopExecutionException(Component message) {
        this(message, (ImmutableList<Command>)ImmutableList.of());
    }

    public StopExecutionException(Component message, ImmutableList<Command> commands) {
        super(message, commands);
    }
}

