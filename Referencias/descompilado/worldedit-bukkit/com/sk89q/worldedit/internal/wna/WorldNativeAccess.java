/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.internal.wna;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface WorldNativeAccess<NC, NBS, NP> {
    default public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, SideEffectSet sideEffects) throws WorldEditException {
        BaseBlock baseBlock;
        LinCompoundTag tag;
        NBS lastValue;
        boolean successful;
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull(block);
        this.setCurrentSideEffectSet(sideEffects);
        int x = position.x();
        int y = position.y();
        int z = position.z();
        NC chunk = this.getChunk(x >> 4, z >> 4);
        NP pos = this.getPosition(x, y, z);
        NBS old = this.getBlockState(chunk, pos);
        NBS newState = this.toNative(block.toImmutableState());
        if (sideEffects.shouldApply(SideEffect.VALIDATION)) {
            newState = this.getValidBlockForPosition(newState, pos);
        }
        boolean bl = successful = (lastValue = this.setBlockState(chunk, pos, newState)) != null;
        if ((successful || old == newState) && block instanceof BaseBlock && (tag = (baseBlock = (BaseBlock)block).getNbt()) != null) {
            LinCompoundTag.Builder tagBuilder = tag.toBuilder().putInt("x", position.x()).putInt("y", position.y()).putInt("z", position.z());
            if (!baseBlock.getNbtId().isBlank()) {
                tagBuilder.putString("id", baseBlock.getNbtId());
            }
            tag = tagBuilder.build();
            successful = this.updateTileEntity(pos, tag);
        }
        if (successful) {
            if (sideEffects.getState(SideEffect.LIGHTING) == SideEffect.State.ON) {
                this.updateLightingForBlock(pos);
            }
            this.markAndNotifyBlock(pos, chunk, old, newState, sideEffects);
        }
        return successful;
    }

    default public void applySideEffects(BlockVector3 position, BlockState previousType, SideEffectSet sideEffectSet) {
        this.setCurrentSideEffectSet(sideEffectSet);
        NP pos = this.getPosition(position.x(), position.y(), position.z());
        NC chunk = this.getChunk(position.x() >> 4, position.z() >> 4);
        NBS oldData = this.toNative(previousType);
        NBS newData = this.getBlockState(chunk, pos);
        if (sideEffectSet.shouldApply(SideEffect.UPDATE)) {
            this.updateBlock(pos, oldData, newData);
        }
        if (sideEffectSet.getState(SideEffect.LIGHTING) == SideEffect.State.ON) {
            this.updateLightingForBlock(pos);
        }
        this.markAndNotifyBlock(pos, chunk, oldData, newData, sideEffectSet);
    }

    default public void setCurrentSideEffectSet(SideEffectSet sideEffectSet) {
    }

    public NC getChunk(int var1, int var2);

    public NBS toNative(BlockState var1);

    public NBS getBlockState(NC var1, NP var2);

    @Nullable
    public NBS setBlockState(NC var1, NP var2, NBS var3);

    public NBS getValidBlockForPosition(NBS var1, NP var2);

    public NP getPosition(int var1, int var2, int var3);

    public void updateLightingForBlock(NP var1);

    public boolean updateTileEntity(NP var1, LinCompoundTag var2);

    public void notifyBlockUpdate(NC var1, NP var2, NBS var3, NBS var4);

    public boolean isChunkTicking(NC var1);

    public void markBlockChanged(NC var1, NP var2);

    public void notifyNeighbors(NP var1, NBS var2, NBS var3);

    default public void updateBlock(NP pos, NBS oldState, NBS newState) {
    }

    public void updateNeighbors(NP var1, NBS var2, NBS var3, int var4);

    public void onBlockStateChange(NP var1, NBS var2, NBS var3);

    default public void markAndNotifyBlock(NP pos, NC chunk, NBS oldState, NBS newState, SideEffectSet sideEffectSet) {
        NBS blockState1 = this.getBlockState(chunk, pos);
        if (blockState1 != newState) {
            return;
        }
        if (this.isChunkTicking(chunk)) {
            if (sideEffectSet.shouldApply(SideEffect.ENTITY_AI)) {
                this.notifyBlockUpdate(chunk, pos, oldState, newState);
            } else if (sideEffectSet.shouldApply(SideEffect.NETWORK)) {
                this.markBlockChanged(chunk, pos);
            }
        }
        if (sideEffectSet.shouldApply(SideEffect.NEIGHBORS)) {
            this.notifyNeighbors(pos, oldState, newState);
        }
        if (sideEffectSet.shouldApply(SideEffect.NEIGHBORS)) {
            this.updateNeighbors(pos, oldState, newState, 512);
        }
        if (sideEffectSet.shouldApply(SideEffect.POI_UPDATE)) {
            this.onBlockStateChange(pos, oldState, blockState1);
        }
    }
}

