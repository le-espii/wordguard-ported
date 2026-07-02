/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.ClipboardMask;
import com.sk89q.worldedit.internal.annotation.Direction;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.ExtendingCuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class ClipboardCommands {
    private void checkRegionBounds(Region region, LocalSession session) throws MaxChangedBlocksException {
        int limit = session.getBlockChangeLimit();
        if (limit >= 0 && region.getBoundingBox().getVolume() >= (long)limit) {
            throw new MaxChangedBlocksException(limit);
        }
    }

    @Command(name="/copy", desc="Copy the selection to the clipboard")
    @CommandPermissions(value={"worldedit.clipboard.copy"})
    public void copy(Actor actor, LocalSession session, EditSession editSession, @Selection Region region, @Switch(name=101, desc="Also copy entities") boolean copyEntities, @Switch(name=98, desc="Also copy biomes") boolean copyBiomes, @ArgFlag(name=109, desc="Set the include mask, non-matching blocks become air") Mask mask) throws WorldEditException {
        this.checkRegionBounds(region, session);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(session.getPlacementPosition(actor));
        ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        copy.setCopyingEntities(copyEntities);
        copy.setCopyingBiomes(copyBiomes);
        if (mask != null) {
            copy.setSourceMask(mask);
        }
        Operations.completeLegacy(copy);
        session.setClipboard(new ClipboardHolder(clipboard));
        copy.getStatusMessages().forEach(actor::print);
    }

    @Command(name="/cut", desc="Cut the selection to the clipboard")
    @CommandPermissions(value={"worldedit.clipboard.cut"})
    @Logging(value=Logging.LogMode.REGION)
    public void cut(Actor actor, LocalSession session, EditSession editSession, @Selection Region region, @Arg(desc="Pattern to leave in place of the selection", def={"air"}) Pattern leavePattern, @Switch(name=101, desc="Also cut entities") boolean copyEntities, @Switch(name=98, desc="Also copy biomes, source biomes are unaffected") boolean copyBiomes, @ArgFlag(name=109, desc="Set the exclude mask, non-matching blocks become air") Mask mask) throws WorldEditException {
        this.checkRegionBounds(region, session);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(session.getPlacementPosition(actor));
        ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        copy.setSourceFunction(new BlockReplace(editSession, leavePattern));
        copy.setCopyingEntities(copyEntities);
        copy.setRemovingEntities(true);
        copy.setCopyingBiomes(copyBiomes);
        if (mask != null) {
            copy.setSourceMask(mask);
        }
        Operations.completeLegacy(copy);
        session.setClipboard(new ClipboardHolder(clipboard));
        copy.getStatusMessages().forEach(actor::print);
    }

    @Command(name="/paste", desc="Paste the clipboard's contents")
    @CommandPermissions(value={"worldedit.clipboard.paste"})
    @Logging(value=Logging.LogMode.PLACEMENT)
    public void paste(Actor actor, World world, LocalSession session, EditSession editSession, @Switch(name=97, desc="Skip air blocks") boolean ignoreAirBlocks, @Switch(name=118, desc="Include structure void blocks") boolean pasteStructureVoid, @Switch(name=111, desc="Paste at the original position") boolean atOrigin, @Switch(name=115, desc="Select the region after pasting") boolean selectPasted, @Switch(name=110, desc="No paste, select only. (Implies -s)") boolean onlySelect, @Switch(name=101, desc="Paste entities if available") boolean pasteEntities, @Switch(name=98, desc="Paste biomes if available") boolean pasteBiomes, @ClipboardMask @ArgFlag(name=109, desc="Only paste blocks matching this mask") Mask sourceMask) throws WorldEditException {
        BlockVector3 to;
        ClipboardHolder holder = session.getClipboard();
        Clipboard clipboard = holder.getClipboard();
        Region region = clipboard.getRegion();
        ArrayList messages = Lists.newArrayList();
        BlockVector3 blockVector3 = to = atOrigin ? clipboard.getOrigin() : session.getPlacementPosition(actor);
        if (!onlySelect) {
            Operation operation = holder.createPaste(editSession).to(to).ignoreAirBlocks(ignoreAirBlocks).ignoreStructureVoidBlocks(!pasteStructureVoid).copyBiomes(pasteBiomes).copyEntities(pasteEntities).maskSource(sourceMask).build();
            Operations.completeLegacy(operation);
            messages.addAll(Lists.newArrayList(operation.getStatusMessages()));
        }
        if (selectPasted || onlySelect) {
            BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
            Vector3 realTo = to.toVector3().add(holder.getTransform().apply(clipboardOffset.toVector3()));
            Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
            CuboidRegionSelector selector = session.getRegionSelector(world) instanceof ExtendingCuboidRegionSelector ? new ExtendingCuboidRegionSelector(world, realTo.toBlockPoint(), max.toBlockPoint()) : new CuboidRegionSelector(world, realTo.toBlockPoint(), max.toBlockPoint());
            session.setRegionSelector(world, selector);
            selector.learnChanges();
            selector.explainRegionAdjust(actor, session);
        }
        if (onlySelect) {
            actor.printInfo(TranslatableComponent.of("worldedit.paste.selected"));
        } else {
            actor.printInfo(TranslatableComponent.of("worldedit.paste.pasted", TextComponent.of(to.toString())));
        }
        messages.forEach(actor::print);
    }

    @Command(name="/rotate", desc="Rotate the contents of the clipboard", descFooter="Non-destructively rotate the contents of the clipboard.\nAngles are provided in degrees and a positive angle will result in a clockwise rotation. Multiple rotations can be stacked. Interpolation is not performed so angles should be a multiple of 90 degrees.\n")
    @CommandPermissions(value={"worldedit.clipboard.rotate"})
    public void rotate(Actor actor, LocalSession session, @Arg(desc="Amount to rotate on the y-axis") double rotateY, @Arg(desc="Amount to rotate on the x-axis", def={"0"}) double rotateX, @Arg(desc="Amount to rotate on the z-axis", def={"0"}) double rotateZ) throws WorldEditException {
        if (Math.abs(rotateY % 90.0) > 0.001 || Math.abs(rotateX % 90.0) > 0.001 || Math.abs(rotateZ % 90.0) > 0.001) {
            actor.printDebug(TranslatableComponent.of("worldedit.rotate.no-interpolation"));
        }
        ClipboardHolder holder = session.getClipboard();
        AffineTransform transform = new AffineTransform();
        transform = transform.rotateY(-rotateY);
        transform = transform.rotateX(-rotateX);
        transform = transform.rotateZ(-rotateZ);
        holder.setTransform(holder.getTransform().combine(transform));
        actor.printInfo(TranslatableComponent.of("worldedit.rotate.rotated"));
    }

    @Command(name="/flip", desc="Flip the contents of the clipboard across the origin")
    @CommandPermissions(value={"worldedit.clipboard.flip"})
    public void flip(Actor actor, LocalSession session, @Direction @Arg(desc="The direction to flip, defaults to look direction.", def={"me"}) BlockVector3 direction) throws WorldEditException {
        ClipboardHolder holder = session.getClipboard();
        AffineTransform transform = new AffineTransform();
        transform = transform.scale(direction.abs().multiply(-2).add(1, 1, 1).toVector3());
        holder.setTransform(holder.getTransform().combine(transform));
        actor.printInfo(TranslatableComponent.of("worldedit.flip.flipped"));
    }

    @Command(name="clearclipboard", desc="Clear your clipboard")
    @CommandPermissions(value={"worldedit.clipboard.clear"})
    public void clearClipboard(Actor actor, LocalSession session) {
        session.setClipboard(null);
        actor.printInfo(TranslatableComponent.of("worldedit.clearclipboard.cleared"));
    }
}

