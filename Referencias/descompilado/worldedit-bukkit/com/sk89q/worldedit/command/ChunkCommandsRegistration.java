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
import com.sk89q.worldedit.command.ChunkCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
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
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;

public final class ChunkCommandsRegistration
implements CommandRegistration<ChunkCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<ZonedDateTime> zonedDateTime_Key = Key.of(ZonedDateTime.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private ChunkCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("Page number.")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final ArgAcceptingCommandFlag beforeTimePart = CommandParts.flag('o', TextComponent.of("Only delete chunks older than the specified time.")).withRequiredArg().argNamed(TranslatableComponent.of("beforeTime")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(zonedDateTime_Key)).build();

    private ChunkCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ChunkCommandsRegistration builder() {
        return new ChunkCommandsRegistration();
    }

    public ChunkCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ChunkCommandsRegistration containerInstance(ChunkCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public ChunkCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public ChunkCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("chunkinfo", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Get information about the chunk you're inside"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$chunkinfo);
            Method commandMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "chunkInfo", Player.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("listchunks", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("List chunks that your selection includes"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.pagePart));
            b.action(this::cmd$listchunks);
            Method commandMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "listChunks", Actor.class, World.class, LocalSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("delchunks", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Delete chunks that your selection includes"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.beforeTimePart));
            b.action(this::cmd$delchunks);
            Method commandMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "deleteChunks", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$chunkinfo(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "chunkInfo", Player.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.chunkInfo(this.extract$player(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$listchunks(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "listChunks", Actor.class, World.class, LocalSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.listChunks(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$page(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$delchunks(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ChunkCommands.class, "deleteChunks", Actor.class, World.class, LocalSession.class, ZonedDateTime.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.deleteChunks(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$beforeTime(parameters));
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

    private Actor extract$actor(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(actor_Key, "actor", parameters.injectedValue(actor_Key));
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private ZonedDateTime extract$beforeTime(CommandParameters parameters) {
        return this.beforeTimePart.value(parameters).asSingle(zonedDateTime_Key);
    }
}

