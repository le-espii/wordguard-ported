/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.exception;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;

public class NoSuchCommandException
extends CommandException {
    private final String requestedCommand;

    public NoSuchCommandException(String requestedCommand) {
        super(TextComponent.of("No such command: " + requestedCommand), (ImmutableList<Command>)ImmutableList.of());
        this.requestedCommand = requestedCommand;
    }

    public String getRequestedCommand() {
        return this.requestedCommand;
    }
}

