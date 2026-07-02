/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.SnapshotCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.gen.CommandRegistration;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.internal.RegistrationUtil;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;

public final class SnapshotCommandsRegistration
implements CommandRegistration<SnapshotCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<ZonedDateTime> zonedDateTime_Key = Key.of(ZonedDateTime.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private SnapshotCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("Page of results to return")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument namePart = CommandParts.arg(TranslatableComponent.of("name"), TextComponent.of("Snapshot to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final CommandArgument indexPart = CommandParts.arg(TranslatableComponent.of("index"), TextComponent.of("The list ID to select")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument datePart = CommandParts.arg(TranslatableComponent.of("date"), TextComponent.of("The soonest date that may be used")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(zonedDateTime_Key)).build();

    private SnapshotCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static SnapshotCommandsRegistration builder() {
        return new SnapshotCommandsRegistration();
    }

    public SnapshotCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public SnapshotCommandsRegistration containerInstance(SnapshotCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public SnapshotCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public SnapshotCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("list", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("List snapshots"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.pagePart));
            b.action(this::cmd$list);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "list", Actor.class, World.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("use", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose a snapshot to use"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.namePart));
            b.action(this::cmd$use);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "use", Actor.class, World.class, LocalSession.class, String.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("sel", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose the snapshot based on the list id"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.indexPart));
            b.action(this::cmd$sel);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "sel", Actor.class, World.class, LocalSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("before", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose the nearest snapshot before a date"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.datePart));
            b.action(this::cmd$before);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "before", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("after", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose the nearest snapshot after a date"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.datePart));
            b.action(this::cmd$after);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "after", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$list(CommandParameters parameters) throws WorldEditException, IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "list", Actor.class, World.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.list(this.extract$actor(parameters), this.extract$world(parameters), this.extract$page(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$use(CommandParameters parameters) throws IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "use", Actor.class, World.class, LocalSession.class, String.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.use(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$name(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$sel(CommandParameters parameters) throws IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "sel", Actor.class, World.class, LocalSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.sel(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$index(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$before(CommandParameters parameters) throws IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "before", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.before(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$date(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$after(CommandParameters parameters) throws IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotCommands.class, "after", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.after(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$date(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private Actor extract$actor(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(actor_Key, "actor", parameters.injectedValue(actor_Key));
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private String extract$name(CommandParameters parameters) {
        return this.namePart.value(parameters).asSingle(string_Key);
    }

    private int extract$index(CommandParameters parameters) {
        return this.indexPart.value(parameters).asSingle(integer_Key);
    }

    private ZonedDateTime extract$date(CommandParameters parameters) {
        return this.datePart.value(parameters).asSingle(zonedDateTime_Key);
    }
}

