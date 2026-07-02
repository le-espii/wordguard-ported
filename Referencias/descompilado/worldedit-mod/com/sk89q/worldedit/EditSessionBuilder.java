/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.TracedEditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.world.World;
import javax.annotation.Nullable;

public final class EditSessionBuilder {
    private final EventBus eventBus;
    @Nullable
    private World world;
    private int maxBlocks = -1;
    @Nullable
    private Actor actor;
    @Nullable
    private BlockBag blockBag;
    private boolean tracing;

    EditSessionBuilder(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    public EditSessionBuilder world(@Nullable World world) {
        this.world = world;
        return this;
    }

    public int getMaxBlocks() {
        return this.maxBlocks;
    }

    public EditSessionBuilder maxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
        return this;
    }

    @Nullable
    public Actor getActor() {
        return this.actor;
    }

    public EditSessionBuilder actor(@Nullable Actor actor) {
        this.actor = actor;
        return this;
    }

    @Nullable
    public BlockBag getBlockBag() {
        return this.blockBag;
    }

    public EditSessionBuilder blockBag(@Nullable BlockBag blockBag) {
        this.blockBag = blockBag;
        return this;
    }

    public boolean isTracing() {
        return this.tracing;
    }

    public EditSessionBuilder tracing(boolean tracing) {
        this.tracing = tracing;
        return this;
    }

    public <A extends Actor & Locatable> EditSessionBuilder locatableActor(A locatable) {
        Extent extent = ((Locatable)locatable).getExtent();
        Preconditions.checkArgument((boolean)(extent instanceof World), (String)"%s is not located in a World", locatable);
        return this.world((World)extent).actor(locatable);
    }

    public EditSession build() {
        if (WorldEdit.getInstance().getConfiguration().traceUnflushedSessions) {
            return new TracedEditSession(this.eventBus, this.world, this.maxBlocks, this.blockBag, this.actor, this.tracing);
        }
        return new EditSession(this.eventBus, this.world, this.maxBlocks, this.blockBag, this.actor, this.tracing);
    }
}

