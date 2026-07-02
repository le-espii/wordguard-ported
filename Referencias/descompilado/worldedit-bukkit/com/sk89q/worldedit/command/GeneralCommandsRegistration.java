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
import com.sk89q.worldedit.command.GeneralCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.HookMode;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.annotation.Offset;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.PlacementType;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
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
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;
import org.enginehub.piston.part.NoArgCommandFlag;

public final class GeneralCommandsRegistration
implements CommandRegistration<GeneralCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Boolean> boolean_Key = Key.of(Boolean.class);
    private static final Key<SideEffect> sideEffect_Key = Key.of(SideEffect.class);
    private static final Key<SideEffect.State> state_Key = Key.of(SideEffect.State.class);
    private static final Key<EditSession.ReorderMode> reorderMode_Key = Key.of(EditSession.ReorderMode.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<HookMode> hookMode_Key = Key.of(HookMode.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<PlacementType> placementType_Key = Key.of(PlacementType.class);
    private static final Key<BlockVector3> blockVector3_offset_Key = Key.of(BlockVector3.class, Offset.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private CommandManager commandManager;
    private GeneralCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument limitPart = CommandParts.arg(TranslatableComponent.of("limit"), TextComponent.of("The limit to set")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument limitPart2 = CommandParts.arg(TranslatableComponent.of("limit"), TextComponent.of("The timeout time to set")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument fastModePart = CommandParts.arg(TranslatableComponent.of("fastMode"), TextComponent.of("The new fast mode state")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(boolean_Key)).build();
    private final CommandArgument sideEffectPart = CommandParts.arg(TranslatableComponent.of("sideEffect"), TextComponent.of("The side effect")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(sideEffect_Key)).build();
    private final CommandArgument newStatePart = CommandParts.arg(TranslatableComponent.of("newState"), TextComponent.of("The new side effect state")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(state_Key)).build();
    private final NoArgCommandFlag showInfoBoxPart = CommandParts.flag('h', TextComponent.of("Show the info box")).build();
    private final CommandArgument reorderModePart = CommandParts.arg(TranslatableComponent.of("reorderMode"), TextComponent.of("The reorder mode")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(reorderMode_Key)).build();
    private final CommandArgument drawSelectionPart = CommandParts.arg(TranslatableComponent.of("drawSelection"), TextComponent.of("The new draw selection state")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(boolean_Key)).build();
    private final CommandArgument worldPart = CommandParts.arg(TranslatableComponent.of("world"), TextComponent.of("The world override")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(world_Key)).build();
    private final CommandArgument hookModePart = CommandParts.arg(TranslatableComponent.of("hookMode"), TextComponent.of("The mode to set the watchdog hook to")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(hookMode_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask to set")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument placementTypePart = CommandParts.arg(TranslatableComponent.of("placementType"), TextComponent.of("Which placement type to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(placementType_Key)).build();
    private final CommandArgument multiplierPart = CommandParts.arg(TranslatableComponent.of("multiplier"), TextComponent.of("number of times to apply the offset")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument offsetPart = CommandParts.arg(TranslatableComponent.of("offset"), TextComponent.of("How much to offset from it placement to use")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0,0,0")).ofTypes((Collection<Key<?>>)ImmutableList.of(blockVector3_offset_Key)).build();
    private final NoArgCommandFlag blocksOnlyPart = CommandParts.flag('b', TextComponent.of("Only search for blocks")).build();
    private final NoArgCommandFlag itemsOnlyPart = CommandParts.flag('i', TextComponent.of("Only search for items")).build();
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("Page of results to return")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument queryPart = CommandParts.arg(TranslatableComponent.of("query"), TextComponent.of("Search query")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();

    private GeneralCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static GeneralCommandsRegistration builder() {
        return new GeneralCommandsRegistration();
    }

    public GeneralCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public GeneralCommandsRegistration containerInstance(GeneralCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public GeneralCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public GeneralCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/limit", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Modify block change limit"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.limitPart));
            b.action(this::cmd$_limit);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "limit", Actor.class, LocalSession.class, Integer.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/timeout", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Modify evaluation timeout time."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.limitPart2));
            b.action(this::cmd$_timeout);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "timeout", Actor.class, LocalSession.class, Integer.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/fast", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Toggle fast mode"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.fastModePart));
            b.action(this::cmd$_fast);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "fast", Actor.class, LocalSession.class, Boolean.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/perf", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Toggle side effects for performance"));
            b.footer(TextComponent.of("Note that this command is GOING to change in the future. Do not depend on the exact format of this command yet."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sideEffectPart, (Object)this.newStatePart, (Object)this.showInfoBoxPart));
            b.action(this::cmd$_perf);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "perf", Actor.class, LocalSession.class, SideEffect.class, SideEffect.State.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/reorder", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Sets the reorder mode of WorldEdit"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.reorderModePart));
            b.action(this::cmd$_reorder);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "reorderMode", Actor.class, LocalSession.class, EditSession.ReorderMode.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/drawsel", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Toggle drawing the current selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.drawSelectionPart));
            b.action(this::cmd$_drawsel);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "drawSelection", Player.class, LocalSession.class, Boolean.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/world", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Sets the world override"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.worldPart));
            b.action(this::cmd$_world);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "world", Actor.class, LocalSession.class, World.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/watchdog", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Changes watchdog hook state."));
            b.footer(TextComponent.of("This is dependent on platform implementation. Not all platforms support watchdog hooks, or contain a watchdog."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.hookModePart));
            b.action(this::cmd$_watchdog);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "watchdog", Actor.class, LocalSession.class, HookMode.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("gmask", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/gmask"));
            b.description(TextComponent.of("Set the global mask"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.maskPart));
            b.action(this::cmd$gmask);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "gmask", Actor.class, LocalSession.class, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("toggleplace", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/toggleplace"));
            b.description(TextComponent.of("Switch between your position and pos1 for placement"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$toggleplace);
        });
        this.commandManager.register("placement", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/placement"));
            b.description(TextComponent.of("Select which placement to use"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.placementTypePart, (Object)this.multiplierPart, (Object)this.offsetPart));
            b.action(this::cmd$placement);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "placement", Actor.class, LocalSession.class, PlacementType.class, Integer.TYPE, BlockVector3.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("searchitem", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/searchitem", (Object)"/l", (Object)"/search"));
            b.description(TextComponent.of("Search for an item"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.blocksOnlyPart, (Object)this.itemsOnlyPart, (Object)this.pagePart, (Object)this.queryPart));
            b.action(this::cmd$searchitem);
            Method commandMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "searchItem", Actor.class, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_limit(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "limit", Actor.class, LocalSession.class, Integer.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.limit(this.extract$actor(parameters), this.extract$session(parameters), this.extract$limit(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_timeout(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "timeout", Actor.class, LocalSession.class, Integer.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.timeout(this.extract$actor(parameters), this.extract$session(parameters), this.extract$limit2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_fast(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "fast", Actor.class, LocalSession.class, Boolean.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.fast(this.extract$actor(parameters), this.extract$session(parameters), this.extract$fastMode(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_perf(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "perf", Actor.class, LocalSession.class, SideEffect.class, SideEffect.State.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.perf(this.extract$actor(parameters), this.extract$session(parameters), this.extract$sideEffect(parameters), this.extract$newState(parameters), this.extract$showInfoBox(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_reorder(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "reorderMode", Actor.class, LocalSession.class, EditSession.ReorderMode.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.reorderMode(this.extract$actor(parameters), this.extract$session(parameters), this.extract$reorderMode(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_drawsel(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "drawSelection", Player.class, LocalSession.class, Boolean.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.drawSelection(this.extract$player(parameters), this.extract$session(parameters), this.extract$drawSelection(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_world(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "world", Actor.class, LocalSession.class, World.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.world(this.extract$actor(parameters), this.extract$session(parameters), this.extract$world(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_watchdog(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "watchdog", Actor.class, LocalSession.class, HookMode.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.watchdog(this.extract$actor(parameters), this.extract$session(parameters), this.extract$hookMode(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$gmask(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "gmask", Actor.class, LocalSession.class, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.gmask(this.extract$actor(parameters), this.extract$session(parameters), this.extract$mask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$toggleplace(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "togglePlace", Actor.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.togglePlace(this.extract$actor(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$placement(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "placement", Actor.class, LocalSession.class, PlacementType.class, Integer.TYPE, BlockVector3.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.placement(this.extract$actor(parameters), this.extract$session(parameters), this.extract$placementType(parameters), this.extract$multiplier(parameters), this.extract$offset(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$searchitem(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GeneralCommands.class, "searchItem", Actor.class, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.searchItem(this.extract$actor(parameters), this.extract$blocksOnly(parameters), this.extract$itemsOnly(parameters), this.extract$page(parameters), this.extract$query(parameters));
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

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private Integer extract$limit(CommandParameters parameters) {
        return this.limitPart.value(parameters).asSingle(integer_Key);
    }

    private Integer extract$limit2(CommandParameters parameters) {
        return this.limitPart2.value(parameters).asSingle(integer_Key);
    }

    private Boolean extract$fastMode(CommandParameters parameters) {
        return this.fastModePart.value(parameters).asSingle(boolean_Key);
    }

    private SideEffect extract$sideEffect(CommandParameters parameters) {
        return this.sideEffectPart.value(parameters).asSingle(sideEffect_Key);
    }

    private SideEffect.State extract$newState(CommandParameters parameters) {
        return this.newStatePart.value(parameters).asSingle(state_Key);
    }

    private boolean extract$showInfoBox(CommandParameters parameters) {
        return this.showInfoBoxPart.in(parameters);
    }

    private EditSession.ReorderMode extract$reorderMode(CommandParameters parameters) {
        return this.reorderModePart.value(parameters).asSingle(reorderMode_Key);
    }

    private Player extract$player(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(player_Key, "player", parameters.injectedValue(player_Key));
    }

    private Boolean extract$drawSelection(CommandParameters parameters) {
        return this.drawSelectionPart.value(parameters).asSingle(boolean_Key);
    }

    private World extract$world(CommandParameters parameters) {
        return this.worldPart.value(parameters).asSingle(world_Key);
    }

    private HookMode extract$hookMode(CommandParameters parameters) {
        return this.hookModePart.value(parameters).asSingle(hookMode_Key);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private PlacementType extract$placementType(CommandParameters parameters) {
        return this.placementTypePart.value(parameters).asSingle(placementType_Key);
    }

    private int extract$multiplier(CommandParameters parameters) {
        return this.multiplierPart.value(parameters).asSingle(integer_Key);
    }

    private BlockVector3 extract$offset(CommandParameters parameters) {
        return this.offsetPart.value(parameters).asSingle(blockVector3_offset_Key);
    }

    private boolean extract$blocksOnly(CommandParameters parameters) {
        return this.blocksOnlyPart.in(parameters);
    }

    private boolean extract$itemsOnly(CommandParameters parameters) {
        return this.itemsOnlyPart.in(parameters);
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private List<String> extract$query(CommandParameters parameters) {
        return this.queryPart.value(parameters).asMultiple(string_Key);
    }
}

