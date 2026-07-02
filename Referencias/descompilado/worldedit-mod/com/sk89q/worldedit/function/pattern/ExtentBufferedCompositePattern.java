/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public class ExtentBufferedCompositePattern
extends AbstractExtentPattern {
    private final Pattern[] patterns;

    public ExtentBufferedCompositePattern(Extent extent, Pattern ... patterns) {
        super(extent);
        Preconditions.checkArgument((patterns.length != 0 ? 1 : 0) != 0, (Object)"patterns cannot be empty");
        this.patterns = patterns;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        BaseBlock lastBlock = null;
        for (Pattern pattern : this.patterns) {
            lastBlock = pattern.applyBlock(position);
            try {
                this.getExtent().setBlock(position, lastBlock);
            }
            catch (WorldEditException worldEditException) {
                // empty catch block
            }
        }
        return lastBlock;
    }
}

