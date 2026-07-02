/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.SnapshotUtilCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
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

public final class SnapshotUtilCommandsRegistration
implements CommandRegistration<SnapshotUtilCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private CommandManager commandManager;
    private SnapshotUtilCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument snapshotNamePart = CommandParts.arg(TranslatableComponent.of("snapshot"), TextComponent.of("The snapshot to restore")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();

    private SnapshotUtilCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static SnapshotUtilCommandsRegistration builder() {
        return new SnapshotUtilCommandsRegistration();
    }

    public SnapshotUtilCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public SnapshotUtilCommandsRegistration containerInstance(SnapshotUtilCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public SnapshotUtilCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public SnapshotUtilCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("restore", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/restore"));
            b.description(TextComponent.of("Restore the selection from a snapshot"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.snapshotNamePart));
            b.action(this::cmd$restore);
            Method commandMethod = RegistrationUtil.getCommandMethod(SnapshotUtilCommands.class, "restore", Actor.class, World.class, LocalSession.class, EditSession.class, String.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$restore(CommandParameters parameters) throws WorldEditException, IOException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(SnapshotUtilCommands.class, "restore", Actor.class, World.class, LocalSession.class, EditSession.class, String.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.restore(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$snapshotName(parameters));
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

    private EditSession extract$editSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(editSession_Key, "editSession", parameters.injectedValue(editSession_Key));
    }

    private String extract$snapshotName(CommandParameters parameters) {
        return this.snapshotNamePart.value(parameters).asSingle(string_Key);
    }
}

