/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.exception;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import org.enginehub.piston.Command;
import org.enginehub.piston.util.TextHelper;

public class CommandException
extends RuntimeException {
    private final Component message;
    protected final ImmutableList<Command> commands;

    public CommandException(ImmutableList<Command> commands) {
        this.message = TextComponent.empty();
        this.commands = commands;
    }

    public CommandException(Component message, ImmutableList<Command> commands) {
        super(TextHelper.reduceToText(message));
        this.message = message;
        this.commands = commands;
    }

    public CommandException(Component message, Throwable cause, ImmutableList<Command> commands) {
        super(TextHelper.reduceToText(message), cause);
        this.message = message;
        this.commands = commands;
    }

    public CommandException(Throwable cause, ImmutableList<Command> commands) {
        super(cause);
        this.message = TextComponent.empty();
        this.commands = commands;
    }

    public Component getRichMessage() {
        return this.message;
    }

    public ImmutableList<Command> getCommands() {
        return this.commands;
    }
}

