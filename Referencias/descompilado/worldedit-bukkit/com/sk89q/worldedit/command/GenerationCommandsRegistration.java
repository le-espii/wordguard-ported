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
import com.sk89q.worldedit.command.GenerationCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.Radii;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import java.lang.annotation.Annotation;
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
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.CommandParts;
import org.enginehub.piston.part.NoArgCommandFlag;

public final class GenerationCommandsRegistration
implements CommandRegistration<GenerationCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Double> double_radii2_Key = Key.of(Double.class, new Object(){

        Annotation a(@Radii(value=2) Object ah) {
            try {
                return this.getClass().getDeclaredMethod("a", Object.class).getParameterAnnotations()[0][0];
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }.a(null));
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<Double> double_radii3_Key = Key.of(Double.class, new Object(){

        Annotation a(@Radii(value=3) Object ah) {
            try {
                return this.getClass().getDeclaredMethod("a", Object.class).getParameterAnnotations()[0][0];
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }.a(null));
    private static final Key<TreeGenerator.TreeType> treeType_Key = Key.of(TreeGenerator.TreeType.class);
    private static final Key<ConfiguredFeatureType> configuredFeatureType_Key = Key.of(ConfiguredFeatureType.class);
    private static final Key<StructureType> structureType_Key = Key.of(StructureType.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<BiomeType> biomeType_Key = Key.of(BiomeType.class);
    private static final Key<Actor> actor_Key = Key.of(Actor.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private static final Key<EditSession> editSession_Key = Key.of(EditSession.class);
    private static final Key<Region> region_selection_Key = Key.of(Region.class, Selection.class);
    private CommandManager commandManager;
    private GenerationCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to generate")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument radiiPart = CommandParts.arg(TranslatableComponent.of("radii"), TextComponent.of("The radii of the cylinder. 1st is N/S, 2nd is E/W")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_radii2_Key)).build();
    private final CommandArgument heightPart = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cylinder")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag hollowPart = CommandParts.flag('h', TextComponent.of("Make a hollow cylinder")).build();
    private final CommandArgument radiiPart2 = CommandParts.arg(TranslatableComponent.of("radii"), TextComponent.of("The radii of the cone. 1st is N/S, 2nd is E/W")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_radii2_Key)).build();
    private final CommandArgument heightPart2 = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cone")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag hollowPart2 = CommandParts.flag('h', TextComponent.of("Make a hollow cone")).build();
    private final CommandArgument thicknessPart = CommandParts.arg(TranslatableComponent.of("thickness"), TextComponent.of("Thickness of the hollow cone")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument radiiPart3 = CommandParts.arg(TranslatableComponent.of("radii"), TextComponent.of("The radii of the sphere. Order is N/S, U/D, E/W")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(double_radii3_Key)).build();
    private final NoArgCommandFlag raisedPart = CommandParts.flag('r', TextComponent.of("Raise the bottom of the sphere to the placement position")).build();
    private final NoArgCommandFlag hollowPart3 = CommandParts.flag('h', TextComponent.of("Make a hollow sphere")).build();
    private final CommandArgument sizePart = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The size of the forest, in blocks")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument typePart = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("The type of forest")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"tree")).ofTypes((Collection<Key<?>>)ImmutableList.of(treeType_Key)).build();
    private final CommandArgument densityPart = CommandParts.arg(TranslatableComponent.of("density"), TextComponent.of("The density of the forest, between 0 and 100")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument sizePart2 = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The size of the patch")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"10")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument featurePart = CommandParts.arg(TranslatableComponent.of("feature"), TextComponent.of("The feature")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(configuredFeatureType_Key)).build();
    private final CommandArgument featurePart2 = CommandParts.arg(TranslatableComponent.of("feature"), TextComponent.of("The structure")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(structureType_Key)).build();
    private final CommandArgument patternPart2 = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to set")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument sizePart3 = CommandParts.arg(TranslatableComponent.of("size"), TextComponent.of("The size of the pyramid")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag hollowPart4 = CommandParts.flag('h', TextComponent.of("Make a hollow pyramid")).build();
    private final CommandArgument expressionPart = CommandParts.arg(TranslatableComponent.of("expression"), TextComponent.of("Expression to test block placement locations and set block type")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();
    private final NoArgCommandFlag hollowPart5 = CommandParts.flag('h', TextComponent.of("Generate a hollow shape")).build();
    private final NoArgCommandFlag useRawCoordsPart = CommandParts.flag('r', TextComponent.of("Use the game's coordinate origin")).build();
    private final NoArgCommandFlag offsetPart = CommandParts.flag('o', TextComponent.of("Use the placement's coordinate origin")).build();
    private final NoArgCommandFlag offsetCenterPart = CommandParts.flag('c', TextComponent.of("Use the selection's center as origin")).build();
    private final CommandArgument targetPart = CommandParts.arg(TranslatableComponent.of("target"), TextComponent.of("The biome type to set")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(biomeType_Key)).build();
    private final CommandArgument expressionPart2 = CommandParts.arg(TranslatableComponent.of("expression"), TextComponent.of("Expression to test block placement locations and set biome type")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).variable(true).build();

    private GenerationCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static GenerationCommandsRegistration builder() {
        return new GenerationCommandsRegistration();
    }

    public GenerationCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public GenerationCommandsRegistration containerInstance(GenerationCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public GenerationCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public GenerationCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("/hcyl", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generates a hollow cylinder."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiiPart, (Object)this.heightPart));
            b.action(this::cmd$_hcyl);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hcyl", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/cyl", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generates a cylinder."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiiPart, (Object)this.heightPart, (Object)this.hollowPart));
            b.action(this::cmd$_cyl);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "cyl", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/cone", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generates a cone."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiiPart2, (Object)this.heightPart2, (Object)this.hollowPart2, (Object)this.thicknessPart));
            b.action(this::cmd$_cone);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "cone", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE, Boolean.TYPE, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/hsphere", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generates a hollow sphere."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiiPart3, (Object)this.raisedPart));
            b.action(this::cmd$_hsphere);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hsphere", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/sphere", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generates a filled sphere."));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiiPart3, (Object)this.raisedPart, (Object)this.hollowPart3));
            b.action(this::cmd$_sphere);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "sphere", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("forestgen", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate a forest"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart, (Object)this.typePart, (Object)this.densityPart));
            b.action(this::cmd$forestgen);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "forestGen", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, TreeGenerator.TreeType.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("pumpkins", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate pumpkin patches"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.sizePart2));
            b.action(this::cmd$pumpkins);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "pumpkins", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/feature", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate Minecraft features"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.featurePart));
            b.action(this::cmd$_feature);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "feature", Actor.class, LocalSession.class, EditSession.class, ConfiguredFeatureType.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/structure", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate Minecraft structures"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.featurePart2));
            b.action(this::cmd$_structure);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "structure", Actor.class, LocalSession.class, EditSession.class, StructureType.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/hpyramid", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate a hollow pyramid"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.sizePart3));
            b.action(this::cmd$_hpyramid);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hollowPyramid", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/pyramid", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Generate a filled pyramid"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.sizePart3, (Object)this.hollowPart4));
            b.action(this::cmd$_pyramid);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "pyramid", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Integer.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/generate", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/gen", (Object)"/g"));
            b.description(TextComponent.of("Generates a shape according to a formula."));
            b.footer(TextComponent.of("For details, see https://ehub.to/we/expr"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart2, (Object)this.expressionPart, (Object)this.hollowPart5, (Object)this.useRawCoordsPart, (Object)this.offsetPart, (Object)this.offsetCenterPart));
            b.action(this::cmd$_generate);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "generate", Actor.class, LocalSession.class, EditSession.class, Region.class, Pattern.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("/generatebiome", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"/genbiome", (Object)"/gb"));
            b.description(TextComponent.of("Sets biome according to a formula."));
            b.footer(TextComponent.of("For details, see https://ehub.to/we/expr"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.targetPart, (Object)this.expressionPart2, (Object)this.hollowPart5, (Object)this.useRawCoordsPart, (Object)this.offsetPart, (Object)this.offsetCenterPart));
            b.action(this::cmd$_generatebiome);
            Method commandMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "generateBiome", Actor.class, LocalSession.class, EditSession.class, Region.class, BiomeType.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$_hcyl(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hcyl", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.hcyl(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radii(parameters), this.extract$height(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_cyl(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "cyl", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.cyl(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radii(parameters), this.extract$height(parameters), this.extract$hollow(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_cone(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "cone", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Integer.TYPE, Boolean.TYPE, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.cone(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radii2(parameters), this.extract$height2(parameters), this.extract$hollow2(parameters), this.extract$thickness(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_hsphere(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hsphere", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.hsphere(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radii3(parameters), this.extract$raised(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_sphere(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "sphere", Actor.class, LocalSession.class, EditSession.class, Pattern.class, List.class, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.sphere(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern(parameters), this.extract$radii3(parameters), this.extract$raised(parameters), this.extract$hollow3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$forestgen(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "forestGen", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE, TreeGenerator.TreeType.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.forestGen(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size(parameters), this.extract$type(parameters), this.extract$density(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$pumpkins(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "pumpkins", Actor.class, LocalSession.class, EditSession.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.pumpkins(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$size2(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_feature(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "feature", Actor.class, LocalSession.class, EditSession.class, ConfiguredFeatureType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.feature(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$feature(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_structure(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "structure", Actor.class, LocalSession.class, EditSession.class, StructureType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.structure(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$feature2(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_hpyramid(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "hollowPyramid", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.hollowPyramid(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern2(parameters), this.extract$size3(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_pyramid(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "pyramid", Actor.class, LocalSession.class, EditSession.class, Pattern.class, Integer.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.pyramid(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$pattern2(parameters), this.extract$size3(parameters), this.extract$hollow4(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_generate(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "generate", Actor.class, LocalSession.class, EditSession.class, Region.class, Pattern.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.generate(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$pattern2(parameters), this.extract$expression(parameters), this.extract$hollow5(parameters), this.extract$useRawCoords(parameters), this.extract$offset(parameters), this.extract$offsetCenter(parameters));
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$_generatebiome(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(GenerationCommands.class, "generateBiome", Actor.class, LocalSession.class, EditSession.class, Region.class, BiomeType.class, List.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            int result = this.containerInstance.generateBiome(this.extract$actor(parameters), this.extract$session(parameters), this.extract$editSession(parameters), this.extract$region(parameters), this.extract$target(parameters), this.extract$expression2(parameters), this.extract$hollow5(parameters), this.extract$useRawCoords(parameters), this.extract$offset(parameters), this.extract$offsetCenter(parameters));
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

    private List<Double> extract$radii(CommandParameters parameters) {
        return this.radiiPart.value(parameters).asMultiple(double_radii2_Key);
    }

    private int extract$height(CommandParameters parameters) {
        return this.heightPart.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$hollow(CommandParameters parameters) {
        return this.hollowPart.in(parameters);
    }

    private List<Double> extract$radii2(CommandParameters parameters) {
        return this.radiiPart2.value(parameters).asMultiple(double_radii2_Key);
    }

    private int extract$height2(CommandParameters parameters) {
        return this.heightPart2.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$hollow2(CommandParameters parameters) {
        return this.hollowPart2.in(parameters);
    }

    private double extract$thickness(CommandParameters parameters) {
        return this.thicknessPart.value(parameters).asSingle(double_Key);
    }

    private List<Double> extract$radii3(CommandParameters parameters) {
        return this.radiiPart3.value(parameters).asMultiple(double_radii3_Key);
    }

    private boolean extract$raised(CommandParameters parameters) {
        return this.raisedPart.in(parameters);
    }

    private boolean extract$hollow3(CommandParameters parameters) {
        return this.hollowPart3.in(parameters);
    }

    private int extract$size(CommandParameters parameters) {
        return this.sizePart.value(parameters).asSingle(integer_Key);
    }

    private TreeGenerator.TreeType extract$type(CommandParameters parameters) {
        return this.typePart.value(parameters).asSingle(treeType_Key);
    }

    private double extract$density(CommandParameters parameters) {
        return this.densityPart.value(parameters).asSingle(double_Key);
    }

    private int extract$size2(CommandParameters parameters) {
        return this.sizePart2.value(parameters).asSingle(integer_Key);
    }

    private ConfiguredFeatureType extract$feature(CommandParameters parameters) {
        return this.featurePart.value(parameters).asSingle(configuredFeatureType_Key);
    }

    private StructureType extract$feature2(CommandParameters parameters) {
        return this.featurePart2.value(parameters).asSingle(structureType_Key);
    }

    private Pattern extract$pattern2(CommandParameters parameters) {
        return this.patternPart2.value(parameters).asSingle(pattern_Key);
    }

    private int extract$size3(CommandParameters parameters) {
        return this.sizePart3.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$hollow4(CommandParameters parameters) {
        return this.hollowPart4.in(parameters);
    }

    private Region extract$region(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(region_selection_Key, "region", parameters.injectedValue(region_selection_Key));
    }

    private List<String> extract$expression(CommandParameters parameters) {
        return this.expressionPart.value(parameters).asMultiple(string_Key);
    }

    private boolean extract$hollow5(CommandParameters parameters) {
        return this.hollowPart5.in(parameters);
    }

    private boolean extract$useRawCoords(CommandParameters parameters) {
        return this.useRawCoordsPart.in(parameters);
    }

    private boolean extract$offset(CommandParameters parameters) {
        return this.offsetPart.in(parameters);
    }

    private boolean extract$offsetCenter(CommandParameters parameters) {
        return this.offsetCenterPart.in(parameters);
    }

    private BiomeType extract$target(CommandParameters parameters) {
        return this.targetPart.value(parameters).asSingle(biomeType_Key);
    }

    private List<String> extract$expression2(CommandParameters parameters) {
        return this.expressionPart2.value(parameters).asMultiple(string_Key);
    }
}

