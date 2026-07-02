/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.cui;

import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;

public class SelectionPoint2DEvent
implements CUIEvent {
    protected final int id;
    protected final int blockX;
    protected final int blockZ;
    protected final long area;

    @Deprecated
    public SelectionPoint2DEvent(int id, BlockVector2 pos, int area) {
        this.id = id;
        this.blockX = pos.x();
        this.blockZ = pos.z();
        this.area = area;
    }

    @Deprecated
    public SelectionPoint2DEvent(int id, BlockVector3 pos, int area) {
        this.id = id;
        this.blockX = pos.x();
        this.blockZ = pos.z();
        this.area = area;
    }

    public SelectionPoint2DEvent(int id, BlockVector2 pos, long area) {
        this.id = id;
        this.blockX = pos.x();
        this.blockZ = pos.z();
        this.area = area;
    }

    public SelectionPoint2DEvent(int id, BlockVector3 pos, long area) {
        this.id = id;
        this.blockX = pos.x();
        this.blockZ = pos.z();
        this.area = area;
    }

    @Override
    public String getTypeId() {
        return "p2";
    }

    @Override
    public String[] getParameters() {
        return new String[]{String.valueOf(this.id), String.valueOf(this.blockX), String.valueOf(this.blockZ), String.valueOf(this.area)};
    }
}

