/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;
import com.sk89q.worldedit.world.generation.StructureType;
import com.sk89q.worldedit.world.weather.WeatherType;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.Nullable;

public interface World
extends Extent,
Keyed {
    public String getName();

    @Nullable
    public Path getStoragePath();

    public int getMinY();

    public int getMaxY();

    public Mask createLiquidMask();

    public boolean useItem(BlockVector3 var1, BaseItem var2, Direction var3);

    @Deprecated
    default public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight) throws WorldEditException {
        return this.setBlock(position, block, notifyAndLight ? SideEffectSet.defaults() : SideEffectSet.none());
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 var1, B var2, SideEffectSet var3) throws WorldEditException;

    @Deprecated
    default public boolean notifyAndLightBlock(BlockVector3 position, BlockState previousType) throws WorldEditException {
        return !this.applySideEffects(position, previousType, SideEffectSet.defaults()).isEmpty();
    }

    public Set<SideEffect> applySideEffects(BlockVector3 var1, BlockState var2, SideEffectSet var3) throws WorldEditException;

    public int getBlockLightLevel(BlockVector3 var1);

    public boolean clearContainerBlockContents(BlockVector3 var1);

    public void dropItem(Vector3 var1, BaseItemStack var2, int var3);

    public void dropItem(Vector3 var1, BaseItemStack var2);

    public void simulateBlockMine(BlockVector3 var1);

    default public boolean canPlaceAt(BlockVector3 position, BlockState blockState) {
        return true;
    }

    default public boolean regenerate(Region region, EditSession editSession) {
        return this.regenerate(region, editSession, RegenOptions.builder().build());
    }

    default public boolean regenerate(Region region, Extent extent) {
        return this.regenerate(region, extent, RegenOptions.builder().build());
    }

    @NonAbstractForCompatibility(delegateName="regenerate", delegateParams={Region.class, EditSession.class})
    default public boolean regenerate(Region region, Extent extent, RegenOptions options) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        if (extent instanceof EditSession) {
            return this.regenerate(region, (EditSession)extent);
        }
        throw new UnsupportedOperationException("This World class (" + this.getClass().getName() + ") does not implement the general Extent variant of this method");
    }

    public boolean generateTree(TreeGenerator.TreeType var1, EditSession var2, BlockVector3 var3) throws MaxChangedBlocksException;

    default public boolean generateStructure(StructureType type, EditSession editSession, BlockVector3 position) {
        return false;
    }

    default public boolean generateFeature(ConfiguredFeatureType type, EditSession editSession, BlockVector3 position) {
        return false;
    }

    public void checkLoadedChunk(BlockVector3 var1);

    public void fixAfterFastMode(Iterable<BlockVector2> var1);

    public void sendBiomeUpdates(Iterable<BlockVector2> var1);

    public void fixLighting(Iterable<BlockVector2> var1);

    public boolean playEffect(Vector3 var1, int var2, int var3);

    public boolean queueBlockBreakEffect(Platform var1, BlockVector3 var2, BlockType var3, double var4);

    public WeatherType getWeather();

    public long getRemainingWeatherDuration();

    public void setWeather(WeatherType var1);

    public void setWeather(WeatherType var1, long var2);

    public BlockVector3 getSpawnPosition();

    public boolean equals(Object var1);

    public int hashCode();
}

