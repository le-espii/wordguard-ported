/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Strings;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.command.argument.SelectorChoice;
import com.sk89q.worldedit.command.argument.SelectorChoiceOrList;
import com.sk89q.worldedit.command.tool.NavigationWand;
import com.sk89q.worldedit.command.tool.SelectionWand;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.command.util.WorldEditAsyncCommandBuilder;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.block.BlockDistributionCounter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.internal.annotation.Chunk3d;
import com.sk89q.worldedit.internal.annotation.MultiDirection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.ExtendingCuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.RegionSelectorType;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.CommandListBox;
import com.sk89q.worldedit.util.formatting.component.InvalidComponentException;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldedit.world.storage.ChunkStore;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;
import org.enginehub.piston.exception.StopExecutionException;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class SelectionCommands {
    private final WorldEdit we;

    public SelectionCommands(WorldEdit we) {
        this.we = we;
    }

    @Nullable
    private BlockVector3 getLocatablePosition(Actor actor) {
        if (actor instanceof Locatable) {
            Locatable locatable = (Locatable)((Object)actor);
            return locatable.getLocation().toVector().toBlockPoint();
        }
        actor.printError(TranslatableComponent.of("worldedit.pos.console-require-coords"));
        return null;
    }

    @Command(name="/pos", desc="Set positions")
    @Logging(value=Logging.LogMode.POSITION)
    @CommandPermissions(value={"worldedit.selection.pos"})
    public void pos(Actor actor, World world, LocalSession session, @Arg(desc="Coordinates to set the primary position to. Defaults to the player position if not passed.", def={""}) BlockVector3 pos1, @Arg(desc="Coordinates to add as secondary positions. Defaults to the player position if not passed.", def={""}, variable=true) List<BlockVector3> pos2, @ArgFlag(name=115, desc="Selector to switch to") SelectorChoice selectorChoice) throws WorldEditException {
        if (pos1 == null && (pos1 = this.getLocatablePosition(actor)) == null) {
            return;
        }
        if (pos2.isEmpty()) {
            BlockVector3 newPos2 = this.getLocatablePosition(actor);
            if (newPos2 == null) {
                return;
            }
            pos2 = List.of((Object)((Object)newPos2));
        }
        RegionSelector regionSelector = session.getRegionSelector(world);
        if (selectorChoice != null) {
            RegionSelector newSelector = selectorChoice.createNewSelector(world);
            session.setRegionSelector(world, newSelector);
            selectorChoice.explainNewSelector(actor);
            regionSelector = newSelector;
        }
        regionSelector.selectPrimary(pos1, ActorSelectorLimits.forActor(actor));
        for (BlockVector3 vector : pos2) {
            regionSelector.selectSecondary(vector, ActorSelectorLimits.forActor(actor));
        }
        session.dispatchCUISelection(actor);
        for (Component line : regionSelector.getSelectionInfoLines()) {
            actor.printInfo(line);
        }
        actor.printInfo(TranslatableComponent.of("worldedit.selection.updated"));
    }

    @Command(name="/pos1", desc="Set position 1")
    @Logging(value=Logging.LogMode.POSITION)
    @CommandPermissions(value={"worldedit.selection.pos"})
    public void pos1(Actor actor, World world, LocalSession session, @Arg(desc="Coordinates to set position 1 to", def={""}) BlockVector3 coordinates) {
        if (coordinates == null && (coordinates = this.getLocatablePosition(actor)) == null) {
            return;
        }
        if (!session.getRegionSelector(world).selectPrimary(coordinates, ActorSelectorLimits.forActor(actor))) {
            actor.printError(TranslatableComponent.of("worldedit.pos.already-set"));
            return;
        }
        session.getRegionSelector(world).explainPrimarySelection(actor, session, coordinates);
    }

    @Command(name="/pos2", desc="Set position 2")
    @Logging(value=Logging.LogMode.POSITION)
    @CommandPermissions(value={"worldedit.selection.pos"})
    public void pos2(Actor actor, World world, LocalSession session, @Arg(desc="Coordinates to set position 2 to", def={""}) BlockVector3 coordinates) {
        if (coordinates == null && (coordinates = this.getLocatablePosition(actor)) == null) {
            return;
        }
        if (!session.getRegionSelector(world).selectSecondary(coordinates, ActorSelectorLimits.forActor(actor))) {
            actor.printError(TranslatableComponent.of("worldedit.pos.already-set"));
            return;
        }
        session.getRegionSelector(world).explainSecondarySelection(actor, session, coordinates);
    }

    @Command(name="/hpos1", desc="Set position 1 to targeted block")
    @CommandPermissions(value={"worldedit.selection.hpos"})
    public void hpos1(Player player, LocalSession session) {
        Location pos = player.getBlockTrace(300);
        if (pos != null) {
            if (!session.getRegionSelector(player.getWorld()).selectPrimary(pos.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
                player.printError(TranslatableComponent.of("worldedit.hpos.already-set"));
                return;
            }
            session.getRegionSelector(player.getWorld()).explainPrimarySelection(player, session, pos.toVector().toBlockPoint());
        } else {
            player.printError(TranslatableComponent.of("worldedit.hpos.no-block"));
        }
    }

    @Command(name="/hpos2", desc="Set position 2 to targeted block")
    @CommandPermissions(value={"worldedit.selection.hpos"})
    public void hpos2(Player player, LocalSession session) {
        Location pos = player.getBlockTrace(300);
        if (pos != null) {
            if (!session.getRegionSelector(player.getWorld()).selectSecondary(pos.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
                player.printError(TranslatableComponent.of("worldedit.hpos.already-set"));
                return;
            }
            session.getRegionSelector(player.getWorld()).explainSecondarySelection(player, session, pos.toVector().toBlockPoint());
        } else {
            player.printError(TranslatableComponent.of("worldedit.hpos.no-block"));
        }
    }

    @Command(name="/chunk", desc="Set the selection to your current chunk.", descFooter="This command selects 256-block-tall areas,\nwhich can be specified by the y-coordinate.\nE.g. -c x,1,z will select from y=256 to y=511.")
    @Logging(value=Logging.LogMode.POSITION)
    @CommandPermissions(value={"worldedit.selection.chunk"})
    public void chunk(Actor actor, World world, LocalSession session, @Chunk3d @Arg(desc="The chunk to select", def={""}) BlockVector3 coordinates, @Switch(name=115, desc="Expand your selection to encompass all chunks that are part of it") boolean expandSelection, @Switch(name=99, desc="Use chunk coordinates instead of block coordinates") boolean useChunkCoordinates) throws WorldEditException {
        BlockVector3 max;
        BlockVector3 min;
        if (expandSelection) {
            Region region = session.getSelection(world);
            int minChunkY = world.getMinY() >> 8;
            int maxChunkY = world.getMaxY() >> 8;
            BlockVector3 minChunk = ChunkStore.toChunk3d(region.getMinimumPoint()).clampY(minChunkY, maxChunkY);
            BlockVector3 maxChunk = ChunkStore.toChunk3d(region.getMaximumPoint()).clampY(minChunkY, maxChunkY);
            min = minChunk.shl(4, 8, 4);
            max = maxChunk.shl(4, 8, 4).add(15, 255, 15);
            actor.printInfo(TranslatableComponent.of("worldedit.chunk.selected-multiple", TextComponent.of(minChunk.x()), TextComponent.of(minChunk.y()), TextComponent.of(minChunk.z()), TextComponent.of(maxChunk.x()), TextComponent.of(maxChunk.y()), TextComponent.of(maxChunk.z())));
        } else {
            BlockVector3 minChunk;
            if (coordinates != null) {
                minChunk = useChunkCoordinates ? coordinates : ChunkStore.toChunk3d(coordinates);
            } else if (actor instanceof Locatable) {
                minChunk = ChunkStore.toChunk3d(((Locatable)((Object)actor)).getBlockLocation().toVector().toBlockPoint());
            } else {
                throw new StopExecutionException(TextComponent.of("A player or coordinates are required."));
            }
            min = minChunk.shl(4, 8, 4);
            max = min.add(15, 255, 15);
            actor.printInfo(TranslatableComponent.of("worldedit.chunk.selected", TextComponent.of(minChunk.x()), TextComponent.of(minChunk.y()), TextComponent.of(minChunk.z())));
        }
        CuboidRegionSelector selector = session.getRegionSelector(world) instanceof ExtendingCuboidRegionSelector ? new ExtendingCuboidRegionSelector(world) : new CuboidRegionSelector(world);
        selector.selectPrimary(min, ActorSelectorLimits.forActor(actor));
        selector.selectSecondary(max, ActorSelectorLimits.forActor(actor));
        session.setRegionSelector(world, selector);
        session.dispatchCUISelection(actor);
    }

    @Command(name="/wand", desc="Get the wand item", descFooter="You must have also have permission to use at least one of the features of the requested wand.")
    @CommandPermissions(value={"worldedit.wand"})
    public void wand(Player player, LocalSession session, @Switch(name=110, desc="Get a navigation wand") boolean navWand) throws WorldEditException {
        ItemType itemType;
        String wandId;
        Tool tool;
        Tool tool2 = tool = navWand ? new NavigationWand() : new SelectionWand();
        if (!tool.canUse(player)) {
            player.printError(TranslatableComponent.of("worldedit.command.permissions"));
            return;
        }
        String string = wandId = navWand ? session.getNavWandItem() : session.getWandItem();
        if (wandId == null) {
            String string2 = wandId = navWand ? this.we.getConfiguration().navigationWand : this.we.getConfiguration().wandItem;
        }
        if ((itemType = ItemTypes.get(wandId)) == null) {
            player.printError(TranslatableComponent.of("worldedit.wand.invalid"));
            return;
        }
        player.giveItem(new BaseItemStack(itemType, 1));
        session.setTool(itemType, tool);
        player.printInfo(TranslatableComponent.of("worldedit.wand." + (navWand ? "nav" : "sel") + "wand.info"));
    }

    @Command(name="toggleeditwand", desc="Remind the user that the wand is now a tool and can be unbound with /tool none.")
    @CommandPermissions(value={"worldedit.wand.toggle"})
    public void toggleWand(Player player) {
        player.printInfo(TranslatableComponent.of("worldedit.wand.selwand.now.tool", TextComponent.of("/tool none", TextColor.AQUA).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/tool none")), TextComponent.of("/tool selwand", TextColor.AQUA).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/tool selwand")), TextComponent.of("//wand", TextColor.AQUA).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "//wand"))));
    }

    @Command(name="/contract", desc="Contract the selection area")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.selection.contract"})
    public void contract(Actor actor, World world, LocalSession session, @Arg(desc="Amount to contract the selection by") int amount, @Arg(desc="Amount to contract the selection by in the other direction", def={"0"}) int reverseAmount, @MultiDirection @Arg(desc="Direction to contract", def={"me"}) List<BlockVector3> direction) throws WorldEditException {
        try {
            Region region = session.getSelection(world);
            long oldSize = region.getVolume();
            if (reverseAmount == 0) {
                for (BlockVector3 dir : direction) {
                    region.contract(dir.multiply(amount));
                }
            } else {
                for (BlockVector3 dir : direction) {
                    region.contract(dir.multiply(amount), dir.multiply(-reverseAmount));
                }
            }
            session.getRegionSelector(world).learnChanges();
            long newSize = region.getVolume();
            session.getRegionSelector(world).explainRegionAdjust(actor, session);
            actor.printInfo(TranslatableComponent.of("worldedit.contract.contracted", TextComponent.of(oldSize - newSize)));
        }
        catch (RegionOperationException e) {
            actor.printError(TextComponent.of(e.getMessage()));
        }
    }

    @Command(name="/shift", desc="Shift the selection area")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.selection.shift"})
    public void shift(Actor actor, World world, LocalSession session, @Arg(desc="Amount to shift the selection by") int amount, @MultiDirection @Arg(desc="Direction to contract", def={"me"}) List<BlockVector3> direction) throws WorldEditException {
        try {
            Region region = session.getSelection(world);
            for (BlockVector3 dir : direction) {
                region.shift(dir.multiply(amount));
            }
            session.getRegionSelector(world).learnChanges();
            session.getRegionSelector(world).explainRegionAdjust(actor, session);
            actor.printInfo(TranslatableComponent.of("worldedit.shift.shifted"));
        }
        catch (RegionOperationException e) {
            actor.printError(TextComponent.of(e.getMessage()));
        }
    }

    @Command(name="/outset", desc="Outset the selection area")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.selection.outset"})
    public void outset(Actor actor, World world, LocalSession session, @Arg(desc="Amount to expand the selection by in all directions") int amount, @Switch(name=104, desc="Only expand horizontally") boolean onlyHorizontal, @Switch(name=118, desc="Only expand vertically") boolean onlyVertical) throws WorldEditException {
        Region region = session.getSelection(world);
        region.expand(this.getChangesForEachDir(amount, onlyHorizontal, onlyVertical));
        session.getRegionSelector(world).learnChanges();
        session.getRegionSelector(world).explainRegionAdjust(actor, session);
        actor.printInfo(TranslatableComponent.of("worldedit.outset.outset"));
    }

    @Command(name="/inset", desc="Inset the selection area")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.selection.inset"})
    public void inset(Actor actor, World world, LocalSession session, @Arg(desc="Amount to contract the selection by in all directions") int amount, @Switch(name=104, desc="Only contract horizontally") boolean onlyHorizontal, @Switch(name=118, desc="Only contract vertically") boolean onlyVertical) throws WorldEditException {
        Region region = session.getSelection(world);
        region.contract(this.getChangesForEachDir(amount, onlyHorizontal, onlyVertical));
        session.getRegionSelector(world).learnChanges();
        session.getRegionSelector(world).explainRegionAdjust(actor, session);
        actor.printInfo(TranslatableComponent.of("worldedit.inset.inset"));
    }

    private BlockVector3[] getChangesForEachDir(int amount, boolean onlyHorizontal, boolean onlyVertical) {
        Stream.Builder<BlockVector3> changes = Stream.builder();
        if (!onlyHorizontal) {
            changes.add(BlockVector3.UNIT_Y);
            changes.add(BlockVector3.UNIT_MINUS_Y);
        }
        if (!onlyVertical) {
            changes.add(BlockVector3.UNIT_X);
            changes.add(BlockVector3.UNIT_MINUS_X);
            changes.add(BlockVector3.UNIT_Z);
            changes.add(BlockVector3.UNIT_MINUS_Z);
        }
        return (BlockVector3[])changes.build().map(v -> v.multiply(amount)).toArray(BlockVector3[]::new);
    }

    @Command(name="/trim", desc="Minimize the selection to encompass matching blocks")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.selection.trim"})
    public void trim(Actor actor, World world, LocalSession session, @Arg(desc="Mask of blocks to keep within the selection", def={"#existing"}) Mask mask) throws WorldEditException {
        int x;
        Region originalRegion = session.getSelection(world);
        if (!(originalRegion instanceof CuboidRegion)) {
            mask = new MaskIntersection(new RegionMask(originalRegion), mask);
        }
        mask = Masks.memoize(mask);
        CuboidRegion region = originalRegion.getBoundingBox();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int minY = 0;
        boolean found = false;
        block0: for (int y = min.y(); y <= max.y(); ++y) {
            for (int x2 = min.x(); x2 <= max.x(); ++x2) {
                for (int z = min.z(); z <= max.z(); ++z) {
                    BlockVector3 vec = BlockVector3.at(x2, y, z);
                    if (!mask.test(vec)) continue;
                    found = true;
                    minY = y;
                    break block0;
                }
            }
        }
        if (!found) {
            throw new StopExecutionException(TranslatableComponent.of("worldedit.trim.no-blocks"));
        }
        int maxY = minY;
        block3: for (int y = max.y(); y > minY; --y) {
            for (x = min.x(); x <= max.x(); ++x) {
                for (int z = min.z(); z <= max.z(); ++z) {
                    BlockVector3 vec = BlockVector3.at(x, y, z);
                    if (!mask.test(vec)) continue;
                    maxY = y;
                    break block3;
                }
            }
        }
        int minX = 0;
        block6: for (x = min.x(); x <= max.x(); ++x) {
            for (int z = min.z(); z <= max.z(); ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    BlockVector3 vec = BlockVector3.at(x, y, z);
                    if (!mask.test(vec)) continue;
                    minX = x;
                    break block6;
                }
            }
        }
        int maxX = minX;
        block9: for (int x3 = max.x(); x3 > minX; --x3) {
            for (int z = min.z(); z <= max.z(); ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    BlockVector3 vec = BlockVector3.at(x3, y, z);
                    if (!mask.test(vec)) continue;
                    maxX = x3;
                    break block9;
                }
            }
        }
        int minZ = 0;
        block12: for (int z = min.z(); z <= max.z(); ++z) {
            for (int x4 = minX; x4 <= maxX; ++x4) {
                for (int y = minY; y <= maxY; ++y) {
                    BlockVector3 vec = BlockVector3.at(x4, y, z);
                    if (!mask.test(vec)) continue;
                    minZ = z;
                    break block12;
                }
            }
        }
        int maxZ = minZ;
        block15: for (int z = max.z(); z > minZ; --z) {
            for (int x5 = minX; x5 <= maxX; ++x5) {
                for (int y = minY; y <= maxY; ++y) {
                    BlockVector3 vec = BlockVector3.at(x5, y, z);
                    if (!mask.test(vec)) continue;
                    maxZ = z;
                    break block15;
                }
            }
        }
        CuboidRegionSelector selector = session.getRegionSelector(world) instanceof ExtendingCuboidRegionSelector ? new ExtendingCuboidRegionSelector(world, BlockVector3.at(minX, minY, minZ), BlockVector3.at(maxX, maxY, maxZ)) : new CuboidRegionSelector(world, BlockVector3.at(minX, minY, minZ), BlockVector3.at(maxX, maxY, maxZ));
        session.setRegionSelector(world, selector);
        session.getRegionSelector(world).learnChanges();
        session.getRegionSelector(world).explainRegionAdjust(actor, session);
        actor.printInfo(TranslatableComponent.of("worldedit.trim.trim"));
    }

    @Command(name="/size", desc="Get information about the selection")
    @CommandPermissions(value={"worldedit.selection.size"})
    public void size(Actor actor, World world, LocalSession session, @Switch(name=99, desc="Get clipboard info instead") boolean clipboardInfo) throws WorldEditException {
        Region region;
        if (clipboardInfo) {
            ClipboardHolder holder = session.getClipboard();
            Clipboard clipboard = holder.getClipboard();
            region = clipboard.getRegion();
            BlockVector3 origin = clipboard.getOrigin();
            actor.printInfo(TranslatableComponent.of("worldedit.size.offset", TextComponent.of(origin.toString())));
        } else {
            region = session.getSelection(world);
            actor.printInfo(TranslatableComponent.of("worldedit.size.type", TextComponent.of(session.getRegionSelector(world).getTypeName())));
            for (Component line : session.getRegionSelector(world).getSelectionInfoLines()) {
                actor.printInfo(line);
            }
        }
        BlockVector3 size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);
        actor.printInfo(TranslatableComponent.of("worldedit.size.size", TextComponent.of(size.toString())));
        actor.printInfo(TranslatableComponent.of("worldedit.size.distance", TextComponent.of(region.getMaximumPoint().distance(region.getMinimumPoint()))));
        actor.printInfo(TranslatableComponent.of("worldedit.size.blocks", TextComponent.of(region.getVolume())));
    }

    @Command(name="/count", desc="Counts the number of blocks matching a mask")
    @CommandPermissions(value={"worldedit.analysis.count"})
    public int count(Actor actor, World world, LocalSession session, EditSession editSession, @Arg(desc="The mask of blocks to match") Mask mask) throws WorldEditException {
        int count = editSession.countBlocks(session.getSelection(world), mask);
        actor.printInfo(TranslatableComponent.of("worldedit.count.counted", TextComponent.of(count)));
        return count;
    }

    @Command(name="/distr", desc="Get the distribution of blocks in the selection")
    @CommandPermissions(value={"worldedit.analysis.distr"})
    public void distr(Actor actor, World world, LocalSession session, @Switch(name=99, desc="Get the distribution of the clipboard instead") boolean clipboardDistr, @Switch(name=100, desc="Separate blocks by state") boolean separateStates, @ArgFlag(name=112, desc="Gets page from a previous distribution.") Integer page) throws WorldEditException {
        List<Countable<BlockState>> distribution;
        if (page == null) {
            if (clipboardDistr) {
                Clipboard clipboard = session.getClipboard().getClipboard();
                BlockDistributionCounter count = new BlockDistributionCounter(clipboard, separateStates);
                RegionVisitor visitor = new RegionVisitor(clipboard.getRegion(), count);
                Operations.completeBlindly(visitor);
                distribution = count.getDistribution();
            } else {
                try (EditSession editSession = session.createEditSession(actor);){
                    distribution = editSession.getBlockDistribution(session.getSelection(world), separateStates);
                }
            }
            session.setLastDistribution(distribution);
            page = 1;
        } else {
            distribution = session.getLastDistribution();
            if (distribution == null) {
                actor.printError(TranslatableComponent.of("worldedit.distr.no-previous"));
                return;
            }
        }
        if (distribution.isEmpty()) {
            actor.printError(TranslatableComponent.of("worldedit.distr.no-blocks"));
            return;
        }
        int finalPage = page;
        WorldEditAsyncCommandBuilder.createAndSendMessage(actor, () -> {
            BlockDistributionResult res = new BlockDistributionResult(distribution, separateStates);
            if (!actor.isPlayer()) {
                res.formatForConsole();
            }
            return res.create(finalPage);
        }, (Component)null);
    }

    @Command(name="/sel", aliases={";", "/desel", "/deselect"}, desc="Choose a region selector")
    public void select(Actor actor, World world, LocalSession session, @Arg(desc="Selector to switch to", def={""}) SelectorChoiceOrList selectorChoiceOrList, @Switch(name=100, desc="Set default selector") boolean setDefaultSelector) throws WorldEditException {
        if (selectorChoiceOrList == null) {
            session.getRegionSelector(world).clear();
            session.dispatchCUISelection(actor);
            actor.printInfo(TranslatableComponent.of("worldedit.select.cleared"));
            return;
        }
        if (!(selectorChoiceOrList instanceof SelectorChoice)) {
            CommandListBox box = new CommandListBox("Selection modes", null, null);
            box.setHidingHelp(true);
            TextComponentProducer contents = box.getContents();
            contents.append(SubtleFormat.wrap("Select one of the modes below:")).newline();
            box.appendCommand("cuboid", TranslatableComponent.of("worldedit.select.cuboid.description"), "//sel cuboid");
            box.appendCommand("extend", TranslatableComponent.of("worldedit.select.extend.description"), "//sel extend");
            box.appendCommand("poly", TranslatableComponent.of("worldedit.select.poly.description"), "//sel poly");
            box.appendCommand("ellipsoid", TranslatableComponent.of("worldedit.select.ellipsoid.description"), "//sel ellipsoid");
            box.appendCommand("sphere", TranslatableComponent.of("worldedit.select.sphere.description"), "//sel sphere");
            box.appendCommand("cyl", TranslatableComponent.of("worldedit.select.cyl.description"), "//sel cyl");
            box.appendCommand("convex", TranslatableComponent.of("worldedit.select.convex.description"), "//sel convex");
            actor.print(box.create(1));
            return;
        }
        SelectorChoice selectorChoice = (SelectorChoice)selectorChoiceOrList;
        RegionSelector oldSelector = session.getRegionSelector(world);
        RegionSelector newSelector = selectorChoice.createNewSelector(oldSelector);
        selectorChoice.explainNewSelector(actor);
        if (setDefaultSelector) {
            RegionSelectorType found = null;
            for (RegionSelectorType type : RegionSelectorType.values()) {
                if (type.getSelectorClass() != newSelector.getClass()) continue;
                found = type;
                break;
            }
            if (found != null) {
                session.setDefaultRegionSelector(found);
                actor.printInfo(TranslatableComponent.of("worldedit.select.default-set", TextComponent.of(found.name())));
            } else {
                throw new RuntimeException("Something unexpected happened. Please report this.");
            }
        }
        session.setRegionSelector(world, newSelector);
        session.dispatchCUISelection(actor);
    }

    private static class BlockDistributionResult
    extends PaginationBox {
        private final List<Countable<BlockState>> distribution;
        private final int totalBlocks;
        private final boolean separateStates;

        BlockDistributionResult(List<Countable<BlockState>> distribution, boolean separateStates) {
            super("Block Distribution", "//distr -p %page%" + (separateStates ? " -d" : ""));
            this.distribution = distribution;
            this.totalBlocks = distribution.stream().mapToInt(Countable::getAmount).sum();
            this.separateStates = separateStates;
            this.setComponentsPerPage(7);
        }

        @Override
        public Component getComponent(int number) {
            TextComponent toolTip;
            Countable<BlockState> c = this.distribution.get(number);
            TextComponent.Builder line = TextComponent.builder();
            int count = c.getAmount();
            double perc = (double)count / (double)this.totalBlocks * 100.0;
            int maxDigits = (int)(Math.log10(this.totalBlocks) + 1.0);
            int curDigits = (int)(Math.log10(count) + 1.0);
            line.append(String.format("%s%.3f%%  ", perc < 10.0 ? "  " : "", perc), TextColor.GOLD);
            int space = maxDigits - curDigits;
            String pad = Strings.repeat((String)" ", (int)(space == 0 ? 2 : 2 * space + 1));
            line.append(String.format("%s%s", count, pad), TextColor.YELLOW);
            BlockState state = c.getID();
            BlockType blockType = state.getBlockType();
            Component blockName = blockType.getRichName().color(TextColor.LIGHT_PURPLE);
            if (this.separateStates && state != blockType.getDefaultState()) {
                toolTip = TextComponent.of(state.getAsString(), TextColor.GRAY);
                blockName = blockName.append(TextComponent.of("*", TextColor.LIGHT_PURPLE));
            } else {
                toolTip = TextComponent.of(blockType.id(), TextColor.GRAY);
            }
            blockName = blockName.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, toolTip));
            line.append(blockName);
            return line.build();
        }

        @Override
        public int getComponentsSize() {
            return this.distribution.size();
        }

        @Override
        public Component create(int page) throws InvalidComponentException {
            super.getContents().append(TranslatableComponent.of("worldedit.distr.total", TextColor.GRAY, TextComponent.of(this.totalBlocks))).append(TextComponent.newline());
            return super.create(page);
        }
    }
}

