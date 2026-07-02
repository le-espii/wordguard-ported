/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.cui;

import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;

public class SelectionPointEvent
implements CUIEvent {
    protected final int id;
    protected final BlockVector3 pos;
    protected final long area;

    @Deprecated
    public SelectionPointEvent(int id, BlockVector3 pos, int area) {
        this.id = id;
        this.pos = pos;
        this.area = area;
    }

    public SelectionPointEvent(int id, BlockVector3 pos, long area) {
        this.id = id;
        this.pos = pos;
        this.area = area;
    }

    @Override
    public String getTypeId() {
        return "p";
    }

    @Override
    public String[] getParameters() {
        return new String[]{String.valueOf(this.id), String.valueOf(this.pos.x()), String.valueOf(this.pos.y()), String.valueOf(this.pos.z()), String.valueOf(this.area)};
    }
}

