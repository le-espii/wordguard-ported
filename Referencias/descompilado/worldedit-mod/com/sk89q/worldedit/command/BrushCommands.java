/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.ToolCommands;
import com.sk89q.worldedit.command.factory.FeatureGeneratorFactory;
import com.sk89q.worldedit.command.factory.ReplaceFactory;
import com.sk89q.worldedit.command.factory.TreeGeneratorFactory;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.command.tool.brush.ButcherBrush;
import com.sk89q.worldedit.command.tool.brush.ClipboardBrush;
import com.sk89q.worldedit.command.tool.brush.CylinderBrush;
import com.sk89q.worldedit.command.tool.brush.GravityBrush;
import com.sk89q.worldedit.command.tool.brush.HollowCylinderBrush;
import com.sk89q.worldedit.command.tool.brush.HollowSphereBrush;
import com.sk89q.worldedit.command.tool.brush.ImageHeightmapBrush;
import com.sk89q.worldedit.command.tool.brush.MorphBrush;
import com.sk89q.worldedit.command.tool.brush.OperationFactoryBrush;
import com.sk89q.worldedit.command.tool.brush.SmoothBrush;
import com.sk89q.worldedit.command.tool.brush.SnowSmoothBrush;
import com.sk89q.worldedit.command.tool.brush.SphereBrush;
import com.sk89q.worldedit.command.tool.brush.SplatterBrush;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.CreatureButcher;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.factory.ApplyLayer;
import com.sk89q.worldedit.function.factory.ApplyRegion;
import com.sk89q.worldedit.function.factory.BiomeFactory;
import com.sk89q.worldedit.function.factory.Deform;
import com.sk89q.worldedit.function.factory.Paint;
import com.sk89q.worldedit.function.factory.Snow;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.ClipboardMask;
import com.sk89q.worldedit.internal.annotation.VertHeight;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.factory.CuboidRegionFactory;
import com.sk89q.worldedit.regions.factory.CylinderRegionFactory;
import com.sk89q.worldedit.regions.factory.FixedHeightCuboidRegionFactory;
import com.sk89q.worldedit.regions.factory.FixedHeightCylinderRegionFactory;
import com.sk89q.worldedit.regions.factory.RegionFactory;
import com.sk89q.worldedit.regions.factory.SphereRegionFactory;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.request.RequestExtent;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.asset.AssetLoadTask;
import com.sk89q.worldedit.util.asset.AssetLoader;
import com.sk89q.worldedit.util.asset.holder.ImageHeightmap;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import java.util.Optional;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class BrushCommands {
    private final WorldEdit worldEdit;
    private static final Component UNBIND_COMMAND_COMPONENT = ((TextComponent.Builder)TextComponent.builder("/brush unbind", TextColor.AQUA).clickEvent(ClickEvent.suggestCommand("/brush unbind"))).build();

    public BrushCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="none", aliases={"unbind"}, desc="Unbind a bound brush from your current item")
    void none(Player player, LocalSession session) throws WorldEditException {
        ToolCommands.setToolNone(player, session, true);
    }

    @Command(name="sphere", aliases={"s"}, desc="Choose the sphere brush")
    @CommandPermissions(value={"worldedit.brush.sphere"})
    public void sphereBrush(Player player, LocalSession session, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="The radius of the sphere", def={"2"}) double radius, @Switch(name=104, desc="Create hollow spheres instead") boolean hollow) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        Brush brush = hollow ? new HollowSphereBrush() : new SphereBrush();
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), brush, "worldedit.brush.sphere");
        tool.setFill(pattern);
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.sphere.equip", TextComponent.of(String.format("%.0f", radius))));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="cylinder", aliases={"cyl", "c"}, desc="Choose the cylinder brush")
    @CommandPermissions(value={"worldedit.brush.cylinder"})
    public void cylinderBrush(Player player, LocalSession session, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="The radius of the cylinder", def={"2"}) double radius, @Arg(desc="The height of the cylinder", def={"1"}) int height, @Switch(name=104, desc="Create hollow cylinders instead") boolean hollow) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        this.worldEdit.checkMaxBrushRadius(height);
        Brush brush = hollow ? new HollowCylinderBrush(height) : new CylinderBrush(height);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), brush, "worldedit.brush.cylinder");
        tool.setFill(pattern);
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.cylinder.equip", TextComponent.of((int)radius), TextComponent.of(height)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="splatter", aliases={"splat"}, desc="Choose the splatter brush")
    @CommandPermissions(value={"worldedit.brush.splatter"})
    public void splatterBrush(Player player, LocalSession session, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="The radius of the splatter", def={"2"}) double radius, @Arg(desc="The decay of the splatter between 0 and 10", def={"1"}) int decay) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        if (decay < 0 || decay > 10) {
            player.printError(TranslatableComponent.of("worldedit.brush.splatter.decay-out-of-range", TextComponent.of(decay)));
            return;
        }
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new SplatterBrush(decay), "worldedit.brush.splatter");
        tool.setFill(pattern);
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.splatter.equip", TextComponent.of((int)radius), TextComponent.of(decay)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="clipboard", aliases={"copy"}, desc="Choose the clipboard brush")
    @CommandPermissions(value={"worldedit.brush.clipboard"})
    public void clipboardBrush(Player player, LocalSession session, @Switch(name=97, desc="Don't paste air from the clipboard") boolean ignoreAir, @Switch(name=118, desc="Include structure void blocks") boolean pasteStructureVoid, @Switch(name=111, desc="Paste starting at the target location, instead of centering on it") boolean usingOrigin, @Switch(name=101, desc="Paste entities if available") boolean pasteEntities, @Switch(name=98, desc="Paste biomes if available") boolean pasteBiomes, @ClipboardMask @ArgFlag(name=109, desc="Skip blocks matching this mask in the clipboard") Mask sourceMask) throws WorldEditException {
        ClipboardHolder holder = session.getClipboard();
        Clipboard clipboard = holder.getClipboard();
        ClipboardHolder newHolder = new ClipboardHolder(clipboard);
        newHolder.setTransform(holder.getTransform());
        BlockVector3 size = clipboard.getDimensions();
        this.worldEdit.checkMaxBrushRadius((double)size.x() / 2.0 - 1.0);
        this.worldEdit.checkMaxBrushRadius((double)size.y() / 2.0 - 1.0);
        this.worldEdit.checkMaxBrushRadius((double)size.z() / 2.0 - 1.0);
        session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new ClipboardBrush(newHolder, ignoreAir, !pasteStructureVoid, usingOrigin, pasteEntities, pasteBiomes, sourceMask), "worldedit.brush.clipboard");
        player.printInfo(TranslatableComponent.of("worldedit.brush.clipboard.equip"));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="smooth", desc="Choose the terrain softener brush", descFooter="Example: '/brush smooth 2 4 grass_block,dirt,stone'")
    @CommandPermissions(value={"worldedit.brush.smooth"})
    public void smoothBrush(Player player, LocalSession session, @Arg(desc="The radius to sample for softening", def={"2"}) double radius, @Arg(desc="The number of iterations to perform", def={"4"}) int iterations, @Arg(desc="The mask of blocks to use for the heightmap", def={""}) Mask mask) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new SmoothBrush(iterations, mask), "worldedit.brush.smooth");
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.smooth.equip", TextComponent.of((int)radius), TextComponent.of(iterations), TranslatableComponent.of("worldedit.brush.smooth." + (mask == null ? "no" : "") + "filter")));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="snowsmooth", desc="Choose the snow terrain softener brush", descFooter="Example: '/brush snowsmooth 5 1 -l 3'")
    @CommandPermissions(value={"worldedit.brush.snowsmooth"})
    public void snowSmoothBrush(Player player, LocalSession session, @Arg(desc="The radius to sample for softening", def={"2"}) double radius, @Arg(desc="The number of iterations to perform", def={"4"}) int iterations, @ArgFlag(name=108, desc="The number of snow blocks under snow", def={"1"}) int snowBlockCount, @ArgFlag(name=109, desc="The mask of blocks to use for the heightmap") Mask mask) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new SnowSmoothBrush(iterations, snowBlockCount, mask), "worldedit.brush.snowsmooth");
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.snowsmooth.equip", TextComponent.of((int)radius), TextComponent.of(iterations), TranslatableComponent.of("worldedit.brush.snowsmooth." + (mask == null ? "no" : "") + "filter"), TextComponent.of(snowBlockCount)));
    }

    @Command(name="extinguish", aliases={"ex"}, desc="Shortcut fire extinguisher brush")
    @CommandPermissions(value={"worldedit.brush.ex"})
    public void extinguishBrush(Player player, LocalSession session, @Arg(desc="The radius to extinguish", def={"5"}) double radius) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new SphereBrush(), "worldedit.brush.ex");
        tool.setFill(BlockTypes.AIR.getDefaultState());
        tool.setSize(radius);
        tool.setMask(new BlockTypeMask((Extent)new RequestExtent(), BlockTypes.FIRE));
        player.printInfo(TranslatableComponent.of("worldedit.brush.extinguish.equip", TextComponent.of((int)radius)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="gravity", aliases={"grav"}, desc="Gravity brush, simulates the effect of gravity")
    @CommandPermissions(value={"worldedit.brush.gravity"})
    public void gravityBrush(Player player, LocalSession session, @Arg(desc="The radius to apply gravity in", def={"5"}) double radius, @VertHeight @ArgFlag(name=104, desc="Affect blocks between the given height, upwards and downwards, rather than the target location Y + radius", def={"default-vertical-height"}) Integer height) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(radius);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new GravityBrush(height), "worldedit.brush.gravity");
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.gravity.equip", TextComponent.of((int)radius)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="butcher", aliases={"kill"}, desc="Butcher brush, kills mobs within a radius")
    @CommandPermissions(value={"worldedit.brush.butcher"})
    public void butcherBrush(Player player, LocalSession session, @Arg(desc="Radius to kill mobs in", def={"5"}) double radius, @Switch(name=112, desc="Also kill pets") boolean killPets, @Switch(name=110, desc="Also kill NPCs") boolean killNpcs, @Switch(name=103, desc="Also kill golems") boolean killGolems, @Switch(name=97, desc="Also kill animals") boolean killAnimals, @Switch(name=98, desc="Also kill ambient mobs") boolean killAmbient, @Switch(name=116, desc="Also kill mobs with name tags") boolean killWithName, @Switch(name=102, desc="Also kill all friendly mobs (Applies the flags `-abgnpt`)") boolean killFriendly, @Switch(name=114, desc="Also destroy armor stands") boolean killArmorStands, @Switch(name=119, desc="Also kill water mobs") boolean killWater) throws WorldEditException {
        LocalConfiguration config = this.worldEdit.getConfiguration();
        double maxRadius = config.maxBrushRadius;
        if (player.hasPermission("worldedit.butcher")) {
            maxRadius = Math.max(config.maxBrushRadius, config.butcherMaxRadius);
        }
        if (radius > maxRadius) {
            player.printError(TranslatableComponent.of("worldedit.brush.radius-too-large", TextComponent.of(maxRadius)));
            return;
        }
        CreatureButcher flags = new CreatureButcher(player);
        flags.or(191, killFriendly);
        flags.or(1, killPets, "worldedit.butcher.pets");
        flags.or(2, killNpcs, "worldedit.butcher.npcs");
        flags.or(8, killGolems, "worldedit.butcher.golems");
        flags.or(4, killAnimals, "worldedit.butcher.animals");
        flags.or(16, killAmbient, "worldedit.butcher.ambient");
        flags.or(32, killWithName, "worldedit.butcher.tagged");
        flags.or(64, killArmorStands, "worldedit.butcher.armorstands");
        flags.or(128, killWater, "worldedit.butcher.water");
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new ButcherBrush(flags), "worldedit.brush.butcher");
        tool.setSize(radius);
        player.printInfo(TranslatableComponent.of("worldedit.brush.butcher.equip", TextComponent.of((int)radius)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="heightmap", desc="Heightmap brush, raises or lowers terrain using an image heightmap")
    @CommandPermissions(value={"worldedit.brush.heightmap"})
    void heightmapBrush(Player player, LocalSession session, @Arg(desc="The name of the image") String imageName, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="The intensity of the brush", def={"5"}) double intensity, @Switch(name=101, desc="Erase blocks instead of filling them") boolean erase, @Switch(name=102, desc="Don't change blocks above the selected height") boolean flatten, @Switch(name=114, desc="Randomizes the brush's height slightly.") boolean randomize) throws WorldEditException {
        Optional<AssetLoader<ImageHeightmap>> loader = this.worldEdit.getAssetLoaders().getAssetLoader(ImageHeightmap.class, imageName);
        if (loader.isPresent()) {
            this.worldEdit.checkMaxBrushRadius(radius);
            AssetLoadTask<ImageHeightmap> task = new AssetLoadTask<ImageHeightmap>(loader.get(), imageName);
            AsyncCommandBuilder.wrap(task, player).registerWithSupervisor(this.worldEdit.getSupervisor(), "Loading asset " + imageName).setDelayMessage(TranslatableComponent.of("worldedit.asset.load.loading")).setWorkingMessage(TranslatableComponent.of("worldedit.asset.load.still-loading")).onSuccess(TranslatableComponent.of("worldedit.brush.heightmap.equip", TextComponent.of((int)radius)), heightmap -> {
                BrushTool tool;
                try {
                    tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new ImageHeightmapBrush((ImageHeightmap)heightmap, intensity, erase, flatten, randomize), "worldedit.brush.heightmap");
                }
                catch (InvalidToolBindException e) {
                    throw new RuntimeException(e);
                }
                tool.setSize(radius);
                ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
            }).onFailure(TranslatableComponent.of("worldedit.asset.load.failed"), this.worldEdit.getPlatformManager().getPlatformCommandManager().getExceptionConverter()).buildAndExec(this.worldEdit.getExecutorService());
        } else {
            player.printError(TranslatableComponent.of("worldedit.brush.heightmap.unknown", TextComponent.of(imageName)));
        }
    }

    @Command(name="deform", desc="Deform brush, applies an expression to an area")
    @CommandPermissions(value={"worldedit.brush.deform"})
    public void deform(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="Expression to apply", def={"y-=0.2"}) String expression, @Switch(name=114, desc="Use the game's coordinate origin") boolean useRawCoords, @Switch(name=111, desc="Use the placement position as the origin") boolean usePlacement) throws WorldEditException {
        Deform deform = new Deform(expression);
        if (useRawCoords) {
            deform.setMode(Deform.Mode.RAW_COORD);
        } else if (usePlacement) {
            deform.setMode(Deform.Mode.OFFSET);
            deform.setOffset(localSession.getPlacementPosition(player).toVector3());
        }
        BrushCommands.setOperationBasedBrush(player, localSession, radius, deform, shape, "worldedit.brush.deform");
    }

    @Command(name="set", desc="Set brush, sets all blocks in the area")
    @CommandPermissions(value={"worldedit.brush.set"})
    public void set(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="The pattern of blocks to set") Pattern pattern) throws WorldEditException {
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new ApplyRegion(new ReplaceFactory(pattern)), shape, "worldedit.brush.set");
    }

    @Command(name="forest", desc="Forest brush, creates a forest in the area")
    @CommandPermissions(value={"worldedit.brush.forest"})
    public void forest(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="The density of the brush", def={"20"}) double density, @Arg(desc="The type of tree to use") TreeGenerator.TreeType type) throws WorldEditException {
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new Paint(new TreeGeneratorFactory(type), density / 100.0), shape, "worldedit.brush.forest");
    }

    @Command(name="feature", desc="Feature brush, paints Minecraft generation features")
    @CommandPermissions(value={"worldedit.brush.feature"})
    public void feature(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="The density of the brush", def={"5"}) double density, @Arg(desc="The type of feature to use") ConfiguredFeatureType type) throws WorldEditException {
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new ApplyRegion(new FeatureGeneratorFactory(type, density / 100.0)), shape, "worldedit.brush.feature");
    }

    @Command(name="raise", desc="Raise brush, raise all blocks by one")
    @CommandPermissions(value={"worldedit.brush.raise"})
    public void raise(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius) throws WorldEditException {
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new Deform("y-=1", Deform.Mode.RAW_COORD), shape, "worldedit.brush.raise");
    }

    @Command(name="lower", desc="Lower brush, lower all blocks by one")
    @CommandPermissions(value={"worldedit.brush.lower"})
    public void lower(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius) throws WorldEditException {
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new Deform("y+=1", Deform.Mode.RAW_COORD), shape, "worldedit.brush.lower");
    }

    @Command(name="snow", desc="Snow brush, sets snow in the area")
    @CommandPermissions(value={"worldedit.brush.snow"})
    public void snow(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Switch(name=115, desc="Whether to stack snow") boolean stack) throws WorldEditException {
        if (shape instanceof CylinderRegionFactory) {
            shape = new CylinderRegionFactory(radius);
        }
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new ApplyLayer(new Snow(stack)), shape, "worldedit.brush.snow");
    }

    @Command(name="biome", desc="Biome brush, sets biomes in the area")
    @CommandPermissions(value={"worldedit.brush.biome"})
    public void biome(Player player, LocalSession localSession, @Arg(desc="The shape of the region") RegionFactory shape, @Arg(desc="The size of the brush", def={"5"}) double radius, @Arg(desc="The biome type") BiomeType biomeType, @Switch(name=99, desc="Whether to set the full column") boolean column) throws WorldEditException {
        if (column) {
            if (shape instanceof CylinderRegionFactory || shape instanceof SphereRegionFactory) {
                shape = new FixedHeightCylinderRegionFactory(player.getWorld().getMinY(), player.getWorld().getMaxY());
            } else if (shape instanceof CuboidRegionFactory) {
                shape = new FixedHeightCuboidRegionFactory(player.getWorld().getMinY(), player.getWorld().getMaxY());
            } else {
                player.printError(TranslatableComponent.of("worldedit.brush.biome.column-supported-types"));
                return;
            }
        }
        BrushCommands.setOperationBasedBrush(player, localSession, radius, new ApplyRegion(new BiomeFactory(biomeType)), shape, "worldedit.brush.biome");
        player.printInfo(TranslatableComponent.of("worldedit.setbiome.warning"));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="morph", desc="Morph brush, morphs blocks in the area")
    @CommandPermissions(value={"worldedit.brush.morph"})
    public void morph(Player player, LocalSession session, @Arg(desc="The size of the brush", def={"5"}) double brushSize, @Arg(desc="Minimum number of faces for erosion", def={"3"}) int minErodeFaces, @Arg(desc="Erode iterations", def={"1"}) int numErodeIterations, @Arg(desc="Minimum number of faces for dilation", def={"3"}) int minDilateFaces, @Arg(desc="Dilate iterations", def={"1"}) int numDilateIterations) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(brushSize);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new MorphBrush(minErodeFaces, numErodeIterations, minDilateFaces, numDilateIterations), "worldedit.brush.morph");
        tool.setSize(brushSize);
        player.printInfo(TranslatableComponent.of("worldedit.brush.morph.equip", TextComponent.of((int)brushSize)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="erode", desc="Erode preset for morph brush, erodes blocks in the area")
    @CommandPermissions(value={"worldedit.brush.morph"})
    public void erode(Player player, LocalSession session, @Arg(desc="The size of the brush", def={"5"}) double brushSize) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(brushSize);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new MorphBrush(2, 1, 5, 1), "worldedit.brush.morph");
        tool.setSize(brushSize);
        player.printInfo(TranslatableComponent.of("worldedit.brush.morph.equip", TextComponent.of((int)brushSize)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    @Command(name="dilate", desc="Dilate preset for morph brush, dilates blocks in the area")
    @CommandPermissions(value={"worldedit.brush.morph"})
    public void dilate(Player player, LocalSession session, @Arg(desc="The size of the brush", def={"5"}) double brushSize) throws WorldEditException {
        this.worldEdit.checkMaxBrushRadius(brushSize);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new MorphBrush(5, 1, 2, 1), "worldedit.brush.morph");
        tool.setSize(brushSize);
        player.printInfo(TranslatableComponent.of("worldedit.brush.morph.equip", TextComponent.of((int)brushSize)));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }

    static void setOperationBasedBrush(Player player, LocalSession session, double radius, Contextual<? extends Operation> factory, RegionFactory shape, String permission) throws WorldEditException {
        WorldEdit.getInstance().checkMaxBrushRadius(radius);
        BrushTool tool = session.forceBrush(player.getItemInHand(HandSide.MAIN_HAND).getType(), new OperationFactoryBrush(factory, shape, session), permission);
        tool.setSize(radius);
        tool.setFill(null);
        player.printInfo(TranslatableComponent.of("worldedit.brush.operation.equip", TextComponent.of(factory.toString())));
        ToolCommands.sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
    }
}

