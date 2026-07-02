/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.util.ClipboardTransformBaker;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;

public interface Clipboard
extends Extent {
    public Region getRegion();

    public BlockVector3 getDimensions();

    public BlockVector3 getOrigin();

    public void setOrigin(BlockVector3 var1);

    default public boolean hasBiomes() {
        return false;
    }

    default public Clipboard transform(Transform transform) throws WorldEditException {
        return ClipboardTransformBaker.bakeTransform(this, transform);
    }
}

