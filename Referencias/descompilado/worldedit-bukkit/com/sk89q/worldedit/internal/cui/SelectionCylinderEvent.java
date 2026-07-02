/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.cui;

import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;

public class SelectionCylinderEvent
implements CUIEvent {
    protected final BlockVector3 pos;
    protected final Vector2 radius;

    public SelectionCylinderEvent(BlockVector3 pos, Vector2 radius) {
        this.pos = pos;
        this.radius = radius;
    }

    @Override
    public String getTypeId() {
        return "cyl";
    }

    @Override
    public String[] getParameters() {
        return new String[]{String.valueOf(this.pos.x()), String.valueOf(this.pos.y()), String.valueOf(this.pos.z()), String.valueOf(this.radius.x()), String.valueOf(this.radius.z())};
    }
}

