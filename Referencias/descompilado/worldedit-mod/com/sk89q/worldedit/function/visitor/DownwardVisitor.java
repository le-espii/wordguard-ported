/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.visitor;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.visitor.RecursiveVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.Collection;

public class DownwardVisitor
extends RecursiveVisitor {
    private final int baseY;

    public DownwardVisitor(Mask mask, RegionFunction function, int baseY) {
        super(mask, function);
        Preconditions.checkNotNull((Object)mask);
        this.baseY = baseY;
        Collection<BlockVector3> directions = this.getDirections();
        directions.clear();
        directions.add(BlockVector3.UNIT_X);
        directions.add(BlockVector3.UNIT_MINUS_X);
        directions.add(BlockVector3.UNIT_Z);
        directions.add(BlockVector3.UNIT_MINUS_Z);
        directions.add(BlockVector3.UNIT_MINUS_Y);
    }

    @Override
    protected boolean isVisitable(BlockVector3 from, BlockVector3 to) {
        int fromY = from.y();
        return (fromY == this.baseY || to.subtract(from).y() < 0) && super.isVisitable(from, to);
    }
}

