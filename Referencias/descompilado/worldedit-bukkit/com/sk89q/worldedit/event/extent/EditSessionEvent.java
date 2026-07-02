/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.event.extent;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.TracingExtent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EditSessionEvent
extends Event {
    private final World world;
    private final Actor actor;
    private final int maxBlocks;
    private final EditSession.Stage stage;
    private final List<TracingExtent> tracingExtents = new ArrayList<TracingExtent>();
    private Extent extent;
    private boolean tracing;

    public EditSessionEvent(@Nullable World world, Actor actor, int maxBlocks, EditSession.Stage stage) {
        this.world = world;
        this.actor = actor;
        this.maxBlocks = maxBlocks;
        this.stage = stage;
    }

    @Nullable
    public Actor getActor() {
        return this.actor;
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    public int getMaxBlocks() {
        return this.maxBlocks;
    }

    public Extent getExtent() {
        return this.extent;
    }

    public EditSession.Stage getStage() {
        return this.stage;
    }

    public void setExtent(Extent extent) {
        Preconditions.checkNotNull((Object)extent);
        if (this.tracing && extent != this.extent) {
            TracingExtent tracingExtent = new TracingExtent(extent);
            extent = tracingExtent;
            this.tracingExtents.add(tracingExtent);
        }
        this.extent = extent;
    }

    public void setTracing(boolean tracing) {
        this.tracing = tracing;
    }

    public List<TracingExtent> getTracingExtents() {
        return this.tracingExtents;
    }

    public EditSessionEvent clone(EditSession.Stage stage) {
        return new EditSessionEvent(this.world, this.actor, this.maxBlocks, stage);
    }
}

