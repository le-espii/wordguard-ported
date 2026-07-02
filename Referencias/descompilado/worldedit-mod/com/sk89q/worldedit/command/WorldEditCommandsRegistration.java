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
import com.sk89q.worldedit.command.WorldEditCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.HookMode;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
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
import org.enginehub.piston.part.NoArgCommandFlag;

public final class WorldEditCommandsRegistration
implements CommandRegistration<WorldEditCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<HookMode> hookMode_Key = Key.of(HookMode.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private CommandManager commandManager;
    private WorldEditCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final NoArgCommandFlag pastebinPart = CommandParts.flag('p', TextComponent.of("Pastebins the report")).build();
    private final CommandArgument hookModePart = CommandParts.arg(TranslatableComponent.of("hookMode"), TextComponent.of("The mode to set the trace hook to")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(hookMode_Key)).build();
    private final CommandArgument timezonePart = CommandParts.arg(TranslatableComponent.of("timezone"), TextComponent.of("The timezone to set")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final NoArgCommandFlag listSubCommandsPart = CommandParts.flag('s', TextComponent.of("List sub-commands of the given command, if applicable")).build();
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("The page to retrieve")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument commandPart = CommandParts.arg(TranslatableComponent.of("command"), TextComponent.of("The command to retrieve help for")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();

    private WorldEditCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static WorldEditCommandsRegistration builder() {
        return new WorldEditCommandsRegistration();
    }

    public WorldEditCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public WorldEditCommandsRegistration containerInstance(WorldEditCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public WorldEditCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public WorldEditCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("version", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"ver"));
            b.description(TextComponent.of("Get WorldEdit version"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$version);
        });
        this.commandManager.register("reload", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Reload configuration"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$reload);
            Method commandMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "reload", Actor.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("report", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Writes a report on WorldEdit"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.pastebinPart));
            b.action(this::cmd$report);
            Method commandMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "report", Actor.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("trace", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Toggles trace hook"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.hookModePart));
            b.action(this::cmd$trace);
        });
        this.commandManager.register("cui", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Complete CUI handshake (internal usage)"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$cui);
        });
        this.commandManager.register("tz", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set your timezone for snapshots"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.timezonePart));
            b.action(this::cmd$tz);
        });
        this.commandManager.register("help", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Displays help for WorldEdit commands"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.listSubCommandsPart, (Object)this.pagePart, (Object)this.commandPart));
            b.action(this::cmd$help);
            Method commandMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "help", Actor.class, Boolean.TYPE, Integer.TYPE, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$version(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "version", Actor.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.version(this.extract$actor(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$reload(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "reload", Actor.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.reload(this.extract$actor(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$report(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "report", Actor.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.report(this.extract$actor(parameters), this.extract$pastebin(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$trace(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "trace", Actor.class, LocalSession.class, HookMode.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.trace(this.extract$actor(parameters), this.extract$session(parameters), this.extract$hookMode(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$cui(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "cui", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.cui(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$tz(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "tz", Actor.class, LocalSession.class, String.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.tz(this.extract$actor(parameters), this.extract$session(parameters), this.extract$timezone(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$help(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(WorldEditCommands.class, "help", Actor.class, Boolean.TYPE, Integer.TYPE, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.help(this.extract$actor(parameters), this.extract$listSubCommands(parameters), this.extract$page(parameters), this.extract$command(parameters));
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

    private boolean extract$pastebin(CommandParameters parameters) {
        return this.pastebinPart.in(parameters);
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private HookMode extract$hookMode(CommandParameters parameters) {
        return this.hookModePart.value(parameters).asSingle(hookMode_Key);
    }

    private Player extract$player(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(player_Key, "player", parameters.injectedValue(player_Key));
    }

    private String extract$timezone(CommandParameters parameters) {
        return this.timezonePart.value(parameters).asSingle(string_Key);
    }

    private boolean extract$listSubCommands(CommandParameters parameters) {
        return this.listSubCommandsPart.in(parameters);
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private List<String> extract$command(CommandParameters parameters) {
        return this.commandPart.value(parameters).asMultiple(string_Key);
    }
}

