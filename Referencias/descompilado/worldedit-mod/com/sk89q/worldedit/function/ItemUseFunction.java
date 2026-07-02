/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;

public final class ItemUseFunction
implements RegionFunction {
    private final World world;
    private final BaseItem item;
    private final Direction dir;

    public ItemUseFunction(World world, BaseItem item) {
        this(world, item, Direction.UP);
    }

    public ItemUseFunction(World world, BaseItem item, Direction dir) {
        this.world = world;
        this.item = item;
        this.dir = dir;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        return this.world.useItem(position, this.item, this.dir);
    }
}

