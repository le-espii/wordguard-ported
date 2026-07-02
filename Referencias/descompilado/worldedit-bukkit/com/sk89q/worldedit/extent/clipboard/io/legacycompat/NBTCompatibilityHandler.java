/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.HashMap;
import java.util.Map;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTag;

public interface NBTCompatibilityHandler {
    @Deprecated
    default public <B extends BlockStateHolder<B>> boolean isAffectedBlock(B block) {
        BaseBlock updated;
        BaseBlock state = block.toBaseBlock();
        return state != (updated = this.updateNbt(state));
    }

    @Deprecated
    default public <B extends BlockStateHolder<B>> BlockStateHolder<?> updateNBT(B block, Map<String, Tag<?, ?>> values) {
        BaseBlock changed = this.updateNbt(block.toBaseBlock(LazyReference.from(() -> {
            LinCompoundTag.Builder builder = LinCompoundTag.builder();
            for (Map.Entry entry : values.entrySet()) {
                builder.put((String)entry.getKey(), (LinTag<?>)((Tag)entry.getValue()).toLinTag());
            }
            return builder.build();
        })));
        CompoundTag data = changed.getNbtData();
        values.clear();
        if (data != null) {
            values.putAll((Map<String, Tag<?, ?>>)data.getValue());
        }
        return changed;
    }

    @NonAbstractForCompatibility(delegateName="updateNBT", delegateParams={BlockStateHolder.class, Map.class})
    default public BaseBlock updateNbt(BaseBlock block) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        if (!this.isAffectedBlock(block)) {
            return block;
        }
        if (block.getNbt() == null) {
            return block;
        }
        HashMap values = new HashMap((Map<String, Tag<?, ?>>)new CompoundTag(block.getNbt()).getValue());
        BlockStateHolder<?> changedBlock = this.updateNBT(block, values);
        return changedBlock.toBaseBlock(LazyReference.from(() -> {
            LinCompoundTag.Builder builder = LinCompoundTag.builder();
            for (Map.Entry entry : values.entrySet()) {
                Object linTag = ((Tag)entry.getValue()).toLinTag();
                builder.put((String)entry.getKey(), (LinTag<?>)linTag);
            }
            return builder.build();
        }));
    }
}

