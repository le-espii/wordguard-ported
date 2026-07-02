/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.Radii;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import java.util.List;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class GenerationCommands {
    private final WorldEdit worldEdit;

    public GenerationCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="/hcyl", desc="Generates a hollow cylinder.")
    @CommandPermissions(value={"worldedit.generation.cylinder"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int hcyl(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to generate") Pattern pattern, @Radii(value=2) @Arg(desc="The radii of the cylinder. 1st is N/S, 2nd is E/W") List<Double> radii, @Arg(desc="The height of the cylinder", def={"1"}) int height) throws WorldEditException {
        return this.cyl(actor, session, editSession, pattern, radii, height, true);
    }

    @Command(name="/cyl", desc="Generates a cylinder.")
    @CommandPermissions(value={"worldedit.generation.cylinder"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int cyl(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to generate") Pattern pattern, @Radii(value=2) @Arg(desc="The radii of the cylinder. 1st is N/S, 2nd is E/W") List<Double> radii, @Arg(desc="The height of the cylinder", def={"1"}) int height, @Switch(name=104, desc="Make a hollow cylinder") boolean hollow) throws WorldEditException {
        double radiusX;
        double radiusZ;
        switch (radii.size()) {
            case 1: {
                radiusX = radiusZ = Math.max(1.0, radii.get(0));
                break;
            }
            case 2: {
                radiusX = Math.max(1.0, radii.get(0));
                radiusZ = Math.max(1.0, radii.get(1));
                break;
            }
            default: {
                actor.printError(TranslatableComponent.of("worldedit.cyl.invalid-radius"));
                return 0;
            }
        }
        this.worldEdit.checkMaxRadius(radiusX);
        this.worldEdit.checkMaxRadius(radiusZ);
        this.worldEdit.checkMaxRadius(height);
        BlockVector3 pos = session.getPlacementPosition(actor);
        int affected = editSession.makeCylinder(pos, pattern, radiusX, radiusZ, height, !hollow);
        actor.printInfo(TranslatableComponent.of("worldedit.cyl.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/cone", desc="Generates a cone.")
    @CommandPermissions(value={"worldedit.generation.cone"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int cone(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to generate") Pattern pattern, @Radii(value=2) @Arg(desc="The radii of the cone. 1st is N/S, 2nd is E/W") List<Double> radii, @Arg(desc="The height of the cone", def={"1"}) int height, @Switch(name=104, desc="Make a hollow cone") boolean hollow, @Arg(desc="Thickness of the hollow cone", def={"1"}) double thickness) throws WorldEditException {
        double radiusX;
        double radiusZ;
        switch (radii.size()) {
            case 1: {
                radiusX = radiusZ = Math.max(1.0, radii.get(0));
                break;
            }
            case 2: {
                radiusX = Math.max(1.0, radii.get(0));
                radiusZ = Math.max(1.0, radii.get(1));
                break;
            }
            default: {
                actor.printError(TranslatableComponent.of("worldedit.cone.invalid-radius"));
                return 0;
            }
        }
        this.worldEdit.checkMaxRadius(radiusX);
        this.worldEdit.checkMaxRadius(radiusZ);
        this.worldEdit.checkMaxRadius(height);
        BlockVector3 pos = session.getPlacementPosition(actor);
        int affected = editSession.makeCone(pos, pattern, radiusX, radiusZ, height, !hollow, thickness);
        actor.printInfo(TranslatableComponent.of("worldedit.cone.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/hsphere", desc="Generates a hollow sphere.")
    @CommandPermissions(value={"worldedit.generation.sphere"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int hsphere(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to generate") Pattern pattern, @Radii(value=3) @Arg(desc="The radii of the sphere. Order is N/S, U/D, E/W") List<Double> radii, @Switch(name=114, desc="Raise the bottom of the sphere to the placement position") boolean raised) throws WorldEditException {
        return this.sphere(actor, session, editSession, pattern, radii, raised, true);
    }

    @Command(name="/sphere", desc="Generates a filled sphere.")
    @CommandPermissions(value={"worldedit.generation.sphere"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int sphere(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to generate") Pattern pattern, @Radii(value=3) @Arg(desc="The radii of the sphere. Order is N/S, U/D, E/W") List<Double> radii, @Switch(name=114, desc="Raise the bottom of the sphere to the placement position") boolean raised, @Switch(name=104, desc="Make a hollow sphere") boolean hollow) throws WorldEditException {
        double radiusX;
        double radiusY;
        double radiusZ;
        switch (radii.size()) {
            case 1: {
                radiusY = radiusZ = Math.max(0.0, radii.get(0));
                radiusX = radiusZ;
                break;
            }
            case 3: {
                radiusX = Math.max(0.0, radii.get(0));
                radiusY = Math.max(0.0, radii.get(1));
                radiusZ = Math.max(0.0, radii.get(2));
                break;
            }
            default: {
                actor.printError(TranslatableComponent.of("worldedit.sphere.invalid-radius"));
                return 0;
            }
        }
        this.worldEdit.checkMaxRadius(radiusX);
        this.worldEdit.checkMaxRadius(radiusY);
        this.worldEdit.checkMaxRadius(radiusZ);
        BlockVector3 pos = session.getPlacementPosition(actor);
        if (raised) {
            pos = pos.add(0, (int)radiusY, 0);
        }
        int affected = editSession.makeSphere(pos, pattern, radiusX, radiusY, radiusZ, !hollow);
        if (actor instanceof Player) {
            ((Player)actor).findFreePosition();
        }
        actor.printInfo(TranslatableComponent.of("worldedit.sphere.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="forestgen", desc="Generate a forest")
    @CommandPermissions(value={"worldedit.generation.forest"})
    @Logging(value=Logging.LogMode.POSITION)
    public int forestGen(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The size of the forest, in blocks", def={"10"}) int size, @Arg(desc="The type of forest", def={"tree"}) TreeGenerator.TreeType type, @Arg(desc="The density of the forest, between 0 and 100", def={"5"}) double density) throws WorldEditException {
        CommandUtil.checkCommandArgument(0.0 <= density && density <= 100.0, "Density must be between 0 and 100");
        this.worldEdit.checkMaxRadius(size);
        int affected = editSession.makeForest(session.getPlacementPosition(actor), size, density /= 100.0, type);
        actor.printInfo(TranslatableComponent.of("worldedit.forestgen.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="pumpkins", desc="Generate pumpkin patches")
    @CommandPermissions(value={"worldedit.generation.pumpkins"})
    @Logging(value=Logging.LogMode.POSITION)
    public int pumpkins(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The size of the patch", def={"10"}) int size) throws WorldEditException {
        this.worldEdit.checkMaxRadius(size);
        int affected = editSession.makePumpkinPatches(session.getPlacementPosition(actor), size);
        actor.printInfo(TranslatableComponent.of("worldedit.pumpkins.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/feature", desc="Generate Minecraft features")
    @CommandPermissions(value={"worldedit.generation.feature"})
    @Logging(value=Logging.LogMode.POSITION)
    public int feature(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The feature") ConfiguredFeatureType feature) throws WorldEditException {
        if (editSession.getWorld().generateFeature(feature, editSession, session.getPlacementPosition(actor))) {
            actor.printInfo(TranslatableComponent.of("worldedit.feature.created"));
        } else {
            actor.printError(TranslatableComponent.of("worldedit.feature.failed"));
        }
        return 0;
    }

    @Command(name="/structure", desc="Generate Minecraft structures")
    @CommandPermissions(value={"worldedit.generation.structure"})
    @Logging(value=Logging.LogMode.POSITION)
    public int structure(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The structure") StructureType feature) throws WorldEditException {
        if (editSession.getWorld().generateStructure(feature, editSession, session.getPlacementPosition(actor))) {
            actor.printInfo(TranslatableComponent.of("worldedit.structure.created"));
        } else {
            actor.printError(TranslatableComponent.of("worldedit.structure.failed"));
        }
        return 0;
    }

    @Command(name="/hpyramid", desc="Generate a hollow pyramid")
    @CommandPermissions(value={"worldedit.generation.pyramid"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int hollowPyramid(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="The size of the pyramid") int size) throws WorldEditException {
        return this.pyramid(actor, session, editSession, pattern, size, true);
    }

    @Command(name="/pyramid", desc="Generate a filled pyramid")
    @CommandPermissions(value={"worldedit.generation.pyramid"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public int pyramid(Actor actor, LocalSession session, EditSession editSession, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="The size of the pyramid") int size, @Switch(name=104, desc="Make a hollow pyramid") boolean hollow) throws WorldEditException {
        this.worldEdit.checkMaxRadius(size);
        BlockVector3 pos = session.getPlacementPosition(actor);
        int affected = editSession.makePyramid(pos, pattern, size, !hollow);
        if (actor instanceof Player) {
            ((Player)actor).findFreePosition();
        }
        actor.printInfo(TranslatableComponent.of("worldedit.pyramid.created", TextComponent.of(affected)));
        return affected;
    }

    @Command(name="/generate", aliases={"/gen", "/g"}, desc="Generates a shape according to a formula.", descFooter="For details, see https://ehub.to/we/expr")
    @CommandPermissions(value={"worldedit.generation.shape"})
    @Logging(value=Logging.LogMode.ALL)
    public int generate(Actor actor, LocalSession session, EditSession editSession, @Selection Region region, @Arg(desc="The pattern of blocks to set") Pattern pattern, @Arg(desc="Expression to test block placement locations and set block type", variable=true) List<String> expression, @Switch(name=104, desc="Generate a hollow shape") boolean hollow, @Switch(name=114, desc="Use the game's coordinate origin") boolean useRawCoords, @Switch(name=111, desc="Use the placement's coordinate origin") boolean offset, @Switch(name=99, desc="Use the selection's center as origin") boolean offsetCenter) throws WorldEditException {
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
            unit = max.subtract(zero = max.add(min).multiply(0.5));
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
            int affected = editSession.makeShape(region, zero, unit, pattern, String.join((CharSequence)" ", expression), hollow, session.getTimeout());
            if (actor instanceof Player) {
                ((Player)actor).findFreePosition();
            }
            actor.printInfo(TranslatableComponent.of("worldedit.generate.created", TextComponent.of(affected)));
            return affected;
        }
        catch (ExpressionException e) {
            actor.printError(TextComponent.of(e.getMessage()));
            return 0;
        }
    }

    @Command(name="/generatebiome", aliases={"/genbiome", "/gb"}, desc="Sets biome according to a formula.", descFooter="For details, see https://ehub.to/we/expr")
    @CommandPermissions(value={"worldedit.generation.shape.biome"})
    @Logging(value=Logging.LogMode.ALL)
    public int generateBiome(Actor actor, LocalSession session, EditSession editSession, @Selection Region region, @Arg(desc="The biome type to set") BiomeType target, @Arg(desc="Expression to test block placement locations and set biome type", variable=true) List<String> expression, @Switch(name=104, desc="Generate a hollow shape") boolean hollow, @Switch(name=114, desc="Use the game's coordinate origin") boolean useRawCoords, @Switch(name=111, desc="Use the placement's coordinate origin") boolean offset, @Switch(name=99, desc="Use the selection's center as origin") boolean offsetCenter) throws WorldEditException {
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
            unit = max.subtract(zero = max.add(min).multiply(0.5));
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
            int affected = editSession.makeBiomeShape(region, zero, unit, target, String.join((CharSequence)" ", expression), hollow, session.getTimeout());
            actor.printInfo(TranslatableComponent.of("worldedit.generatebiome.changed", TextComponent.of(affected)));
            return affected;
        }
        catch (ExpressionException e) {
            actor.printError(TextComponent.of(e.getMessage()));
            return 0;
        }
    }
}

