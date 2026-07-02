/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.extent.ChangeSetExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.MaskingExtent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.extent.TracingExtent;
import com.sk89q.worldedit.extent.buffer.ForgetfulExtentBuffer;
import com.sk89q.worldedit.extent.buffer.internal.BatchingExtent;
import com.sk89q.worldedit.extent.cache.LastAccessExtentCache;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.extent.inventory.BlockBagExtent;
import com.sk89q.worldedit.extent.reorder.ChunkBatchingExtent;
import com.sk89q.worldedit.extent.reorder.MultiStageReorder;
import com.sk89q.worldedit.extent.validation.BlockChangeLimiter;
import com.sk89q.worldedit.extent.validation.DataValidatorExtent;
import com.sk89q.worldedit.extent.world.BiomeQuirkExtent;
import com.sk89q.worldedit.extent.world.ChunkLoadingExtent;
import com.sk89q.worldedit.extent.world.SideEffectExtent;
import com.sk89q.worldedit.extent.world.SurvivalModeExtent;
import com.sk89q.worldedit.extent.world.WatchdogTickingExtent;
import com.sk89q.worldedit.function.GroundFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.block.BlockDistributionCounter;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.block.Counter;
import com.sk89q.worldedit.function.block.Naturalizer;
import com.sk89q.worldedit.function.block.SnowSimulator;
import com.sk89q.worldedit.function.generator.ForestGenerator;
import com.sk89q.worldedit.function.generator.GardenPatchGenerator;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.BlockStateMask;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.BoundedHeightMask;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.MaskUnion;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.mask.NoiseFilter2D;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.function.operation.ChangeSetExecutor;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.OperationQueue;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.WaterloggedRemover;
import com.sk89q.worldedit.function.util.RegionOffset;
import com.sk89q.worldedit.function.visitor.DownwardVisitor;
import com.sk89q.worldedit.function.visitor.LayerVisitor;
import com.sk89q.worldedit.function.visitor.NonRisingVisitor;
import com.sk89q.worldedit.function.visitor.RecursiveVisitor;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.history.UndoContext;
import com.sk89q.worldedit.history.changeset.BlockOptimizedHistory;
import com.sk89q.worldedit.history.changeset.ChangeSet;
import com.sk89q.worldedit.internal.expression.Expression;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.internal.expression.ExpressionTimeoutException;
import com.sk89q.worldedit.internal.expression.LocalSlot;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.MathUtils;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.interpolation.KochanekBartelsInterpolation;
import com.sk89q.worldedit.math.interpolation.Node;
import com.sk89q.worldedit.math.noise.RandomNoise;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldedit.regions.shape.ArbitraryBiomeShape;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;
import com.sk89q.worldedit.regions.shape.RegionShape;
import com.sk89q.worldedit.regions.shape.WorldEditExpressionEnvironment;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.util.collection.DoubleArrayList;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.NullWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public class EditSession
implements Extent,
AutoCloseable {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    protected final World world;
    @Nullable
    private final Actor actor;
    private final ChangeSet changeSet = new BlockOptimizedHistory();
    @Nullable
    private SideEffectExtent sideEffectExtent;
    private final SurvivalModeExtent survivalExtent;
    @Nullable
    private BatchingExtent batchingExtent;
    @Nullable
    private ChunkBatchingExtent chunkBatchingExtent;
    private final BlockBagExtent blockBagExtent;
    private final MultiStageReorder reorderExtent;
    private final MaskingExtent maskingExtent;
    private final BlockChangeLimiter changeLimiter;
    @Nullable
    private ChangeSetExtent changeSetExtent;
    private final List<WatchdogTickingExtent> watchdogExtents = new ArrayList<WatchdogTickingExtent>(2);
    private final Extent bypassReorderHistory;
    private final Extent bypassHistory;
    private final Extent bypassNone;
    @Nullable
    private final List<TracingExtent> tracingExtents;
    @Deprecated
    private ReorderMode reorderMode = ReorderMode.FAST;
    private Mask oldMask;
    private static final BlockVector3[] recurseDirections = new BlockVector3[]{Direction.NORTH.toBlockVector(), Direction.EAST.toBlockVector(), Direction.SOUTH.toBlockVector(), Direction.WEST.toBlockVector(), Direction.UP.toBlockVector(), Direction.DOWN.toBlockVector()};

    EditSession(EventBus eventBus, World world, int maxBlocks, @Nullable BlockBag blockBag, @Nullable Actor actor, boolean tracing) {
        Preconditions.checkNotNull((Object)eventBus);
        Preconditions.checkArgument((maxBlocks >= -1 ? 1 : 0) != 0, (Object)"maxBlocks >= -1 required");
        if (tracing) {
            this.tracingExtents = new ArrayList<TracingExtent>();
            Preconditions.checkNotNull((Object)actor, (Object)"An actor is required while tracing");
        } else {
            this.tracingExtents = null;
        }
        this.world = world;
        this.actor = actor;
        if (world != null) {
            MultiStageReorder reorder;
            EditSessionEvent event = new EditSessionEvent(world, actor, maxBlocks, null);
            Watchdog watchdog = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWatchdog();
            this.sideEffectExtent = new SideEffectExtent(world);
            Extent extent = this.traceIfNeeded(this.sideEffectExtent);
            if (watchdog != null) {
                WatchdogTickingExtent watchdogExtent = new WatchdogTickingExtent(extent, watchdog);
                extent = this.traceIfNeeded(watchdogExtent);
                this.watchdogExtents.add(watchdogExtent);
            }
            this.survivalExtent = new SurvivalModeExtent(extent, world);
            extent = this.traceIfNeeded(this.survivalExtent);
            extent = this.traceIfNeeded(new BiomeQuirkExtent(extent));
            extent = this.traceIfNeeded(new ChunkLoadingExtent(extent, world));
            extent = this.traceIfNeeded(new LastAccessExtentCache(extent));
            this.blockBagExtent = new BlockBagExtent(extent, blockBag);
            extent = this.traceIfNeeded(this.blockBagExtent);
            extent = this.wrapExtent(extent, eventBus, event, Stage.BEFORE_CHANGE);
            this.bypassReorderHistory = this.traceIfNeeded(new DataValidatorExtent(extent, world));
            this.batchingExtent = new BatchingExtent(extent);
            extent = this.traceIfNeeded(this.batchingExtent);
            this.reorderExtent = reorder = new MultiStageReorder(extent, false);
            extent = this.traceIfNeeded(this.reorderExtent);
            this.chunkBatchingExtent = new ChunkBatchingExtent(extent, false);
            extent = this.traceIfNeeded(this.chunkBatchingExtent);
            extent = this.wrapExtent(extent, eventBus, event, Stage.BEFORE_REORDER);
            if (watchdog != null) {
                WatchdogTickingExtent watchdogExtent = new WatchdogTickingExtent(extent, watchdog);
                extent = this.traceIfNeeded(watchdogExtent);
                this.watchdogExtents.add(watchdogExtent);
            }
            this.bypassHistory = this.traceIfNeeded(new DataValidatorExtent(extent, world));
            this.changeSetExtent = new ChangeSetExtent(extent, this.changeSet);
            extent = this.traceIfNeeded(this.changeSetExtent);
            this.maskingExtent = new MaskingExtent(extent, Masks.alwaysTrue());
            extent = this.traceIfNeeded(this.maskingExtent);
            this.changeLimiter = new BlockChangeLimiter(extent, maxBlocks);
            extent = this.traceIfNeeded(this.changeLimiter);
            extent = this.wrapExtent(extent, eventBus, event, Stage.BEFORE_HISTORY);
            this.bypassNone = this.traceIfNeeded(new DataValidatorExtent(extent, world));
        } else {
            MultiStageReorder reorder;
            Extent extent = new NullExtent();
            this.survivalExtent = new SurvivalModeExtent(extent, NullWorld.getInstance());
            extent = this.traceIfNeeded(this.survivalExtent);
            this.blockBagExtent = new BlockBagExtent(extent, blockBag);
            extent = this.traceIfNeeded(this.blockBagExtent);
            this.reorderExtent = reorder = new MultiStageReorder(extent, false);
            extent = this.traceIfNeeded(this.reorderExtent);
            this.maskingExtent = new MaskingExtent(extent, Masks.alwaysTrue());
            extent = this.traceIfNeeded(this.maskingExtent);
            this.changeLimiter = new BlockChangeLimiter(extent, maxBlocks);
            extent = this.traceIfNeeded(this.changeLimiter);
            this.bypassReorderHistory = extent;
            this.bypassHistory = extent;
            this.bypassNone = extent;
        }
        this.setReorderMode(this.reorderMode);
    }

    private Extent traceIfNeeded(Extent input) {
        Extent output = input;
        if (this.tracingExtents != null) {
            TracingExtent newExtent = new TracingExtent(input);
            output = newExtent;
            this.tracingExtents.add(newExtent);
        }
        return output;
    }

    private Extent wrapExtent(Extent extent, EventBus eventBus, EditSessionEvent event, Stage stage) {
        event = event.clone(stage);
        event.setExtent(extent);
        boolean tracing = this.tracingExtents != null;
        event.setTracing(tracing);
        eventBus.post(event);
        if (tracing) {
            this.tracingExtents.addAll(event.getTracingExtents());
        }
        return event.getExtent();
    }

    private boolean commitRequired() {
        if (this.reorderExtent != null && this.reorderExtent.commitRequired()) {
            return true;
        }
        if (this.chunkBatchingExtent != null && this.chunkBatchingExtent.commitRequired()) {
            return true;
        }
        return this.sideEffectExtent != null && this.sideEffectExtent.commitRequired();
    }

    private List<TracingExtent> getActiveTracingExtents() {
        if (this.tracingExtents == null) {
            return ImmutableList.of();
        }
        return this.tracingExtents.stream().filter(TracingExtent::isActive).toList();
    }

    public void enableStandardMode() {
    }

    @Deprecated
    public void setReorderMode(ReorderMode reorderMode) {
        if (this.world == null && reorderMode == ReorderMode.FAST) {
            reorderMode = ReorderMode.MULTI_STAGE;
        }
        if (reorderMode == ReorderMode.FAST && this.sideEffectExtent == null) {
            throw new IllegalArgumentException("An EditSession without a fast mode tried to use it for reordering!");
        }
        if (reorderMode == ReorderMode.MULTI_STAGE && this.reorderExtent == null) {
            throw new IllegalArgumentException("An EditSession without a reorder extent tried to use it for reordering!");
        }
        if (this.commitRequired()) {
            this.internalFlushSession();
        }
        this.reorderMode = reorderMode;
        switch (reorderMode.ordinal()) {
            case 0: {
                if (this.sideEffectExtent != null) {
                    this.sideEffectExtent.setPostEditSimulationEnabled(false);
                }
                this.reorderExtent.setEnabled(true);
                break;
            }
            case 1: {
                this.sideEffectExtent.setPostEditSimulationEnabled(true);
                if (this.reorderExtent == null) break;
                this.reorderExtent.setEnabled(false);
                break;
            }
            case 2: {
                if (this.sideEffectExtent != null) {
                    this.sideEffectExtent.setPostEditSimulationEnabled(false);
                }
                if (this.reorderExtent == null) break;
                this.reorderExtent.setEnabled(false);
                break;
            }
        }
    }

    @Deprecated
    public ReorderMode getReorderMode() {
        return this.reorderMode;
    }

    public World getWorld() {
        return this.world;
    }

    public ChangeSet getChangeSet() {
        return this.changeSet;
    }

    public int getBlockChangeLimit() {
        return this.changeLimiter.getLimit();
    }

    public void setBlockChangeLimit(int limit) {
        this.changeLimiter.setLimit(limit);
    }

    @Deprecated
    public boolean isQueueEnabled() {
        return this.reorderMode == ReorderMode.MULTI_STAGE && this.reorderExtent.isEnabled();
    }

    @Deprecated
    public void enableQueue() {
        this.setReorderMode(ReorderMode.MULTI_STAGE);
    }

    @Deprecated
    public void disableQueue() {
        if (this.isQueueEnabled()) {
            this.internalFlushSession();
        }
        this.setReorderMode(ReorderMode.NONE);
    }

    public Mask getMask() {
        return this.oldMask;
    }

    public void setMask(Mask mask) {
        this.oldMask = mask;
        if (mask == null) {
            this.maskingExtent.setMask(Masks.alwaysTrue());
        } else {
            this.maskingExtent.setMask(mask);
        }
    }

    public SurvivalModeExtent getSurvivalExtent() {
        return this.survivalExtent;
    }

    @Deprecated
    public void setFastMode(boolean enabled) {
        if (this.sideEffectExtent != null) {
            this.sideEffectExtent.setSideEffectSet(enabled ? SideEffectSet.defaults() : SideEffectSet.none());
        }
    }

    public void setSideEffectApplier(SideEffectSet sideEffectSet) {
        if (this.sideEffectExtent != null) {
            this.sideEffectExtent.setSideEffectSet(sideEffectSet);
        }
    }

    @Deprecated
    public boolean hasFastMode() {
        return this.sideEffectExtent != null && !this.sideEffectExtent.getSideEffectSet().doesApplyAny();
    }

    public SideEffectSet getSideEffectApplier() {
        if (this.sideEffectExtent == null) {
            return SideEffectSet.defaults();
        }
        return this.sideEffectExtent.getSideEffectSet();
    }

    public BlockBag getBlockBag() {
        return this.blockBagExtent.getBlockBag();
    }

    public void setBlockBag(BlockBag blockBag) {
        this.blockBagExtent.setBlockBag(blockBag);
    }

    public Map<BlockType, Integer> popMissingBlocks() {
        return this.blockBagExtent.popMissing();
    }

    public boolean isBatchingChunks() {
        return this.chunkBatchingExtent != null && this.chunkBatchingExtent.isEnabled();
    }

    public void setBatchingChunks(boolean batchingChunks) {
        if (this.chunkBatchingExtent == null) {
            if (batchingChunks) {
                throw new UnsupportedOperationException("Chunk batching not supported by this session.");
            }
            return;
        }
        assert (this.batchingExtent != null) : "same nullness as chunkBatchingExtent";
        if (!batchingChunks && this.isBatchingChunks()) {
            this.internalFlushSession();
        }
        this.chunkBatchingExtent.setEnabled(batchingChunks);
        this.batchingExtent.setEnabled(!batchingChunks);
    }

    public boolean isBufferingEnabled() {
        return this.isBatchingChunks() || this.sideEffectExtent != null && this.sideEffectExtent.isPostEditSimulationEnabled();
    }

    public void disableBuffering() {
        if (this.commitRequired()) {
            this.internalFlushSession();
        }
        if (this.sideEffectExtent != null) {
            this.sideEffectExtent.setPostEditSimulationEnabled(false);
        }
        this.setReorderMode(ReorderMode.NONE);
        if (this.chunkBatchingExtent != null) {
            this.chunkBatchingExtent.setEnabled(false);
            assert (this.batchingExtent != null) : "same nullness as chunkBatchingExtent";
            this.batchingExtent.setEnabled(true);
        }
    }

    public boolean isTickingWatchdog() {
        return this.watchdogExtents.stream().anyMatch(WatchdogTickingExtent::isEnabled);
    }

    public void setTickingWatchdog(boolean active) {
        for (WatchdogTickingExtent extent : this.watchdogExtents) {
            extent.setEnabled(active);
        }
    }

    public int getBlockChangeCount() {
        return this.changeSet.size();
    }

    @Override
    public boolean fullySupports3DBiomes() {
        return this.bypassNone.fullySupports3DBiomes();
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        return this.bypassNone.getBiome(position);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return this.bypassNone.setBiome(position, biome);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return this.world.getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return this.world.getFullBlock(position);
    }

    public int getHighestTerrainBlock(int x, int z, int minY, int maxY) {
        return this.getHighestTerrainBlock(x, z, minY, maxY, null);
    }

    public int getHighestTerrainBlock(int x, int z, int minY, int maxY, Mask filter) {
        for (int y = maxY; y >= minY; --y) {
            BlockVector3 pt = BlockVector3.at(x, y, z);
            if (!(filter == null ? this.getBlock(pt).getBlockType().getMaterial().isMovementBlocker() : filter.test(pt))) continue;
            return y;
        }
        return minY;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, Stage stage) throws WorldEditException {
        return switch (stage.ordinal()) {
            case 0 -> this.bypassNone.setBlock(position, block);
            case 2 -> this.bypassHistory.setBlock(position, block);
            case 1 -> this.bypassReorderHistory.setBlock(position, block);
            default -> throw new RuntimeException("New enum entry added that is unhandled here");
        };
    }

    public <B extends BlockStateHolder<B>> boolean rawSetBlock(BlockVector3 position, B block) {
        try {
            return this.setBlock(position, block, Stage.BEFORE_CHANGE);
        }
        catch (WorldEditException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public <B extends BlockStateHolder<B>> boolean smartSetBlock(BlockVector3 position, B block) {
        try {
            return this.setBlock(position, block, Stage.BEFORE_REORDER);
        }
        catch (WorldEditException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws MaxChangedBlocksException {
        try {
            return this.setBlock(position, block, Stage.BEFORE_HISTORY);
        }
        catch (MaxChangedBlocksException e) {
            throw e;
        }
        catch (WorldEditException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public boolean setBlock(BlockVector3 position, Pattern pattern) throws MaxChangedBlocksException {
        return this.setBlock(position, (B)pattern.applyBlock(position));
    }

    private int setBlocks(Set<BlockVector3> vset, Pattern pattern) throws MaxChangedBlocksException {
        int affected = 0;
        for (BlockVector3 v : vset) {
            affected += this.setBlock(v, pattern) ? 1 : 0;
        }
        return affected;
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        return this.bypassNone.createEntity(location, entity);
    }

    public void undo(EditSession editSession) {
        UndoContext context = new UndoContext();
        context.setExtent(editSession.bypassHistory);
        Operations.completeBlindly(ChangeSetExecutor.createUndo(this.changeSet, context));
        editSession.internalFlushSession();
    }

    public void redo(EditSession editSession) {
        UndoContext context = new UndoContext();
        context.setExtent(editSession.bypassHistory);
        Operations.completeBlindly(ChangeSetExecutor.createRedo(this.changeSet, context));
        editSession.internalFlushSession();
    }

    public boolean isTrackingHistory() {
        return this.changeSetExtent != null && this.changeSetExtent.isEnabled();
    }

    public void setTrackingHistory(boolean trackHistory) {
        if (this.changeSetExtent != null) {
            this.changeSetExtent.setEnabled(trackHistory);
        } else if (trackHistory) {
            throw new IllegalStateException("No ChangeSetExtent is available");
        }
    }

    public int size() {
        return this.getBlockChangeCount();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.getWorld().getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.getWorld().getMaximumPoint();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return this.bypassNone.getEntities(region);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return this.bypassNone.getEntities();
    }

    @Override
    public void close() {
        this.internalFlushSession();
        this.dumpTracingInformation();
    }

    private void dumpTracingInformation() {
        if (this.tracingExtents == null) {
            return;
        }
        List<TracingExtent> tracingExtents = this.getActiveTracingExtents();
        assert (this.actor != null);
        if (tracingExtents.isEmpty()) {
            this.actor.printError(TranslatableComponent.of("worldedit.trace.no-tracing-extents"));
            return;
        }
        LinkedHashSet<List> stacks = new LinkedHashSet<List>();
        HashMap<List, BlockVector3> stackToPosition = new HashMap<List, BlockVector3>();
        Set<BlockVector3> touchedLocations = Collections.newSetFromMap(BlockMap.create());
        for (TracingExtent tracingExtent : tracingExtents) {
            touchedLocations.addAll(tracingExtent.getTouchedLocations());
        }
        for (BlockVector3 loc : touchedLocations) {
            List stack2 = tracingExtents.stream().filter(it -> it.getTouchedLocations().contains((Object)loc)).toList();
            boolean anyFailed = stack2.stream().anyMatch(it -> it.getFailedActions().containsKey((Object)loc));
            if (!anyFailed || !stacks.add(stack2)) continue;
            stackToPosition.put(stack2, loc);
        }
        stackToPosition.forEach((stack, position) -> {
            TracingExtent failure = (TracingExtent)stack.get(0);
            this.actor.printDebug((Component)TranslatableComponent.builder("worldedit.trace.action-failed").args(TextComponent.of(failure.getFailedActions().get((Object)position).toString()), TextComponent.of(position.toString()), TextComponent.of(failure.getExtent().getClass().getName())).build());
        });
    }

    @Deprecated
    public void flushSession() {
        this.internalFlushSession();
    }

    private void internalFlushSession() {
        Operations.completeBlindly(this.commit());
    }

    @Override
    @Nullable
    public Operation commit() {
        return this.bypassNone.commit();
    }

    public int countBlocks(Region region, Set<BaseBlock> searchBlocks) {
        BlockMask mask = new BlockMask((Extent)this, searchBlocks);
        return this.countBlocks(region, mask);
    }

    public int countBlocks(Region region, Mask searchMask) {
        Counter count = new Counter();
        RegionMaskingFilter filter = new RegionMaskingFilter(searchMask, count);
        RegionVisitor visitor = new RegionVisitor(region, filter);
        Operations.completeBlindly(visitor);
        return count.getCount();
    }

    public <B extends BlockStateHolder<B>> int fillXZ(BlockVector3 origin, B block, double radius, int depth, boolean recursive) throws MaxChangedBlocksException {
        return this.fillXZ(origin, (Pattern)block, radius, depth, recursive);
    }

    public int fillXZ(BlockVector3 origin, Pattern pattern, double radius, int depth, boolean recursive) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)origin));
        Preconditions.checkNotNull((Object)pattern);
        Preconditions.checkArgument((radius >= 0.0 ? 1 : 0) != 0, (Object)"radius >= 0");
        Preconditions.checkArgument((depth >= 1 ? 1 : 0) != 0, (Object)"depth >= 1");
        int lowerBound = origin.y() - depth + 1;
        if (lowerBound > origin.y()) {
            lowerBound = Integer.MIN_VALUE;
        }
        MaskIntersection mask = new MaskIntersection(new RegionMask(new EllipsoidRegion(null, origin, Vector3.at(radius, radius, radius))), new BoundedHeightMask(Math.max(lowerBound, this.getWorld().getMinY()), Math.min(this.getWorld().getMaxY(), origin.y())), Masks.negate(new ExistingBlockMask(this)));
        BlockReplace replace = new BlockReplace(this, pattern);
        RecursiveVisitor visitor = recursive ? new RecursiveVisitor(mask, replace) : new DownwardVisitor(mask, replace, origin.y());
        visitor.visit(origin);
        Operations.completeLegacy(visitor);
        return visitor.getAffected();
    }

    public int removeAbove(BlockVector3 position, int apothem, int height) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkArgument((apothem >= 1 ? 1 : 0) != 0, (Object)"apothem >= 1");
        Preconditions.checkArgument((height >= 1 ? 1 : 0) != 0, (Object)"height >= 1");
        CuboidRegion region = new CuboidRegion(this.getWorld(), position.add(-apothem + 1, 0, -apothem + 1), position.add(apothem - 1, height - 1, apothem - 1));
        return this.setBlocks((Region)region, (BlockStateHolder)BlockTypes.AIR.getDefaultState());
    }

    public int removeBelow(BlockVector3 position, int apothem, int height) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkArgument((apothem >= 1 ? 1 : 0) != 0, (Object)"apothem >= 1");
        Preconditions.checkArgument((height >= 1 ? 1 : 0) != 0, (Object)"height >= 1");
        CuboidRegion region = new CuboidRegion(this.getWorld(), position.add(-apothem + 1, 0, -apothem + 1), position.add(apothem - 1, -height + 1, apothem - 1));
        return this.setBlocks((Region)region, (BlockStateHolder)BlockTypes.AIR.getDefaultState());
    }

    public int removeNear(BlockVector3 position, Mask mask, int apothem) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkArgument((apothem >= 1 ? 1 : 0) != 0, (Object)"apothem >= 1");
        BlockVector3 adjustment = BlockVector3.ONE.multiply(apothem - 1);
        CuboidRegion region = new CuboidRegion(this.getWorld(), position.add(adjustment.multiply(-1)), position.add(adjustment));
        return this.replaceBlocks((Region)region, mask, (Pattern)BlockTypes.AIR.getDefaultState());
    }

    public <B extends BlockStateHolder<B>> int setBlocks(Region region, B block) throws MaxChangedBlocksException {
        return this.setBlocks(region, (Pattern)block);
    }

    public int setBlocks(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        BlockReplace replace = new BlockReplace(this, pattern);
        RegionVisitor visitor = new RegionVisitor(region, replace);
        Operations.completeLegacy(visitor);
        return visitor.getAffected();
    }

    public <B extends BlockStateHolder<B>> int replaceBlocks(Region region, Set<BaseBlock> filter, B replacement) throws MaxChangedBlocksException {
        return this.replaceBlocks(region, filter, (Pattern)replacement);
    }

    public int replaceBlocks(Region region, Set<BaseBlock> filter, Pattern pattern) throws MaxChangedBlocksException {
        AbstractExtentMask mask = filter == null ? new ExistingBlockMask(this) : new BlockMask((Extent)this, filter);
        return this.replaceBlocks(region, mask, pattern);
    }

    public int replaceBlocks(Region region, Mask mask, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)mask);
        Preconditions.checkNotNull((Object)pattern);
        BlockReplace replace = new BlockReplace(this, pattern);
        RegionMaskingFilter filter = new RegionMaskingFilter(mask, replace);
        RegionVisitor visitor = new RegionVisitor(region, filter);
        Operations.completeLegacy(visitor);
        return visitor.getAffected();
    }

    public int center(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        Vector3 center = region.getCenter();
        CuboidRegion centerRegion = new CuboidRegion(this.getWorld(), BlockVector3.at((int)center.x(), (int)center.y(), (int)center.z()), BlockVector3.at(MathUtils.roundHalfUp(center.x()), MathUtils.roundHalfUp(center.y()), MathUtils.roundHalfUp(center.z())));
        return this.setBlocks((Region)centerRegion, pattern);
    }

    @Deprecated
    public <B extends BlockStateHolder<B>> int makeCuboidFaces(Region region, B block) throws MaxChangedBlocksException {
        return this.makeCuboidFaces(region, (Pattern)block);
    }

    public int makeCuboidFaces(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        CuboidRegion cuboid = CuboidRegion.makeCuboid(region);
        Region faces = cuboid.getFaces();
        return this.setBlocks(faces, pattern);
    }

    public int makeFaces(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        if (region instanceof CuboidRegion) {
            return this.makeCuboidFaces(region, pattern);
        }
        return new RegionShape(region).generate(this, pattern, true);
    }

    public <B extends BlockStateHolder<B>> int makeCuboidWalls(Region region, B block) throws MaxChangedBlocksException {
        return this.makeCuboidWalls(region, (Pattern)block);
    }

    public int makeCuboidWalls(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        CuboidRegion cuboid = CuboidRegion.makeCuboid(region);
        Region faces = cuboid.getWalls();
        return this.setBlocks(faces, pattern);
    }

    public int makeWalls(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        if (region instanceof CuboidRegion) {
            return this.makeCuboidWalls(region, pattern);
        }
        final int minY = region.getMinimumPoint().y();
        final int maxY = region.getMaximumPoint().y();
        RegionShape shape = new RegionShape(this, region){

            @Override
            protected BaseBlock getMaterial(int x, int y, int z, BaseBlock defaultMaterial) {
                if (y > maxY || y < minY) {
                    return defaultMaterial;
                }
                return super.getMaterial(x, y, z, defaultMaterial);
            }
        };
        return shape.generate(this, pattern, true);
    }

    @Deprecated
    public <B extends BlockStateHolder<B>> int overlayCuboidBlocks(Region region, B block) throws MaxChangedBlocksException {
        Preconditions.checkNotNull(block);
        return this.overlayCuboidBlocks(region, (Pattern)block);
    }

    public int overlayCuboidBlocks(Region region, Pattern pattern) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)pattern);
        BlockReplace replace = new BlockReplace(this, pattern);
        RegionOffset offset = new RegionOffset(BlockVector3.UNIT_Y, replace);
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(this), offset);
        LayerVisitor visitor = new LayerVisitor(Regions.asFlatRegion(region), Regions.minimumBlockY(region), Regions.maximumBlockY(region), ground);
        Operations.completeLegacy(visitor);
        return ground.getAffected();
    }

    public int naturalizeCuboidBlocks(Region region) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Naturalizer naturalizer = new Naturalizer(this);
        FlatRegion flatRegion = Regions.asFlatRegion(region);
        LayerVisitor visitor = new LayerVisitor(flatRegion, Regions.minimumBlockY(region), Regions.maximumBlockY(region), naturalizer);
        Operations.completeLegacy(visitor);
        return naturalizer.getAffected();
    }

    public int stackCuboidRegion(Region region, BlockVector3 dir, int count, boolean copyAir) throws MaxChangedBlocksException {
        return this.stackCuboidRegion(region, dir, count, true, false, copyAir ? null : new ExistingBlockMask(this));
    }

    public int stackCuboidRegion(Region region, BlockVector3 offset, int count, boolean copyEntities, boolean copyBiomes, Mask mask) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)((Object)offset));
        BlockVector3 size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);
        try {
            return this.stackRegionBlockUnits(region, offset.multiply(size), count, copyEntities, copyBiomes, mask);
        }
        catch (RegionOperationException e) {
            throw new AssertionError((Object)e);
        }
    }

    public int stackRegionBlockUnits(Region region, BlockVector3 offset, int count, boolean copyEntities, boolean copyBiomes, Mask mask) throws MaxChangedBlocksException, RegionOperationException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)((Object)offset));
        Preconditions.checkArgument((count >= 1 ? 1 : 0) != 0, (Object)"count >= 1 required");
        BlockVector3 size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);
        BlockVector3 offsetAbs = offset.abs();
        if (offsetAbs.x() < size.x() && offsetAbs.y() < size.y() && offsetAbs.z() < size.z()) {
            throw new RegionOperationException(TranslatableComponent.of("worldedit.stack.intersecting-region"));
        }
        BlockVector3 to = region.getMinimumPoint();
        ForwardExtentCopy copy = new ForwardExtentCopy(this, region, this, to);
        copy.setRepetitions(count);
        copy.setTransform(new AffineTransform().translate(offset));
        copy.setCopyingEntities(copyEntities);
        copy.setCopyingBiomes(copyBiomes);
        if (mask != null) {
            copy.setSourceMask(mask);
        }
        Operations.completeLegacy(copy);
        return copy.getAffected();
    }

    public int moveRegion(Region region, BlockVector3 offset, int multiplier, boolean copyAir, Pattern replacement) throws MaxChangedBlocksException {
        return this.moveRegion(region, offset, multiplier, true, false, copyAir ? new ExistingBlockMask(this) : null, replacement);
    }

    public int moveRegion(Region region, BlockVector3 offset, int multiplier, boolean moveEntities, boolean copyBiomes, Mask mask, Pattern replacement) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)((Object)offset));
        Preconditions.checkArgument((multiplier >= 1 ? 1 : 0) != 0, (Object)"multiplier >= 1 required");
        Preconditions.checkArgument((!copyBiomes || region instanceof FlatRegion ? 1 : 0) != 0, (Object)"can't copy biomes from non-flat region");
        BlockVector3 to = region.getMinimumPoint();
        Pattern pattern = replacement != null ? replacement : BlockTypes.AIR.getDefaultState();
        BlockReplace remove = new BlockReplace(this, pattern);
        ForgetfulExtentBuffer buffer = new ForgetfulExtentBuffer(this, new RegionMask(region));
        ForwardExtentCopy copy = new ForwardExtentCopy(this, region, buffer, to);
        copy.setTransform(new AffineTransform().translate(offset.multiply(multiplier)));
        copy.setSourceFunction(remove);
        copy.setCopyingEntities(moveEntities);
        copy.setRemovingEntities(moveEntities);
        copy.setCopyingBiomes(copyBiomes);
        if (mask != null) {
            copy.setSourceMask(mask);
        }
        BlockReplace replace = new BlockReplace(this, buffer);
        RegionVisitor visitor = new RegionVisitor(buffer.asRegion(), replace);
        OperationQueue operation = new OperationQueue(copy, visitor);
        if (copyBiomes) {
            BiomeReplace biomeReplace = new BiomeReplace((Extent)this, buffer);
            RegionVisitor biomeVisitor = new RegionVisitor(buffer.asRegion(), biomeReplace);
            operation.offer(biomeVisitor);
        }
        Operations.completeLegacy(operation);
        return copy.getAffected();
    }

    public int moveCuboidRegion(Region region, BlockVector3 dir, int distance, boolean copyAir, Pattern replacement) throws MaxChangedBlocksException {
        return this.moveRegion(region, dir, distance, copyAir, replacement);
    }

    public int drainArea(BlockVector3 origin, double radius) throws MaxChangedBlocksException {
        return this.drainArea(origin, radius, false);
    }

    public int drainArea(BlockVector3 origin, double radius, boolean waterlogged) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)origin));
        Preconditions.checkArgument((radius >= 0.0 ? 1 : 0) != 0, (Object)"radius >= 0 required");
        BlockStateMask waterloggedMask = null;
        if (waterlogged) {
            HashMap<String, String> stateMap = new HashMap<String, String>();
            stateMap.put("waterlogged", "true");
            waterloggedMask = new BlockStateMask(this, stateMap, true);
        }
        MaskIntersection mask = new MaskIntersection(new BoundedHeightMask(this.getWorld().getMinY(), this.getWorld().getMaxY()), new RegionMask(new EllipsoidRegion(null, origin, Vector3.at(radius, radius, radius))), waterlogged ? new MaskUnion(this.getWorld().createLiquidMask(), waterloggedMask) : this.getWorld().createLiquidMask());
        BlockReplace replace = waterlogged ? new BlockReplace(this, new WaterloggedRemover(this)) : new BlockReplace(this, BlockTypes.AIR.getDefaultState());
        RecursiveVisitor visitor = new RecursiveVisitor(mask, replace);
        for (BlockVector3 position : CuboidRegion.fromCenter(origin, 1)) {
            if (!mask.test(position)) continue;
            visitor.visit(position);
        }
        Operations.completeLegacy(visitor);
        return visitor.getAffected();
    }

    public int fixLiquid(BlockVector3 origin, double radius, BlockType fluid) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)((Object)origin));
        Preconditions.checkArgument((radius >= 0.0 ? 1 : 0) != 0, (Object)"radius >= 0 required");
        BlockTypeMask liquidMask = new BlockTypeMask((Extent)this, fluid);
        MaskUnion blockMask = new MaskUnion(liquidMask, Masks.negate(new ExistingBlockMask(this)));
        MaskIntersection mask = new MaskIntersection(new BoundedHeightMask(this.getWorld().getMinY(), Math.min(origin.y(), this.getWorld().getMaxY())), new RegionMask(new EllipsoidRegion(null, origin, Vector3.at(radius, radius, radius))), blockMask);
        BlockReplace replace = new BlockReplace(this, fluid.getDefaultState());
        NonRisingVisitor visitor = new NonRisingVisitor(mask, replace);
        for (BlockVector3 position : CuboidRegion.fromCenter(origin, 1)) {
            if (!liquidMask.test(position)) continue;
            visitor.visit(position);
        }
        Operations.completeLegacy(visitor);
        return visitor.getAffected();
    }

    public int makeCylinder(BlockVector3 pos, Pattern block, double radius, int height, boolean filled) throws MaxChangedBlocksException {
        return this.makeCylinder(pos, block, radius, radius, height, filled);
    }

    public int makeCylinder(BlockVector3 pos, Pattern block, double radiusX, double radiusZ, int height, boolean filled) throws MaxChangedBlocksException {
        int affected = 0;
        radiusX += 0.5;
        radiusZ += 0.5;
        if (height == 0) {
            return 0;
        }
        if (height < 0) {
            height = -height;
            pos = pos.subtract(0, height, 0);
        }
        if (pos.y() < this.world.getMinY()) {
            pos = pos.withY(this.world.getMinY());
        } else if (pos.y() + height - 1 > this.world.getMaxY()) {
            height = this.world.getMaxY() - pos.y() + 1;
        }
        double invRadiusX = 1.0 / radiusX;
        double invRadiusZ = 1.0 / radiusZ;
        int ceilRadiusX = (int)Math.ceil(radiusX);
        int ceilRadiusZ = (int)Math.ceil(radiusZ);
        double nextXn = 0.0;
        block0: for (int x = 0; x <= ceilRadiusX; ++x) {
            double xn = nextXn;
            nextXn = (double)(x + 1) * invRadiusX;
            double nextZn = 0.0;
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                double zn = nextZn;
                nextZn = (double)(z + 1) * invRadiusZ;
                double distanceSq = EditSession.lengthSq(xn, zn);
                if (distanceSq > 1.0) {
                    if (z != 0) continue block0;
                    break block0;
                }
                if (!filled && EditSession.lengthSq(nextXn, zn) <= 1.0 && EditSession.lengthSq(xn, nextZn) <= 1.0) continue;
                for (int y = 0; y < height; ++y) {
                    if (this.setBlock(pos.add(x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(-x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(x, y, -z), block)) {
                        ++affected;
                    }
                    if (!this.setBlock(pos.add(-x, y, -z), block)) continue;
                    ++affected;
                }
            }
        }
        return affected;
    }

    public int makeCone(BlockVector3 pos, Pattern block, double radiusX, double radiusZ, int height, boolean filled, double thickness) throws MaxChangedBlocksException {
        int affected = 0;
        int ceilRadiusX = (int)Math.ceil(radiusX);
        int ceilRadiusZ = (int)Math.ceil(radiusZ);
        double radiusXPow = Math.pow(radiusX, 2.0);
        double radiusZPow = Math.pow(radiusZ, 2.0);
        double heightPow = Math.pow(height, 2.0);
        block0: for (int y = 0; y < height; ++y) {
            double ySquaredMinusHeightOverHeightSquared = Math.pow(y - height, 2.0) / heightPow;
            block1: for (int x = 0; x <= ceilRadiusX; ++x) {
                double xSquaredOverRadiusX = Math.pow(x, 2.0) / radiusXPow;
                for (int z = 0; z <= ceilRadiusZ; ++z) {
                    double zSquaredOverRadiusZ = Math.pow(z, 2.0) / radiusZPow;
                    double distanceFromOriginMinusHeightSquared = xSquaredOverRadiusX + zSquaredOverRadiusZ - ySquaredMinusHeightOverHeightSquared;
                    if (distanceFromOriginMinusHeightSquared > 1.0) {
                        if (z != 0) continue block1;
                        continue block0;
                    }
                    if (!filled) {
                        double xNext = Math.pow((double)x + thickness, 2.0) / radiusXPow + zSquaredOverRadiusZ - ySquaredMinusHeightOverHeightSquared;
                        double yNext = xSquaredOverRadiusX + zSquaredOverRadiusZ - Math.pow((double)y + thickness - (double)height, 2.0) / heightPow;
                        double zNext = xSquaredOverRadiusX + Math.pow((double)z + thickness, 2.0) / radiusZPow - ySquaredMinusHeightOverHeightSquared;
                        if (xNext <= 0.0 && zNext <= 0.0 && yNext <= 0.0 && (double)y + thickness != (double)height) continue;
                    }
                    if (!(distanceFromOriginMinusHeightSquared <= 0.0)) continue;
                    if (this.setBlock(pos.add(x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(-x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(x, y, -z), block)) {
                        ++affected;
                    }
                    if (!this.setBlock(pos.add(-x, y, -z), block)) continue;
                    ++affected;
                }
            }
        }
        return affected;
    }

    public int makeSphere(BlockVector3 pos, Pattern block, double radius, boolean filled) throws MaxChangedBlocksException {
        return this.makeSphere(pos, block, radius, radius, radius, filled);
    }

    public int makeSphere(BlockVector3 pos, Pattern block, double radiusX, double radiusY, double radiusZ, boolean filled) throws MaxChangedBlocksException {
        int affected = 0;
        double invRadiusX = 1.0 / (radiusX += 0.5);
        double invRadiusY = 1.0 / (radiusY += 0.5);
        double invRadiusZ = 1.0 / (radiusZ += 0.5);
        int ceilRadiusX = (int)Math.ceil(radiusX);
        int ceilRadiusY = (int)Math.ceil(radiusY);
        int ceilRadiusZ = (int)Math.ceil(radiusZ);
        double nextXn = 0.0;
        block0: for (int x = 0; x <= ceilRadiusX; ++x) {
            double xn = nextXn;
            nextXn = (double)(x + 1) * invRadiusX;
            double nextYn = 0.0;
            block1: for (int y = 0; y <= ceilRadiusY; ++y) {
                double yn = nextYn;
                nextYn = (double)(y + 1) * invRadiusY;
                double nextZn = 0.0;
                for (int z = 0; z <= ceilRadiusZ; ++z) {
                    double zn = nextZn;
                    nextZn = (double)(z + 1) * invRadiusZ;
                    double distanceSq = EditSession.lengthSq(xn, yn, zn);
                    if (distanceSq > 1.0) {
                        if (z != 0) continue block1;
                        if (y != 0) continue block0;
                        break block0;
                    }
                    if (!filled && EditSession.lengthSq(nextXn, yn, zn) <= 1.0 && EditSession.lengthSq(xn, nextYn, zn) <= 1.0 && EditSession.lengthSq(xn, yn, nextZn) <= 1.0) continue;
                    if (this.setBlock(pos.add(x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(-x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(x, -y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(x, y, -z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(-x, -y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(x, -y, -z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(pos.add(-x, y, -z), block)) {
                        ++affected;
                    }
                    if (!this.setBlock(pos.add(-x, -y, -z), block)) continue;
                    ++affected;
                }
            }
        }
        return affected;
    }

    public int makePyramid(BlockVector3 position, Pattern block, int size, boolean filled) throws MaxChangedBlocksException {
        int affected = 0;
        int height = size;
        for (int y = 0; y <= height; ++y) {
            --size;
            for (int x = 0; x <= size; ++x) {
                for (int z = 0; z <= size; ++z) {
                    if ((!filled || z > size || x > size) && z != size && x != size) continue;
                    if (this.setBlock(position.add(x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(position.add(-x, y, z), block)) {
                        ++affected;
                    }
                    if (this.setBlock(position.add(x, y, -z), block)) {
                        ++affected;
                    }
                    if (!this.setBlock(position.add(-x, y, -z), block)) continue;
                    ++affected;
                }
            }
        }
        return affected;
    }

    @Deprecated
    public int thaw(BlockVector3 position, double radius) throws MaxChangedBlocksException {
        return this.thaw(position, radius, WorldEdit.getInstance().getConfiguration().defaultVerticalHeight);
    }

    public int thaw(BlockVector3 position, double radius, int height) throws MaxChangedBlocksException {
        int affected = 0;
        double radiusSq = radius * radius;
        int ox = position.x();
        int oy = position.y();
        int oz = position.z();
        BlockState air = BlockTypes.AIR.getDefaultState();
        BlockState water = BlockTypes.WATER.getDefaultState();
        int centerY = Math.max(this.getWorld().getMinY(), Math.min(this.getWorld().getMaxY(), oy));
        int minY = Math.max(this.getWorld().getMinY(), centerY - height);
        int maxY = Math.min(this.getWorld().getMaxY(), centerY + height);
        int ceilRadius = (int)Math.ceil(radius);
        for (int x = ox - ceilRadius; x <= ox + ceilRadius; ++x) {
            block1: for (int z = oz - ceilRadius; z <= oz + ceilRadius; ++z) {
                if ((double)BlockVector3.at(x, oy, z).distanceSq(position) > radiusSq) continue;
                for (int y = maxY; y > minY; --y) {
                    BlockVector3 pt = BlockVector3.at(x, y, z);
                    BlockType id = this.getBlock(pt).getBlockType();
                    if (id == BlockTypes.ICE) {
                        if (!this.setBlock(pt, (B)water)) continue block1;
                        ++affected;
                        continue block1;
                    }
                    if (id == BlockTypes.SNOW) {
                        if (!this.setBlock(pt, (B)air)) continue block1;
                        ++affected;
                        continue block1;
                    }
                    if (!id.getMaterial().isAir()) continue block1;
                }
            }
        }
        return affected;
    }

    @Deprecated
    public int simulateSnow(BlockVector3 position, double radius) throws MaxChangedBlocksException {
        return this.simulateSnow(position, radius, WorldEdit.getInstance().getConfiguration().defaultVerticalHeight);
    }

    public int simulateSnow(BlockVector3 position, double radius, int height) throws MaxChangedBlocksException {
        return this.simulateSnow(new CylinderRegion(position, Vector2.at(radius, radius), position.y(), height), false);
    }

    public int simulateSnow(FlatRegion region, boolean stack) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)region);
        SnowSimulator snowSimulator = new SnowSimulator(this, stack);
        LayerVisitor layerVisitor = new LayerVisitor(region, region.getMinimumY(), region.getMaximumY(), snowSimulator);
        Operations.completeLegacy(layerVisitor);
        return snowSimulator.getAffected();
    }

    @Deprecated
    public int green(BlockVector3 position, double radius, boolean onlyNormalDirt) throws MaxChangedBlocksException {
        return this.green(position, radius, WorldEdit.getInstance().getConfiguration().defaultVerticalHeight, onlyNormalDirt);
    }

    public int green(BlockVector3 position, double radius, int height, boolean onlyNormalDirt) throws MaxChangedBlocksException {
        int affected = 0;
        double radiusSq = radius * radius;
        int ox = position.x();
        int oy = position.y();
        int oz = position.z();
        BlockState grass = BlockTypes.GRASS_BLOCK.getDefaultState();
        int centerY = Math.max(this.getWorld().getMinY(), Math.min(this.getWorld().getMaxY(), oy));
        int minY = Math.max(this.getWorld().getMinY(), centerY - height);
        int maxY = Math.min(this.getWorld().getMaxY(), centerY + height);
        int ceilRadius = (int)Math.ceil(radius);
        for (int x = ox - ceilRadius; x <= ox + ceilRadius; ++x) {
            block1: for (int z = oz - ceilRadius; z <= oz + ceilRadius; ++z) {
                if ((double)BlockVector3.at(x, oy, z).distanceSq(position) > radiusSq) continue;
                for (int y = maxY; y > minY; --y) {
                    BlockVector3 pt = BlockVector3.at(x, y, z);
                    BlockState block = this.getBlock(pt);
                    if (block.getBlockType() == BlockTypes.DIRT || !onlyNormalDirt && block.getBlockType() == BlockTypes.COARSE_DIRT) {
                        if (!this.setBlock(pt, (B)grass)) continue block1;
                        ++affected;
                        continue block1;
                    }
                    if (block.getBlockType() == BlockTypes.WATER || block.getBlockType() == BlockTypes.LAVA || block.getBlockType().getMaterial().isMovementBlocker()) continue block1;
                }
            }
        }
        return affected;
    }

    public int makePumpkinPatches(BlockVector3 position, int apothem) throws MaxChangedBlocksException {
        GardenPatchGenerator generator = new GardenPatchGenerator(this);
        generator.setPlant(GardenPatchGenerator.getPumpkinPattern());
        CuboidRegion region = new CuboidRegion(this.getWorld(), position.add(-apothem, -5, -apothem), position.add(apothem, 10, apothem));
        double density = 0.02;
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(this), generator);
        LayerVisitor visitor = new LayerVisitor(region, Regions.minimumBlockY(region), Regions.maximumBlockY(region), ground);
        visitor.setMask(new NoiseFilter2D(new RandomNoise(), density));
        Operations.completeLegacy(visitor);
        return ground.getAffected();
    }

    public int makeForest(BlockVector3 basePosition, int size, double density, TreeGenerator.TreeType treeType) throws MaxChangedBlocksException {
        return this.makeForest(CuboidRegion.fromCenter(basePosition, size), density, treeType);
    }

    public int makeForest(Region region, double density, TreeGenerator.TreeType treeType) throws MaxChangedBlocksException {
        ForestGenerator generator = new ForestGenerator(this, treeType);
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(this), generator);
        LayerVisitor visitor = new LayerVisitor(Regions.asFlatRegion(region), Regions.minimumBlockY(region), Regions.maximumBlockY(region), ground);
        visitor.setMask(new NoiseFilter2D(new RandomNoise(), density));
        Operations.completeLegacy(visitor);
        return ground.getAffected();
    }

    public List<Countable<BlockState>> getBlockDistribution(Region region, boolean separateStates) {
        BlockDistributionCounter count = new BlockDistributionCounter(this, separateStates);
        RegionVisitor visitor = new RegionVisitor(region, count);
        Operations.completeBlindly(visitor);
        return count.getDistribution();
    }

    public int makeShape(Region region, Vector3 zero, Vector3 unit, Pattern pattern, String expressionString, boolean hollow) throws ExpressionException, MaxChangedBlocksException {
        return this.makeShape(region, zero, unit, pattern, expressionString, hollow, WorldEdit.getInstance().getConfiguration().calculationTimeout);
    }

    public int makeShape(Region region, Vector3 zero, Vector3 unit, Pattern pattern, String expressionString, boolean hollow, int timeout) throws ExpressionException, MaxChangedBlocksException {
        Expression expression = Expression.compile(expressionString, "x", "y", "z", "type", "data");
        expression.optimize();
        return this.makeShape(region, zero, unit, pattern, expression, hollow, timeout);
    }

    public int makeShape(Region region, final Vector3 zero, final Vector3 unit, Pattern pattern, final Expression expression, boolean hollow, final int timeout) throws ExpressionException, MaxChangedBlocksException {
        expression.getSlots().getVariable("x").orElseThrow(IllegalStateException::new);
        expression.getSlots().getVariable("y").orElseThrow(IllegalStateException::new);
        expression.getSlots().getVariable("z").orElseThrow(IllegalStateException::new);
        final LocalSlot.Variable typeVariable = expression.getSlots().getVariable("type").orElseThrow(IllegalStateException::new);
        final LocalSlot.Variable dataVariable = expression.getSlots().getVariable("data").orElseThrow(IllegalStateException::new);
        final WorldEditExpressionEnvironment environment = new WorldEditExpressionEnvironment(this, unit, zero);
        expression.setEnvironment(environment);
        final int[] timedOut = new int[]{0};
        ArbitraryShape shape = new ArbitraryShape(this, region){

            @Override
            protected BaseBlock getMaterial(int x, int y, int z, BaseBlock defaultMaterial) {
                Vector3 current = Vector3.at(x, y, z);
                environment.setCurrentBlock(current);
                Vector3 scaled = current.subtract(zero).divide(unit);
                try {
                    int[] legacy = LegacyMapper.getInstance().getLegacyFromBlock(defaultMaterial.toImmutableState());
                    int typeVar = 0;
                    int dataVar = 0;
                    if (legacy != null) {
                        typeVar = legacy[0];
                        if (legacy.length > 1) {
                            dataVar = legacy[1];
                        }
                    }
                    double[] dArray = new double[]{scaled.x(), scaled.y(), scaled.z(), typeVar, dataVar};
                    if (expression.evaluate(dArray, timeout) <= 0.0) {
                        return null;
                    }
                    int newType = (int)typeVariable.value();
                    int newData = (int)dataVariable.value();
                    if (newType != typeVar || newData != dataVar) {
                        BlockState state = LegacyMapper.getInstance().getBlockFromLegacy(newType, newData);
                        return state == null ? defaultMaterial : state.toBaseBlock();
                    }
                    return defaultMaterial;
                }
                catch (ExpressionTimeoutException e) {
                    timedOut[0] = timedOut[0] + 1;
                    return null;
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        int changed = shape.generate(this, pattern, hollow);
        if (timedOut[0] > 0) {
            throw new ExpressionTimeoutException(String.format("%d blocks changed. %d blocks took too long to evaluate (increase with //timeout).", changed, timedOut[0]));
        }
        return changed;
    }

    public int deformRegion(Region region, Vector3 zero, Vector3 unit, String expressionString) throws ExpressionException, MaxChangedBlocksException {
        return this.deformRegion(region, zero, unit, expressionString, WorldEdit.getInstance().getConfiguration().calculationTimeout);
    }

    public int deformRegion(Region region, Vector3 zero, Vector3 unit, String expressionString, int timeout) throws ExpressionException, MaxChangedBlocksException {
        Expression expression = Expression.compile(expressionString, "x", "y", "z");
        expression.optimize();
        return this.deformRegion(region, zero, unit, expression, timeout);
    }

    public int deformRegion(Region region, Vector3 zero, Vector3 unit, Expression expression, int timeout) throws ExpressionException, MaxChangedBlocksException {
        LocalSlot.Variable x = expression.getSlots().getVariable("x").orElseThrow(IllegalStateException::new);
        LocalSlot.Variable y = expression.getSlots().getVariable("y").orElseThrow(IllegalStateException::new);
        LocalSlot.Variable z = expression.getSlots().getVariable("z").orElseThrow(IllegalStateException::new);
        WorldEditExpressionEnvironment environment = new WorldEditExpressionEnvironment(this, unit, zero);
        expression.setEnvironment(environment);
        DoubleArrayList<BlockVector3, BaseBlock> queue = new DoubleArrayList<BlockVector3, BaseBlock>(false);
        for (BlockVector3 targetBlockPosition : region) {
            Vector3 targetPosition = targetBlockPosition.toVector3();
            environment.setCurrentBlock(targetPosition);
            Vector3 scaled = targetPosition.subtract(zero).divide(unit);
            expression.evaluate(new double[]{scaled.x(), scaled.y(), scaled.z()}, timeout);
            BlockVector3 sourcePosition = environment.toWorld(x.value(), y.value(), z.value());
            BaseBlock material = this.world.getFullBlock(sourcePosition);
            queue.put(targetBlockPosition, material);
        }
        int affected = 0;
        for (Map.Entry entry : queue) {
            BaseBlock material;
            BlockVector3 position = (BlockVector3)((Object)entry.getKey());
            if (!this.setBlock(position, (B)(material = (BaseBlock)entry.getValue()))) continue;
            ++affected;
        }
        return affected;
    }

    public int hollowOutRegion(Region region, int thickness, Pattern pattern) throws MaxChangedBlocksException {
        int affected = 0;
        HashSet<BlockVector3> outside = new HashSet<BlockVector3>();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int minX = min.x();
        int minY = min.y();
        int minZ = min.z();
        int maxX = max.x();
        int maxY = max.y();
        int maxZ = max.z();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                this.recurseHollow(region, BlockVector3.at(x, y, minZ), outside);
                this.recurseHollow(region, BlockVector3.at(x, y, maxZ), outside);
            }
        }
        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                this.recurseHollow(region, BlockVector3.at(minX, y, z), outside);
                this.recurseHollow(region, BlockVector3.at(maxX, y, z), outside);
            }
        }
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                this.recurseHollow(region, BlockVector3.at(x, minY, z), outside);
                this.recurseHollow(region, BlockVector3.at(x, maxY, z), outside);
            }
        }
        HashSet<BlockVector3> newOutside = new HashSet<BlockVector3>();
        for (int i = 1; i < thickness; ++i) {
            block7: for (BlockVector3 position : region) {
                BlockVector3[] blockVector3Array = recurseDirections;
                int n = blockVector3Array.length;
                for (int j = 0; j < n; ++j) {
                    BlockVector3 recurseDirection = blockVector3Array[j];
                    BlockVector3 neighbor = position.add(recurseDirection);
                    if (!outside.contains((Object)neighbor)) continue;
                    newOutside.add(position);
                    continue block7;
                }
            }
            outside.addAll(newOutside);
            newOutside.clear();
        }
        block9: for (BlockVector3 position : region) {
            for (BlockVector3 recurseDirection : recurseDirections) {
                BlockVector3 neighbor = position.add(recurseDirection);
                if (outside.contains((Object)neighbor)) continue block9;
            }
            if (!this.setBlock(position, (B)pattern.applyBlock(position))) continue;
            ++affected;
        }
        return affected;
    }

    public int drawLine(Pattern pattern, BlockVector3 pos1, BlockVector3 pos2, double radius, boolean filled) throws MaxChangedBlocksException {
        return this.drawLine(pattern, (List<BlockVector3>)ImmutableList.of((Object)((Object)pos1), (Object)((Object)pos2)), radius, filled);
    }

    public int drawLine(Pattern pattern, List<BlockVector3> vectors, double radius, boolean filled) throws MaxChangedBlocksException {
        Set<BlockVector3> vset = new HashSet<BlockVector3>();
        for (int i = 0; !vectors.isEmpty() && i < vectors.size() - 1; ++i) {
            int domstep;
            int dz;
            int dy;
            BlockVector3 pos1 = vectors.get(i);
            BlockVector3 pos2 = vectors.get(i + 1);
            int x1 = pos1.x();
            int y1 = pos1.y();
            int z1 = pos1.z();
            int x2 = pos2.x();
            int y2 = pos2.y();
            int z2 = pos2.z();
            int tipx = x1;
            int tipy = y1;
            int tipz = z1;
            int dx = Math.abs(x2 - x1);
            if (dx + (dy = Math.abs(y2 - y1)) + (dz = Math.abs(z2 - z1)) == 0) {
                vset.add(BlockVector3.at(tipx, tipy, tipz));
                continue;
            }
            int dMax = Math.max(Math.max(dx, dy), dz);
            if (dMax == dx) {
                for (domstep = 0; domstep <= dx; ++domstep) {
                    tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
                    tipy = (int)Math.round((double)y1 + (double)domstep * (double)dy / (double)dx * (double)(y2 - y1 > 0 ? 1 : -1));
                    tipz = (int)Math.round((double)z1 + (double)domstep * (double)dz / (double)dx * (double)(z2 - z1 > 0 ? 1 : -1));
                    vset.add(BlockVector3.at(tipx, tipy, tipz));
                }
                continue;
            }
            if (dMax == dy) {
                for (domstep = 0; domstep <= dy; ++domstep) {
                    tipy = y1 + domstep * (y2 - y1 > 0 ? 1 : -1);
                    tipx = (int)Math.round((double)x1 + (double)domstep * (double)dx / (double)dy * (double)(x2 - x1 > 0 ? 1 : -1));
                    tipz = (int)Math.round((double)z1 + (double)domstep * (double)dz / (double)dy * (double)(z2 - z1 > 0 ? 1 : -1));
                    vset.add(BlockVector3.at(tipx, tipy, tipz));
                }
                continue;
            }
            for (domstep = 0; domstep <= dz; ++domstep) {
                tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
                tipy = (int)Math.round((double)y1 + (double)domstep * (double)dy / (double)dz * (double)(y2 - y1 > 0 ? 1 : -1));
                tipx = (int)Math.round((double)x1 + (double)domstep * (double)dx / (double)dz * (double)(x2 - x1 > 0 ? 1 : -1));
                vset.add(BlockVector3.at(tipx, tipy, tipz));
            }
        }
        vset = EditSession.getBallooned(vset, radius);
        if (!filled) {
            vset = EditSession.getHollowed(vset);
        }
        return this.setBlocks(vset, pattern);
    }

    public int drawSpline(Pattern pattern, List<BlockVector3> nodevectors, double tension, double bias, double continuity, double quality, double radius, boolean filled) throws MaxChangedBlocksException {
        Set<BlockVector3> vset = new HashSet<BlockVector3>();
        ArrayList<Node> nodes = new ArrayList<Node>(nodevectors.size());
        KochanekBartelsInterpolation interpol = new KochanekBartelsInterpolation();
        for (BlockVector3 nodevector : nodevectors) {
            Node n = new Node(nodevector.toVector3().add(Vector3.at(0.5, 0.5, 0.5)));
            n.setTension(tension);
            n.setBias(bias);
            n.setContinuity(continuity);
            nodes.add(n);
        }
        interpol.setNodes(nodes);
        double splinelength = interpol.arcLength(0.0, 1.0);
        for (double loop = 0.0; loop <= 1.0; loop += 1.0 / splinelength / quality) {
            Vector3 tipv = interpol.getPosition(loop);
            vset.add(tipv.toBlockPoint());
        }
        vset = EditSession.getBallooned(vset, radius);
        if (!filled) {
            vset = EditSession.getHollowed(vset);
        }
        return this.setBlocks(vset, pattern);
    }

    private static Set<BlockVector3> getBallooned(Set<BlockVector3> vset, double radius) {
        HashSet<BlockVector3> returnset = new HashSet<BlockVector3>();
        int ceilrad = (int)Math.ceil(radius);
        double radiusSquare = Math.pow(radius, 2.0);
        for (BlockVector3 v : vset) {
            int tipx = v.x();
            int tipy = v.y();
            int tipz = v.z();
            for (int loopx = tipx - ceilrad; loopx <= tipx + ceilrad; ++loopx) {
                for (int loopy = tipy - ceilrad; loopy <= tipy + ceilrad; ++loopy) {
                    for (int loopz = tipz - ceilrad; loopz <= tipz + ceilrad; ++loopz) {
                        if (!(EditSession.lengthSq(loopx - tipx, loopy - tipy, loopz - tipz) <= radiusSquare)) continue;
                        returnset.add(BlockVector3.at(loopx, loopy, loopz));
                    }
                }
            }
        }
        return returnset;
    }

    private static Set<BlockVector3> getHollowed(Set<BlockVector3> vset) {
        HashSet<BlockVector3> returnset = new HashSet<BlockVector3>();
        for (BlockVector3 v : vset) {
            double z;
            double y;
            double x = v.x();
            if (vset.contains((Object)BlockVector3.at(x + 1.0, y = (double)v.y(), z = (double)v.z())) && vset.contains((Object)BlockVector3.at(x - 1.0, y, z)) && vset.contains((Object)BlockVector3.at(x, y + 1.0, z)) && vset.contains((Object)BlockVector3.at(x, y - 1.0, z)) && vset.contains((Object)BlockVector3.at(x, y, z + 1.0)) && vset.contains((Object)BlockVector3.at(x, y, z - 1.0))) continue;
            returnset.add(v);
        }
        return returnset;
    }

    private void recurseHollow(Region region, BlockVector3 origin, Set<BlockVector3> outside) {
        LinkedList<BlockVector3> queue = new LinkedList<BlockVector3>();
        queue.addLast(origin);
        while (!queue.isEmpty()) {
            BlockVector3 current = (BlockVector3)((Object)queue.removeFirst());
            BlockState block = this.getBlock(current);
            if (block.getBlockType().getMaterial().isMovementBlocker() || !outside.add(current) || !region.contains(current)) continue;
            for (BlockVector3 recurseDirection : recurseDirections) {
                queue.addLast(current.add(recurseDirection));
            }
        }
    }

    public int makeBiomeShape(Region region, Vector3 zero, Vector3 unit, BiomeType biomeType, String expressionString, boolean hollow) throws ExpressionException {
        return this.makeBiomeShape(region, zero, unit, biomeType, expressionString, hollow, WorldEdit.getInstance().getConfiguration().calculationTimeout);
    }

    public int makeBiomeShape(Region region, final Vector3 zero, final Vector3 unit, BiomeType biomeType, String expressionString, boolean hollow, final int timeout) throws ExpressionException {
        final Expression expression = Expression.compile(expressionString, "x", "y", "z");
        expression.optimize();
        EditSession editSession = this;
        final WorldEditExpressionEnvironment environment = new WorldEditExpressionEnvironment(editSession, unit, zero);
        expression.setEnvironment(environment);
        final AtomicInteger timedOut = new AtomicInteger();
        ArbitraryBiomeShape shape = new ArbitraryBiomeShape(this, region){

            @Override
            protected BiomeType getBiome(int x, int y, int z, BiomeType defaultBiomeType) {
                Vector3 current = Vector3.at(x, y, z);
                environment.setCurrentBlock(current);
                Vector3 scaled = current.subtract(zero).divide(unit);
                try {
                    double[] dArray = new double[]{scaled.x(), scaled.y(), scaled.z()};
                    if (expression.evaluate(dArray, timeout) <= 0.0) {
                        return null;
                    }
                    return defaultBiomeType;
                }
                catch (ExpressionTimeoutException e) {
                    timedOut.getAndIncrement();
                    return null;
                }
                catch (Exception e) {
                    LOGGER.warn("Failed to create shape", (Throwable)e);
                    return null;
                }
            }
        };
        int changed = shape.generate(this, biomeType, hollow);
        if (timedOut.get() > 0) {
            throw new ExpressionTimeoutException(String.format("%d biomes changed. %d biomes took too long to evaluate (increase time with //timeout)", changed, timedOut.get()));
        }
        return changed;
    }

    public int morph(BlockVector3 position, double brushSize, int minErodeFaces, int numErodeIterations, int minDilateFaces, int numDilateIterations) throws MaxChangedBlocksException {
        BlockState[][][] tmp;
        int newFreq;
        BlockState adj;
        BlockState highestState;
        int highestFreq;
        BlockState blockState;
        int realZ;
        int realY;
        int realX;
        int z;
        int y;
        int x;
        int i;
        int ceilBrushSize = (int)Math.ceil(brushSize);
        int bufferSize = ceilBrushSize * 2 + 3;
        BlockState[][][] currentBuffer = new BlockState[bufferSize][bufferSize][bufferSize];
        BlockState[][][] nextBuffer = new BlockState[bufferSize][bufferSize][bufferSize];
        for (int x2 = 0; x2 < bufferSize; ++x2) {
            for (int y2 = 0; y2 < bufferSize; ++y2) {
                for (int z2 = 0; z2 < bufferSize; ++z2) {
                    BlockState blockState2;
                    currentBuffer[x2][y2][z2] = blockState2 = this.getBlock(position.add(x2 - ceilBrushSize - 1, y2 - ceilBrushSize - 1, z2 - ceilBrushSize - 1));
                    nextBuffer[x2][y2][z2] = blockState2;
                }
            }
        }
        double brushSizeSq = brushSize * brushSize;
        HashMap<BlockState, Integer> blockStateFrequency = new HashMap<BlockState, Integer>();
        for (i = 0; i < numErodeIterations; ++i) {
            for (x = 0; x <= ceilBrushSize * 2; ++x) {
                for (y = 0; y <= ceilBrushSize * 2; ++y) {
                    for (z = 0; z <= ceilBrushSize * 2; ++z) {
                        realX = x - ceilBrushSize;
                        realY = y - ceilBrushSize;
                        realZ = z - ceilBrushSize;
                        if (EditSession.lengthSq(realX, realY, realZ) > brushSizeSq) continue;
                        nextBuffer[x + 1][y + 1][z + 1] = currentBuffer[x + 1][y + 1][z + 1];
                        blockState = currentBuffer[x + 1][y + 1][z + 1];
                        if (blockState.getBlockType().getMaterial().isLiquid() || blockState.getBlockType().getMaterial().isAir()) continue;
                        blockStateFrequency.clear();
                        int totalFaces = 0;
                        highestFreq = 0;
                        highestState = blockState;
                        for (BlockVector3 vec3 : recurseDirections) {
                            adj = currentBuffer[x + 1 + vec3.x()][y + 1 + vec3.y()][z + 1 + vec3.z()];
                            if (!adj.getBlockType().getMaterial().isLiquid() && !adj.getBlockType().getMaterial().isAir()) continue;
                            ++totalFaces;
                            newFreq = blockStateFrequency.getOrDefault(adj, 0) + 1;
                            blockStateFrequency.put(adj, newFreq);
                            if (newFreq <= highestFreq) continue;
                            highestFreq = newFreq;
                            highestState = adj;
                        }
                        if (totalFaces < minErodeFaces) continue;
                        nextBuffer[x + 1][y + 1][z + 1] = highestState;
                    }
                }
            }
            tmp = currentBuffer;
            currentBuffer = nextBuffer;
            nextBuffer = tmp;
        }
        for (i = 0; i < numDilateIterations; ++i) {
            for (x = 0; x <= ceilBrushSize * 2; ++x) {
                for (y = 0; y <= ceilBrushSize * 2; ++y) {
                    for (z = 0; z <= ceilBrushSize * 2; ++z) {
                        realX = x - ceilBrushSize;
                        realY = y - ceilBrushSize;
                        realZ = z - ceilBrushSize;
                        if (EditSession.lengthSq(realX, realY, realZ) > brushSizeSq) continue;
                        nextBuffer[x + 1][y + 1][z + 1] = currentBuffer[x + 1][y + 1][z + 1];
                        blockState = currentBuffer[x + 1][y + 1][z + 1];
                        if (!blockState.getBlockType().getMaterial().isLiquid() && !blockState.getBlockType().getMaterial().isAir()) continue;
                        blockStateFrequency.clear();
                        int totalFaces = 0;
                        highestFreq = 0;
                        highestState = blockState;
                        for (BlockVector3 vec3 : recurseDirections) {
                            adj = currentBuffer[x + 1 + vec3.x()][y + 1 + vec3.y()][z + 1 + vec3.z()];
                            if (adj.getBlockType().getMaterial().isLiquid() || adj.getBlockType().getMaterial().isAir()) continue;
                            ++totalFaces;
                            newFreq = blockStateFrequency.getOrDefault(adj, 0) + 1;
                            blockStateFrequency.put(adj, newFreq);
                            if (newFreq <= highestFreq) continue;
                            highestFreq = newFreq;
                            highestState = adj;
                        }
                        if (totalFaces < minDilateFaces) continue;
                        nextBuffer[x + 1][y + 1][z + 1] = highestState;
                    }
                }
            }
            tmp = currentBuffer;
            currentBuffer = nextBuffer;
            nextBuffer = tmp;
        }
        int changed = 0;
        for (x = 0; x < bufferSize; ++x) {
            for (y = 0; y < bufferSize; ++y) {
                for (z = 0; z < bufferSize; ++z) {
                    if (!this.setBlock(position.add(x - ceilBrushSize - 1, y - ceilBrushSize - 1, z - ceilBrushSize - 1), (B)currentBuffer[x][y][z])) continue;
                    ++changed;
                }
            }
        }
        return changed;
    }

    private static double lengthSq(double x, double y, double z) {
        return x * x + y * y + z * z;
    }

    private static double lengthSq(double x, double z) {
        return x * x + z * z;
    }

    @Deprecated
    public static enum ReorderMode {
        MULTI_STAGE("multi"),
        FAST("fast"),
        NONE("none");

        private final String displayName;

        private ReorderMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }

    public static enum Stage {
        BEFORE_HISTORY,
        BEFORE_REORDER,
        BEFORE_CHANGE;

    }
}

