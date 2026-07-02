/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.blocks;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;

@Deprecated
public class LegacyBaseBlockWrapper
extends BaseBlock {
    protected LegacyBaseBlockWrapper(BlockState blockState) {
        super(blockState);
    }

    @Override
    @Nullable
    public LazyReference<LinCompoundTag> getNbtReference() {
        CompoundTag nbtData = this.getNbtData();
        return nbtData == null ? null : LazyReference.from(nbtData::toLinTag);
    }

    @Override
    public void setNbtReference(@Nullable LazyReference<LinCompoundTag> nbtData) {
        this.setNbtData(nbtData == null ? null : new CompoundTag(nbtData.getValue()));
    }
}

