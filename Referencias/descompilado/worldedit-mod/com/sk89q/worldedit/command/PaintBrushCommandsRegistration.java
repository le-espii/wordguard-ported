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
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.command.PaintBrushCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.Direction;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.lang.annotation.Annotation;
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

public final class PaintBrushCommandsRegistration
implements CommandRegistration<PaintBrushCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<TreeGenerator.TreeType> treeType_Key = Key.of(TreeGenerator.TreeType.class);
    private static final Key<BaseItem> baseItem_Key = Key.of(BaseItem.class);
    private static final Key<com.sk89q.worldedit.util.Direction> direction_directionIncludeDiagonalstrue_Key = Key.of(com.sk89q.worldedit.util.Direction.class, new Object(){

        Annotation a(@Direction(includeDiagonals=true) Object ah) {
            try {
                return this.getClass().getDeclaredMethod("a", Object.class).getParameterAnnotations()[0][0];
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }.a(null));
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private PaintBrushCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument typePart = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("The type of tree to plant")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(treeType_Key)).build();
    private final CommandArgument itemPart = CommandParts.arg(TranslatableComponent.of("item"), TextComponent.of("The type of item to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(baseItem_Key)).build();
    private final CommandArgument directionPart = CommandParts.arg(TranslatableComponent.of("direction"), TextComponent.of("The direction in which the item will be applied")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"up")).ofTypes((Collection<Key<?>>)ImmutableList.of(direction_directionIncludeDiagonalstrue_Key)).build();
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();

    private PaintBrushCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static PaintBrushCommandsRegistration builder() {
        return new PaintBrushCommandsRegistration();
    }

    public PaintBrushCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public PaintBrushCommandsRegistration containerInstance(PaintBrushCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public PaintBrushCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public PaintBrushCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("forest", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Plant trees"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.typePart));
            b.action(this::cmd$forest);
        });
        this.commandManager.register("item", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Use an item"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.itemPart, (Object)this.directionPart));
            b.action(this::cmd$item);
            Method commandMethod = RegistrationUtil.getCommandMethod(PaintBrushCommands.class, "item", CommandParameters.class, Player.class, LocalSession.class, BaseItem.class, com.sk89q.worldedit.util.Direction.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("set", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Place a block"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$set);
        });
    }

    private int cmd$forest(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(PaintBrushCommands.class, "forest", CommandParameters.class, Player.class, LocalSession.class, TreeGenerator.TreeType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.forest(this.extract$parameters(parameters), this.extract$player(parameters), this.extract$localSession(parameters), this.extract$type(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$item(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(PaintBrushCommands.class, "item", CommandParameters.class, Player.class, LocalSession.class, BaseItem.class, com.sk89q.worldedit.util.Direction.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.item(this.extract$parameters(parameters), this.extract$player(parameters), this.extract$localSession(parameters), this.extract$item(parameters), this.extract$direction(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$set(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(PaintBrushCommands.class, "set", CommandParameters.class, Player.class, LocalSession.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.set(this.extract$parameters(parameters), this.extract$player(parameters), this.extract$localSession(parameters), this.extract$pattern(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private CommandParameters extract$parameters(CommandParameters parameters) {
        return parameters;
    }

    private Player extract$player(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(player_Key, "player", parameters.injectedValue(player_Key));
    }

    private LocalSession extract$localSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "localSession", parameters.injectedValue(localSession_Key));
    }

    private TreeGenerator.TreeType extract$type(CommandParameters parameters) {
        return this.typePart.value(parameters).asSingle(treeType_Key);
    }

    private BaseItem extract$item(CommandParameters parameters) {
        return this.itemPart.value(parameters).asSingle(baseItem_Key);
    }

    private com.sk89q.worldedit.util.Direction extract$direction(CommandParameters parameters) {
        return this.directionPart.value(parameters).asSingle(direction_directionIncludeDiagonalstrue_Key);
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }
}

