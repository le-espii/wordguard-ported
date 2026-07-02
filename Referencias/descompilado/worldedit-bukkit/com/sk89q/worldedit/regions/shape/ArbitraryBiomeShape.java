/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.shape;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.util.BitSet;

public abstract class ArbitraryBiomeShape {
    private final Region extent;
    private final int cacheOffsetX;
    private final int cacheOffsetY;
    private final int cacheOffsetZ;
    private final int cacheSizeX;
    private final int cacheSizeY;
    private final int cacheSizeZ;
    private final BiomeType[] cache;
    private final BitSet isCached;

    public ArbitraryBiomeShape(Region extent) {
        this.extent = extent;
        BlockVector3 min = extent.getMinimumPoint();
        BlockVector3 max = extent.getMaximumPoint();
        this.cacheOffsetX = min.x() - 1;
        this.cacheOffsetY = min.y() - 1;
        this.cacheOffsetZ = min.z() - 1;
        this.cacheSizeX = max.x() - this.cacheOffsetX + 2;
        this.cacheSizeY = max.y() - this.cacheOffsetY + 2;
        this.cacheSizeZ = max.z() - this.cacheOffsetZ + 2;
        this.cache = new BiomeType[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];
        this.isCached = new BitSet(this.cache.length);
    }

    protected Iterable<BlockVector3> getExtent() {
        return this.extent;
    }

    protected abstract BiomeType getBiome(int var1, int var2, int var3, BiomeType var4);

    private BiomeType getBiomeCached(int x, int y, int z, BiomeType baseBiome) {
        int index = y - this.cacheOffsetY + (z - this.cacheOffsetZ) * this.cacheSizeY + (x - this.cacheOffsetX) * this.cacheSizeY * this.cacheSizeZ;
        if (!this.isCached.get(index)) {
            BiomeType material = this.getBiome(x, y, z, baseBiome);
            this.isCached.set(index);
            this.cache[index] = material;
            return material;
        }
        return this.cache[index];
    }

    private boolean isOutside(int x, int y, int z, BiomeType baseBiome) {
        return this.getBiomeCached(x, y, z, baseBiome) == null;
    }

    public int generate(EditSession editSession, BiomeType baseBiome, boolean hollow) {
        int affected = 0;
        for (BlockVector3 position : this.getExtent()) {
            BiomeType material;
            int x = position.x();
            int y = position.y();
            int z = position.z();
            if (!hollow) {
                material = this.getBiome(x, y, z, baseBiome);
                if (material == null) continue;
                editSession.getWorld().setBiome(position, material);
                ++affected;
                continue;
            }
            material = this.getBiomeCached(x, y, z, baseBiome);
            if (material == null || !this.shouldDraw(x, y, z, material)) continue;
            editSession.getWorld().setBiome(position, material);
            ++affected;
        }
        return affected;
    }

    private boolean shouldDraw(int x, int y, int z, BiomeType material) {
        if (this.isOutside(x + 1, y, z, material)) {
            return true;
        }
        if (this.isOutside(x - 1, y, z, material)) {
            return true;
        }
        if (this.isOutside(x, y, z + 1, material)) {
            return true;
        }
        if (this.isOutside(x, y, z - 1, material)) {
            return true;
        }
        if (this.isOutside(x, y + 1, z, material)) {
            return true;
        }
        return this.isOutside(x, y - 1, z, material);
    }
}

