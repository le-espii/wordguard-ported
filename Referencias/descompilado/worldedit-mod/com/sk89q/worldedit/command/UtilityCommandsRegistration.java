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
import com.sk89q.worldedit.command.UtilityCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.EntityRemover;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.VertHeight;
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

public final class UtilityCommandsRegistration
implements CommandRegistration<UtilityCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Integer> integer_vertHeight_Key = Key.of(Integer.class, VertHeight.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<EntityRemover> entityRemover_Key = Key.of(EntityRemover.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private CommandManager commandManager;
    private UtilityCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The blocks to fill with")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument radiusPart = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to fill in")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument depthPart = CommandParts.arg(TranslatableComponent.of("depth"), TextComponent.of("The depth to fill")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument depthPart2 = CommandParts.arg(TranslatableComponent.of("depth"), TextComponent.of("The depth to fill")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument radiusPart2 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to drain")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final NoArgCommandFlag waterloggedPart = CommandParts.flag('w', TextComponent.of("Also un-waterlog blocks")).build();
    private final CommandArgument radiusPart3 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to fix in")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument sizePart = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The apothem of the square to remove from")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument heightPart = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The maximum height above you to remove from")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final CommandArgument heightPart2 = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The maximum height below you to remove from")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask of blocks to remove")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument radiusPart4 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the square to remove from")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"50")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument radiusPart5 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the square to remove in")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument fromPart = CommandParts.arg(TranslatableComponent.of("from"), TextComponent.of("The mask matching blocks to remove")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument toPart = CommandParts.arg(TranslatableComponent.of("to"), TextComponent.of("The pattern of blocks to replace with")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument sizePart2 = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The radius of the cylinder to snow in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument heightPart3 = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cylinder to snow in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final NoArgCommandFlag stackPart = CommandParts.flag('s', TextComponent.of("Stack snow layers")).build();
    private final CommandArgument sizePart3 = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The radius of the cylinder to thaw in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument heightPart4 = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cylinder to thaw in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final CommandArgument sizePart4 = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The radius of the cylinder to convert in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument heightPart5 = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cylinder to convert in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final NoArgCommandFlag convertCoarsePart = CommandParts.flag('f', TextComponent.of("Also convert coarse dirt")).build();
    private final CommandArgument radiusPart6 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the square to remove in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument radiusPart7 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("Radius to kill mobs in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag killPetsPart = CommandParts.flag('p', TextComponent.of("Also kill pets")).build();
    private final NoArgCommandFlag killNpcsPart = CommandParts.flag('n', TextComponent.of("Also kill NPCs")).build();
    private final NoArgCommandFlag killGolemsPart = CommandParts.flag('g', TextComponent.of("Also kill golems")).build();
    private final NoArgCommandFlag killAnimalsPart = CommandParts.flag('a', TextComponent.of("Also kill animals")).build();
    private final NoArgCommandFlag killAmbientPart = CommandParts.flag('b', TextComponent.of("Also kill ambient mobs")).build();
    private final NoArgCommandFlag killWithNamePart = CommandParts.flag('t', TextComponent.of("Also kill mobs with name tags")).build();
    private final NoArgCommandFlag killFriendlyPart = CommandParts.flag('f', TextComponent.of("Also kill all friendly mobs (Applies the flags `-abgnpt`)")).build();
    private final NoArgCommandFlag killArmorStandsPart = CommandParts.flag('r', TextComponent.of("Also destroy armor stands")).build();
    private final NoArgCommandFlag killWaterPart = CommandParts.flag('w', TextComponent.of("Also kill water mobs")).build();
    private final CommandArgument removerPart = CommandParts.arg(TranslatableComponent.of("remover"), TextComponent.of("The type of entity to remove")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(entityRemover_Key)).build();
    private final CommandArgument radiusPart8 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the cuboid to remove from")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument inputPart = CommandParts.arg(TranslatableComponent.of("input"), TextComponent.of("Expression to evaluate")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();
    private final NoArgCommandFlag listSubCommandsPart = CommandParts.flag('s', TextComponent.of("List sub-commands of the given command, if applicable")).build();
    private final ArgAcceptingCommandFlag pagePart = CommandParts.flag('p', TextComponent.of("The page to retrieve")).withRequiredArg().argNamed(TranslatableComponent.of("page")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument commandPart = CommandParts.arg(TranslatableComponent.of("command"), TextComponent.of("The command to retrieve help for")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();

    private UtilityCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static UtilityCommandsRegistration builder() {
        return new UtilityCommandsRegistration();
    }

    public UtilityCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public UtilityCommandsRegistration containerInstance(UtilityCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public UtilityCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public UtilityCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/fill", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Fill a hole"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiusPart, (Object)this.depthPart));
            b.action(this::cmd$_fill);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fill", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Double.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/fillr", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Fill a hole recursively"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiusPart, (Object)this.depthPart2));
            b.action(this::cmd$_fillr);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fillr", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Double.TYPE, Integer.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/drain", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Drain a pool"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart2, (Object)this.waterloggedPart));
            b.action(this::cmd$_drain);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "drain", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("fixlava", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/fixlava"));
            b.description(TextComponent.of("Fix lava to be stationary"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart3));
            b.action(this::cmd$fixlava);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fixLava", Actor.class, LocalSession.class, EditSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("fixwater", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/fixwater"));
            b.description(TextComponent.of("Fix water to be stationary"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart3));
            b.action(this::cmd$fixwater);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fixWater", Actor.class, LocalSession.class, EditSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("removeabove", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/removeabove"));
            b.description(TextComponent.of("Remove blocks above your head."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart, (Object)this.heightPart));
            b.action(this::cmd$removeabove);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeAbove", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("removebelow", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/removebelow"));
            b.description(TextComponent.of("Remove blocks below you."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart, (Object)this.heightPart2));
            b.action(this::cmd$removebelow);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeBelow", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("removenear", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/removenear"));
            b.description(TextComponent.of("Remove blocks near you."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.maskPart, (Object)this.radiusPart4));
            b.action(this::cmd$removenear);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeNear", Actor.class, LocalSession.class, EditSession.class, Mask.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("replacenear", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/replacenear"));
            b.description(TextComponent.of("Replace nearby blocks"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart5, (Object)this.fromPart, (Object)this.toPart));
            b.action(this::cmd$replacenear);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "replaceNear", Actor.class, World.class, LocalSession.class, EditSession.class, Integer.TYPE, Mask.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("snow", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/snow"));
            b.description(TextComponent.of("Simulates snow"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart2, (Object)this.heightPart3, (Object)this.stackPart));
            b.action(this::cmd$snow);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "snow", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("thaw", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/thaw"));
            b.description(TextComponent.of("Thaws the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart3, (Object)this.heightPart4));
            b.action(this::cmd$thaw);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "thaw", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("green", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/green"));
            b.description(TextComponent.of("Converts dirt to grass blocks in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart4, (Object)this.heightPart5, (Object)this.convertCoarsePart));
            b.action(this::cmd$green);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "green", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("extinguish", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/ex", (Object)"/ext", (Object)"/extinguish", (Object)"ex", (Object)"ext"));
            b.description(TextComponent.of("Extinguish nearby fire"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart6));
            b.action(this::cmd$extinguish);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "extinguish", Actor.class, LocalSession.class, EditSession.class, Integer.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("butcher", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Kill all or nearby mobs"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart7, (Object)this.killPetsPart, (Object)this.killNpcsPart, (Object)this.killGolemsPart, (Object)this.killAnimalsPart, (Object)this.killAmbientPart, (Object)this.killWithNamePart, (Object)this.killFriendlyPart, (Object)this.killArmorStandsPart, (Object)this.killWaterPart));
            b.action(this::cmd$butcher);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "butcher", Actor.class, Integer.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("remove", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"rem", (Object)"rement"));
            b.description(TextComponent.of("Remove all entities of a type"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.removerPart, (Object)this.radiusPart8));
            b.action(this::cmd$remove);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "remove", Actor.class, EntityRemover.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/calculate", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/calc", (Object)"/eval", (Object)"/evaluate", (Object)"/solve"));
            b.description(TextComponent.of("Evaluate a mathematical expression"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.inputPart));
            b.action(this::cmd$_calculate);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "calc", Actor.class, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/help", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Displays help for WorldEdit commands"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.listSubCommandsPart, (Object)this.pagePart, (Object)this.commandPart));
            b.action(this::cmd$_help);
            Method commandMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "help", Actor.class, Boolean.TYPE, Integer.TYPE, List.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_fill(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fill", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Double.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.fill(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radius(parameters), this.extract$depth(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_fillr(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fillr", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Double.TYPE, Integer.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.fillr(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radius(parameters), this.extract$depth2(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_drain(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "drain", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.drain(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$radius2(parameters), this.extract$waterlogged(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$fixlava(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fixLava", Actor.class, LocalSession.class, EditSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.fixLava(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$radius3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$fixwater(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "fixWater", Actor.class, LocalSession.class, EditSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.fixWater(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$radius3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$removeabove(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeAbove", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.removeAbove(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size(parameters), this.extract$height(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$removebelow(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeBelow", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.removeBelow(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size(parameters), this.extract$height2(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$removenear(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "removeNear", Actor.class, LocalSession.class, EditSession.class, Mask.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.removeNear(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$mask(parameters), this.extract$radius4(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$replacenear(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "replaceNear", Actor.class, World.class, LocalSession.class, EditSession.class, Integer.TYPE, Mask.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.replaceNear(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$radius5(parameters), this.extract$from(parameters), this.extract$to(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$snow(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "snow", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.snow(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size2(parameters), this.extract$height3(parameters), this.extract$stack(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$thaw(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "thaw", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.thaw(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size3(parameters), this.extract$height4(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$green(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "green", Actor.class, LocalSession.class, EditSession.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.green(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size4(parameters), this.extract$height5(parameters), this.extract$convertCoarse(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$extinguish(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "extinguish", Actor.class, LocalSession.class, EditSession.class, Integer.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.extinguish(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$radius6(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$butcher(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "butcher", Actor.class, Integer.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.butcher(this.extract$actor(parameters), this.extract$radius7(parameters), this.extract$killPets(parameters), this.extract$killNpcs(parameters), this.extract$killGolems(parameters), this.extract$killAnimals(parameters), this.extract$killAmbient(parameters), this.extract$killWithName(parameters), this.extract$killFriendly(parameters), this.extract$killArmorStands(parameters), this.extract$killWater(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$remove(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "remove", Actor.class, EntityRemover.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.remove(this.extract$actor(parameters), this.extract$remover(parameters), this.extract$radius8(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_calculate(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "calc", Actor.class, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.calc(this.extract$actor(parameters), this.extract$input(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_help(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(UtilityCommands.class, "help", Actor.class, Boolean.TYPE, Integer.TYPE, List.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.help(this.extract$actor(parameters), this.extract$listSubCommands(parameters), this.extract$page(parameters), this.extract$command(parameters));
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

    private EditSession extract$editSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(editSession_Key, "editSession", parameters.injectedValue(editSession_Key));
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }

    private double extract$radius(CommandParameters parameters) {
        return this.radiusPart.value(parameters).asSingle(double_Key);
    }

    private int extract$depth(CommandParameters parameters) {
        return this.depthPart.value(parameters).asSingle(integer_Key);
    }

    private Integer extract$depth2(CommandParameters parameters) {
        return this.depthPart2.value(parameters).asSingle(integer_Key);
    }

    private double extract$radius2(CommandParameters parameters) {
        return this.radiusPart2.value(parameters).asSingle(double_Key);
    }

    private boolean extract$waterlogged(CommandParameters parameters) {
        return this.waterloggedPart.in(parameters);
    }

    private double extract$radius3(CommandParameters parameters) {
        return this.radiusPart3.value(parameters).asSingle(double_Key);
    }

    private int extract$size(CommandParameters parameters) {
        return this.sizePart.value(parameters).asSingle(integer_Key);
    }

    private int extract$height(CommandParameters parameters) {
        return this.heightPart.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private int extract$height2(CommandParameters parameters) {
        return this.heightPart2.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private int extract$radius4(CommandParameters parameters) {
        return this.radiusPart4.value(parameters).asSingle(integer_Key);
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private int extract$radius5(CommandParameters parameters) {
        return this.radiusPart5.value(parameters).asSingle(integer_Key);
    }

    private Mask extract$from(CommandParameters parameters) {
        return this.fromPart.value(parameters).asSingle(mask_Key);
    }

    private Pattern extract$to(CommandParameters parameters) {
        return this.toPart.value(parameters).asSingle(pattern_Key);
    }

    private double extract$size2(CommandParameters parameters) {
        return this.sizePart2.value(parameters).asSingle(double_Key);
    }

    private int extract$height3(CommandParameters parameters) {
        return this.heightPart3.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private boolean extract$stack(CommandParameters parameters) {
        return this.stackPart.in(parameters);
    }

    private double extract$size3(CommandParameters parameters) {
        return this.sizePart3.value(parameters).asSingle(double_Key);
    }

    private int extract$height4(CommandParameters parameters) {
        return this.heightPart4.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private double extract$size4(CommandParameters parameters) {
        return this.sizePart4.value(parameters).asSingle(double_Key);
    }

    private int extract$height5(CommandParameters parameters) {
        return this.heightPart5.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private boolean extract$convertCoarse(CommandParameters parameters) {
        return this.convertCoarsePart.in(parameters);
    }

    private Integer extract$radius6(CommandParameters parameters) {
        return this.radiusPart6.value(parameters).asSingle(integer_Key);
    }

    private Integer extract$radius7(CommandParameters parameters) {
        return this.radiusPart7.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$killPets(CommandParameters parameters) {
        return this.killPetsPart.in(parameters);
    }

    private boolean extract$killNpcs(CommandParameters parameters) {
        return this.killNpcsPart.in(parameters);
    }

    private boolean extract$killGolems(CommandParameters parameters) {
        return this.killGolemsPart.in(parameters);
    }

    private boolean extract$killAnimals(CommandParameters parameters) {
        return this.killAnimalsPart.in(parameters);
    }

    private boolean extract$killAmbient(CommandParameters parameters) {
        return this.killAmbientPart.in(parameters);
    }

    private boolean extract$killWithName(CommandParameters parameters) {
        return this.killWithNamePart.in(parameters);
    }

    private boolean extract$killFriendly(CommandParameters parameters) {
        return this.killFriendlyPart.in(parameters);
    }

    private boolean extract$killArmorStands(CommandParameters parameters) {
        return this.killArmorStandsPart.in(parameters);
    }

    private boolean extract$killWater(CommandParameters parameters) {
        return this.killWaterPart.in(parameters);
    }

    private EntityRemover extract$remover(CommandParameters parameters) {
        return this.removerPart.value(parameters).asSingle(entityRemover_Key);
    }

    private int extract$radius8(CommandParameters parameters) {
        return this.radiusPart8.value(parameters).asSingle(integer_Key);
    }

    private List<String> extract$input(CommandParameters parameters) {
        return this.inputPart.value(parameters).asMultiple(string_Key);
    }

    private boolean extract$listSubCommands(CommandParameters parameters) {
        return this.listSubCommandsPart.in(parameters);
    }

    private int extract$page(CommandParameters parameters) {
        return this.pagePart.value(parameters).asSingle(integer_Key);
    }

    private List<String> extract$command(CommandParameters parameters) {
        return this.commandPart.value(parameters).asMultiple(string_Key);
    }
}

