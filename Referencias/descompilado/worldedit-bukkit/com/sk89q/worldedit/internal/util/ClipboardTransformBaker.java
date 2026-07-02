/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.internal.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.CombinedTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class ClipboardTransformBaker {
    private final Clipboard original;
    private final Transform transform;

    private ClipboardTransformBaker(Clipboard original, Transform transform) {
        Preconditions.checkNotNull((Object)original);
        Preconditions.checkNotNull((Object)transform);
        this.original = original;
        this.transform = transform;
    }

    private Region getTransformedRegion() {
        Region region = this.original.getRegion();
        Vector3 minimum = region.getMinimumPoint().toVector3();
        Vector3 maximum = region.getMaximumPoint().toVector3();
        CombinedTransform transformAround = new CombinedTransform(new AffineTransform().translate(this.original.getOrigin().multiply(-1)), this.transform, new AffineTransform().translate(this.original.getOrigin()));
        Vector3[] corners = new Vector3[]{minimum, maximum, minimum.withX(maximum.x()), minimum.withY(maximum.y()), minimum.withZ(maximum.z()), maximum.withX(minimum.x()), maximum.withY(minimum.y()), maximum.withZ(minimum.z())};
        for (int i = 0; i < corners.length; ++i) {
            corners[i] = transformAround.apply(corners[i]);
        }
        Vector3 newMinimum = corners[0];
        Vector3 newMaximum = corners[0];
        for (int i = 1; i < corners.length; ++i) {
            newMinimum = newMinimum.getMinimum(corners[i]);
            newMaximum = newMaximum.getMaximum(corners[i]);
        }
        newMinimum = newMinimum.floor();
        newMaximum = newMaximum.ceil();
        return new CuboidRegion(newMinimum.toBlockPoint(), newMaximum.toBlockPoint());
    }

    private Operation copyTo(Extent target) {
        BlockTransformExtent extent = new BlockTransformExtent(this.original, this.transform);
        ForwardExtentCopy copy = new ForwardExtentCopy(extent, this.original.getRegion(), this.original.getOrigin(), target, this.original.getOrigin());
        copy.setTransform(this.transform);
        if (this.original.hasBiomes()) {
            copy.setCopyingBiomes(true);
        }
        return copy;
    }

    public static Clipboard bakeTransform(Clipboard original, Transform transform) throws WorldEditException {
        if (transform.isIdentity()) {
            return original;
        }
        ClipboardTransformBaker baker = new ClipboardTransformBaker(original, transform);
        BlockArrayClipboard target = new BlockArrayClipboard(baker.getTransformedRegion());
        target.setOrigin(original.getOrigin());
        Operations.complete(baker.copyTo(target));
        return target;
    }
}

