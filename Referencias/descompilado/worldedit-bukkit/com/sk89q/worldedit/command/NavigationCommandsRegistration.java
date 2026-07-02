/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.NavigationCommands;
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
import org.enginehub.piston.part.NoArgCommandFlag;

public final class NavigationCommandsRegistration
implements CommandRegistration<NavigationCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private CommandManager commandManager;
    private NavigationCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument levelsPart = CommandParts.arg(TranslatableComponent.of("levels"), TextComponent.of("# of levels to ascend")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument levelsPart2 = CommandParts.arg(TranslatableComponent.of("levels"), TextComponent.of("# of levels to descend")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument clearancePart = CommandParts.arg(TranslatableComponent.of("clearance"), TextComponent.of("# of blocks to leave above you")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag forceFlightPart = CommandParts.flag('f', TextComponent.of("Force using flight to keep you still")).build();
    private final NoArgCommandFlag forceGlassPart = CommandParts.flag('g', TextComponent.of("Force using glass to keep you still")).build();
    private final CommandArgument distancePart = CommandParts.arg(TranslatableComponent.of("distance"), TextComponent.of("Distance to go upwards")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();

    private NavigationCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static NavigationCommandsRegistration builder() {
        return new NavigationCommandsRegistration();
    }

    public NavigationCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public NavigationCommandsRegistration containerInstance(NavigationCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public NavigationCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public NavigationCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("unstuck", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"!"));
            b.description(TextComponent.of("Escape from being stuck inside a block"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$unstuck);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "unstuck", Player.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("ascend", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"asc"));
            b.description(TextComponent.of("Go up a floor"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.levelsPart));
            b.action(this::cmd$ascend);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "ascend", Player.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("descend", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"desc"));
            b.description(TextComponent.of("Go down a floor"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.levelsPart2));
            b.action(this::cmd$descend);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "descend", Player.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("ceil", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Go to the ceiling"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.clearancePart, (Object)this.forceFlightPart, (Object)this.forceGlassPart));
            b.action(this::cmd$ceil);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "ceiling", Player.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("thru", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Pass through walls"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$thru);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "thru", Player.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("jumpto", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"j"));
            b.description(TextComponent.of("Teleport to a location"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$jumpto);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "jumpTo", Player.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("up", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Go upwards some distance"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.distancePart, (Object)this.forceFlightPart, (Object)this.forceGlassPart));
            b.action(this::cmd$up);
            Method commandMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "up", Player.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$unstuck(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "unstuck", Player.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.unstuck(this.extract$player(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$ascend(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "ascend", Player.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.ascend(this.extract$player(parameters), this.extract$levels(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$descend(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "descend", Player.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.descend(this.extract$player(parameters), this.extract$levels2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$ceil(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "ceiling", Player.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.ceiling(this.extract$player(parameters), this.extract$clearance(parameters), this.extract$forceFlight(parameters), this.extract$forceGlass(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$thru(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "thru", Player.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.thru(this.extract$player(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$jumpto(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "jumpTo", Player.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.jumpTo(this.extract$player(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$up(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(NavigationCommands.class, "up", Player.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.up(this.extract$player(parameters), this.extract$distance(parameters), this.extract$forceFlight(parameters), this.extract$forceGlass(parameters));
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

    private int extract$levels(CommandParameters parameters) {
        return this.levelsPart.value(parameters).asSingle(integer_Key);
    }

    private int extract$levels2(CommandParameters parameters) {
        return this.levelsPart2.value(parameters).asSingle(integer_Key);
    }

    private int extract$clearance(CommandParameters parameters) {
        return this.clearancePart.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$forceFlight(CommandParameters parameters) {
        return this.forceFlightPart.in(parameters);
    }

    private boolean extract$forceGlass(CommandParameters parameters) {
        return this.forceGlassPart.in(parameters);
    }

    private int extract$distance(CommandParameters parameters) {
        return this.distancePart.value(parameters).asSingle(integer_Key);
    }
}

