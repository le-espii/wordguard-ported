/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableTable
 *  com.google.common.collect.ImmutableTable$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Table
 */
package com.sk89q.worldedit.world.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.enginehub.linbus.tree.LinCompoundTag;

public class BlockState
implements BlockStateHolder<BlockState> {
    private final BlockType blockType;
    private final Map<Property<?>, Object> values;
    private final BaseBlock emptyBaseBlock;
    private final LazyReference<String> lazyStringRepresentation;
    private Table<Property<?>, Object, BlockState> states;
    private volatile int internalId = BlockStateIdAccess.invalidId();
    private Integer hashCodeCache = null;

    BlockState(BlockType blockType) {
        this.blockType = blockType;
        this.values = new LinkedHashMap();
        this.emptyBaseBlock = new BaseBlock(this);
        this.lazyStringRepresentation = LazyReference.from(() -> BlockStateHolder.super.getAsString());
    }

    static Map<Map<Property<?>, Object>, BlockState> generateStateMap(BlockType blockType) {
        List<Property<?>> properties = blockType.getProperties();
        ImmutableMap.Builder stateMapBuilder = null;
        if (!properties.isEmpty()) {
            ArrayList separatedValues = Lists.newArrayListWithCapacity((int)properties.size());
            for (Property<?> property : properties) {
                separatedValues.add(ImmutableList.copyOf(property.getValues()));
            }
            List valueLists = Lists.cartesianProduct((List)separatedValues);
            stateMapBuilder = ImmutableMap.builderWithExpectedSize((int)valueLists.size());
            for (List valueList : valueLists) {
                TreeMap valueMap = Maps.newTreeMap(Comparator.comparing(Property::getName));
                BlockState stateMaker = new BlockState(blockType);
                int valueCount = valueList.size();
                for (int i = 0; i < valueCount; ++i) {
                    Property<?> property = properties.get(i);
                    Object value = valueList.get(i);
                    valueMap.put(property, value);
                    stateMaker.setState(property, value);
                }
                stateMapBuilder.put((Object)ImmutableMap.copyOf((Map)valueMap), (Object)stateMaker);
            }
        }
        ImmutableMap stateMap = stateMapBuilder == null ? ImmutableMap.of((Object)ImmutableMap.of(), (Object)new BlockState(blockType)) : stateMapBuilder.build();
        Watchdog watchdog = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWatchdog();
        long l = System.currentTimeMillis();
        for (BlockState state : stateMap.values()) {
            state.populate((Map<Map<Property<?>, Object>, BlockState>)stateMap);
            if (watchdog == null) continue;
            watchdog.tick();
        }
        long timeTaken = System.currentTimeMillis() - l;
        if (timeTaken > 5000L) {
            WorldEdit.logger.warn("Took more than 5 seconds to generate complete state map for " + blockType.id() + ". This block is likely improperly using properties. State count: " + stateMap.size() + ". " + timeTaken + "ms elapsed.");
        }
        return stateMap;
    }

    private void populate(Map<Map<Property<?>, Object>, BlockState> stateMap) {
        ImmutableTable.Builder states = ImmutableTable.builder();
        for (Map.Entry<Property<?>, Object> entry : this.values.entrySet()) {
            Property<?> property = entry.getKey();
            for (Object value : property.getValues()) {
                if (value == entry.getValue()) continue;
                BlockState modifiedState = stateMap.get(this.withValue(property, value));
                if (modifiedState != null) {
                    states.put(property, value, (Object)modifiedState);
                    continue;
                }
                System.out.println(stateMap);
                WorldEdit.logger.warn("Found a null state at " + String.valueOf(this.withValue(property, value)));
            }
        }
        this.states = states.build();
    }

    private <V> Map<Property<?>, Object> withValue(Property<V> property, V value) {
        ImmutableMap.Builder values = ImmutableMap.builderWithExpectedSize((int)this.values.size());
        for (Map.Entry<Property<?>, Object> entry : this.values.entrySet()) {
            if (entry.getKey().equals(property)) {
                values.put(entry.getKey(), value);
                continue;
            }
            values.put(entry);
        }
        return values.build();
    }

    @Override
    public BlockType getBlockType() {
        return this.blockType;
    }

    @Override
    public <V> BlockState with(Property<V> property, V value) {
        BlockState result = (BlockState)this.states.get(property, value);
        return result == null ? this : result;
    }

    @Override
    public <V> V getState(Property<V> property) {
        return (V)this.values.get(property);
    }

    @Override
    public Map<Property<?>, Object> getStates() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public boolean equalsFuzzy(BlockStateHolder<?> o) {
        if (null == o) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!this.getBlockType().equals(o.getBlockType())) {
            return false;
        }
        HashSet differingProperties = new HashSet();
        for (Property<?> state : o.getStates().keySet()) {
            if (this.getState(state) != null) continue;
            differingProperties.add(state);
        }
        for (Property<?> property : this.getStates().keySet()) {
            if (o.getState(property) != null) continue;
            differingProperties.add(property);
        }
        for (Property<?> property : this.getStates().keySet()) {
            if (differingProperties.contains(property) || Objects.equals(this.getState(property), o.getState(property))) continue;
            return false;
        }
        return true;
    }

    @Override
    public BlockState toImmutableState() {
        return this;
    }

    @Override
    public BaseBlock toBaseBlock() {
        return this.emptyBaseBlock;
    }

    @Override
    public BaseBlock toBaseBlock(LazyReference<LinCompoundTag> compoundTag) {
        if (compoundTag == null) {
            return this.toBaseBlock();
        }
        return new BaseBlock(this, compoundTag);
    }

    BlockState setState(Property<?> property, Object value) {
        this.values.put(property, value);
        return this;
    }

    @Override
    public String getAsString() {
        return this.lazyStringRepresentation.getValue();
    }

    public String toString() {
        return this.getAsString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BlockState)) {
            return false;
        }
        return this.equalsFuzzy((BlockState)obj);
    }

    public int hashCode() {
        if (this.hashCodeCache == null) {
            this.hashCodeCache = Objects.hash(this.blockType, this.values);
        }
        return this.hashCodeCache;
    }

    static {
        BlockStateIdAccess.setBlockStateInternalId(new BlockStateIdAccess.BlockStateInternalId(){

            @Override
            public int getInternalId(BlockState blockState) {
                return blockState.internalId;
            }

            @Override
            public void setInternalId(BlockState blockState, int internalId) {
                blockState.internalId = internalId;
            }
        });
    }
}

