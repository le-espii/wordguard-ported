/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.buffer;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.pattern.BiomePattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractFlatRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ForgetfulExtentBuffer
extends AbstractDelegateExtent
implements Pattern,
BiomePattern {
    private final Map<BlockVector3, BaseBlock> buffer = new LinkedHashMap<BlockVector3, BaseBlock>();
    private final Map<BlockVector3, BiomeType> biomeBuffer = new LinkedHashMap<BlockVector3, BiomeType>();
    private final Mask mask;
    private BlockVector3 min = null;
    private BlockVector3 max = null;

    public ForgetfulExtentBuffer(Extent delegate) {
        this(delegate, Masks.alwaysTrue());
    }

    public ForgetfulExtentBuffer(Extent delegate, Mask mask) {
        super(delegate);
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    private void updateBounds(BlockVector3 position) {
        this.min = this.min == null ? position : this.min.getMinimum(position);
        this.max = this.max == null ? position : this.max.getMaximum(position);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        this.updateBounds(location);
        if (this.mask.test(location)) {
            this.buffer.put(location, block.toBaseBlock());
            return true;
        }
        return this.getExtent().setBlock(location, block);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        this.updateBounds(position);
        if (this.mask.test(position)) {
            this.biomeBuffer.put(position, biome);
            return true;
        }
        return this.getExtent().setBiome(position, biome);
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 pos) {
        BaseBlock block = this.buffer.get((Object)pos);
        if (block != null) {
            return block;
        }
        return BlockTypes.AIR.getDefaultState().toBaseBlock();
    }

    @Override
    public BiomeType applyBiome(BlockVector3 pos) {
        BiomeType biome = this.biomeBuffer.get((Object)pos);
        if (biome != null) {
            return biome;
        }
        return BiomeTypes.OCEAN;
    }

    public Region asRegion() {
        return new AbstractFlatRegion(null){

            @Override
            public BlockVector3 getMinimumPoint() {
                return ForgetfulExtentBuffer.this.min != null ? ForgetfulExtentBuffer.this.min : BlockVector3.ZERO;
            }

            @Override
            public BlockVector3 getMaximumPoint() {
                return ForgetfulExtentBuffer.this.max != null ? ForgetfulExtentBuffer.this.max : BlockVector3.ZERO;
            }

            @Override
            public void expand(BlockVector3 ... changes) throws RegionOperationException {
                throw new UnsupportedOperationException("Cannot change the size of this region");
            }

            @Override
            public void contract(BlockVector3 ... changes) throws RegionOperationException {
                throw new UnsupportedOperationException("Cannot change the size of this region");
            }

            @Override
            public boolean contains(BlockVector3 position) {
                return ForgetfulExtentBuffer.this.buffer.containsKey((Object)position);
            }

            @Override
            public Iterator<BlockVector3> iterator() {
                return ForgetfulExtentBuffer.this.buffer.keySet().iterator();
            }

            @Override
            public Iterable<BlockVector2> asFlatRegion() {
                return ForgetfulExtentBuffer.this.biomeBuffer.keySet().stream().map(BlockVector3::toBlockVector2).collect(Collectors.toSet());
            }
        };
    }
}

