/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.class_2248
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  net.minecraft.class_2689
 *  net.minecraft.class_2746
 *  net.minecraft.class_2753
 *  net.minecraft.class_2754
 *  net.minecraft.class_2758
 *  net.minecraft.class_2769
 *  net.minecraft.class_3542
 */
package com.sk89q.worldedit.fabric.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.internal.PropertyAdapter;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.DirectionalProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2746;
import net.minecraft.class_2753;
import net.minecraft.class_2754;
import net.minecraft.class_2758;
import net.minecraft.class_2769;
import net.minecraft.class_3542;

public class FabricTransmogrifier {
    private static final LoadingCache<class_2769<?>, Property<?>> PROPERTY_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<class_2769<?>, Property<?>>(){

        public Property<?> load(class_2769<?> property) throws Exception {
            if (property instanceof class_2746) {
                return new BooleanProperty(property.method_11899(), (List<Boolean>)ImmutableList.copyOf((Collection)((class_2746)property).method_11898()));
            }
            if (property instanceof class_2758) {
                return new IntegerProperty(property.method_11899(), (List<Integer>)ImmutableList.copyOf((Collection)((class_2758)property).method_11898()));
            }
            if (property instanceof class_2753) {
                return new DirectionalProperty(property.method_11899(), ((class_2753)property).method_11898().stream().map(FabricAdapter::adaptEnumFacing).collect(Collectors.toList()));
            }
            if (property instanceof class_2754) {
                return new EnumProperty(property.method_11899(), ((class_2754)property).method_11898().stream().map(x -> ((class_3542)x).method_15434()).collect(Collectors.toList()));
            }
            return new PropertyAdapter(property);
        }
    });

    public static Property<?> transmogToWorldEditProperty(class_2769<?> property) {
        return (Property)PROPERTY_CACHE.getUnchecked(property);
    }

    private static Map<Property<?>, Object> transmogToWorldEditProperties(BlockType block, Map<class_2769<?>, Comparable<?>> mcProps) {
        TreeMap props = new TreeMap(Comparator.comparing(Property::getName));
        for (Map.Entry<class_2769<?>, Comparable<?>> prop : mcProps.entrySet()) {
            Object value = prop.getValue();
            if (prop.getKey() instanceof class_2753) {
                value = FabricAdapter.adaptEnumFacing((class_2350)value);
            } else if (prop.getKey() instanceof class_2754) {
                value = ((class_3542)value).method_15434();
            }
            props.put(block.getProperty(prop.getKey().method_11899()), value);
        }
        return props;
    }

    private static class_2680 transmogToMinecraftProperties(class_2689<class_2248, class_2680> stateContainer, class_2680 newState, Map<Property<?>, Object> states) {
        for (Map.Entry<Property<?>, Object> state : states.entrySet()) {
            class_2769 property = stateContainer.method_11663(state.getKey().getName());
            Comparable value = (Comparable)state.getValue();
            if (property instanceof class_2753) {
                Direction dir = (Direction)((Object)value);
                value = FabricAdapter.adapt(dir);
            } else if (property instanceof class_2754) {
                String enumName = (String)((Object)value);
                value = (Comparable)((Object)((class_2754)property).method_11900((String)((Object)value)).orElseGet(() -> {
                    throw new IllegalStateException("Enum property " + property.method_11899() + " does not contain " + enumName);
                }));
            }
            newState = (class_2680)newState.method_11657(property, value);
        }
        return newState;
    }

    public static class_2680 transmogToMinecraft(BlockState blockState) {
        class_2248 mcBlock = FabricAdapter.adapt(blockState.getBlockType());
        class_2680 newState = mcBlock.method_9564();
        Map<Property<?>, Object> states = blockState.getStates();
        return FabricTransmogrifier.transmogToMinecraftProperties((class_2689<class_2248, class_2680>)mcBlock.method_9595(), newState, states);
    }

    public static BlockState transmogToWorldEdit(class_2680 blockState) {
        BlockType blockType = FabricAdapter.adapt(blockState.method_26204());
        return blockType.getState(FabricTransmogrifier.transmogToWorldEditProperties(blockType, blockState.method_11656()));
    }

    private FabricTransmogrifier() {
    }
}

