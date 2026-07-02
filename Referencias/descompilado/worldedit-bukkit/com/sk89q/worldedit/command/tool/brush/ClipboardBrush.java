/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public class ClipboardBrush
implements Brush {
    private final ClipboardHolder holder;
    private final boolean ignoreAirBlocks;
    private final boolean ignoreStructureVoidBlocks;
    private final boolean usingOrigin;
    private final boolean pasteEntities;
    private final boolean pasteBiomes;
    private final Mask sourceMask;

    public ClipboardBrush(ClipboardHolder holder, boolean ignoreAirBlocks, boolean usingOrigin) {
        this(holder, ignoreAirBlocks, usingOrigin, false, false, null);
    }

    @Deprecated
    public ClipboardBrush(ClipboardHolder holder, boolean ignoreAirBlocks, boolean usingOrigin, boolean pasteEntities, boolean pasteBiomes, Mask sourceMask) {
        this(holder, ignoreAirBlocks, false, usingOrigin, pasteEntities, pasteBiomes, sourceMask);
    }

    public ClipboardBrush(ClipboardHolder holder, boolean ignoreAirBlocks, boolean ignoreStructureVoidBlocks, boolean usingOrigin, boolean pasteEntities, boolean pasteBiomes, Mask sourceMask) {
        this.holder = holder;
        this.ignoreAirBlocks = ignoreAirBlocks;
        this.ignoreStructureVoidBlocks = ignoreStructureVoidBlocks;
        this.usingOrigin = usingOrigin;
        this.pasteEntities = pasteEntities;
        this.pasteBiomes = pasteBiomes;
        this.sourceMask = sourceMask;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        Clipboard clipboard = this.holder.getClipboard();
        Region region = clipboard.getRegion();
        BlockVector3 centerOffset = region.getCenter().toBlockPoint().subtract(clipboard.getOrigin());
        Operation operation = this.holder.createPaste(editSession).to(this.usingOrigin ? position : position.subtract(centerOffset)).ignoreAirBlocks(this.ignoreAirBlocks).ignoreStructureVoidBlocks(this.ignoreStructureVoidBlocks).copyEntities(this.pasteEntities).copyBiomes(this.pasteBiomes).maskSource(this.sourceMask).build();
        Operations.completeLegacy(operation);
    }
}

