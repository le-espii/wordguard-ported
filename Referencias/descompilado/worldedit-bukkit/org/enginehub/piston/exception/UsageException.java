/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.exception;

import com.sk89q.worldedit.util.formatting.text.Component;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.exception.CommandException;

public class UsageException
extends CommandException {
    private final CommandParseResult commandParseResult;

    public UsageException(CommandParseResult commandParseResult) {
        super(commandParseResult.getExecutionPath());
        this.commandParseResult = commandParseResult;
    }

    public UsageException(Component message, CommandParseResult commandParseResult) {
        super(message, commandParseResult.getExecutionPath());
        this.commandParseResult = commandParseResult;
    }

    public UsageException(Component message, Throwable cause, CommandParseResult commandParseResult) {
        super(message, cause, commandParseResult.getExecutionPath());
        this.commandParseResult = commandParseResult;
    }

    public UsageException(Throwable cause, CommandParseResult commandParseResult) {
        super(cause, commandParseResult.getExecutionPath());
        this.commandParseResult = commandParseResult;
    }

    public CommandParseResult getCommandParseResult() {
        return this.commandParseResult;
    }
}

