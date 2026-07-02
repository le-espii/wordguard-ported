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
import com.sk89q.worldedit.command.ClipboardCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.ClipboardMask;
import com.sk89q.worldedit.internal.annotation.Direction;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
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

public final class ClipboardCommandsRegistration
implements CommandRegistration<ClipboardCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Mask> mask_clipboardMask_Key = Key.of(Mask.class, ClipboardMask.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<BlockVector3> blockVector3_direction_Key = Key.of(BlockVector3.class, Direction.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private static final Key<Region> region_selection_Key = Key.of(Region.class, Selection.class);
    private static final Key<World> world_Key = Key.of(World.class);
    private CommandManager commandManager;
    private ClipboardCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final NoArgCommandFlag copyEntitiesPart = CommandParts.flag('e', TextComponent.of("Also copy entities")).build();
    private final NoArgCommandFlag copyBiomesPart = CommandParts.flag('b', TextComponent.of("Also copy biomes")).build();
    private final ArgAcceptingCommandFlag maskPart = CommandParts.flag('m', TextComponent.of("Set the include mask, non-matching blocks become air")).withRequiredArg().argNamed(TranslatableComponent.of("mask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument leavePatternPart = CommandParts.arg(TranslatableComponent.of("leavePattern"), TextComponent.of("Pattern to leave in place of the selection")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"air")).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final NoArgCommandFlag copyEntitiesPart2 = CommandParts.flag('e', TextComponent.of("Also cut entities")).build();
    private final NoArgCommandFlag copyBiomesPart2 = CommandParts.flag('b', TextComponent.of("Also copy biomes, source biomes are unaffected")).build();
    private final ArgAcceptingCommandFlag maskPart2 = CommandParts.flag('m', TextComponent.of("Set the exclude mask, non-matching blocks become air")).withRequiredArg().argNamed(TranslatableComponent.of("mask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final NoArgCommandFlag ignoreAirBlocksPart = CommandParts.flag('a', TextComponent.of("Skip air blocks")).build();
    private final NoArgCommandFlag pasteStructureVoidPart = CommandParts.flag('v', TextComponent.of("Include structure void blocks")).build();
    private final NoArgCommandFlag atOriginPart = CommandParts.flag('o', TextComponent.of("Paste at the original position")).build();
    private final NoArgCommandFlag selectPastedPart = CommandParts.flag('s', TextComponent.of("Select the region after pasting")).build();
    private final NoArgCommandFlag onlySelectPart = CommandParts.flag('n', TextComponent.of("No paste, select only. (Implies -s)")).build();
    private final NoArgCommandFlag pasteEntitiesPart = CommandParts.flag('e', TextComponent.of("Paste entities if available")).build();
    private final NoArgCommandFlag pasteBiomesPart = CommandParts.flag('b', TextComponent.of("Paste biomes if available")).build();
    private final ArgAcceptingCommandFlag sourceMaskPart = CommandParts.flag('m', TextComponent.of("Only paste blocks matching this mask")).withRequiredArg().argNamed(TranslatableComponent.of("sourceMask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_clipboardMask_Key)).build();
    private final CommandArgument rotateYPart = CommandParts.arg(TranslatableComponent.of("rotateY"), TextComponent.of("Amount to rotate on the y-axis")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument rotateXPart = CommandParts.arg(TranslatableComponent.of("rotateX"), TextComponent.of("Amount to rotate on the x-axis")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument rotateZPart = CommandParts.arg(TranslatableComponent.of("rotateZ"), TextComponent.of("Amount to rotate on the z-axis")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"0")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument directionPart = CommandParts.arg(TranslatableComponent.of("direction"), TextComponent.of("The direction to flip, defaults to look direction.")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"me")).ofTypes((Collection<Key<?>>)ImmutableList.of(blockVector3_direction_Key)).build();

    private ClipboardCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static ClipboardCommandsRegistration builder() {
        return new ClipboardCommandsRegistration();
    }

    public ClipboardCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public ClipboardCommandsRegistration containerInstance(ClipboardCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public ClipboardCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public ClipboardCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/copy", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Copy the selection to the clipboard"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.copyEntitiesPart, (Object)this.copyBiomesPart, (Object)this.maskPart));
            b.action(this::cmd$_copy);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "copy", Actor.class, LocalSession.class, EditSession.class, Region.class, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/cut", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Cut the selection to the clipboard"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.leavePatternPart, (Object)this.copyEntitiesPart2, (Object)this.copyBiomesPart2, (Object)this.maskPart2));
            b.action(this::cmd$_cut);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "cut", Actor.class, LocalSession.class, EditSession.class, Region.class, Pattern.class, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/paste", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Paste the clipboard's contents"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.ignoreAirBlocksPart, (Object)this.pasteStructureVoidPart, (Object)this.atOriginPart, (Object)this.selectPastedPart, (Object)this.onlySelectPart, (Object)this.pasteEntitiesPart, (Object)this.pasteBiomesPart, (Object)this.sourceMaskPart));
            b.action(this::cmd$_paste);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "paste", Actor.class, World.class, LocalSession.class, EditSession.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/rotate", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Rotate the contents of the clipboard"));
            b.footer(TextComponent.of("Non-destructively rotate the contents of the clipboard.\nAngles are provided in degrees and a positive angle will result in a clockwise rotation. Multiple rotations can be stacked. Interpolation is not performed so angles should be a multiple of 90 degrees.\n"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.rotateYPart, (Object)this.rotateXPart, (Object)this.rotateZPart));
            b.action(this::cmd$_rotate);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "rotate", Actor.class, LocalSession.class, Double.TYPE, Double.TYPE, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/flip", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Flip the contents of the clipboard across the origin"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.directionPart));
            b.action(this::cmd$_flip);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "flip", Actor.class, LocalSession.class, BlockVector3.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("clearclipboard", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Clear your clipboard"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$clearclipboard);
            Method commandMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "clearClipboard", Actor.class, LocalSession.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_copy(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "copy", Actor.class, LocalSession.class, EditSession.class, Region.class, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.copy(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$copyEntities(parameters), this.extract$copyBiomes(parameters), this.extract$mask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_cut(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "cut", Actor.class, LocalSession.class, EditSession.class, Region.class, Pattern.class, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.cut(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$leavePattern(parameters), this.extract$copyEntities2(parameters), this.extract$copyBiomes2(parameters), this.extract$mask2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_paste(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "paste", Actor.class, World.class, LocalSession.class, EditSession.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.paste(this.extract$actor(parameters), this.extract$world(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$ignoreAirBlocks(parameters), this.extract$pasteStructureVoid(parameters), this.extract$atOrigin(parameters), this.extract$selectPasted(parameters), this.extract$onlySelect(parameters), this.extract$pasteEntities(parameters), this.extract$pasteBiomes(parameters), this.extract$sourceMask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_rotate(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "rotate", Actor.class, LocalSession.class, Double.TYPE, Double.TYPE, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.rotate(this.extract$actor(parameters), this.extract$session(parameters), this.extract$rotateY(parameters), this.extract$rotateX(parameters), this.extract$rotateZ(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_flip(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "flip", Actor.class, LocalSession.class, BlockVector3.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.flip(this.extract$actor(parameters), this.extract$session(parameters), this.extract$direction(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$clearclipboard(CommandParameters parameters) {
        Method cmdMethod = RegistrationUtil.getCommandMethod(ClipboardCommands.class, "clearClipboard", Actor.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.clearClipboard(this.extract$actor(parameters), this.extract$session(parameters));
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

    private Region extract$region(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(region_selection_Key, "region", parameters.injectedValue(region_selection_Key));
    }

    private boolean extract$copyEntities(CommandParameters parameters) {
        return this.copyEntitiesPart.in(parameters);
    }

    private boolean extract$copyBiomes(CommandParameters parameters) {
        return this.copyBiomesPart.in(parameters);
    }

    private Mask extract$mask(CommandParameters parameters) {
        return this.maskPart.value(parameters).asSingle(mask_Key);
    }

    private Pattern extract$leavePattern(CommandParameters parameters) {
        return this.leavePatternPart.value(parameters).asSingle(pattern_Key);
    }

    private boolean extract$copyEntities2(CommandParameters parameters) {
        return this.copyEntitiesPart2.in(parameters);
    }

    private boolean extract$copyBiomes2(CommandParameters parameters) {
        return this.copyBiomesPart2.in(parameters);
    }

    private Mask extract$mask2(CommandParameters parameters) {
        return this.maskPart2.value(parameters).asSingle(mask_Key);
    }

    private World extract$world(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(world_Key, "world", parameters.injectedValue(world_Key));
    }

    private boolean extract$ignoreAirBlocks(CommandParameters parameters) {
        return this.ignoreAirBlocksPart.in(parameters);
    }

    private boolean extract$pasteStructureVoid(CommandParameters parameters) {
        return this.pasteStructureVoidPart.in(parameters);
    }

    private boolean extract$atOrigin(CommandParameters parameters) {
        return this.atOriginPart.in(parameters);
    }

    private boolean extract$selectPasted(CommandParameters parameters) {
        return this.selectPastedPart.in(parameters);
    }

    private boolean extract$onlySelect(CommandParameters parameters) {
        return this.onlySelectPart.in(parameters);
    }

    private boolean extract$pasteEntities(CommandParameters parameters) {
        return this.pasteEntitiesPart.in(parameters);
    }

    private boolean extract$pasteBiomes(CommandParameters parameters) {
        return this.pasteBiomesPart.in(parameters);
    }

    private Mask extract$sourceMask(CommandParameters parameters) {
        return this.sourceMaskPart.value(parameters).asSingle(mask_clipboardMask_Key);
    }

    private double extract$rotateY(CommandParameters parameters) {
        return this.rotateYPart.value(parameters).asSingle(double_Key);
    }

    private double extract$rotateX(CommandParameters parameters) {
        return this.rotateXPart.value(parameters).asSingle(double_Key);
    }

    private double extract$rotateZ(CommandParameters parameters) {
        return this.rotateZPart.value(parameters).asSingle(double_Key);
    }

    private BlockVector3 extract$direction(CommandParameters parameters) {
        return this.directionPart.value(parameters).asSingle(blockVector3_direction_Key);
    }
}

