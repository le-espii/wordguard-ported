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
import com.sk89q.worldedit.command.BrushCommands;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.ClipboardMask;
import com.sk89q.worldedit.internal.annotation.VertHeight;
import com.sk89q.worldedit.regions.factory.RegionFactory;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
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

public final class BrushCommandsRegistration
implements CommandRegistration<BrushCommands>,
CommandPermissionsConditionGenerator.Registration {
    private static final Key<Pattern> pattern_Key = Key.of(Pattern.class);
    private static final Key<Double> double_Key = Key.of(Double.class);
    private static final Key<Integer> integer_Key = Key.of(Integer.class);
    private static final Key<Mask> mask_clipboardMask_Key = Key.of(Mask.class, ClipboardMask.class);
    private static final Key<Mask> mask_Key = Key.of(Mask.class);
    private static final Key<Integer> integer_vertHeight_Key = Key.of(Integer.class, VertHeight.class);
    private static final Key<String> string_Key = Key.of(String.class);
    private static final Key<RegionFactory> regionFactory_Key = Key.of(RegionFactory.class);
    private static final Key<TreeGenerator.TreeType> treeType_Key = Key.of(TreeGenerator.TreeType.class);
    private static final Key<ConfiguredFeatureType> configuredFeatureType_Key = Key.of(ConfiguredFeatureType.class);
    private static final Key<BiomeType> biomeType_Key = Key.of(BiomeType.class);
    private static final Key<Player> player_Key = Key.of(Player.class);
    private static final Key<LocalSession> localSession_Key = Key.of(LocalSession.class);
    private CommandManager commandManager;
    private BrushCommands containerInstance;
    private CommandPermissionsConditionGenerator commandPermissionsConditionGenerator;
    private ImmutableList<CommandCallListener> listeners;
    private final CommandArgument patternPart = CommandParts.arg(TranslatableComponent.of("pattern"), TextComponent.of("The pattern of blocks to set")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(pattern_Key)).build();
    private final CommandArgument radiusPart = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the sphere")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"2")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final NoArgCommandFlag hollowPart = CommandParts.flag('h', TextComponent.of("Create hollow spheres instead")).build();
    private final CommandArgument radiusPart2 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the cylinder")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"2")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument heightPart = CommandParts.arg(TranslatableComponent.of("height"), TextComponent.of("The height of the cylinder")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag hollowPart2 = CommandParts.flag('h', TextComponent.of("Create hollow cylinders instead")).build();
    private final CommandArgument radiusPart3 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius of the splatter")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"2")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument decayPart = CommandParts.arg(TranslatableComponent.of("decay"), TextComponent.of("The decay of the splatter between 0 and 10")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final NoArgCommandFlag ignoreAirPart = CommandParts.flag('a', TextComponent.of("Don't paste air from the clipboard")).build();
    private final NoArgCommandFlag pasteStructureVoidPart = CommandParts.flag('v', TextComponent.of("Include structure void blocks")).build();
    private final NoArgCommandFlag usingOriginPart = CommandParts.flag('o', TextComponent.of("Paste starting at the target location, instead of centering on it")).build();
    private final NoArgCommandFlag pasteEntitiesPart = CommandParts.flag('e', TextComponent.of("Paste entities if available")).build();
    private final NoArgCommandFlag pasteBiomesPart = CommandParts.flag('b', TextComponent.of("Paste biomes if available")).build();
    private final ArgAcceptingCommandFlag sourceMaskPart = CommandParts.flag('m', TextComponent.of("Skip blocks matching this mask in the clipboard")).withRequiredArg().argNamed(TranslatableComponent.of("sourceMask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_clipboardMask_Key)).build();
    private final CommandArgument radiusPart4 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to sample for softening")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"2")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument iterationsPart = CommandParts.arg(TranslatableComponent.of("iterations"), TextComponent.of("The number of iterations to perform")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"4")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument maskPart = CommandParts.arg(TranslatableComponent.of("mask"), TextComponent.of("The mask of blocks to use for the heightmap")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"")).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final ArgAcceptingCommandFlag snowBlockCountPart = CommandParts.flag('l', TextComponent.of("The number of snow blocks under snow")).withRequiredArg().argNamed(TranslatableComponent.of("snowBlockCount")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final ArgAcceptingCommandFlag maskPart2 = CommandParts.flag('m', TextComponent.of("The mask of blocks to use for the heightmap")).withRequiredArg().argNamed(TranslatableComponent.of("mask")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(mask_Key)).build();
    private final CommandArgument radiusPart5 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to extinguish")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument radiusPart6 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The radius to apply gravity in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final ArgAcceptingCommandFlag heightPart2 = CommandParts.flag('h', TextComponent.of("Affect blocks between the given height, upwards and downwards, rather than the target location Y + radius")).withRequiredArg().argNamed(TranslatableComponent.of("height")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"default-vertical-height")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_vertHeight_Key)).build();
    private final CommandArgument radiusPart7 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("Radius to kill mobs in")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final NoArgCommandFlag killPetsPart = CommandParts.flag('p', TextComponent.of("Also kill pets")).build();
    private final NoArgCommandFlag killNpcsPart = CommandParts.flag('n', TextComponent.of("Also kill NPCs")).build();
    private final NoArgCommandFlag killGolemsPart = CommandParts.flag('g', TextComponent.of("Also kill golems")).build();
    private final NoArgCommandFlag killAnimalsPart = CommandParts.flag('a', TextComponent.of("Also kill animals")).build();
    private final NoArgCommandFlag killAmbientPart = CommandParts.flag('b', TextComponent.of("Also kill ambient mobs")).build();
    private final NoArgCommandFlag killWithNamePart = CommandParts.flag('t', TextComponent.of("Also kill mobs with name tags")).build();
    private final NoArgCommandFlag killFriendlyPart = CommandParts.flag('f', TextComponent.of("Also kill all friendly mobs (Applies the flags `-abgnpt`)")).build();
    private final NoArgCommandFlag killArmorStandsPart = CommandParts.flag('r', TextComponent.of("Also destroy armor stands")).build();
    private final NoArgCommandFlag killWaterPart = CommandParts.flag('w', TextComponent.of("Also kill water mobs")).build();
    private final CommandArgument imageNamePart = CommandParts.arg(TranslatableComponent.of("imageName"), TextComponent.of("The name of the image")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final CommandArgument radiusPart8 = CommandParts.arg(TranslatableComponent.of("radius"), TextComponent.of("The size of the brush")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument intensityPart = CommandParts.arg(TranslatableComponent.of("intensity"), TextComponent.of("The intensity of the brush")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final NoArgCommandFlag erasePart = CommandParts.flag('e', TextComponent.of("Erase blocks instead of filling them")).build();
    private final NoArgCommandFlag flattenPart = CommandParts.flag('f', TextComponent.of("Don't change blocks above the selected height")).build();
    private final NoArgCommandFlag randomizePart = CommandParts.flag('r', TextComponent.of("Randomizes the brush's height slightly.")).build();
    private final CommandArgument shapePart = CommandParts.arg(TranslatableComponent.of("shape"), TextComponent.of("The shape of the region")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(regionFactory_Key)).build();
    private final CommandArgument expressionPart = CommandParts.arg(TranslatableComponent.of("expression"), TextComponent.of("Expression to apply")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"y-=0.2")).ofTypes((Collection<Key<?>>)ImmutableList.of(string_Key)).build();
    private final NoArgCommandFlag useRawCoordsPart = CommandParts.flag('r', TextComponent.of("Use the game's coordinate origin")).build();
    private final NoArgCommandFlag usePlacementPart = CommandParts.flag('o', TextComponent.of("Use the placement position as the origin")).build();
    private final CommandArgument densityPart = CommandParts.arg(TranslatableComponent.of("density"), TextComponent.of("The density of the brush")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"20")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument typePart = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("The type of tree to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(treeType_Key)).build();
    private final CommandArgument densityPart2 = CommandParts.arg(TranslatableComponent.of("density"), TextComponent.of("The density of the brush")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument typePart2 = CommandParts.arg(TranslatableComponent.of("type"), TextComponent.of("The type of feature to use")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(configuredFeatureType_Key)).build();
    private final NoArgCommandFlag stackPart = CommandParts.flag('s', TextComponent.of("Whether to stack snow")).build();
    private final CommandArgument biomeTypePart = CommandParts.arg(TranslatableComponent.of("biomeType"), TextComponent.of("The biome type")).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of(biomeType_Key)).build();
    private final NoArgCommandFlag columnPart = CommandParts.flag('c', TextComponent.of("Whether to set the full column")).build();
    private final CommandArgument brushSizePart = CommandParts.arg(TranslatableComponent.of("brushSize"), TextComponent.of("The size of the brush")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"5")).ofTypes((Collection<Key<?>>)ImmutableList.of(double_Key)).build();
    private final CommandArgument minErodeFacesPart = CommandParts.arg(TranslatableComponent.of("minErodeFaces"), TextComponent.of("Minimum number of faces for erosion")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"3")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument numErodeIterationsPart = CommandParts.arg(TranslatableComponent.of("numErodeIterations"), TextComponent.of("Erode iterations")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument minDilateFacesPart = CommandParts.arg(TranslatableComponent.of("minDilateFaces"), TextComponent.of("Minimum number of faces for dilation")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"3")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();
    private final CommandArgument numDilateIterationsPart = CommandParts.arg(TranslatableComponent.of("numDilateIterations"), TextComponent.of("Dilate iterations")).defaultsTo((Iterable<String>)ImmutableList.of((Object)"1")).ofTypes((Collection<Key<?>>)ImmutableList.of(integer_Key)).build();

    private BrushCommandsRegistration() {
        this.listeners = ImmutableList.of();
    }

    public static BrushCommandsRegistration builder() {
        return new BrushCommandsRegistration();
    }

    public BrushCommandsRegistration commandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public BrushCommandsRegistration containerInstance(BrushCommands containerInstance) {
        this.containerInstance = containerInstance;
        return this;
    }

    @Override
    public BrushCommandsRegistration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator commandPermissionsConditionGenerator) {
        this.commandPermissionsConditionGenerator = commandPermissionsConditionGenerator;
        return this;
    }

    public BrushCommandsRegistration listeners(Collection<CommandCallListener> listeners) {
        this.listeners = ImmutableList.copyOf(listeners);
        return this;
    }

    @Override
    public void build() {
        this.commandManager.register("none", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"unbind"));
            b.description(TextComponent.of("Unbind a bound brush from your current item"));
            b.parts((Collection<CommandPart>)ImmutableList.of());
            b.action(this::cmd$none);
        });
        this.commandManager.register("sphere", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"s"));
            b.description(TextComponent.of("Choose the sphere brush"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiusPart, (Object)this.hollowPart));
            b.action(this::cmd$sphere);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "sphereBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("cylinder", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"cyl", (Object)"c"));
            b.description(TextComponent.of("Choose the cylinder brush"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiusPart2, (Object)this.heightPart, (Object)this.hollowPart2));
            b.action(this::cmd$cylinder);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "cylinderBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("splatter", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"splat"));
            b.description(TextComponent.of("Choose the splatter brush"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.patternPart, (Object)this.radiusPart3, (Object)this.decayPart));
            b.action(this::cmd$splatter);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "splatterBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("clipboard", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"copy"));
            b.description(TextComponent.of("Choose the clipboard brush"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.ignoreAirPart, (Object)this.pasteStructureVoidPart, (Object)this.usingOriginPart, (Object)this.pasteEntitiesPart, (Object)this.pasteBiomesPart, (Object)this.sourceMaskPart));
            b.action(this::cmd$clipboard);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "clipboardBrush", Player.class, LocalSession.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("smooth", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose the terrain softener brush"));
            b.footer(TextComponent.of("Example: '/brush smooth 2 4 grass_block,dirt,stone'"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart4, (Object)this.iterationsPart, (Object)this.maskPart));
            b.action(this::cmd$smooth);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "smoothBrush", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("snowsmooth", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Choose the snow terrain softener brush"));
            b.footer(TextComponent.of("Example: '/brush snowsmooth 5 1 -l 3'"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart4, (Object)this.iterationsPart, (Object)this.snowBlockCountPart, (Object)this.maskPart2));
            b.action(this::cmd$snowsmooth);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "snowSmoothBrush", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Integer.TYPE, Mask.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("extinguish", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"ex"));
            b.description(TextComponent.of("Shortcut fire extinguisher brush"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart5));
            b.action(this::cmd$extinguish);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "extinguishBrush", Player.class, LocalSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("gravity", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"grav"));
            b.description(TextComponent.of("Gravity brush, simulates the effect of gravity"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart6, (Object)this.heightPart2));
            b.action(this::cmd$gravity);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "gravityBrush", Player.class, LocalSession.class, Double.TYPE, Integer.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("butcher", b -> {
            b.aliases((Collection<String>)ImmutableList.of((Object)"kill"));
            b.description(TextComponent.of("Butcher brush, kills mobs within a radius"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.radiusPart7, (Object)this.killPetsPart, (Object)this.killNpcsPart, (Object)this.killGolemsPart, (Object)this.killAnimalsPart, (Object)this.killAmbientPart, (Object)this.killWithNamePart, (Object)this.killFriendlyPart, (Object)this.killArmorStandsPart, (Object)this.killWaterPart));
            b.action(this::cmd$butcher);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "butcherBrush", Player.class, LocalSession.class, Double.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("heightmap", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Heightmap brush, raises or lowers terrain using an image heightmap"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.imageNamePart, (Object)this.radiusPart8, (Object)this.intensityPart, (Object)this.erasePart, (Object)this.flattenPart, (Object)this.randomizePart));
            b.action(this::cmd$heightmap);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "heightmapBrush", Player.class, LocalSession.class, String.class, Double.TYPE, Double.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("deform", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Deform brush, applies an expression to an area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.expressionPart, (Object)this.useRawCoordsPart, (Object)this.usePlacementPart));
            b.action(this::cmd$deform);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "deform", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, String.class, Boolean.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("set", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Set brush, sets all blocks in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.patternPart));
            b.action(this::cmd$set);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "set", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Pattern.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("forest", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Forest brush, creates a forest in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.densityPart, (Object)this.typePart));
            b.action(this::cmd$forest);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "forest", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Double.TYPE, TreeGenerator.TreeType.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("feature", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Feature brush, paints Minecraft generation features"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.densityPart2, (Object)this.typePart2));
            b.action(this::cmd$feature);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "feature", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Double.TYPE, ConfiguredFeatureType.class);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("raise", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Raise brush, raise all blocks by one"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8));
            b.action(this::cmd$raise);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "raise", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("lower", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Lower brush, lower all blocks by one"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8));
            b.action(this::cmd$lower);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "lower", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("snow", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Snow brush, sets snow in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.stackPart));
            b.action(this::cmd$snow);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "snow", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("biome", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Biome brush, sets biomes in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.shapePart, (Object)this.radiusPart8, (Object)this.biomeTypePart, (Object)this.columnPart));
            b.action(this::cmd$biome);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "biome", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, BiomeType.class, Boolean.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("morph", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Morph brush, morphs blocks in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.brushSizePart, (Object)this.minErodeFacesPart, (Object)this.numErodeIterationsPart, (Object)this.minDilateFacesPart, (Object)this.numDilateIterationsPart));
            b.action(this::cmd$morph);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "morph", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("erode", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Erode preset for morph brush, erodes blocks in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.brushSizePart));
            b.action(this::cmd$erode);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "erode", Player.class, LocalSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
        this.commandManager.register("dilate", b -> {
            b.aliases((Collection<String>)ImmutableList.of());
            b.description(TextComponent.of("Dilate preset for morph brush, dilates blocks in the area"));
            b.parts((Collection<CommandPart>)ImmutableList.of((Object)this.brushSizePart));
            b.action(this::cmd$dilate);
            Method commandMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "dilate", Player.class, LocalSession.class, Double.TYPE);
            Command.Condition condition = this.commandPermissionsConditionGenerator.generateCondition(commandMethod);
            b.condition(condition);
        });
    }

    private int cmd$none(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "none", Player.class, LocalSession.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.none(this.extract$player(parameters), this.extract$session(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$sphere(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "sphereBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.sphereBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern(parameters), this.extract$radius(parameters), this.extract$hollow(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$cylinder(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "cylinderBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Integer.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.cylinderBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern(parameters), this.extract$radius2(parameters), this.extract$height(parameters), this.extract$hollow2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$splatter(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "splatterBrush", Player.class, LocalSession.class, Pattern.class, Double.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.splatterBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$pattern(parameters), this.extract$radius3(parameters), this.extract$decay(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$clipboard(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "clipboardBrush", Player.class, LocalSession.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.clipboardBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$ignoreAir(parameters), this.extract$pasteStructureVoid(parameters), this.extract$usingOrigin(parameters), this.extract$pasteEntities(parameters), this.extract$pasteBiomes(parameters), this.extract$sourceMask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$smooth(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "smoothBrush", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.smoothBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$radius4(parameters), this.extract$iterations(parameters), this.extract$mask(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$snowsmooth(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "snowSmoothBrush", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Integer.TYPE, Mask.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.snowSmoothBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$radius4(parameters), this.extract$iterations(parameters), this.extract$snowBlockCount(parameters), this.extract$mask2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$extinguish(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "extinguishBrush", Player.class, LocalSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.extinguishBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$radius5(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$gravity(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "gravityBrush", Player.class, LocalSession.class, Double.TYPE, Integer.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.gravityBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$radius6(parameters), this.extract$height2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$butcher(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "butcherBrush", Player.class, LocalSession.class, Double.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.butcherBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$radius7(parameters), this.extract$killPets(parameters), this.extract$killNpcs(parameters), this.extract$killGolems(parameters), this.extract$killAnimals(parameters), this.extract$killAmbient(parameters), this.extract$killWithName(parameters), this.extract$killFriendly(parameters), this.extract$killArmorStands(parameters), this.extract$killWater(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$heightmap(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "heightmapBrush", Player.class, LocalSession.class, String.class, Double.TYPE, Double.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.heightmapBrush(this.extract$player(parameters), this.extract$session(parameters), this.extract$imageName(parameters), this.extract$radius8(parameters), this.extract$intensity(parameters), this.extract$erase(parameters), this.extract$flatten(parameters), this.extract$randomize(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$deform(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "deform", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, String.class, Boolean.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.deform(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$expression(parameters), this.extract$useRawCoords(parameters), this.extract$usePlacement(parameters));
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
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "set", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Pattern.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.set(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$pattern(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$forest(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "forest", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Double.TYPE, TreeGenerator.TreeType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.forest(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$density(parameters), this.extract$type(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$feature(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "feature", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Double.TYPE, ConfiguredFeatureType.class);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.feature(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$density2(parameters), this.extract$type2(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$raise(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "raise", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.raise(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$lower(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "lower", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.lower(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$snow(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "snow", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.snow(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$stack(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$biome(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "biome", Player.class, LocalSession.class, RegionFactory.class, Double.TYPE, BiomeType.class, Boolean.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.biome(this.extract$player(parameters), this.extract$localSession(parameters), this.extract$shape(parameters), this.extract$radius8(parameters), this.extract$biomeType(parameters), this.extract$column(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$morph(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "morph", Player.class, LocalSession.class, Double.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.morph(this.extract$player(parameters), this.extract$session(parameters), this.extract$brushSize(parameters), this.extract$minErodeFaces(parameters), this.extract$numErodeIterations(parameters), this.extract$minDilateFaces(parameters), this.extract$numDilateIterations(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$erode(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "erode", Player.class, LocalSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.erode(this.extract$player(parameters), this.extract$session(parameters), this.extract$brushSize(parameters));
            int result = 1;
            RegistrationUtil.listenersAfterCall(this.listeners, cmdMethod, parameters);
            return result;
        }
        catch (Throwable t) {
            RegistrationUtil.listenersAfterThrow(this.listeners, cmdMethod, parameters, t);
            throw t;
        }
    }

    private int cmd$dilate(CommandParameters parameters) throws WorldEditException {
        Method cmdMethod = RegistrationUtil.getCommandMethod(BrushCommands.class, "dilate", Player.class, LocalSession.class, Double.TYPE);
        RegistrationUtil.listenersBeforeCall(this.listeners, cmdMethod, parameters);
        try {
            this.containerInstance.dilate(this.extract$player(parameters), this.extract$session(parameters), this.extract$brushSize(parameters));
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

    private LocalSession extract$session(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "session", parameters.injectedValue(localSession_Key));
    }

    private Pattern extract$pattern(CommandParameters parameters) {
        return this.patternPart.value(parameters).asSingle(pattern_Key);
    }

    private double extract$radius(CommandParameters parameters) {
        return this.radiusPart.value(parameters).asSingle(double_Key);
    }

    private boolean extract$hollow(CommandParameters parameters) {
        return this.hollowPart.in(parameters);
    }

    private double extract$radius2(CommandParameters parameters) {
        return this.radiusPart2.value(parameters).asSingle(double_Key);
    }

    private int extract$height(CommandParameters parameters) {
        return this.heightPart.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$hollow2(CommandParameters parameters) {
        return this.hollowPart2.in(parameters);
    }

    private double extract$radius3(CommandParameters parameters) {
        return this.radiusPart3.value(parameters).asSingle(double_Key);
    }

    private int extract$decay(CommandParameters parameters) {
        return this.decayPart.value(parameters).asSingle(integer_Key);
    }

    private boolean extract$ignoreAir(CommandParameters parameters) {
        return this.ignoreAirPart.in(parameters);
    }

    private boolean extract$pasteStructureVoid(CommandParameters parameters) {
        return this.pasteStructureVoidPart.in(parameters);
    }

    private boolean extract$usingOrigin(CommandParameters parameters) {
        return this.usingOriginPart.in(parameters);
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

    private double extract$radius4(CommandParameters parameters) {
        return this.radiusPart4.value(parameters).asSingle(double_Key);
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

    private double extract$radius5(CommandParameters parameters) {
        return this.radiusPart5.value(parameters).asSingle(double_Key);
    }

    private double extract$radius6(CommandParameters parameters) {
        return this.radiusPart6.value(parameters).asSingle(double_Key);
    }

    private Integer extract$height2(CommandParameters parameters) {
        return this.heightPart2.value(parameters).asSingle(integer_vertHeight_Key);
    }

    private double extract$radius7(CommandParameters parameters) {
        return this.radiusPart7.value(parameters).asSingle(double_Key);
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

    private String extract$imageName(CommandParameters parameters) {
        return this.imageNamePart.value(parameters).asSingle(string_Key);
    }

    private double extract$radius8(CommandParameters parameters) {
        return this.radiusPart8.value(parameters).asSingle(double_Key);
    }

    private double extract$intensity(CommandParameters parameters) {
        return this.intensityPart.value(parameters).asSingle(double_Key);
    }

    private boolean extract$erase(CommandParameters parameters) {
        return this.erasePart.in(parameters);
    }

    private boolean extract$flatten(CommandParameters parameters) {
        return this.flattenPart.in(parameters);
    }

    private boolean extract$randomize(CommandParameters parameters) {
        return this.randomizePart.in(parameters);
    }

    private LocalSession extract$localSession(CommandParameters parameters) {
        return RegistrationUtil.requireOptional(localSession_Key, "localSession", parameters.injectedValue(localSession_Key));
    }

    private RegionFactory extract$shape(CommandParameters parameters) {
        return this.shapePart.value(parameters).asSingle(regionFactory_Key);
    }

    private String extract$expression(CommandParameters parameters) {
        return this.expressionPart.value(parameters).asSingle(string_Key);
    }

    private boolean extract$useRawCoords(CommandParameters parameters) {
        return this.useRawCoordsPart.in(parameters);
    }

    private boolean extract$usePlacement(CommandParameters parameters) {
        return this.usePlacementPart.in(parameters);
    }

    private double extract$density(CommandParameters parameters) {
        return this.densityPart.value(parameters).asSingle(double_Key);
    }

    private TreeGenerator.TreeType extract$type(CommandParameters parameters) {
        return this.typePart.value(parameters).asSingle(treeType_Key);
    }

    private double extract$density2(CommandParameters parameters) {
        return this.densityPart2.value(parameters).asSingle(double_Key);
    }

    private ConfiguredFeatureType extract$type2(CommandParameters parameters) {
        return this.typePart2.value(parameters).asSingle(configuredFeatureType_Key);
    }

    private boolean extract$stack(CommandParameters parameters) {
        return this.stackPart.in(parameters);
    }

    private BiomeType extract$biomeType(CommandParameters parameters) {
        return this.biomeTypePart.value(parameters).asSingle(biomeType_Key);
    }

    private boolean extract$column(CommandParameters parameters) {
        return this.columnPart.in(parameters);
    }

    private double extract$brushSize(CommandParameters parameters) {
        return this.brushSizePart.value(parameters).asSingle(double_Key);
    }

    private int extract$minErodeFaces(CommandParameters parameters) {
        return this.minErodeFacesPart.value(parameters).asSingle(integer_Key);
    }

    private int extract$numErodeIterations(CommandParameters parameters) {
        return this.numErodeIterationsPart.value(parameters).asSingle(integer_Key);
    }

    private int extract$minDilateFaces(CommandParameters parameters) {
        return this.minDilateFacesPart.value(parameters).asSingle(integer_Key);
    }

    private int extract$numDilateIterations(CommandParameters parameters) {
        return this.numDilateIterationsPart.value(parameters).asSingle(integer_Key);
    }
}

