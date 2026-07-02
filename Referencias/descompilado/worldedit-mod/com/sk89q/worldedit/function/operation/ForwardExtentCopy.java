/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.sk89q.worldedit.function.operation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.CombinedRegionFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.biome.ExtentBiomeCopy;
import com.sk89q.worldedit.function.block.ExtentBlockCopy;
import com.sk89q.worldedit.function.entity.ExtentEntityCopy;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.operation.DelegateOperation;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.OperationQueue;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.function.visitor.EntityVisitor;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Identity;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.ArrayList;

public class ForwardExtentCopy
implements Operation {
    private final Extent source;
    private final Extent destination;
    private final Region region;
    private final BlockVector3 from;
    private final BlockVector3 to;
    private int repetitions = 1;
    private Mask sourceMask = Masks.alwaysTrue();
    private boolean removingEntities;
    private boolean copyingEntities = true;
    private boolean copyingBiomes;
    private RegionFunction sourceFunction = null;
    private Transform transform = new Identity();
    private Transform currentTransform = null;
    private RegionVisitor lastVisitor;
    private RegionVisitor lastBiomeVisitor;
    private EntityVisitor lastEntityVisitor;
    private int affectedBlocks;
    private int affectedBiomeCols;
    private int affectedEntities;

    public ForwardExtentCopy(Extent source, Region region, Extent destination, BlockVector3 to) {
        this(source, region, region.getMinimumPoint(), destination, to);
    }

    public ForwardExtentCopy(Extent source, Region region, BlockVector3 from, Extent destination, BlockVector3 to) {
        Preconditions.checkNotNull((Object)source);
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)((Object)from));
        Preconditions.checkNotNull((Object)destination);
        Preconditions.checkNotNull((Object)((Object)to));
        this.source = source;
        this.destination = destination;
        this.region = region;
        this.from = from;
        this.to = to;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public void setTransform(Transform transform) {
        Preconditions.checkNotNull((Object)transform);
        this.transform = transform;
    }

    public Mask getSourceMask() {
        return this.sourceMask;
    }

    public void setSourceMask(Mask sourceMask) {
        Preconditions.checkNotNull((Object)sourceMask);
        this.sourceMask = sourceMask;
    }

    public RegionFunction getSourceFunction() {
        return this.sourceFunction;
    }

    public void setSourceFunction(RegionFunction function) {
        this.sourceFunction = function;
    }

    public int getRepetitions() {
        return this.repetitions;
    }

    public void setRepetitions(int repetitions) {
        Preconditions.checkArgument((repetitions >= 0 ? 1 : 0) != 0, (Object)"number of repetitions must be non-negative");
        this.repetitions = repetitions;
    }

    public boolean isCopyingEntities() {
        return this.copyingEntities;
    }

    public void setCopyingEntities(boolean copyingEntities) {
        this.copyingEntities = copyingEntities;
    }

    public boolean isRemovingEntities() {
        return this.removingEntities;
    }

    public void setRemovingEntities(boolean removingEntities) {
        this.removingEntities = removingEntities;
    }

    public boolean isCopyingBiomes() {
        return this.copyingBiomes;
    }

    public void setCopyingBiomes(boolean copyingBiomes) {
        this.copyingBiomes = copyingBiomes;
    }

    public int getAffected() {
        return this.affectedBlocks + this.affectedBiomeCols + this.affectedEntities;
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        if (this.lastVisitor != null) {
            this.affectedBlocks += this.lastVisitor.getAffected();
            this.lastVisitor = null;
        }
        if (this.lastBiomeVisitor != null) {
            this.affectedBiomeCols += this.lastBiomeVisitor.getAffected();
            this.lastBiomeVisitor = null;
        }
        if (this.lastEntityVisitor != null) {
            this.affectedEntities += this.lastEntityVisitor.getAffected();
            this.lastEntityVisitor = null;
        }
        if (this.repetitions > 0) {
            RegionVisitor blockVisitor;
            --this.repetitions;
            this.currentTransform = this.currentTransform == null ? this.transform : this.currentTransform.combine(this.transform);
            ExtentBlockCopy blockCopy = new ExtentBlockCopy(this.source, this.from, this.destination, this.to, this.currentTransform);
            RegionMaskingFilter filteredFunction = new RegionMaskingFilter(this.sourceMask, this.sourceFunction == null ? blockCopy : new CombinedRegionFunction(blockCopy, this.sourceFunction));
            this.lastVisitor = blockVisitor = new RegionVisitor(this.region, filteredFunction);
            if (!this.copyingBiomes && !this.copyingEntities) {
                return new DelegateOperation(this, blockVisitor);
            }
            ArrayList ops = Lists.newArrayList((Object[])new Operation[]{blockVisitor});
            if (this.copyingBiomes) {
                ExtentBiomeCopy biomeCopy = new ExtentBiomeCopy(this.source, this.from, this.destination, this.to, this.currentTransform);
                RegionFunction biomeFunction = this.sourceFunction == null ? biomeCopy : new RegionMaskingFilter(this.sourceMask, biomeCopy);
                RegionVisitor biomeVisitor = new RegionVisitor(this.region, biomeFunction);
                ops.add(biomeVisitor);
                this.lastBiomeVisitor = biomeVisitor;
            }
            if (this.copyingEntities) {
                ExtentEntityCopy entityCopy = new ExtentEntityCopy(this.from.toVector3(), this.destination, this.to.toVector3(), this.currentTransform);
                entityCopy.setRemoving(this.removingEntities);
                ArrayList entities = Lists.newArrayList(this.source.getEntities(this.region));
                entities.removeIf(entity -> {
                    EntityProperties properties = entity.getFacet(EntityProperties.class);
                    return properties != null && !properties.isPasteable();
                });
                EntityVisitor entityVisitor = new EntityVisitor(entities.iterator(), entityCopy);
                ops.add(entityVisitor);
                this.lastEntityVisitor = entityVisitor;
            }
            return new DelegateOperation(this, new OperationQueue(ops));
        }
        return null;
    }

    @Override
    public void cancel() {
    }

    @Override
    public Iterable<Component> getStatusMessages() {
        return ImmutableList.of((Object)TranslatableComponent.of("worldedit.operation.affected.block", TextComponent.of(this.affectedBlocks)).color(TextColor.LIGHT_PURPLE), (Object)TranslatableComponent.of("worldedit.operation.affected.biome", TextComponent.of(this.affectedBiomeCols)).color(TextColor.LIGHT_PURPLE), (Object)TranslatableComponent.of("worldedit.operation.affected.entity", TextComponent.of(this.affectedEntities)).color(TextColor.LIGHT_PURPLE));
    }
}

