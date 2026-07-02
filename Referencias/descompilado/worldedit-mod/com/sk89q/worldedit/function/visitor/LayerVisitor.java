/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.visitor;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.FlatRegion;

public class LayerVisitor
implements Operation {
    private final FlatRegion flatRegion;
    private final LayerFunction function;
    private Mask2D mask = Masks.alwaysTrue2D();
    private final int minY;
    private final int maxY;

    public LayerVisitor(FlatRegion flatRegion, int minY, int maxY, LayerFunction function) {
        Preconditions.checkNotNull((Object)flatRegion);
        Preconditions.checkArgument((minY <= maxY ? 1 : 0) != 0, (Object)"minY <= maxY required");
        Preconditions.checkNotNull((Object)function);
        this.flatRegion = flatRegion;
        this.minY = minY;
        this.maxY = maxY;
        this.function = function;
    }

    public Mask2D getMask() {
        return this.mask;
    }

    public void setMask(Mask2D mask) {
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        block0: for (BlockVector2 column : this.flatRegion.asFlatRegion()) {
            if (!this.mask.test(column) || this.function.isGround(column.toBlockVector3(this.maxY + 1))) continue;
            boolean found = false;
            int groundY = 0;
            for (int y = this.maxY; y >= this.minY; --y) {
                BlockVector3 test = column.toBlockVector3(y);
                if (!found && this.function.isGround(test)) {
                    found = true;
                    groundY = y;
                }
                if (found && !this.function.apply(test, groundY - y)) continue block0;
            }
        }
        return null;
    }

    @Override
    public void cancel() {
    }
}

