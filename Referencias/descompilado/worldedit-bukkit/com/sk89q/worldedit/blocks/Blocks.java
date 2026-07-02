/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.sk89q.worldedit.blocks;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Blocks {
    private Blocks() {
    }

    public static <B extends BlockStateHolder<B>> boolean containsFuzzy(Collection<? extends BlockStateHolder<?>> collection, B o) {
        for (BlockStateHolder<?> b : collection) {
            if (!b.equalsFuzzy(o)) continue;
            return true;
        }
        return false;
    }

    public static Map<Property<Object>, Object> resolveProperties(Map<String, String> states, BlockType type) {
        Map<String, ? extends Property<?>> existing = type.getPropertyMap();
        HashMap newMap = Maps.newHashMap();
        states.forEach((key, value) -> {
            Property prop = (Property)existing.get(key);
            if (prop == null) {
                return;
            }
            Object val = null;
            try {
                val = prop.getValueFor((String)value);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            if (val == null) {
                return;
            }
            newMap.put(prop, val);
        });
        return newMap;
    }
}

