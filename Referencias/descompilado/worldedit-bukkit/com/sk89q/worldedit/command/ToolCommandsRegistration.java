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
import com.sk89q.worldedit.command.ToolCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.util.TreeGenerator;
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

public final class ToolCommandsRegistration
implements CommandRegistration<ToolCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<TreeGenerator.TreeType> treeType_Key = Key.of(TreeGenerator.TreeType.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private ToolCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument typePart = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("Type of tree to generate")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"tree")).ofTypes((Collection<Key<?>>)ImmutableList.of(treeType_Key)).build();
    private final CommandArgument rangePart = CommandParts.arg(TranslatableComponent.of("range"), TextComponent.of("The max range of the stack")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask to stack until")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"!#existing")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to place")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument patternPart2 = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern to flood fill")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument rangePart2 = CommandParts.arg(TranslatableComponent.of("range"), TextComponent.of("The range to perform the fill")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument primaryPart = CommandParts.arg(TranslatableComponent.of("primary"), TextComponent.of("Pattern to set on left-click")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument secondaryPart = CommandParts.arg(TranslatableComponent.of("secondary"), TextComponent.of("Pattern to set on right-click")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();

    private ToolCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ToolCommandsRegistration builder() {
        return new ToolCommandsRegistration();
    }

    public ToolCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ToolCommandsRegistration containerInstance(ToolCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public ToolCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public ToolCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("none", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"unbind"));
            b.description(TextComponent.of("Unbind a bound tool from your current item"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$none);
        });
        this.commandManager.register("selwand", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/selwand"));
            b.description(TextComponent.of("Selection wand tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$selwand);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "selwand", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("navwand", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/navwand"));
            b.description(TextComponent.of("Navigation wand tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$navwand);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "navwand", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("info", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Block information tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$info);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "info", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("tree", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Tree generator tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.typePart));
            b.action(this::cmd$tree);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "tree", Player.class, LocalSession.class, TreeGenerator.TreeType.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("stacker", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Block stacker tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.rangePart, (Object)this.maskPart));
            b.action(this::cmd$stacker);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "stacker", Player.class, LocalSession.class, Integer.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("repl", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Block replacer tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$repl);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "repl", Player.class, LocalSession.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("cycler", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Block data cycler tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$cycler);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "cycler", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("floodfill", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"flood"));
            b.description(TextComponent.of("Flood fill tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.rangePart2));
            b.action(this::cmd$floodfill);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "floodFill", Player.class, LocalSession.class, Pattern.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("deltree", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Floating tree remover tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$deltree);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "deltree", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("farwand", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Wand at a distance tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$farwand);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "farwand", Player.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("lrbuild", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/lrbuild"));
            b.description(TextComponent.of("Long-range building tool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.primaryPart, (Object)this.secondaryPart));
            b.action(this::cmd$lrbuild);
            Method commandMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "longrangebuildtool", Player.class, LocalSession.class, Pattern.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$none(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "none", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.none(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$selwand(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "selwand", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.selwand(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$navwand(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "navwand", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.navwand(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$info(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "info", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.info(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$tree(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "tree", Player.class, LocalSession.class, TreeGenerator.TreeType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.tree(this.extract$player(parameters), this.extract$session(parameters), this.extract$type(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$stacker(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "stacker", Player.class, LocalSession.class, Integer.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.stacker(this.extract$player(parameters), this.extract$session(parameters), this.extract$range(parameters), this.extract$mask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$repl(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "repl", Player.class, LocalSession.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.repl(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$cycler(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "cycler", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.cycler(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$floodfill(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "floodFill", Player.class, LocalSession.class, Pattern.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.floodFill(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern2(parameters), this.extract$range2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$deltree(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "deltree", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.deltree(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$farwand(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "farwand", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.farwand(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$lrbuild(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ToolCommands.class, "longrangebuildtool", Player.class, LocalSession.class, Pattern.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.longrangebuildtool(this.extract$player(parameters), this.extract$session(parameters), this.extract$primary(parameters), this.extract$secondary(parameters));
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

    private TreeGenerator.TreeType extract$type(CommandParameters parameters) {
        return this.typePart.value(parameters).asSingle(treeType_Key);
    }

    private int extract$range(CommandParameters parameters) {
        return this.rangePart.value(parameters).asSingle(integer_Key);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }

    private Pattern extract$pattern2(CommandParameters parameters) {
        return this.patternPart2.value(parameters).asSingle(pattern_Key);
    }

    private int extract$range2(CommandParameters parameters) {
        return this.rangePart2.value(parameters).asSingle(integer_Key);
    }

    private Pattern extract$primary(CommandParameters parameters) {
        return this.primaryPart.value(parameters).asSingle(pattern_Key);
    }

    private Pattern extract$secondary(CommandParameters parameters) {
        return this.secondaryPart.value(parameters).asSingle(pattern_Key);
    }
}

