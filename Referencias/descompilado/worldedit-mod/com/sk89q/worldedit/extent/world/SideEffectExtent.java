/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.world;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SideEffectExtent
extends AbstractDelegateExtent {
    private final World world;
    private final Map<BlockVector3, BlockState> positions = BlockMap.create();
    private final Set<BlockVector2> dirtyChunks = new HashSet<BlockVector2>();
    private final Set<BlockVector2> dirtyBiomes = new HashSet<BlockVector2>();
    private SideEffectSet sideEffectSet = SideEffectSet.defaults();
    private boolean postEditSimulation;
    private static final SideEffectSet INTERNAL_NONE = new SideEffectSet(Arrays.stream(SideEffect.values()).collect(Collectors.toMap(Function.identity(), state -> SideEffect.State.OFF)));

    public SideEffectExtent(World world) {
        super(world);
        Preconditions.checkNotNull((Object)world);
        this.world = world;
    }

    public boolean isPostEditSimulationEnabled() {
        return this.postEditSimulation;
    }

    public void setPostEditSimulationEnabled(boolean enabled) {
        this.postEditSimulation = enabled;
    }

    public SideEffectSet getSideEffectSet() {
        return this.sideEffectSet;
    }

    public void setSideEffectSet(SideEffectSet sideEffectSet) {
        this.sideEffectSet = sideEffectSet;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (this.sideEffectSet.getState(SideEffect.LIGHTING) == SideEffect.State.DELAYED) {
            this.dirtyChunks.add(BlockVector2.at(location.x() >> 4, location.z() >> 4));
        }
        if (this.postEditSimulation) {
            this.positions.put(location, this.world.getBlock(location));
        }
        return this.world.setBlock(location, block, this.postEditSimulation ? INTERNAL_NONE : this.sideEffectSet);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        this.dirtyBiomes.add(BlockVector2.at(position.x() >> 4, position.z() >> 4));
        return this.world.setBiome(position, biome);
    }

    public boolean commitRequired() {
        return this.postEditSimulation || !this.dirtyChunks.isEmpty() || !this.dirtyBiomes.isEmpty();
    }

    @Override
    protected Operation commitBefore() {
        if (!this.commitRequired()) {
            return null;
        }
        return new Operation(){

            @Override
            public Operation resume(RunContext run) throws WorldEditException {
                if (!SideEffectExtent.this.dirtyChunks.isEmpty()) {
                    SideEffectExtent.this.world.fixAfterFastMode(SideEffectExtent.this.dirtyChunks);
                }
                if (!SideEffectExtent.this.dirtyBiomes.isEmpty()) {
                    SideEffectExtent.this.world.sendBiomeUpdates(SideEffectExtent.this.dirtyBiomes);
                }
                if (SideEffectExtent.this.postEditSimulation) {
                    Iterator<Map.Entry<BlockVector3, BlockState>> positionIterator = SideEffectExtent.this.positions.entrySet().iterator();
                    while (run.shouldContinue() && positionIterator.hasNext()) {
                        Map.Entry<BlockVector3, BlockState> position = positionIterator.next();
                        SideEffectExtent.this.world.applySideEffects(position.getKey(), position.getValue(), SideEffectExtent.this.sideEffectSet);
                        positionIterator.remove();
                    }
                    return !SideEffectExtent.this.positions.isEmpty() ? this : null;
                }
                return null;
            }

            @Override
            public void cancel() {
            }
        };
    }
}

