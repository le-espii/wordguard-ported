/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.shape;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;

public abstract class ArbitraryShape {
    private static final Object OUTSIDE = new Object();
    protected final Region extent;
    private final int cacheOffsetX;
    private final int cacheOffsetY;
    private final int cacheOffsetZ;
    private final int cacheSizeX;
    private final int cacheSizeY;
    private final int cacheSizeZ;
    private Object[] cache;

    public ArbitraryShape(Region extent) {
        this.extent = extent;
        BlockVector3 min = extent.getMinimumPoint();
        BlockVector3 max = extent.getMaximumPoint();
        this.cacheOffsetX = min.x() - 1;
        this.cacheOffsetY = min.y() - 1;
        this.cacheOffsetZ = min.z() - 1;
        this.cacheSizeX = max.x() - this.cacheOffsetX + 2;
        this.cacheSizeY = max.y() - this.cacheOffsetY + 2;
        this.cacheSizeZ = max.z() - this.cacheOffsetZ + 2;
    }

    protected Region getExtent() {
        return this.extent;
    }

    protected abstract BaseBlock getMaterial(int var1, int var2, int var3, BaseBlock var4);

    public int generate(EditSession editSession, Pattern pattern, boolean hollow) throws MaxChangedBlocksException {
        if (hollow && this.cache == null) {
            this.cache = new Object[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];
        }
        int affected = 0;
        for (BlockVector3 position : this.getExtent()) {
            BaseBlock material = this.getMaterial(position, pattern, hollow);
            if (material == null || !editSession.setBlock(position, material)) continue;
            ++affected;
        }
        return affected;
    }

    private BaseBlock getMaterial(BlockVector3 position, Pattern pattern, boolean hollow) {
        int x = position.x();
        int y = position.y();
        int z = position.z();
        if (!hollow) {
            return this.getMaterial(x, y, z, pattern.applyBlock(position));
        }
        Object cacheEntry = this.getMaterialCached(x, y, z, pattern);
        if (cacheEntry == OUTSIDE) {
            return null;
        }
        BaseBlock material = (BaseBlock)cacheEntry;
        if (this.isOutsideCached(x + 1, y, z, pattern)) {
            return material;
        }
        if (this.isOutsideCached(x - 1, y, z, pattern)) {
            return material;
        }
        if (this.isOutsideCached(x, y, z + 1, pattern)) {
            return material;
        }
        if (this.isOutsideCached(x, y, z - 1, pattern)) {
            return material;
        }
        if (this.isOutsideCached(x, y + 1, z, pattern)) {
            return material;
        }
        if (this.isOutsideCached(x, y - 1, z, pattern)) {
            return material;
        }
        return null;
    }

    private boolean isOutsideCached(int x, int y, int z, Pattern pattern) {
        return this.getMaterialCached(x, y, z, pattern) == OUTSIDE;
    }

    private Object getMaterialCached(int x, int y, int z, Pattern pattern) {
        int index = y - this.cacheOffsetY + (z - this.cacheOffsetZ) * this.cacheSizeY + (x - this.cacheOffsetX) * this.cacheSizeY * this.cacheSizeZ;
        Object cacheEntry = this.cache[index];
        if (cacheEntry == null) {
            BaseBlock material = this.getMaterial(x, y, z, pattern.applyBlock(BlockVector3.at(x, y, z)));
            if (material == null) {
                this.cache[index] = OUTSIDE;
                return this.cache[index];
            }
            this.cache[index] = material;
            return this.cache[index];
        }
        return cacheEntry;
    }
}

