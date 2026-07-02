/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.world.block;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.HashMap;
import java.util.Map;

public class FuzzyBlockState
extends BlockState {
    FuzzyBlockState(BlockType blockType) {
        super(blockType);
    }

    private FuzzyBlockState(BlockType blockType, Map<Property<?>, Object> values) {
        this(blockType);
        for (Map.Entry<Property<?>, Object> entry : values.entrySet()) {
            this.setState(entry.getKey(), entry.getValue());
        }
    }

    public BlockState getFullState() {
        BlockStateHolder<BlockState> state = this.getBlockType().getDefaultState();
        for (Map.Entry<Property<?>, Object> entry : this.getStates().entrySet()) {
            Property<?> objKey = entry.getKey();
            state = state.with(objKey, entry.getValue());
        }
        return state;
    }

    @Override
    public BlockState toImmutableState() {
        return this.getFullState();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlockType type;
        private final Map<Property<?>, Object> values = new HashMap();

        public Builder type(BlockType type) {
            Preconditions.checkNotNull((Object)type);
            this.type = type;
            return this;
        }

        public Builder type(BlockState state) {
            Preconditions.checkNotNull((Object)state);
            this.type = state.getBlockType();
            return this;
        }

        public <V> Builder withProperty(Property<V> property, V value) {
            Preconditions.checkNotNull(property);
            Preconditions.checkNotNull(value);
            Preconditions.checkNotNull((Object)this.type, (Object)"The type must be set before the properties!");
            this.type.getProperty(property.getName());
            this.values.put(property, value);
            return this;
        }

        public FuzzyBlockState build() {
            Preconditions.checkNotNull((Object)this.type);
            if (this.values.isEmpty()) {
                return this.type.getFuzzyMatcher();
            }
            return new FuzzyBlockState(this.type, this.values);
        }

        public Builder reset() {
            this.type = null;
            this.values.clear();
            return this;
        }
    }
}

