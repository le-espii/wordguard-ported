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
import com.sk89q.worldedit.command.ExpandCommands;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.annotation.MultiDirection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.gen.CommandRegistration;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.internal.RegistrationUtil;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;

public final class ExpandCommandsRegistration
implements CommandRegistration<ExpandCommands> {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<BlockVector3> blockVector3_multiDirection_Key = Key.of(BlockVector3.class, MultiDirection.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private ExpandCommands containerInstance;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument amountPart = CommandParts.arg(TranslatableComponent.of("amount"), TextComponent.of("Amount to expand the selection by, can be `vert` to expand to the whole vertical column")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument reverseAmountPart = CommandParts.arg(TranslatableComponent.of("reverseAmount"), TextComponent.of("Amount to expand the selection by in the other direction")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument directionPart = CommandParts.arg(TranslatableComponent.of("direction"), TextComponent.of("Direction to expand")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"me")).ofTypes((Collection<Key<?>>)ImmutableList.of(blockVector3_multiDirection_Key)).build();

    private ExpandCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ExpandCommandsRegistration builder() {
        return new ExpandCommandsRegistration();
    }

    public ExpandCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ExpandCommandsRegistration containerInstance(ExpandCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    public ExpandCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/expand", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Expand the selection area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.amountPart, (Object)this.reverseAmountPart, (Object)this.directionPart));
            b.action(this::cmd$_expand);
        });
    }

    private int cmd$_expand(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ExpandCommands.class, "expand", Actor.class, World.class, LocalSession.class, Integer.TYPE, Integer.TYPE, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.expand(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$amount(parameters), this.extract$reverseAmount(parameters), this.extract$direction(parameters));
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

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private int extract$amount(CommandParameters parameters) {
        return this.amountPart.value(parameters).asSingle(integer_Key);
    }

    private int extract$reverseAmount(CommandParameters parameters) {
        return this.reverseAmountPart.value(parameters).asSingle(integer_Key);
    }

    private List<BlockVector3> extract$direction(CommandParameters parameters) {
        return this.directionPart.value(parameters).asMultiple(blockVector3_multiDirection_Key);
    }
}

