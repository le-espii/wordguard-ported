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
import com.sk89q.worldedit.command.ScriptingCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
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
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;

public final class ScriptingCommandsRegistration
implements CommandRegistration<ScriptingCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private ScriptingCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument filenamePart = CommandParts.arg(TranslatableComponent.of("filename"), TextComponent.of("Filename of the CraftScript to load")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final CommandArgument argsPart = CommandParts.arg(TranslatableComponent.of("args"), TextComponent.of("Arguments to the CraftScript")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();

    private ScriptingCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ScriptingCommandsRegistration builder() {
        return new ScriptingCommandsRegistration();
    }

    public ScriptingCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ScriptingCommandsRegistration containerInstance(ScriptingCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public ScriptingCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public ScriptingCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("cs", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Execute a CraftScript"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.filenamePart, (Object)this.argsPart));
            b.action(this::cmd$cs);
            Method commandMethod = RegistrationUtil.getCommandMethod(ScriptingCommands.class, "execute", Player.class, LocalSession.class, String.class, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register(".s", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Execute last CraftScript"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.argsPart));
            b.action(this::cmd$_s);
            Method commandMethod = RegistrationUtil.getCommandMethod(ScriptingCommands.class, "executeLast", Player.class, LocalSession.class, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$cs(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ScriptingCommands.class, "execute", Player.class, LocalSession.class, String.class, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.execute(this.extract$player(parameters), this.extract$session(parameters), this.extract$filename(parameters), this.extract$args(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_s(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ScriptingCommands.class, "executeLast", Player.class, LocalSession.class, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.executeLast(this.extract$player(parameters), this.extract$session(parameters), this.extract$args(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private Player extract$player(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(player_Key, "player", parameters.injectedValue(player_Key));
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private String extract$filename(CommandParameters parameters) {
        return this.filenamePart.value(parameters).asSingle(string_Key);
    }

    private List<String> extract$args(CommandParameters parameters) {
        return this.argsPart.value(parameters).asMultiple(string_Key);
    }
}

