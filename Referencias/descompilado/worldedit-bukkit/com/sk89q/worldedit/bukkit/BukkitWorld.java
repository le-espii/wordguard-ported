/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Effect
 *  org.bukkit.TreeType
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitEntity;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.adapter.UnsupportedVersionEditException;
import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.AbstractWorld;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import org.bukkit.Effect;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BukkitWorld
extends AbstractWorld {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final Map<Integer, Effect> effects = new HashMap<Integer, Effect>();
    private final WeakReference<org.bukkit.World> worldRef;
    private final WorldNativeAccess<?, ?, ?> worldNativeAccess;
    private static final EnumMap<TreeGenerator.TreeType, TreeType> treeTypeMapping;
    private static volatile boolean hasWarnedImplError;

    public BukkitWorld(org.bukkit.World world) {
        this.worldRef = new WeakReference<org.bukkit.World>(world);
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        this.worldNativeAccess = adapter != null ? adapter.createWorldNativeAccess(world) : null;
    }

    public List<Entity> getEntities(Region region) {
        org.bukkit.World world = this.getWorld();
        List ents = world.getEntities();
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (org.bukkit.entity.Entity ent : ents) {
            if (!region.contains(BukkitAdapter.asBlockVector(ent.getLocation()))) continue;
            entities.add(BukkitAdapter.adapt(ent));
        }
        return entities;
    }

    public List<Entity> getEntities() {
        ArrayList<Entity> list = new ArrayList<Entity>();
        for (org.bukkit.entity.Entity entity : this.getWorld().getEntities()) {
            list.add(BukkitAdapter.adapt(entity));
        }
        return list;
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            try {
                org.bukkit.entity.Entity createdEntity = adapter.createEntity(BukkitAdapter.adapt(this.getWorld(), location), entity);
                if (createdEntity != null) {
                    return new BukkitEntity(createdEntity);
                }
                return null;
            }
            catch (Exception e) {
                LOGGER.warn("Corrupt entity found when creating: " + entity.getType().id(), (Throwable)e);
                if (entity.getNbt() != null) {
                    LOGGER.warn(entity.getNbt().toString());
                }
                return null;
            }
        }
        return null;
    }

    public org.bukkit.World getWorld() {
        return (org.bukkit.World)Preconditions.checkNotNull((Object)((org.bukkit.World)this.worldRef.get()), (Object)"The world was unloaded and the reference is unavailable");
    }

    @Override
    public String getName() {
        return this.getWorld().getName();
    }

    @Override
    public String id() {
        return this.getWorld().getName().replace(" ", "_").toLowerCase(Locale.ROOT);
    }

    @Override
    public Path getStoragePath() {
        Path worldFolder = this.getWorld().getWorldFolder().toPath();
        return switch (this.getWorld().getEnvironment()) {
            case World.Environment.NETHER -> worldFolder.resolve("DIM-1");
            case World.Environment.THE_END -> worldFolder.resolve("DIM1");
            default -> worldFolder;
        };
    }

    @Override
    public int getBlockLightLevel(BlockVector3 pt) {
        return this.getWorld().getBlockAt(pt.x(), pt.y(), pt.z()).getLightLevel();
    }

    @Override
    public boolean regenerate(Region region, Extent extent, RegenOptions options) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        try {
            if (adapter != null) {
                return adapter.regenerate(this.getWorld(), region, extent, options);
            }
            throw new UnsupportedOperationException("Missing BukkitImplAdapter for this version.");
        }
        catch (Exception e) {
            LOGGER.warn("Regeneration via adapter failed.", (Throwable)e);
            return false;
        }
    }

    @Override
    public boolean clearContainerBlockContents(BlockVector3 pt) {
        Preconditions.checkNotNull((Object)((Object)pt));
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            try {
                return adapter.clearContainerBlockContents(this.getWorld(), pt);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (!this.getBlock(pt).getBlockType().getMaterial().hasContainer()) {
            return false;
        }
        Block block = this.getWorld().getBlockAt(pt.x(), pt.y(), pt.z());
        BlockState state = PaperLib.getBlockState(block, false).getState();
        if (!(state instanceof InventoryHolder)) {
            return false;
        }
        InventoryHolder inventoryHolder = (InventoryHolder)state;
        Inventory inven = inventoryHolder.getInventory();
        if (inventoryHolder instanceof Chest) {
            inven = ((Chest)inventoryHolder).getBlockInventory();
        }
        inven.clear();
        return true;
    }

    public static TreeType toBukkitTreeType(TreeGenerator.TreeType type) {
        return treeTypeMapping.get((Object)type);
    }

    @Override
    public boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, BlockVector3 pt) {
        org.bukkit.World world = this.getWorld();
        TreeType bukkitType = BukkitWorld.toBukkitTreeType(type);
        if (bukkitType == TreeType.CHORUS_PLANT) {
            pt = pt.add(0, 1, 0);
        }
        return type != null && world.generateTree(BukkitAdapter.adapt(world, pt), (Random)ThreadLocalRandom.current(), bukkitType, block -> {
            Mask mask = editSession.getMask();
            BlockVector3 blockVector = BukkitAdapter.asBlockVector(block.getLocation());
            if (mask != null && !mask.test(blockVector)) {
                return false;
            }
            try {
                editSession.setBlock(blockVector, BukkitAdapter.adapt(block.getBlockData()));
            }
            catch (MaxChangedBlocksException maxChangedBlocksException) {
                // empty catch block
            }
            return false;
        });
    }

    @Override
    public void dropItem(Vector3 pt, BaseItemStack item) {
        org.bukkit.World world = this.getWorld();
        world.dropItemNaturally(BukkitAdapter.adapt(world, pt), BukkitAdapter.adapt(item));
    }

    @Override
    public void checkLoadedChunk(BlockVector3 pt) {
        org.bukkit.World world = this.getWorld();
        world.getChunkAt(pt.x() >> 4, pt.z() >> 4);
    }

    @Override
    public boolean equals(Object other) {
        org.bukkit.World ref = (org.bukkit.World)this.worldRef.get();
        if (ref == null) {
            return false;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof BukkitWorld) {
            org.bukkit.World otherWorld = (org.bukkit.World)((BukkitWorld)other).worldRef.get();
            return ref.equals((Object)otherWorld);
        }
        if (other instanceof World) {
            return ((World)other).getName().equals(ref.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getWorld().hashCode();
    }

    @Override
    public int getMaxY() {
        return this.getWorld().getMaxHeight() - 1;
    }

    @Override
    public int getMinY() {
        return this.getWorld().getMinHeight();
    }

    @Override
    public void fixAfterFastMode(Iterable<BlockVector2> chunks) {
        org.bukkit.World world = this.getWorld();
        for (BlockVector2 chunkPos : chunks) {
            world.refreshChunk(chunkPos.x(), chunkPos.z());
        }
    }

    @Override
    public void sendBiomeUpdates(Iterable<BlockVector2> chunks) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            adapter.sendBiomeUpdates(this.getWorld(), chunks);
        }
    }

    @Override
    public boolean playEffect(Vector3 position, int type, int data) {
        org.bukkit.World world = this.getWorld();
        Effect effect = effects.get(type);
        if (effect == null) {
            return false;
        }
        world.playEffect(BukkitAdapter.adapt(world, position), effect, data);
        return true;
    }

    @Override
    public WeatherType getWeather() {
        if (this.getWorld().isThundering()) {
            return WeatherTypes.THUNDER_STORM;
        }
        if (this.getWorld().hasStorm()) {
            return WeatherTypes.RAIN;
        }
        return WeatherTypes.CLEAR;
    }

    @Override
    public long getRemainingWeatherDuration() {
        return this.getWorld().getWeatherDuration();
    }

    @Override
    public void setWeather(WeatherType weatherType) {
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            this.getWorld().setThundering(true);
        } else if (weatherType == WeatherTypes.RAIN) {
            this.getWorld().setStorm(true);
        } else {
            this.getWorld().setStorm(false);
            this.getWorld().setThundering(false);
        }
    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            this.getWorld().setThundering(true);
            this.getWorld().setThunderDuration((int)duration);
            this.getWorld().setWeatherDuration((int)duration);
        } else if (weatherType == WeatherTypes.RAIN) {
            this.getWorld().setStorm(true);
            this.getWorld().setWeatherDuration((int)duration);
        } else {
            this.getWorld().setStorm(false);
            this.getWorld().setThundering(false);
            this.getWorld().setWeatherDuration((int)duration);
        }
    }

    @Override
    public BlockVector3 getSpawnPosition() {
        return BukkitAdapter.asBlockVector(this.getWorld().getSpawnLocation());
    }

    @Override
    public void simulateBlockMine(BlockVector3 pt) {
        this.getWorld().getBlockAt(pt.x(), pt.y(), pt.z()).breakNaturally();
    }

    @Override
    public boolean canPlaceAt(BlockVector3 position, com.sk89q.worldedit.world.block.BlockState blockState) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.canPlaceAt(this.getWorld(), position, blockState);
        }
        return true;
    }

    @Override
    public boolean generateFeature(ConfiguredFeatureType type, EditSession editSession, BlockVector3 position) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.generateFeature(type, this.getWorld(), editSession, position);
        }
        return false;
    }

    @Override
    public boolean generateStructure(StructureType type, EditSession editSession, BlockVector3 position) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.generateStructure(type, this.getWorld(), editSession, position);
        }
        return false;
    }

    @Override
    public com.sk89q.worldedit.world.block.BlockState getBlock(BlockVector3 position) {
        block4: {
            BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
            if (adapter != null) {
                try {
                    return adapter.getBlock(BukkitAdapter.adapt(this.getWorld(), position));
                }
                catch (Exception e) {
                    if (hasWarnedImplError) break block4;
                    hasWarnedImplError = true;
                    LOGGER.warn("Unable to retrieve block via impl adapter", (Throwable)e);
                }
            }
        }
        if (WorldEditPlugin.getInstance().getLocalConfiguration().unsupportedVersionEditing) {
            Block bukkitBlock = this.getWorld().getBlockAt(position.x(), position.y(), position.z());
            return BukkitAdapter.adapt(bukkitBlock.getBlockData());
        }
        throw new RuntimeException(new UnsupportedVersionEditException());
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, SideEffectSet sideEffects) {
        this.clearContainerBlockContents(position);
        if (this.worldNativeAccess != null) {
            try {
                return this.worldNativeAccess.setBlock(position, block, sideEffects);
            }
            catch (Exception e) {
                BaseBlock baseBlock;
                if (block instanceof BaseBlock && (baseBlock = (BaseBlock)block).getNbt() != null) {
                    LOGGER.warn("Tried to set a corrupt tile entity at " + position.toString() + ": " + String.valueOf(baseBlock.getNbt()), (Throwable)e);
                }
                LOGGER.warn("Failed to set block via adapter, falling back to generic", (Throwable)e);
            }
        }
        if (WorldEditPlugin.getInstance().getLocalConfiguration().unsupportedVersionEditing) {
            Block bukkitBlock = this.getWorld().getBlockAt(position.x(), position.y(), position.z());
            bukkitBlock.setBlockData(BukkitAdapter.adapt(block), sideEffects.doesApplyAny());
            return true;
        }
        throw new RuntimeException(new UnsupportedVersionEditException());
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.getFullBlock(BukkitAdapter.adapt(this.getWorld(), position));
        }
        return this.getBlock(position).toBaseBlock();
    }

    @Override
    public Set<SideEffect> applySideEffects(BlockVector3 position, com.sk89q.worldedit.world.block.BlockState previousType, SideEffectSet sideEffectSet) {
        if (this.worldNativeAccess != null) {
            this.worldNativeAccess.applySideEffects(position, previousType, sideEffectSet);
            return Sets.intersection(WorldEditPlugin.getInstance().getInternalPlatform().getSupportedSideEffects(), sideEffectSet.getSideEffectsToApply());
        }
        return ImmutableSet.of();
    }

    @Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.simulateItemUse(this.getWorld(), position, item, face);
        }
        return false;
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.getBiome(BukkitAdapter.adapt(this.getWorld(), position));
        }
        return BukkitAdapter.adapt(this.getWorld().getBiome(position.x(), position.y(), position.z()));
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            adapter.setBiome(BukkitAdapter.adapt(this.getWorld(), position), biome);
        } else {
            this.getWorld().setBiome(position.x(), position.y(), position.z(), BukkitAdapter.adapt(biome));
        }
        return true;
    }

    static {
        for (Effect effect : Effect.values()) {
            int id = effect.getId();
            effects.put(id, effect);
        }
        treeTypeMapping = new EnumMap(TreeGenerator.TreeType.class);
        for (TreeGenerator.TreeType treeType : TreeGenerator.TreeType.values()) {
            try {
                TreeType bukkitType = TreeType.valueOf((String)treeType.name());
                treeTypeMapping.put(treeType, bukkitType);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        treeTypeMapping.put(TreeGenerator.TreeType.SHORT_JUNGLE, TreeType.SMALL_JUNGLE);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM, TreeType.BROWN_MUSHROOM);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_REDWOOD, TreeType.REDWOOD);
        treeTypeMapping.put(TreeGenerator.TreeType.PINE, TreeType.REDWOOD);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_BIRCH, TreeType.BIRCH);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_JUNGLE, TreeType.JUNGLE);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_MUSHROOM, TreeType.BROWN_MUSHROOM);
        for (TreeGenerator.TreeType treeType : TreeGenerator.TreeType.values()) {
            if (treeTypeMapping.get((Object)treeType) != null) continue;
            WorldEdit.logger.error("No TreeType mapping for TreeGenerator.TreeType." + String.valueOf((Object)treeType));
        }
        hasWarnedImplError = false;
    }
}

