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
import com.sk89q.worldedit.function.visitor.BreadthFirstSearch;
import com.sk89q.worldedit.math.BlockVector3;

public class RecursiveVisitor
extends BreadthFirstSearch {
    private final Mask mask;

    public RecursiveVisitor(Mask mask, RegionFunction function) {
        super(function);
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    @Override
    protected boolean isVisitable(BlockVector3 from, BlockVector3 to) {
        return this.mask.test(to);
    }
}

