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
import com.sk89q.worldedit.command.BiomeCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.lang.reflect.Method;
import java.util.Collection;
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

public final class BiomeCommandsRegistration
implements CommandRegistration<BiomeCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<BiomeType> biomeType_Key = Key.of(BiomeType.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private CommandManager commandManager;
    private BiomeCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("Page number.")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag useLineOfSightPart = CommandParts.flag('t', TextComponent.of("Use the block you are looking at.")).build();
    private final NoArgCommandFlag usePositionPart = CommandParts.flag('p', TextComponent.of("Use the block you are currently in.")).build();
    private final CommandArgument targetPart = CommandParts.arg(TranslatableComponent.of("target"), TextComponent.of("Biome type.")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(biomeType_Key)).build();
    private final NoArgCommandFlag atPositionPart = CommandParts.flag('p', TextComponent.of("Use your current position")).build();

    private BiomeCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static BiomeCommandsRegistration builder() {
        return new BiomeCommandsRegistration();
    }

    public BiomeCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public BiomeCommandsRegistration containerInstance(BiomeCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public BiomeCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public BiomeCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("biomelist", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"biomels"));
            b.description(TextComponent.of("Gets all biomes available."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.pagePart));
            b.action(this::cmd$biomelist);
            Method commandMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "biomeList", Actor.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("biomeinfo", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Get the biome of the targeted block."));
            b.footer(TextComponent.of("By default, uses all blocks in your selection."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.useLineOfSightPart, (Object)this.usePositionPart));
            b.action(this::cmd$biomeinfo);
            Method commandMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "biomeInfo", Actor.class, World.class, LocalSession.class, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/setbiome", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Sets the biome of your current block or region."));
            b.footer(TextComponent.of("By default, uses all the blocks in your selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.targetPart, (Object)this.atPositionPart));
            b.action(this::cmd$_setbiome);
            Method commandMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "setBiome", Actor.class, World.class, LocalSession.class, EditSession.class, BiomeType.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$biomelist(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "biomeList", Actor.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.biomeList(this.extract$actor(parameters), this.extract$page(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$biomeinfo(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "biomeInfo", Actor.class, World.class, LocalSession.class, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.biomeInfo(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$useLineOfSight(parameters), this.extract$usePosition(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_setbiome(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BiomeCommands.class, "setBiome", Actor.class, World.class, LocalSession.class, EditSession.class, BiomeType.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.setBiome(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$target(parameters), this.extract$atPosition(parameters));
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

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private boolean extract$useLineOfSight(CommandParameters parameters) {
        return this.useLineOfSightPart.in(parameters);
    }

    private boolean extract$usePosition(CommandParameters parameters) {
        return this.usePositionPart.in(parameters);
    }

    private EditSession extract$editSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(editSession_Key, "editSession", parameters.injectedValue(editSession_Key));
    }

    private BiomeType extract$target(CommandParameters parameters) {
        return this.targetPart.value(parameters).asSingle(biomeType_Key);
    }

    private boolean extract$atPosition(CommandParameters parameters) {
        return this.atPositionPart.in(parameters);
    }
}

