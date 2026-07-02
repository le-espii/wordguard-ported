/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 */
package com.sk89q.worldguard.bukkit.event.block;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.BulkEvent;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;

abstract class AbstractBlockEvent
extends DelegateEvent
implements BulkEvent {
    private final World world;
    private List<Block> blocks;
    private final List<BlockState> blockStates;
    private final Material effectiveMaterial;

    protected AbstractBlockEvent(@Nullable Event originalEvent, Cause cause, World world, List<Block> blocks, Material effectiveMaterial) {
        super(originalEvent, cause);
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull(blocks);
        Preconditions.checkNotNull((Object)effectiveMaterial);
        this.world = world;
        this.blocks = blocks;
        this.effectiveMaterial = effectiveMaterial;
        this.blockStates = null;
    }

    protected AbstractBlockEvent(@Nullable Event originalEvent, Cause cause, World world, List<BlockState> blocks) {
        super(originalEvent, cause);
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull(blocks);
        Preconditions.checkArgument((!blocks.isEmpty() ? 1 : 0) != 0);
        this.world = world;
        this.blockStates = blocks;
        this.blocks = null;
        this.effectiveMaterial = blocks.get(0).getType();
    }

    protected AbstractBlockEvent(@Nullable Event originalEvent, Cause cause, Block block) {
        this(originalEvent, cause, block.getWorld(), AbstractBlockEvent.createList((Block)Preconditions.checkNotNull((Object)block)), block.getType());
    }

    protected AbstractBlockEvent(@Nullable Event originalEvent, Cause cause, Location target, Material effectiveMaterial) {
        this(originalEvent, cause, target.getWorld(), AbstractBlockEvent.createList(target.getBlock()), effectiveMaterial);
    }

    private static List<Block> createList(Block block) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        return blocks;
    }

    public World getWorld() {
        return this.world;
    }

    public List<Block> getBlocks() {
        if (this.blocks == null) {
            this.blocks = this.blockStates.stream().map(BlockState::getBlock).collect(Collectors.toList());
        }
        return this.blocks;
    }

    public boolean filter(Predicate<Location> predicate, boolean cancelEventOnFalse) {
        return this.blocks == null ? this.filterInternal(this.blockStates, BlockState::getLocation, predicate, cancelEventOnFalse) : this.filterInternal(this.blocks, Block::getLocation, predicate, cancelEventOnFalse);
    }

    private <B> boolean filterInternal(List<B> blockList, Function<B, Location> locFunc, Predicate<Location> predicate, boolean cancelEventOnFalse) {
        boolean hasRemoval = false;
        Iterator<B> it = blockList.iterator();
        while (it.hasNext()) {
            if (predicate.test(locFunc.apply(it.next()))) continue;
            hasRemoval = true;
            if (cancelEventOnFalse) {
                blockList.clear();
                this.setCancelled(true);
                break;
            }
            it.remove();
        }
        return hasRemoval;
    }

    public boolean filter(Predicate<Location> predicate) {
        return this.filter(predicate, false);
    }

    public Material getEffectiveMaterial() {
        return this.effectiveMaterial;
    }

    @Override
    public Event.Result getResult() {
        if (this.blocks == null ? this.blockStates.isEmpty() : this.blocks.isEmpty()) {
            return Event.Result.DENY;
        }
        return super.getResult();
    }

    @Override
    public Event.Result getExplicitResult() {
        return super.getResult();
    }
}

