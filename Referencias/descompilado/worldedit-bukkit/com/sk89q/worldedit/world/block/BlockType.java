/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.FuzzyBlockState;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public class BlockType
implements Keyed {
    public static final NamespacedRegistry<BlockType> REGISTRY = new NamespacedRegistry("block type", true);
    private final String id;
    private final Function<BlockState, BlockState> values;
    private final LazyReference<BlockState> defaultState = LazyReference.from(this::computeDefaultState);
    private final LazyReference<FuzzyBlockState> emptyFuzzy = LazyReference.from(() -> new FuzzyBlockState(this));
    private final LazyReference<Map<String, ? extends Property<?>>> properties = LazyReference.from(() -> ImmutableMap.copyOf(WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBlockRegistry().getProperties(this)));
    private final LazyReference<BlockMaterial> blockMaterial = LazyReference.from(() -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBlockRegistry().getMaterial(this));
    private final LazyReference<Map<Map<Property<?>, Object>, BlockState>> blockStatesMap = LazyReference.from(() -> BlockState.generateStateMap(this));
    @Deprecated
    private final LazyReference<String> name = LazyReference.from(() -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBlockRegistry().getName(this));
    private final LazyReference<Integer> legacyId = LazyReference.from(() -> this.computeLegacy(0));
    private final LazyReference<Integer> legacyData = LazyReference.from(() -> this.computeLegacy(1));

    public BlockType(String id) {
        this(id, null);
    }

    public BlockType(String id, Function<BlockState, BlockState> values) {
        if (!((String)id).contains(":")) {
            id = "minecraft:" + (String)id;
        }
        this.id = id;
        this.values = values;
    }

    private BlockState computeDefaultState() {
        BlockState defaultState = (BlockState)Iterables.getFirst(this.getBlockStatesMap().values(), null);
        if (this.values != null) {
            defaultState = this.values.apply(defaultState);
        }
        return defaultState;
    }

    private Map<Map<Property<?>, Object>, BlockState> getBlockStatesMap() {
        return this.blockStatesMap.getValue();
    }

    @Override
    public String id() {
        return this.id;
    }

    public Component getRichName() {
        return WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBlockRegistry().getRichName(this);
    }

    @Deprecated
    public String getName() {
        String name = this.name.getValue();
        if (name == null || name.isEmpty()) {
            return this.id();
        }
        return name;
    }

    public Map<String, ? extends Property<?>> getPropertyMap() {
        return this.properties.getValue();
    }

    public List<? extends Property<?>> getProperties() {
        return ImmutableList.copyOf(this.getPropertyMap().values());
    }

    public <V> Property<V> getProperty(String name) {
        Property<?> property = this.getPropertyMap().get(name);
        Preconditions.checkArgument((property != null ? 1 : 0) != 0, (String)"%s has no property named %s", (Object)this, (Object)name);
        return property;
    }

    public BlockState getDefaultState() {
        return this.defaultState.getValue();
    }

    public FuzzyBlockState getFuzzyMatcher() {
        return this.emptyFuzzy.getValue();
    }

    public List<BlockState> getAllStates() {
        return ImmutableList.copyOf(this.getBlockStatesMap().values());
    }

    public BlockState getState(Map<Property<?>, Object> key) {
        BlockState state = this.getBlockStatesMap().get(key);
        Preconditions.checkArgument((state != null ? 1 : 0) != 0, (String)"%s has no state for %s", (Object)this, key);
        return state;
    }

    public boolean hasItemType() {
        return this.getItemType() != null;
    }

    @Nullable
    public ItemType getItemType() {
        return ItemTypes.get(this.id);
    }

    public BlockMaterial getMaterial() {
        return this.blockMaterial.getValue();
    }

    @Deprecated
    public int getLegacyId() {
        return this.legacyId.getValue();
    }

    @Deprecated
    public int getLegacyData() {
        return this.legacyData.getValue();
    }

    private int computeLegacy(int index) {
        int[] legacy = LegacyMapper.getInstance().getLegacyFromBlock(this.getDefaultState());
        return legacy != null ? legacy[index] : 0;
    }

    public String toString() {
        return this.id();
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof BlockType && this.id.equals(((BlockType)obj).id);
    }
}

