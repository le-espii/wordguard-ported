/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import javax.annotation.Nullable;

public class EditContext {
    private Extent destination;
    @Nullable
    private Region region;
    @Nullable
    private Pattern fill;
    @Nullable
    private LocalSession session;

    public Extent getDestination() {
        return this.destination;
    }

    public void setDestination(Extent destination) {
        Preconditions.checkNotNull((Object)destination, (Object)"destination");
        this.destination = destination;
    }

    @Nullable
    public Region getRegion() {
        return this.region;
    }

    public void setRegion(@Nullable Region region) {
        this.region = region;
    }

    @Nullable
    public Pattern getFill() {
        return this.fill;
    }

    public void setFill(@Nullable Pattern fill) {
        this.fill = fill;
    }

    @Nullable
    public LocalSession getSession() {
        return this.session;
    }

    public void setSession(@Nullable LocalSession session) {
        this.session = session;
    }
}

