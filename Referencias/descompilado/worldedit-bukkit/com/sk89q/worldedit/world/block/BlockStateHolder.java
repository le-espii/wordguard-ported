/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.block;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface BlockStateHolder<B extends BlockStateHolder<B>>
extends Pattern {
    public BlockType getBlockType();

    public <V> B with(Property<V> var1, V var2);

    public <V> V getState(Property<V> var1);

    public Map<Property<?>, Object> getStates();

    public boolean equalsFuzzy(BlockStateHolder<?> var1);

    public BlockState toImmutableState();

    public BaseBlock toBaseBlock();

    @Deprecated
    default public BaseBlock toBaseBlock(CompoundTag compoundTag) {
        return this.toBaseBlock(compoundTag == null ? null : LazyReference.from(compoundTag::toLinTag));
    }

    @NonAbstractForCompatibility(delegateName="toBaseBlock", delegateParams={CompoundTag.class})
    default public BaseBlock toBaseBlock(LazyReference<LinCompoundTag> compoundTag) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.toBaseBlock(compoundTag == null ? null : new CompoundTag(compoundTag.getValue()));
    }

    default public BaseBlock toBaseBlock(LinCompoundTag compoundTag) {
        return this.toBaseBlock(compoundTag == null ? null : LazyReference.computed(compoundTag));
    }

    @Override
    default public BaseBlock applyBlock(BlockVector3 position) {
        return this.toBaseBlock();
    }

    default public String getAsString() {
        if (this.getStates().isEmpty()) {
            return this.getBlockType().id();
        }
        String properties = this.getStates().entrySet().stream().map(entry -> ((Property)entry.getKey()).getName() + "=" + entry.getValue().toString().toLowerCase(Locale.ROOT)).collect(Collectors.joining(","));
        return this.getBlockType().id() + "[" + properties + "]";
    }
}

