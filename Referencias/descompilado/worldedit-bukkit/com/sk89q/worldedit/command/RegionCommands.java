/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.GroundFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.block.ApplySideEffect;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.generator.FloraGenerator;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.NoiseFilter2D;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.visitor.LayerVisitor;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.internal.annotation.Offset;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.convolution.GaussianKernel;
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.math.convolution.SnowHeightMap;
import com.sk89q.worldedit.math.noise.RandomNoise;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.List;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class RegionCommands {
    @Command(name="/set", desc="Sets all the blocks in the region")
    @CommandPermissions(value={"worldedit.region.set"})
    @Logging(value=Logging.LogMode.REGION)
    public int set(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to set") Pattern pattern) {
        BlockReplace set = new BlockReplace(editSession, pattern);
        RegionVisitor visitor = new RegionVisitor(region, set);
        Operations.completeBlindly(visitor);
        ArrayList messages = Lists.newArrayList(visitor.getStatusMessages());
        if (messages.isEmpty()) {
            actor.printInfo(TranslatableComponent.of("worldedit.set.done"));
        } else {
            actor.printInfo(TranslatableComponent.of("worldedit.set.done.verbose", TextUtils.join(messages, TextComponent.of(", "))));
        }
        return visitor.getAffected();
    }

    @Command(name="/line", desc="Draws line segments between cuboid selection corners or convex polyhedral selection vertices", descFooter="Can only be used with a cuboid selection or a convex polyhedral selection")
    @CommandPermissions(value={"worldedit.region.line"})
    @Logging(value=Logging.LogMode.REGION)
    public int line(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to place") Pattern pattern, @Arg(desc="The thickness of the line", def={"0"}) double thickness, @Switch(name=104, desc="Generate only a shell") boolean shell) throws WorldEditException {
        ImmutableList vectors;
        if (!(region instanceof CuboidRegion) && !(region instanceof ConvexPolyhedralRegion)) {
            actor.printError(TranslatableComponent.of("worldedit.line.invalid-type"));
            return 0;
        }
        CommandUtil.checkCommandArgument(thickness >= 0.0, "Thickness must be >= 0");
        WorldEdit.getInstance().checkMaxRadius(thickness);
        if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion)region;
            vectors = ImmutableList.of((Object)((Object)cuboidRegion.getPos1()), (Object)((Object)cuboidRegion.getPos2()));
        } else {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion)region;
            vectors = ImmutableList.copyOf(convexRegion.getVertices());
        }
        int blocksChanged = editSession.drawLine(pattern, (List<BlockVector3>)vectors, thickness, !shell);
        actor.printInfo(TranslatableComponent.of("worldedit.line.changed", TextComponent.of(blocksChanged)));
        return blocksChanged;
    }

    @Command(name="/curve", desc="Draws a spline through selected points", descFooter="Can only be used with a convex polyhedral selection")
    @CommandPermissions(value={"worldedit.region.curve"})
    @Logging(value=Logging.LogMode.REGION)
    public int curve(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to place") Pattern pattern, @Arg(desc="The thickness of the curve", def={"0"}) double thickness, @Switch(name=104, desc="Generate only a shell") boolean shell) throws WorldEditException {
        if (!(region instanceof ConvexPolyhedralRegion)) {
            actor.printError(TranslatableComponent.of("worldedit.curve.invalid-type"));
            return 0;
        }
        ConvexPolyhedralRegion cpregion = (ConvexPolyhedralRegion)region;
        CommandUtil.checkCommandArgument(thickness >= 0.0, "Thickness must be >= 0");
        WorldEdit.getInstance().checkMaxRadius(thickness);
        ArrayList<BlockVector3> vectors = new ArrayList<BlockVector3>(cpregion.getVertices());
        int blocksChanged = editSession.drawSpline(pattern, vectors, 0.0, 0.0, 0.0, 10.0, thickness, !shell);
        actor.printInfo(TranslatableComponent.of("worldedit.curve.changed", TextComponent.of(blocksChanged)));
        return blocksChanged;
    }

    @Command(name="/replace", aliases={"/re", "/rep"}, desc="Replace all blocks in the selection with another")
    @CommandPermissions(value={"worldedit.region.replace"})
    @Logging(value=Logging.LogMode.REGION)
    public int replace(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The mask representing blocks to replace", def={""}) Mask from, @Arg(desc="The pattern of blocks to replace with") Pattern to) throws WorldEditException {
        if (from == null) {
            from = new ExistingBlockMask(editSession);
        }
        int affected = editSession.replaceBlocks(region, from, to);
        actor.printInfo(TranslatableComponent.of("worldedit.replace.replaced", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/overlay", desc="Set a block on top of blocks in the region")
    @CommandPermissions(value={"worldedit.region.overlay"})
    @Logging(value=Logging.LogMode.REGION)
    public int overlay(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to overlay") Pattern pattern) throws WorldEditException {
        int affected = editSession.overlayCuboidBlocks(region, pattern);
        actor.printInfo(TranslatableComponent.of("worldedit.overlay.overlaid", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/center", aliases={"/middle"}, desc="Set the center block(s)")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.region.center"})
    public int center(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to set") Pattern pattern) throws WorldEditException {
        int affected = editSession.center(region, pattern);
        actor.printInfo(TranslatableComponent.of("worldedit.center.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/naturalize", desc="3 layers of dirt on top then rock below")
    @CommandPermissions(value={"worldedit.region.naturalize"})
    @Logging(value=Logging.LogMode.REGION)
    public int naturalize(Actor actor, EditSession editSession, @Selection Region region) throws WorldEditException {
        int affected = editSession.naturalizeCuboidBlocks(region);
        actor.printInfo(TranslatableComponent.of("worldedit.naturalize.naturalized", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/walls", desc="Build the four sides of the selection")
    @CommandPermissions(value={"worldedit.region.walls"})
    @Logging(value=Logging.LogMode.REGION)
    public int walls(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to set") Pattern pattern) throws WorldEditException {
        int affected = editSession.makeWalls(region, pattern);
        actor.printInfo(TranslatableComponent.of("worldedit.walls.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/faces", aliases={"/outline"}, desc="Build the walls, ceiling, and floor of a selection")
    @CommandPermissions(value={"worldedit.region.faces"})
    @Logging(value=Logging.LogMode.REGION)
    public int faces(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to set") Pattern pattern) throws WorldEditException {
        int affected = editSession.makeFaces(region, pattern);
        actor.printInfo(TranslatableComponent.of("worldedit.faces.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/smooth", desc="Smooth the elevation in the selection", descFooter="Example: '//smooth 1 grass_block,dirt,stone' would only smooth natural surface terrain.")
    @CommandPermissions(value={"worldedit.region.smooth"})
    @Logging(value=Logging.LogMode.REGION)
    public int smooth(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="# of iterations to perform", def={"1"}) int iterations, @Arg(desc="The mask of blocks to use as the height map", def={""}) Mask mask) throws WorldEditException {
        HeightMap heightMap = new HeightMap(editSession, region, mask);
        HeightMapFilter filter = new HeightMapFilter(new GaussianKernel(5, 1.0));
        int affected = heightMap.applyFilter(filter, iterations);
        actor.printInfo(TranslatableComponent.of("worldedit.smooth.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/snowsmooth", desc="Smooth the elevation in the selection with snow layers", descFooter="Example: '//snowsmooth 1 -m snow_block,snow' would only smooth snow terrain.")
    @CommandPermissions(value={"worldedit.region.snowsmooth"})
    @Logging(value=Logging.LogMode.REGION)
    public int snowSmooth(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="# of iterations to perform", def={"1"}) int iterations, @ArgFlag(name=108, desc="Set the amount of snow blocks under the snow", def={"1"}) int snowBlockCount, @ArgFlag(name=109, desc="The mask of blocks to use as the height map") Mask mask) throws WorldEditException {
        SnowHeightMap heightMap = new SnowHeightMap(editSession, region, mask);
        HeightMapFilter filter = new HeightMapFilter(new GaussianKernel(5, 1.0));
        float[] changed = heightMap.applyFilter(filter, iterations);
        int affected = heightMap.applyChanges(changed, snowBlockCount);
        actor.printInfo(TranslatableComponent.of("worldedit.snowsmooth.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/move", desc="Move the contents of the selection")
    @CommandPermissions(value={"worldedit.region.move"})
    @Logging(value=Logging.LogMode.ORIENTATION_REGION)
    public int move(Actor actor, World world, EditSession editSession, LocalSession session, @Selection Region region, @Arg(desc="number of times to apply the offset", def={"1"}) int multiplier, @Offset @Arg(desc="The offset to move", def={"forward"}) BlockVector3 offset, @Arg(desc="The pattern of blocks to leave", def={"air"}) Pattern replace, @Switch(name=115, desc="Shift the selection to the target location") boolean moveSelection, @Switch(name=97, desc="Ignore air blocks") boolean ignoreAirBlocks, @Switch(name=101, desc="Also copy entities") boolean copyEntities, @Switch(name=98, desc="Also copy biomes") boolean copyBiomes, @ArgFlag(name=109, desc="Set the include mask, non-matching blocks become air") Mask mask) throws WorldEditException {
        CommandUtil.checkCommandArgument(multiplier >= 1, "Multiplier must be >= 1");
        Mask combinedMask = ignoreAirBlocks ? (mask == null ? new ExistingBlockMask(editSession) : new MaskIntersection(mask, new ExistingBlockMask(editSession))) : mask;
        int affected = editSession.moveRegion(region, offset, multiplier, copyEntities, copyBiomes, combinedMask, replace);
        if (moveSelection) {
            try {
                region.shift(offset.multiply(multiplier));
                session.getRegionSelector(world).learnChanges();
                session.getRegionSelector(world).explainRegionAdjust(actor, session);
            }
            catch (RegionOperationException e) {
                actor.printError(e.getRichMessage());
            }
        }
        actor.printInfo(TranslatableComponent.of("worldedit.move.moved", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/stack", desc="Repeat the contents of the selection")
    @CommandPermissions(value={"worldedit.region.stack"})
    @Logging(value=Logging.LogMode.ORIENTATION_REGION)
    public int stack(Actor actor, World world, EditSession editSession, LocalSession session, @Selection Region region, @Arg(desc="# of copies to stack", def={"1"}) int count, @Offset @Arg(desc="How far to move the contents each stack", def={"forward"}) BlockVector3 offset, @Switch(name=115, desc="Shift the selection to the last stacked copy") boolean moveSelection, @Switch(name=97, desc="Ignore air blocks") boolean ignoreAirBlocks, @Switch(name=101, desc="Also copy entities") boolean copyEntities, @Switch(name=98, desc="Also copy biomes") boolean copyBiomes, @Switch(name=114, desc="Use block units") boolean blockUnits, @ArgFlag(name=109, desc="Set the include mask, non-matching blocks become air") Mask mask) throws WorldEditException {
        CommandUtil.checkCommandArgument(count >= 1, "Count must be >= 1");
        Mask combinedMask = ignoreAirBlocks ? (mask == null ? new ExistingBlockMask(editSession) : new MaskIntersection(mask, new ExistingBlockMask(editSession))) : mask;
        int affected = blockUnits ? editSession.stackRegionBlockUnits(region, offset, count, copyEntities, copyBiomes, combinedMask) : editSession.stackCuboidRegion(region, offset, count, copyEntities, copyBiomes, combinedMask);
        if (moveSelection) {
            try {
                BlockVector3 size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);
                BlockVector3 shiftSize = blockUnits ? offset : offset.multiply(size);
                BlockVector3 shiftVector = shiftSize.multiply(count);
                region.shift(shiftVector);
                session.getRegionSelector(world).learnChanges();
                session.getRegionSelector(world).explainRegionAdjust(actor, session);
            }
            catch (RegionOperationException e) {
                actor.printError(e.getRichMessage());
            }
        }
        actor.printInfo(TranslatableComponent.of("worldedit.stack.changed", TextComponent.of(affected)));
        return affected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Command(name="/regen", desc="Regenerates the contents of the selection")
    @CommandPermissions(value={"worldedit.regen"})
    @Logging(value=Logging.LogMode.REGION)
    void regenerate(Actor actor, World world, LocalSession session, EditSession editSession, @Selection Region region, @Arg(desc="The seed to regenerate with, otherwise uses world seed", def={""}) Long seed, @Switch(name=98, desc="Regenerate biomes as well") boolean regenBiomes) {
        boolean success;
        Mask mask = session.getMask();
        try {
            session.setMask(null);
            RegenOptions options = RegenOptions.builder().seed(seed).regenBiomes(regenBiomes).build();
            success = world.regenerate(region, editSession, options);
        }
        finally {
            session.setMask(mask);
        }
        if (success) {
            actor.printInfo(TranslatableComponent.of("worldedit.regen.regenerated"));
        } else {
            actor.printError(TranslatableComponent.of("worldedit.regen.failed"));
        }
    }

    @Command(name="/deform", desc="Deforms a selected region with an expression", descFooter="The expression is executed for each block and is expected\nto modify the variables x, y and z to point to a new block\nto fetch. For details, see https://ehub.to/we/expr")
    @CommandPermissions(value={"worldedit.region.deform"})
    @Logging(value=Logging.LogMode.ALL)
    public int deform(Actor actor, LocalSession session, EditSession editSession, @Selection Region region, @Arg(desc="The expression to use", variable=true) List<String> expression, @Switch(name=114, desc="Use the game's coordinate origin") boolean useRawCoords, @Switch(name=111, desc="Use the placement's coordinate origin") boolean offset, @Switch(name=99, desc="Use the selection's center as origin") boolean offsetCenter) throws WorldEditException {
        Vector3 unit;
        Vector3 zero;
        if (useRawCoords) {
            zero = Vector3.ZERO;
            unit = Vector3.ONE;
        } else if (offset) {
            zero = session.getPlacementPosition(actor).toVector3();
            unit = Vector3.ONE;
        } else if (offsetCenter) {
            min = region.getMinimumPoint().toVector3();
            Vector3 max = region.getMaximumPoint().toVector3();
            zero = max.add(min).multiply(0.5);
            unit = Vector3.ONE;
        } else {
            min = region.getMinimumPoint().toVector3();
            Vector3 max = region.getMaximumPoint().toVector3();
            unit = max.subtract(zero = max.add(min).divide(2.0));
            if (unit.x() == 0.0) {
                unit = unit.withX(1.0);
            }
            if (unit.y() == 0.0) {
                unit = unit.withY(1.0);
            }
            if (unit.z() == 0.0) {
                unit = unit.withZ(1.0);
            }
        }
        try {
            int affected = editSession.deformRegion(region, zero, unit, String.join((CharSequence)" ", expression), session.getTimeout());
            if (actor instanceof Player) {
                ((Player)actor).findFreePosition();
            }
            actor.printInfo(TranslatableComponent.of("worldedit.deform.deformed", TextComponent.of(affected)));
            return affected;
        }
        catch (ExpressionException e) {
            actor.printError(TextComponent.of(e.getMessage()));
            return 0;
        }
    }

    @Command(name="/hollow", desc="Hollows out the object contained in this selection", descFooter="Thickness is measured in manhattan distance.")
    @CommandPermissions(value={"worldedit.region.hollow"})
    @Logging(value=Logging.LogMode.REGION)
    public int hollow(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="Thickness of the shell to leave", def={"0"}) int thickness, @Arg(desc="The pattern of blocks to replace the hollowed area with", def={"air"}) Pattern pattern) throws WorldEditException {
        CommandUtil.checkCommandArgument(thickness >= 0, "Thickness must be >= 0");
        int affected = editSession.hollowOutRegion(region, thickness, pattern);
        actor.printInfo(TranslatableComponent.of("worldedit.hollow.changed", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/forest", desc="Make a forest within the region")
    @CommandPermissions(value={"worldedit.region.forest"})
    @Logging(value=Logging.LogMode.REGION)
    public int forest(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The type of tree to place", def={"tree"}) TreeGenerator.TreeType type, @Arg(desc="The density of the forest", def={"5"}) double density) throws WorldEditException {
        CommandUtil.checkCommandArgument(0.0 <= density && density <= 100.0, "Density must be in [0, 100]");
        int affected = editSession.makeForest(region, density / 100.0, type);
        actor.printInfo(TranslatableComponent.of("worldedit.forest.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/flora", desc="Make flora within the region")
    @CommandPermissions(value={"worldedit.region.flora"})
    @Logging(value=Logging.LogMode.REGION)
    public int flora(Actor actor, EditSession editSession, @Selection Region region, @Arg(desc="The density of the forest", def={"5"}) double density) throws WorldEditException {
        CommandUtil.checkCommandArgument(0.0 <= density && density <= 100.0, "Density must be in [0, 100]");
        FloraGenerator generator = new FloraGenerator(editSession);
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(editSession), generator);
        LayerVisitor visitor = new LayerVisitor(Regions.asFlatRegion(region), Regions.minimumBlockY(region), Regions.maximumBlockY(region), ground);
        visitor.setMask(new NoiseFilter2D(new RandomNoise(), density /= 100.0));
        Operations.completeLegacy(visitor);
        int affected = ground.getAffected();
        actor.printInfo(TranslatableComponent.of("worldedit.flora.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/update", desc="Apply side effects to your selection")
    @CommandPermissions(value={"worldedit.update"})
    void update(Actor actor, LocalSession session, World injectedWorld, @Arg(desc="The side effects", def={""}) SideEffectSet sideEffectSet) throws WorldEditException {
        if (sideEffectSet == null) {
            sideEffectSet = SideEffectSet.defaults();
        }
        RegionFunction apply = new ApplySideEffect(injectedWorld, sideEffectSet);
        if (session.getMask() != null) {
            apply = new RegionMaskingFilter(session.getMask(), apply);
        }
        RegionVisitor visitor = new RegionVisitor(session.getSelection(injectedWorld), apply);
        Operations.complete(visitor);
        actor.printInfo(TranslatableComponent.of("worldedit.update"));
    }
}

