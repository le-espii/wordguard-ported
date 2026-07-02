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
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.stream.Collectors;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.exception.UsageException;
import org.enginehub.piston.part.CommandFlag;
import org.enginehub.piston.util.HelpGenerator;
import org.enginehub.piston.util.StreamHelper;

public class NoSuchFlagException
extends UsageException {
    private final char requestedFlag;

    private static String getAllFlags(ImmutableList<Command> commands) {
        return StreamHelper.cast(((Command)Iterables.getLast(commands)).getParts().stream(), CommandFlag.class).map(f -> String.valueOf(f.getName())).collect(Collectors.joining());
    }

    private static Component getMessage(CommandParseResult parseResult, char requestedFlag) {
        TextComponent.Builder message = TextComponent.builder("");
        ((TextComponent.Builder)((TextComponent.Builder)message.append((Component)TextComponent.of("Flag '"))).append(ColorConfig.mainText().wrap(String.valueOf(requestedFlag)))).append((Component)TextComponent.of("' is not a valid flag for "));
        message.append(HelpGenerator.create(parseResult).getFullName());
        String allFlags = NoSuchFlagException.getAllFlags(parseResult.getExecutionPath());
        if (allFlags.isEmpty()) {
            message.append((Component)TextComponent.of(", as it does not have any flags"));
        } else {
            ((TextComponent.Builder)message.append((Component)TextComponent.of(". Options: "))).append(ColorConfig.mainText().wrap(allFlags));
        }
        return message.build();
    }

    public NoSuchFlagException(CommandParseResult parseResult, char requestedFlag) {
        super(NoSuchFlagException.getMessage(parseResult, requestedFlag), parseResult);
        this.requestedFlag = requestedFlag;
    }

    public char getRequestedFlag() {
        return this.requestedFlag;
    }
}

