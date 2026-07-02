/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.cui;

import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;

public class SelectionEllipsoidPointEvent
implements CUIEvent {
    protected final int id;
    protected final BlockVector3 pos;

    public SelectionEllipsoidPointEvent(int id, BlockVector3 pos) {
        this.id = id;
        this.pos = pos;
    }

    @Override
    public String getTypeId() {
        return "e";
    }

    @Override
    public String[] getParameters() {
        return new String[]{String.valueOf(this.id), String.valueOf(this.pos.x()), String.valueOf(this.pos.y()), String.valueOf(this.pos.z())};
    }
}

