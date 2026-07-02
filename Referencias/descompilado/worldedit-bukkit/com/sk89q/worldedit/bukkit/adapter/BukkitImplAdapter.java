/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldedit.bukkit.adapter;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.item.ItemType;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface BukkitImplAdapter {
    @Nullable
    public DataFixer getDataFixer();

    default public boolean supportsWatchdog() {
        return false;
    }

    default public void tickWatchdog() {
    }

    public BlockState getBlock(Location var1);

    public BaseBlock getFullBlock(Location var1);

    public WorldNativeAccess<?, ?, ?> createWorldNativeAccess(World var1);

    @Nullable
    public BaseEntity getEntity(Entity var1);

    @Nullable
    public Entity createEntity(Location var1, BaseEntity var2);

    public Component getRichBlockName(BlockType var1);

    public Component getRichItemName(ItemType var1);

    public Component getRichItemName(BaseItemStack var1);

    public Map<String, ? extends Property<?>> getProperties(BlockType var1);

    public void sendFakeNBT(Player var1, BlockVector3 var2, LinCompoundTag var3);

    public void sendFakeOP(Player var1);

    default public boolean simulateItemUse(World world, BlockVector3 position, BaseItem item, Direction face) {
        return false;
    }

    public boolean canPlaceAt(World var1, BlockVector3 var2, BlockState var3);

    public ItemStack adapt(BaseItemStack var1);

    public BaseItemStack adapt(ItemStack var1);

    public Set<SideEffect> getSupportedSideEffects();

    default public OptionalInt getInternalBlockStateId(BlockData data) {
        return OptionalInt.empty();
    }

    default public OptionalInt getInternalBlockStateId(BlockState state) {
        return OptionalInt.empty();
    }

    default public boolean regenerate(World world, Region region, Extent extent, RegenOptions options) {
        throw new UnsupportedOperationException("This adapter does not support regeneration.");
    }

    default public boolean clearContainerBlockContents(World world, BlockVector3 pt) {
        throw new UnsupportedOperationException("This adapter does not support clearing block contents.");
    }

    default public void setBiome(Location location, BiomeType biome) {
        throw new UnsupportedOperationException("This adapter does not support custom biomes.");
    }

    default public BiomeType getBiome(Location location) {
        throw new UnsupportedOperationException("This adapter does not support custom biomes.");
    }

    default public boolean generateFeature(ConfiguredFeatureType feature, World world, EditSession session, BlockVector3 pt) {
        throw new UnsupportedOperationException("This adapter does not support generating features.");
    }

    default public boolean generateStructure(StructureType feature, World world, EditSession session, BlockVector3 pt) {
        throw new UnsupportedOperationException("This adapter does not support generating features.");
    }

    default public void initializeRegistries() {
    }

    default public void sendBiomeUpdates(World world, Iterable<BlockVector2> chunks) {
    }
}

