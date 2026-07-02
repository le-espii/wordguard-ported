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
import com.sk89q.worldedit.command.RegionCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.Offset;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
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

public final class RegionCommandsRegistration
implements CommandRegistration<RegionCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<BlockVector3> blockVector3_offset_Key = Key.of(BlockVector3.class, Offset.class);
    private static final Key<Long> long_Key = Key.of(Long.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<TreeGenerator.TreeType> treeType_Key = Key.of(TreeGenerator.TreeType.class);
    private static final Key<SideEffectSet> sideEffectSet_Key = Key.of(SideEffectSet.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private static final Key<Region> region_selection_Key = Key.of(Region.class, Selection.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private RegionCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to set")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument patternPart2 = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to place")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument thicknessPart = CommandParts.arg(TranslatableComponent.of("thickness"), TextComponent.of("The thickness of the line")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final NoArgCommandFlag shellPart = CommandParts.flag('h', TextComponent.of("Generate only a shell")).build();
    private final CommandArgument thicknessPart2 = CommandParts.arg(TranslatableComponent.of("thickness"), TextComponent.of("The thickness of the curve")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument fromPart = CommandParts.arg(TranslatableComponent.of("from"), TextComponent.of("The mask representing blocks to replace")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument toPart = CommandParts.arg(TranslatableComponent.of("to"), TextComponent.of("The pattern of blocks to replace with")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument patternPart3 = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to overlay")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument iterationsPart = CommandParts.arg(TranslatableComponent.of("iterations"), TextComponent.of("# of iterations to perform")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask of blocks to use as the height map")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final ArgAcceptingCommandFlag snowBlockCountPart = CommandParts.flag('l', TextComponent.of("Set the amount of snow blocks under the snow")).withRequiredArg().argNamed(TranslatableComponent.of("snowBlockCount")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final ArgAcceptingCommandFlag maskPart2 = CommandParts.flag('m', TextComponent.of("The mask of blocks to use as the height map")).withRequiredArg().argNamed(TranslatableComponent.of("mask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument multiplierPart = CommandParts.arg(TranslatableComponent.of("multiplier"), TextComponent.of("number of times to apply the offset")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument offsetPart = CommandParts.arg(TranslatableComponent.of("offset"), TextComponent.of("The offset to move")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"forward")).ofTypes((Collection<Key<?>>)ImmutableList.of(blockVector3_offset_Key)).build();
    private final CommandArgument replacePart = CommandParts.arg(TranslatableComponent.of("replace"), TextComponent.of("The pattern of blocks to leave")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"air")).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final NoArgCommandFlag moveSelectionPart = CommandParts.flag('s', TextComponent.of("Shift the selection to the target location")).build();
    private final NoArgCommandFlag ignoreAirBlocksPart = CommandParts.flag('a', TextComponent.of("Ignore air blocks")).build();
    private final NoArgCommandFlag copyEntitiesPart = CommandParts.flag('e', TextComponent.of("Also copy entities")).build();
    private final NoArgCommandFlag copyBiomesPart = CommandParts.flag('b', TextComponent.of("Also copy biomes")).build();
    private final ArgAcceptingCommandFlag maskPart3 = CommandParts.flag('m', TextComponent.of("Set the include mask, non-matching blocks become air")).withRequiredArg().argNamed(TranslatableComponent.of("mask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument countPart = CommandParts.arg(TranslatableComponent.of("count"), TextComponent.of("# of copies to stack")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument offsetPart2 = CommandParts.arg(TranslatableComponent.of("offset"), TextComponent.of("How far to move the contents each stack")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"forward")).ofTypes((Collection<Key<?>>)ImmutableList.of(blockVector3_offset_Key)).build();
    private final NoArgCommandFlag moveSelectionPart2 = CommandParts.flag('s', TextComponent.of("Shift the selection to the last stacked copy")).build();
    private final NoArgCommandFlag blockUnitsPart = CommandParts.flag('r', TextComponent.of("Use block units")).build();
    private final CommandArgument seedPart = CommandParts.arg(TranslatableComponent.of("seed"), TextComponent.of("The seed to regenerate with, otherwise uses world seed")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(long_Key)).build();
    private final NoArgCommandFlag regenBiomesPart = CommandParts.flag('b', TextComponent.of("Regenerate biomes as well")).build();
    private final CommandArgument expressionPart = CommandParts.arg(TranslatableComponent.of("expression"), TextComponent.of("The expression to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();
    private final NoArgCommandFlag useRawCoordsPart = CommandParts.flag('r', TextComponent.of("Use the game's coordinate origin")).build();
    private final NoArgCommandFlag offsetPart3 = CommandParts.flag('o', TextComponent.of("Use the placement's coordinate origin")).build();
    private final NoArgCommandFlag offsetCenterPart = CommandParts.flag('c', TextComponent.of("Use the selection's center as origin")).build();
    private final CommandArgument thicknessPart3 = CommandParts.arg(TranslatableComponent.of("thickness"), TextComponent.of("Thickness of the shell to leave")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument patternPart4 = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to replace the hollowed area with")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"air")).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument typePart = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("The type of tree to place")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"tree")).ofTypes((Collection<Key<?>>)ImmutableList.of(treeType_Key)).build();
    private final CommandArgument densityPart = CommandParts.arg(TranslatableComponent.of("density"), TextComponent.of("The density of the forest")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument sideEffectSetPart = CommandParts.arg(TranslatableComponent.of("sideEffectSet"), TextComponent.of("The side effects")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(sideEffectSet_Key)).build();

    private RegionCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static RegionCommandsRegistration builder() {
        return new RegionCommandsRegistration();
    }

    public RegionCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public RegionCommandsRegistration containerInstance(RegionCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public RegionCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public RegionCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/set", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Sets all the blocks in the region"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$_set);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "set", Actor.class, EditSession.class, Region.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/line", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Draws line segments between cuboid selection corners or convex polyhedral selection vertices"));
            b.footer(TextComponent.of("Can only be used with a cuboid selection or a convex polyhedral selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.thicknessPart, (Object)this.shellPart));
            b.action(this::cmd$_line);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "line", Actor.class, EditSession.class, Region.class, Pattern.class, Double.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/curve", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Draws a spline through selected points"));
            b.footer(TextComponent.of("Can only be used with a convex polyhedral selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.thicknessPart2, (Object)this.shellPart));
            b.action(this::cmd$_curve);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "curve", Actor.class, EditSession.class, Region.class, Pattern.class, Double.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/replace", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/re", (Object)"/rep"));
            b.description(TextComponent.of("Replace all blocks in the selection with another"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.fromPart, (Object)this.toPart));
            b.action(this::cmd$_replace);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "replace", Actor.class, EditSession.class, Region.class, Mask.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/overlay", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set a block on top of blocks in the region"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart3));
            b.action(this::cmd$_overlay);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "overlay", Actor.class, EditSession.class, Region.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/center", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/middle"));
            b.description(TextComponent.of("Set the center block(s)"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$_center);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "center", Actor.class, EditSession.class, Region.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/naturalize", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("3 layers of dirt on top then rock below"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$_naturalize);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "naturalize", Actor.class, EditSession.class, Region.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/walls", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Build the four sides of the selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$_walls);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "walls", Actor.class, EditSession.class, Region.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/faces", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/outline"));
            b.description(TextComponent.of("Build the walls, ceiling, and floor of a selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart));
            b.action(this::cmd$_faces);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "faces", Actor.class, EditSession.class, Region.class, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/smooth", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Smooth the elevation in the selection"));
            b.footer(TextComponent.of("Example: '//smooth 1 grass_block,dirt,stone' would only smooth natural surface terrain."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.iterationsPart, (Object)this.maskPart));
            b.action(this::cmd$_smooth);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "smooth", Actor.class, EditSession.class, Region.class, Integer.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/snowsmooth", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Smooth the elevation in the selection with snow layers"));
            b.footer(TextComponent.of("Example: '//snowsmooth 1 -m snow_block,snow' would only smooth snow terrain."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.iterationsPart, (Object)this.snowBlockCountPart, (Object)this.maskPart2));
            b.action(this::cmd$_snowsmooth);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "snowSmooth", Actor.class, EditSession.class, Region.class, Integer.TYPE, Integer.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/move", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Move the contents of the selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.multiplierPart, (Object)this.offsetPart, (Object)this.replacePart, (Object)this.moveSelectionPart, (Object)this.ignoreAirBlocksPart, (Object)this.copyEntitiesPart, (Object)this.copyBiomesPart, (Object)this.maskPart3));
            b.action(this::cmd$_move);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "move", Actor.class, World.class, EditSession.class, LocalSession.class, Region.class, Integer.TYPE, BlockVector3.class, Pattern.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/stack", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Repeat the contents of the selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.countPart, (Object)this.offsetPart2, (Object)this.moveSelectionPart2, (Object)this.ignoreAirBlocksPart, (Object)this.copyEntitiesPart, (Object)this.copyBiomesPart, (Object)this.blockUnitsPart, (Object)this.maskPart3));
            b.action(this::cmd$_stack);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "stack", Actor.class, World.class, EditSession.class, LocalSession.class, Region.class, Integer.TYPE, BlockVector3.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/regen", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Regenerates the contents of the selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.seedPart, (Object)this.regenBiomesPart));
            b.action(this::cmd$_regen);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "regenerate", Actor.class, World.class, LocalSession.class, EditSession.class, Region.class, Long.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/deform", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Deforms a selected region with an expression"));
            b.footer(TextComponent.of("The expression is executed for each block and is expected\nto modify the variables x, y and z to point to a new block\nto fetch. For details, see https://ehub.to/we/expr"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.expressionPart, (Object)this.useRawCoordsPart, (Object)this.offsetPart3, (Object)this.offsetCenterPart));
            b.action(this::cmd$_deform);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "deform", Actor.class, LocalSession.class, EditSession.class, Region.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/hollow", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Hollows out the object contained in this selection"));
            b.footer(TextComponent.of("Thickness is measured in manhattan distance."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.thicknessPart3, (Object)this.patternPart4));
            b.action(this::cmd$_hollow);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "hollow", Actor.class, EditSession.class, Region.class, Integer.TYPE, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/forest", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Make a forest within the region"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.typePart, (Object)this.densityPart));
            b.action(this::cmd$_forest);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "forest", Actor.class, EditSession.class, Region.class, TreeGenerator.TreeType.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/flora", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Make flora within the region"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.densityPart));
            b.action(this::cmd$_flora);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "flora", Actor.class, EditSession.class, Region.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/update", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Apply side effects to your selection"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sideEffectSetPart));
            b.action(this::cmd$_update);
            Method commandMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "update", Actor.class, LocalSession.class, World.class, SideEffectSet.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_set(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "set", Actor.class, EditSession.class, Region.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.set(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_line(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "line", Actor.class, EditSession.class, Region.class, Pattern.class, Double.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.line(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern2(parameters), this.extract$thickness(parameters), this.extract$shell(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_curve(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "curve", Actor.class, EditSession.class, Region.class, Pattern.class, Double.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.curve(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern2(parameters), this.extract$thickness2(parameters), this.extract$shell(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_replace(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "replace", Actor.class, EditSession.class, Region.class, Mask.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.replace(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$from(parameters), this.extract$to(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_overlay(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "overlay", Actor.class, EditSession.class, Region.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.overlay(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_center(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "center", Actor.class, EditSession.class, Region.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.center(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_naturalize(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "naturalize", Actor.class, EditSession.class, Region.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.naturalize(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_walls(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "walls", Actor.class, EditSession.class, Region.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.walls(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_faces(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "faces", Actor.class, EditSession.class, Region.class, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.faces(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_smooth(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "smooth", Actor.class, EditSession.class, Region.class, Integer.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.smooth(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$iterations(parameters), this.extract$mask(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_snowsmooth(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "snowSmooth", Actor.class, EditSession.class, Region.class, Integer.TYPE, Integer.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.snowSmooth(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$iterations(parameters), this.extract$snowBlockCount(parameters), this.extract$mask2(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_move(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "move", Actor.class, World.class, EditSession.class, LocalSession.class, Region.class, Integer.TYPE, BlockVector3.class, Pattern.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.move(this.extract$actor(parameters), this.extract$world(parameters), this.extract$editSession(parameters), this.extract$session(parameters), this.extract$region(parameters), this.extract$multiplier(parameters), this.extract$offset(parameters), this.extract$replace(parameters), this.extract$moveSelection(parameters), this.extract$ignoreAirBlocks(parameters), this.extract$copyEntities(parameters), this.extract$copyBiomes(parameters), this.extract$mask3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_stack(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "stack", Actor.class, World.class, EditSession.class, LocalSession.class, Region.class, Integer.TYPE, BlockVector3.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.stack(this.extract$actor(parameters), this.extract$world(parameters), this.extract$editSession(parameters), this.extract$session(parameters), this.extract$region(parameters), this.extract$count(parameters), this.extract$offset2(parameters), this.extract$moveSelection2(parameters), this.extract$ignoreAirBlocks(parameters), this.extract$copyEntities(parameters), this.extract$copyBiomes(parameters), this.extract$blockUnits(parameters), this.extract$mask3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_regen(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "regenerate", Actor.class, World.class, LocalSession.class, EditSession.class, Region.class, Long.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.regenerate(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$seed(parameters), this.extract$regenBiomes(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_deform(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "deform", Actor.class, LocalSession.class, EditSession.class, Region.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.deform(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$expression(parameters), this.extract$useRawCoords(parameters), this.extract$offset3(parameters), this.extract$offsetCenter(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_hollow(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "hollow", Actor.class, EditSession.class, Region.class, Integer.TYPE, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.hollow(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$thickness3(parameters), this.extract$pattern4(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_forest(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "forest", Actor.class, EditSession.class, Region.class, TreeGenerator.TreeType.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.forest(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$type(parameters), this.extract$density(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_flora(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "flora", Actor.class, EditSession.class, Region.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.flora(this.extract$actor(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$density(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_update(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(RegionCommands.class, "update", Actor.class, LocalSession.class, World.class, SideEffectSet.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.update(this.extract$actor(parameters), this.extract$session(parameters), this.extract$injectedWorld(parameters), this.extract$sideEffectSet(parameters));
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

    private EditSession extract$editSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(editSession_Key, "editSession", parameters.injectedValue(editSession_Key));
    }

    private Region extract$region(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(region_selection_Key, "region", parameters.injectedValue(region_selection_Key));
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }

    private Pattern extract$pattern2(CommandParameters parameters) {
        return this.patternPart2.value(parameters).asSingle(pattern_Key);
    }

    private double extract$thickness(CommandParameters parameters) {
        return this.thicknessPart.value(parameters).asSingle(double_Key);
    }

    private boolean extract$shell(CommandParameters parameters) {
        return this.shellPart.in(parameters);
    }

    private double extract$thickness2(CommandParameters parameters) {
        return this.thicknessPart2.value(parameters).asSingle(double_Key);
    }

    private Mask extract$from(CommandParameters parameters) {
        return this.fromPart.value(parameters).asSingle(mask_Key);
    }

    private Pattern extract$to(CommandParameters parameters) {
        return this.toPart.value(parameters).asSingle(pattern_Key);
    }

    private Pattern extract$pattern3(CommandParameters parameters) {
        return this.patternPart3.value(parameters).asSingle(pattern_Key);
    }

    private int extract$iterations(CommandParameters parameters) {
        return this.iterationsPart.value(parameters).asSingle(integer_Key);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private int extract$snowBlockCount(CommandParameters parameters) {
        return this.snowBlockCountPart.value(parameters).asSingle(integer_Key);
    }

    private Mask extract$mask2(CommandParameters parameters) {
        return this.maskPart2.value(parameters).asSingle(mask_Key);
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private int extract$multiplier(CommandParameters parameters) {
        return this.multiplierPart.value(parameters).asSingle(integer_Key);
    }

    private BlockVector3 extract$offset(CommandParameters parameters) {
        return this.offsetPart.value(parameters).asSingle(blockVector3_offset_Key);
    }

    private Pattern extract$replace(CommandParameters parameters) {
        return this.replacePart.value(parameters).asSingle(pattern_Key);
    }

    private boolean extract$moveSelection(CommandParameters parameters) {
        return this.moveSelectionPart.in(parameters);
    }

    private boolean extract$ignoreAirBlocks(CommandParameters parameters) {
        return this.ignoreAirBlocksPart.in(parameters);
    }

    private boolean extract$copyEntities(CommandParameters parameters) {
        return this.copyEntitiesPart.in(parameters);
    }

    private boolean extract$copyBiomes(CommandParameters parameters) {
        return this.copyBiomesPart.in(parameters);
    }

    private Mask extract$mask3(CommandParameters parameters) {
        return this.maskPart3.value(parameters).asSingle(mask_Key);
    }

    private int extract$count(CommandParameters parameters) {
        return this.countPart.value(parameters).asSingle(integer_Key);
    }

    private BlockVector3 extract$offset2(CommandParameters parameters) {
        return this.offsetPart2.value(parameters).asSingle(blockVector3_offset_Key);
    }

    private boolean extract$moveSelection2(CommandParameters parameters) {
        return this.moveSelectionPart2.in(parameters);
    }

    private boolean extract$blockUnits(CommandParameters parameters) {
        return this.blockUnitsPart.in(parameters);
    }

    private Long extract$seed(CommandParameters parameters) {
        return this.seedPart.value(parameters).asSingle(long_Key);
    }

    private boolean extract$regenBiomes(CommandParameters parameters) {
        return this.regenBiomesPart.in(parameters);
    }

    private List<String> extract$expression(CommandParameters parameters) {
        return this.expressionPart.value(parameters).asMultiple(string_Key);
    }

    private boolean extract$useRawCoords(CommandParameters parameters) {
        return this.useRawCoordsPart.in(parameters);
    }

    private boolean extract$offset3(CommandParameters parameters) {
        return this.offsetPart3.in(parameters);
    }

    private boolean extract$offsetCenter(CommandParameters parameters) {
        return this.offsetCenterPart.in(parameters);
    }

    private int extract$thickness3(CommandParameters parameters) {
        return this.thicknessPart3.value(parameters).asSingle(integer_Key);
    }

    private Pattern extract$pattern4(CommandParameters parameters) {
        return this.patternPart4.value(parameters).asSingle(pattern_Key);
    }

    private TreeGenerator.TreeType extract$type(CommandParameters parameters) {
        return this.typePart.value(parameters).asSingle(treeType_Key);
    }

    private double extract$density(CommandParameters parameters) {
        return this.densityPart.value(parameters).asSingle(double_Key);
    }

    private World extract$injectedWorld(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "injectedWorld", parameters.injectedValue(world_Key));
    }

    private SideEffectSet extract$sideEffectSet(CommandParameters parameters) {
        return this.sideEffectSetPart.value(parameters).asSingle(sideEffectSet_Key);
    }
}

