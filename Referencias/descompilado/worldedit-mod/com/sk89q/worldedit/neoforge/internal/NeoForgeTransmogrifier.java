/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.sk89q.worldedit.neoforge.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.internal.IPropertyAdapter;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.DirectionalProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class NeoForgeTransmogrifier {
    private static final LoadingCache<Property<?>, com.sk89q.worldedit.registry.state.Property<?>> PROPERTY_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Property<?>, com.sk89q.worldedit.registry.state.Property<?>>(){

        public com.sk89q.worldedit.registry.state.Property<?> load(Property<?> property) throws Exception {
            if (property instanceof net.minecraft.world.level.block.state.properties.BooleanProperty) {
                return new BooleanProperty(property.getName(), (List<Boolean>)ImmutableList.copyOf((Collection)((net.minecraft.world.level.block.state.properties.BooleanProperty)property).getPossibleValues()));
            }
            if (property instanceof IntegerProperty) {
                return new com.sk89q.worldedit.registry.state.IntegerProperty(property.getName(), (List<Integer>)ImmutableList.copyOf((Collection)((IntegerProperty)property).getPossibleValues()));
            }
            if (property instanceof DirectionProperty) {
                return new DirectionalProperty(property.getName(), ((DirectionProperty)property).getPossibleValues().stream().map(NeoForgeAdapter::adaptEnumFacing).collect(Collectors.toList()));
            }
            if (property instanceof net.minecraft.world.level.block.state.properties.EnumProperty) {
                return new EnumProperty(property.getName(), ((net.minecraft.world.level.block.state.properties.EnumProperty)property).getPossibleValues().stream().map(x -> ((StringRepresentable)x).getSerializedName()).collect(Collectors.toList()));
            }
            return new IPropertyAdapter(property);
        }
    });

    public static com.sk89q.worldedit.registry.state.Property<?> transmogToWorldEditProperty(Property<?> property) {
        return (com.sk89q.worldedit.registry.state.Property)PROPERTY_CACHE.getUnchecked(property);
    }

    public static Map<com.sk89q.worldedit.registry.state.Property<?>, Object> transmogToWorldEditProperties(BlockType block, Map<Property<?>, Comparable<?>> mcProps) {
        TreeMap props = new TreeMap(Comparator.comparing(com.sk89q.worldedit.registry.state.Property::getName));
        for (Map.Entry<Property<?>, Comparable<?>> prop : mcProps.entrySet()) {
            Object value = prop.getValue();
            if (prop.getKey() instanceof DirectionProperty) {
                value = NeoForgeAdapter.adaptEnumFacing((Direction)value);
            } else if (prop.getKey() instanceof net.minecraft.world.level.block.state.properties.EnumProperty) {
                value = ((StringRepresentable)value).getSerializedName();
            }
            props.put(block.getProperty(prop.getKey().getName()), value);
        }
        return props;
    }

    private static net.minecraft.world.level.block.state.BlockState transmogToMinecraftProperties(StateDefinition<Block, net.minecraft.world.level.block.state.BlockState> stateContainer, net.minecraft.world.level.block.state.BlockState newState, Map<com.sk89q.worldedit.registry.state.Property<?>, Object> states) {
        for (Map.Entry<com.sk89q.worldedit.registry.state.Property<?>, Object> state : states.entrySet()) {
            Property property = stateContainer.getProperty(state.getKey().getName());
            Comparable value = (Comparable)state.getValue();
            if (property instanceof DirectionProperty) {
                com.sk89q.worldedit.util.Direction dir = (com.sk89q.worldedit.util.Direction)((Object)value);
                value = NeoForgeAdapter.adapt(dir);
            } else if (property instanceof net.minecraft.world.level.block.state.properties.EnumProperty) {
                String enumName = (String)((Object)value);
                value = (Comparable)((net.minecraft.world.level.block.state.properties.EnumProperty)property).getValue((String)((Object)value)).orElseThrow(() -> new IllegalStateException("Enum property " + property.getName() + " does not contain " + enumName));
            }
            newState = (net.minecraft.world.level.block.state.BlockState)newState.setValue(property, value);
        }
        return newState;
    }

    public static net.minecraft.world.level.block.state.BlockState transmogToMinecraft(BlockState blockState) {
        Block mcBlock = NeoForgeAdapter.adapt(blockState.getBlockType());
        net.minecraft.world.level.block.state.BlockState newState = mcBlock.defaultBlockState();
        Map<com.sk89q.worldedit.registry.state.Property<?>, Object> states = blockState.getStates();
        return NeoForgeTransmogrifier.transmogToMinecraftProperties((StateDefinition<Block, net.minecraft.world.level.block.state.BlockState>)mcBlock.getStateDefinition(), newState, states);
    }

    public static BlockState transmogToWorldEdit(net.minecraft.world.level.block.state.BlockState blockState) {
        BlockType blockType = NeoForgeAdapter.adapt(blockState.getBlock());
        return blockType.getState(NeoForgeTransmogrifier.transmogToWorldEditProperties(blockType, blockState.getValues()));
    }

    private NeoForgeTransmogrifier() {
    }
}

