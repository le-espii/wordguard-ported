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
import com.sk89q.worldedit.command.SuperPickaxeCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.lang.reflect.Method;
import java.util.Collection;
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

public final class SuperPickaxeCommandsRegistration
implements CommandRegistration<SuperPickaxeCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private SuperPickaxeCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument rangePart = CommandParts.arg(TranslatableComponent.of("range"), TextComponent.of("The range of the area pickaxe")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument rangePart2 = CommandParts.arg(TranslatableComponent.of("range"), TextComponent.of("The range of the recursive pickaxe")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();

    private SuperPickaxeCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static SuperPickaxeCommandsRegistration builder() {
        return new SuperPickaxeCommandsRegistration();
    }

    public SuperPickaxeCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public SuperPickaxeCommandsRegistration containerInstance(SuperPickaxeCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public SuperPickaxeCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public SuperPickaxeCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("single", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Enable the single block super pickaxe mode"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$single);
            Method commandMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "single", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("area", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Enable the area super pickaxe pickaxe mode"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.rangePart));
            b.action(this::cmd$area);
            Method commandMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "area", Player.class, LocalSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("recursive", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"recur"));
            b.description(TextComponent.of("Enable the recursive super pickaxe pickaxe mode"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.rangePart2));
            b.action(this::cmd$recursive);
            Method commandMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "recursive", Player.class, LocalSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$single(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "single", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.single(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$area(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "area", Player.class, LocalSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.area(this.extract$player(parameters), this.extract$session(parameters), this.extract$range(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$recursive(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SuperPickaxeCommands.class, "recursive", Player.class, LocalSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.recursive(this.extract$player(parameters), this.extract$session(parameters), this.extract$range2(parameters));
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

    private int extract$range(CommandParameters parameters) {
        return this.rangePart.value(parameters).asSingle(integer_Key);
    }

    private double extract$range2(CommandParameters parameters) {
        return this.rangePart2.value(parameters).asSingle(double_Key);
    }
}

