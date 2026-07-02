/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.exception;

import com.google.common.collect.ImmutableList;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;

public class CommandExecutionException
extends CommandException {
    public CommandExecutionException(Throwable cause, ImmutableList<Command> commands) {
        super(cause, commands);
    }
}

