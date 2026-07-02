/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.function.visitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public abstract class BreadthFirstSearch
implements Operation {
    private final RegionFunction function;
    private final Queue<BlockVector3> queue = new ArrayDeque<BlockVector3>();
    private final Set<BlockVector3> visited = new HashSet<BlockVector3>();
    private final List<BlockVector3> directions = new ArrayList<BlockVector3>();
    private int affected = 0;

    protected BreadthFirstSearch(RegionFunction function) {
        Preconditions.checkNotNull((Object)function);
        this.function = function;
        this.addAxes();
    }

    protected Collection<BlockVector3> getDirections() {
        return this.directions;
    }

    protected void addAxes() {
        this.directions.add(BlockVector3.UNIT_MINUS_Y);
        this.directions.add(BlockVector3.UNIT_Y);
        this.directions.add(BlockVector3.UNIT_MINUS_X);
        this.directions.add(BlockVector3.UNIT_X);
        this.directions.add(BlockVector3.UNIT_MINUS_Z);
        this.directions.add(BlockVector3.UNIT_Z);
    }

    protected void addDiagonal() {
        this.directions.add(Direction.NORTHEAST.toBlockVector());
        this.directions.add(Direction.SOUTHEAST.toBlockVector());
        this.directions.add(Direction.SOUTHWEST.toBlockVector());
        this.directions.add(Direction.NORTHWEST.toBlockVector());
    }

    public void visit(BlockVector3 position) {
        if (!this.visited.contains((Object)position)) {
            this.queue.add(position);
            this.visited.add(position);
        }
    }

    private void visit(BlockVector3 from, BlockVector3 to) {
        if (!this.visited.contains((Object)to)) {
            this.visited.add(to);
            if (this.isVisitable(from, to)) {
                this.queue.add(to);
            }
        }
    }

    protected abstract boolean isVisitable(BlockVector3 var1, BlockVector3 var2);

    public int getAffected() {
        return this.affected;
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        BlockVector3 position;
        while ((position = this.queue.poll()) != null) {
            if (this.function.apply(position)) {
                ++this.affected;
            }
            for (BlockVector3 dir : this.directions) {
                this.visit(position, position.add(dir));
            }
        }
        return null;
    }

    @Override
    public void cancel() {
    }

    @Override
    public Iterable<Component> getStatusMessages() {
        return ImmutableList.of((Object)TranslatableComponent.of("worldedit.operation.affected.block", TextComponent.of(this.getAffected())).color(TextColor.LIGHT_PURPLE));
    }
}

