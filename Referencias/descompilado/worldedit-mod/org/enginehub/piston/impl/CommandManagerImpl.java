/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.common.reflect.TypeToken
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverters;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.exception.NoSuchCommandException;
import org.enginehub.piston.exception.UsageException;
import org.enginehub.piston.impl.CommandImpl;
import org.enginehub.piston.impl.CommandInfoCache;
import org.enginehub.piston.impl.CommandMetadataImpl;
import org.enginehub.piston.impl.CommandParser;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MemoizingValueAccess;
import org.enginehub.piston.part.SubCommandPart;
import org.enginehub.piston.suggestion.Suggestion;

public class CommandManagerImpl
implements CommandManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Command> commands = new HashMap<String, Command>();
    private final Map<Key<?>, ArgumentConverter<?>> converters = new HashMap();
    private final CommandInfoCache commandInfoCache = new CommandInfoCache();

    public CommandManagerImpl() {
        this.registerConverter(Key.of(String.class), ArgumentConverters.forString());
        UnmodifiableIterator unmodifiableIterator = ImmutableList.of(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Boolean.class).iterator();
        while (unmodifiableIterator.hasNext()) {
            Class wrapperType;
            Class fake = wrapperType = (Class)unmodifiableIterator.next();
            this.registerConverter(Key.of(fake), ArgumentConverters.get(TypeToken.of((Class)fake)));
        }
    }

    @Override
    public Command.Builder newCommand(String name) {
        return CommandImpl.builder(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void register(Command command) {
        this.validateAndCache(command, new HashSet<Command>());
        this.lock.writeLock().lock();
        try {
            this.registerIfAvailable(command.getName(), command);
            for (String alias : command.getAliases()) {
                this.registerIfAvailable(alias, command);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private void validateAndCache(Command command, Set<Command> seen) {
        if (!seen.add(command)) {
            throw new IllegalStateException("Self-referential command");
        }
        this.commandInfoCache.getInfo(command);
        command.getParts().stream().filter(p -> p instanceof SubCommandPart).flatMap(p -> ((SubCommandPart)p).getCommands().stream()).forEach(c -> this.validateAndCache((Command)c, seen));
    }

    private void registerIfAvailable(String name, Command command) {
        Command existing = this.commands.put(name, command);
        if (existing != null) {
            this.commands.put(name, existing);
            throw new IllegalArgumentException("A command is already registered under " + name + "; existing=" + existing + ",rejected=" + command);
        }
    }

    @Override
    public <T> void registerConverter(Key<T> key, ArgumentConverter<T> converter) {
        this.lock.writeLock().lock();
        try {
            this.converters.put(key, converter);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public Set<Key<?>> keySet() {
        this.lock.readLock().lock();
        try {
            Set<Key<?>> set = this.converters.keySet();
            return set;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public <T> Optional<ArgumentConverter<T>> getConverter(Key<T> key) {
        ArgumentConverter<?> converter = this.getArgumentConverter(key);
        return Optional.ofNullable(converter);
    }

    @Nullable
    private <T> ArgumentConverter<?> getArgumentConverter(Key<T> key) {
        this.lock.readLock().lock();
        try {
            ArgumentConverter<?> argumentConverter = this.converters.get(key);
            return argumentConverter;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Stream<Command> getAllCommands() {
        ImmutableSet allCommands;
        this.lock.readLock().lock();
        try {
            allCommands = ImmutableSet.copyOf(this.commands.values());
        }
        finally {
            this.lock.readLock().unlock();
        }
        return allCommands.stream();
    }

    @Override
    public Optional<Command> getCommand(String name) {
        this.lock.readLock().lock();
        try {
            Optional<Command> optional = Optional.ofNullable(this.commands.get(name));
            return optional;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImmutableSet<Suggestion> getSuggestions(InjectedValueAccess context, List<String> args) {
        CommandParseResult parseResult;
        Command command;
        this.lock.readLock().lock();
        try {
            String name = args.get(0);
            command = this.commands.get(name);
            if (command == null) {
                ImmutableSet<Suggestion> immutableSet = this.suggestCommands(context, name);
                return immutableSet;
            }
            try {
                parseResult = this.parse(context, args);
            }
            catch (UsageException e) {
                parseResult = e.getCommandParseResult();
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        List<String> reconstructedArguments = parseResult.getOriginalArguments();
        Preconditions.checkState((reconstructedArguments.size() <= args.size() ? 1 : 0) != 0, (Object)"Reconstructed arguments list bigger than original args list");
        return ImmutableSet.copyOf(command.getSuggester().provideSuggestions(args.subList(1, args.size()), parseResult).stream().map(s -> s.toBuilder().replacedArgument(s.getReplacedArgument() + 1).build()).iterator());
    }

    private ImmutableSet<Suggestion> suggestCommands(InjectedValueAccess context, String name) {
        Predicate<String> nameFilter = SuggestionHelper.byPrefix(name);
        return ImmutableSet.copyOf(this.getAllCommands().filter(c -> nameFilter.test(c.getName()) && c.getCondition().satisfied(context)).map(Command::getName).map(s -> Suggestion.builder().suggestion((String)s).replacedArgument(0).build()).iterator());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CommandParseResult parse(InjectedValueAccess context, List<String> args) {
        this.lock.readLock().lock();
        try {
            String name = args.get(0);
            Command command = this.commands.get(name);
            if (command == null) {
                throw new NoSuchCommandException(name);
            }
            MemoizingValueAccess cachedContext = MemoizingValueAccess.wrap(context);
            CommandMetadataImpl metadata = CommandMetadataImpl.builder().calledName(name).arguments((Collection<String>)ImmutableList.copyOf(args.subList(1, args.size()))).build();
            CommandParseResult commandParseResult = new CommandParser(this, this.commandInfoCache, command, metadata, cachedContext).parse();
            return commandParseResult;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}

