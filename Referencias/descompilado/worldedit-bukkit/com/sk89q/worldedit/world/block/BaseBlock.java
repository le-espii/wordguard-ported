/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.block;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.blocks.TileEntityBlock;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.enginehub.linbus.format.snbt.LinStringIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTagType;

public class BaseBlock
implements BlockStateHolder<BaseBlock>,
TileEntityBlock {
    private final BlockState blockState;
    @Nullable
    private final LazyReference<LinCompoundTag> nbtData;

    protected BaseBlock(BlockState blockState) {
        this.blockState = blockState;
        this.nbtData = null;
    }

    @Deprecated
    protected BaseBlock(BlockState state, CompoundTag nbtData) {
        this(state, LazyReference.from(((CompoundTag)Preconditions.checkNotNull((Object)nbtData))::toLinTag));
    }

    protected BaseBlock(BlockState state, LazyReference<LinCompoundTag> nbtData) {
        Preconditions.checkNotNull(nbtData);
        this.blockState = state;
        this.nbtData = nbtData;
    }

    @Override
    public Map<Property<?>, Object> getStates() {
        return this.blockState.getStates();
    }

    @Override
    public BlockType getBlockType() {
        return this.blockState.getBlockType();
    }

    @Override
    public <V> BaseBlock with(Property<V> property, V value) {
        return ((BlockState)this.blockState.with((Property)property, (Object)value)).toBaseBlock(this.getNbtReference());
    }

    @Override
    public <V> V getState(Property<V> property) {
        return this.blockState.getState(property);
    }

    @Override
    public String getNbtId() {
        LazyReference<LinCompoundTag> nbtData = this.nbtData;
        if (nbtData == null) {
            return "";
        }
        LinStringTag idTag = nbtData.getValue().findTag("id", LinTagType.stringTag());
        return idTag != null ? idTag.value() : "";
    }

    @Override
    @Nullable
    public LazyReference<LinCompoundTag> getNbtReference() {
        return this.nbtData;
    }

    @Override
    public void setNbtReference(@Nullable LazyReference<LinCompoundTag> nbtData) {
        throw new UnsupportedOperationException("This class is immutable.");
    }

    public boolean equals(Object o) {
        if (!(o instanceof BaseBlock)) {
            if (this.nbtData == null && o instanceof BlockStateHolder) {
                return Objects.equals(this.toImmutableState(), ((BlockStateHolder)o).toImmutableState());
            }
            return false;
        }
        BaseBlock otherBlock = (BaseBlock)o;
        return this.blockState.equalsFuzzy(otherBlock.blockState) && Objects.equals(this.getNbt(), otherBlock.getNbt());
    }

    @Override
    public boolean equalsFuzzy(BlockStateHolder<?> o) {
        return this.blockState.equalsFuzzy(o);
    }

    @Override
    public BlockState toImmutableState() {
        return this.blockState;
    }

    @Override
    public BaseBlock toBaseBlock() {
        return this;
    }

    @Override
    public BaseBlock toBaseBlock(LazyReference<LinCompoundTag> compoundTag) {
        if (compoundTag == null) {
            return this.blockState.toBaseBlock();
        }
        if (compoundTag == this.nbtData) {
            return this;
        }
        return new BaseBlock(this.blockState, compoundTag);
    }

    public int hashCode() {
        int ret = this.toImmutableState().hashCode() << 3;
        LinCompoundTag nbtData = this.getNbt();
        if (nbtData != null) {
            ret += nbtData.hashCode();
        }
        return ret;
    }

    @Override
    public String getAsString() {
        String nbtString = "";
        if (this.nbtData != null) {
            nbtString = LinStringIO.writeToString(this.nbtData.getValue());
        }
        return this.blockState.getAsString() + nbtString;
    }

    public String toString() {
        return this.getAsString();
    }
}

