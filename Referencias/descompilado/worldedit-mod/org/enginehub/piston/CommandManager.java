/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.converter.ArgumentConverterStore;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.exception.CommandExecutionException;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.suggestion.Suggestion;

public interface CommandManager
extends ArgumentConverterStore {
    public Command.Builder newCommand(String var1);

    public void register(Command var1);

    default public void register(String name, Consumer<Command.Builder> registrationProcess) {
        Command.Builder builder = this.newCommand(name);
        registrationProcess.accept(builder);
        this.register(builder.build());
    }

    default public void registerManager(CommandManager manager) {
        manager.getAllCommands().forEach(this::register);
        this.registerConverters(manager);
    }

    public Stream<Command> getAllCommands();

    default public boolean containsCommand(String name) {
        return this.getCommand(name).isPresent();
    }

    public Optional<Command> getCommand(String var1);

    public ImmutableSet<Suggestion> getSuggestions(InjectedValueAccess var1, List<String> var2);

    public CommandParseResult parse(InjectedValueAccess var1, List<String> var2);

    default public int execute(InjectedValueAccess context, List<String> args) {
        CommandParseResult parse = this.parse(context, args);
        try {
            return parse.getPrimaryCommand().getAction().run(parse.getParameters());
        }
        catch (CommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CommandExecutionException(e, parse.getExecutionPath());
        }
    }
}

