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
import com.sk89q.worldedit.command.ToolUtilCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
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

public final class ToolUtilCommandsRegistration
implements CommandRegistration<ToolUtilCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Boolean> boolean_Key = Key.of(Boolean.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private ToolUtilCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument superPickaxePart = CommandParts.arg(TranslatableComponent.of("superPickaxe"), TextComponent.of("The new super pickaxe state")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(boolean_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask to set")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument rangePart = CommandParts.arg(TranslatableComponent.of("range"), TextComponent.of("The range of the brush")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument sizePart = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The size of the brush")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument maskPart2 = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The trace mask to set")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();

    private ToolUtilCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ToolUtilCommandsRegistration builder() {
        return new ToolUtilCommandsRegistration();
    }

    public ToolUtilCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ToolUtilCommandsRegistration containerInstance(ToolUtilCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public ToolUtilCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public ToolUtilCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)","));
            b.description(TextComponent.of("Toggle the super pickaxe function"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.superPickaxePart));
            b.action(this::cmd$_);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "togglePickaxe", Player.class, LocalSession.class, Boolean.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("mask", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set the brush mask"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.maskPart));
            b.action(this::cmd$mask);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "mask", Player.class, LocalSession.class, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("material", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/material"));
            b.description(TextComponent.of("Set the brush material"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$material);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "material", Player.class, LocalSession.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("range", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set the brush range"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.rangePart));
            b.action(this::cmd$range);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "range", Player.class, LocalSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("size", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set the brush size"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart));
            b.action(this::cmd$size);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "size", Player.class, LocalSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("tracemask", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set the mask used to stop tool traces"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.maskPart2));
            b.action(this::cmd$tracemask);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "traceMask", Player.class, LocalSession.class, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "togglePickaxe", Player.class, LocalSession.class, Boolean.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.togglePickaxe(this.extract$player(parameters), this.extract$session(parameters), this.extract$superPickaxe(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$mask(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "mask", Player.class, LocalSession.class, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.mask(this.extract$player(parameters), this.extract$session(parameters), this.extract$mask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$material(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "material", Player.class, LocalSession.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.material(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$range(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "range", Player.class, LocalSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.range(this.extract$player(parameters), this.extract$session(parameters), this.extract$range(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$size(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "size", Player.class, LocalSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.size(this.extract$player(parameters), this.extract$session(parameters), this.extract$size(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$tracemask(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolUtilCommands.class, "traceMask", Player.class, LocalSession.class, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.traceMask(this.extract$player(parameters), this.extract$session(parameters), this.extract$mask2(parameters));
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

    private Boolean extract$superPickaxe(CommandParameters parameters) {
        return this.superPickaxePart.value(parameters).asSingle(boolean_Key);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }

    private int extract$range(CommandParameters parameters) {
        return this.rangePart.value(parameters).asSingle(integer_Key);
    }

    private int extract$size(CommandParameters parameters) {
        return this.sizePart.value(parameters).asSingle(integer_Key);
    }

    private Mask extract$mask2(CommandParameters parameters) {
        return this.maskPart2.value(parameters).asSingle(mask_Key);
    }
}

