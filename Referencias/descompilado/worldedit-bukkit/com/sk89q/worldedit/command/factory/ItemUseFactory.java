/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.ItemUseFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;

public final class ItemUseFactory
implements Contextual<RegionFunction> {
    private final BaseItem item;
    private final Direction dir;

    public ItemUseFactory(BaseItem item) {
        this(item, Direction.UP);
    }

    public ItemUseFactory(BaseItem item, Direction dir) {
        this.item = item;
        this.dir = dir;
    }

    @Override
    public RegionFunction createFromContext(EditContext input) {
        World world = ((EditSession)input.getDestination()).getWorld();
        return new ItemUseFunction(world, this.item, this.dir);
    }

    public String toString() {
        return "application of the item " + String.valueOf(this.item.getType());
    }
}

