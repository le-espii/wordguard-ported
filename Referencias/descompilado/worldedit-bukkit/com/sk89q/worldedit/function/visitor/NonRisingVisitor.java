/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.visitor;

import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.visitor.RecursiveVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.Collection;

public class NonRisingVisitor
extends RecursiveVisitor {
    public NonRisingVisitor(Mask mask, RegionFunction function) {
        super(mask, function);
        Collection<BlockVector3> directions = this.getDirections();
        directions.clear();
        directions.add(BlockVector3.UNIT_X);
        directions.add(BlockVector3.UNIT_MINUS_X);
        directions.add(BlockVector3.UNIT_Z);
        directions.add(BlockVector3.UNIT_MINUS_Z);
        directions.add(BlockVector3.UNIT_MINUS_Y);
    }
}

