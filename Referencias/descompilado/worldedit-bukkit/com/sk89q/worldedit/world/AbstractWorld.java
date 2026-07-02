/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import java.nio.file.Path;
import java.util.PriorityQueue;
import javax.annotation.Nullable;

public abstract class AbstractWorld
implements World {
    private final PriorityQueue<QueuedEffect> effectQueue = new PriorityQueue();
    private int taskId = -1;

    @Override
    public boolean useItem(BlockVector3 position, BaseItem item, Direction face) {
        return false;
    }

    public final <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 pt, B block) throws WorldEditException {
        return this.setBlock(pt, block, SideEffectSet.defaults());
    }

    @Override
    public Path getStoragePath() {
        return null;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return 255;
    }

    @Override
    public Mask createLiquidMask() {
        return new BlockTypeMask((Extent)this, BlockTypes.LAVA, BlockTypes.WATER);
    }

    @Override
    public void dropItem(Vector3 pt, BaseItemStack item, int times) {
        for (int i = 0; i < times; ++i) {
            this.dropItem(pt, item);
        }
    }

    @Override
    public void checkLoadedChunk(BlockVector3 pt) {
    }

    @Override
    public void fixAfterFastMode(Iterable<BlockVector2> chunks) {
    }

    @Override
    public void sendBiomeUpdates(Iterable<BlockVector2> chunks) {
    }

    @Override
    public void fixLighting(Iterable<BlockVector2> chunks) {
    }

    @Override
    public boolean playEffect(Vector3 position, int type, int data) {
        return false;
    }

    @Override
    public boolean queueBlockBreakEffect(Platform server, BlockVector3 position, BlockType blockType, double priority) {
        if (this.taskId == -1) {
            this.taskId = server.schedule(0L, 1L, () -> {
                int max = Math.max(1, Math.min(30, this.effectQueue.size() / 3));
                for (int i = 0; i < max; ++i) {
                    if (this.effectQueue.isEmpty()) {
                        return;
                    }
                    this.effectQueue.poll().play();
                }
            });
        }
        if (this.taskId == -1) {
            return false;
        }
        this.effectQueue.offer(new QueuedEffect(position.toVector3(), blockType, priority));
        return true;
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return BlockVector3.at(-30000000, this.getMinY(), -30000000);
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return BlockVector3.at(30000000, this.getMaxY(), 30000000);
    }

    @Override
    @Nullable
    public Operation commit() {
        return null;
    }

    @Override
    public WeatherType getWeather() {
        return WeatherTypes.CLEAR;
    }

    @Override
    public long getRemainingWeatherDuration() {
        return 0L;
    }

    @Override
    public void setWeather(WeatherType weatherType) {
    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {
    }

    private class QueuedEffect
    implements Comparable<QueuedEffect> {
        private final Vector3 position;
        private final BlockType blockType;
        private final double priority;

        private QueuedEffect(Vector3 position, BlockType blockType, double priority) {
            this.position = position;
            this.blockType = blockType;
            this.priority = priority;
        }

        public void play() {
            AbstractWorld.this.playEffect(this.position, 2001, this.blockType.getLegacyId());
        }

        @Override
        public int compareTo(@Nullable QueuedEffect other) {
            return Double.compare(this.priority, other != null ? other.priority : 0.0);
        }
    }
}

