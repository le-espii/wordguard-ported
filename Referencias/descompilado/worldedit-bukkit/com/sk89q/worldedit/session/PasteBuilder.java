/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.session;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;

public class PasteBuilder {
    private final Clipboard clipboard;
    private final Transform transform;
    private final Extent targetExtent;
    private Mask sourceMask = Masks.alwaysTrue();
    private BlockVector3 to = BlockVector3.ZERO;
    private boolean ignoreAirBlocks;
    private boolean ignoreStructureVoidBlocks;
    private boolean copyEntities = true;
    private boolean copyBiomes;

    PasteBuilder(ClipboardHolder holder, Extent targetExtent) {
        Preconditions.checkNotNull((Object)holder);
        Preconditions.checkNotNull((Object)targetExtent);
        this.clipboard = holder.getClipboard();
        this.transform = holder.getTransform();
        this.targetExtent = targetExtent;
    }

    public PasteBuilder to(BlockVector3 to) {
        this.to = to;
        return this;
    }

    public PasteBuilder maskSource(Mask sourceMask) {
        if (sourceMask == null) {
            this.sourceMask = Masks.alwaysTrue();
            return this;
        }
        this.sourceMask = sourceMask;
        return this;
    }

    public PasteBuilder ignoreAirBlocks(boolean ignoreAirBlocks) {
        this.ignoreAirBlocks = ignoreAirBlocks;
        return this;
    }

    public PasteBuilder ignoreStructureVoidBlocks(boolean ignoreStructureVoidBlocks) {
        this.ignoreStructureVoidBlocks = ignoreStructureVoidBlocks;
        return this;
    }

    public PasteBuilder copyEntities(boolean copyEntities) {
        this.copyEntities = copyEntities;
        return this;
    }

    public PasteBuilder copyBiomes(boolean copyBiomes) {
        this.copyBiomes = copyBiomes;
        return this;
    }

    public Operation build() {
        BlockTransformExtent extent = new BlockTransformExtent(this.clipboard, this.transform);
        ForwardExtentCopy copy = new ForwardExtentCopy(extent, this.clipboard.getRegion(), this.clipboard.getOrigin(), this.targetExtent, this.to);
        copy.setTransform(this.transform);
        Mask combinedMask = this.sourceMask;
        if (this.ignoreAirBlocks) {
            Mask mask = combinedMask = combinedMask == Masks.alwaysTrue() ? new ExistingBlockMask(this.clipboard) : new MaskIntersection(combinedMask, new ExistingBlockMask(this.clipboard));
        }
        if (this.ignoreStructureVoidBlocks) {
            Mask structureVoidMask = Masks.negate(new BlockTypeMask((Extent)this.clipboard, BlockTypes.STRUCTURE_VOID));
            combinedMask = combinedMask == Masks.alwaysTrue() ? structureVoidMask : new MaskIntersection(combinedMask, structureVoidMask);
        }
        copy.setSourceMask(combinedMask);
        copy.setCopyingEntities(this.copyEntities);
        copy.setCopyingBiomes(this.copyBiomes && this.clipboard.hasBiomes());
        return copy;
    }
}

