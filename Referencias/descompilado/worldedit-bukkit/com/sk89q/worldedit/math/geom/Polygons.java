/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public final class Polygons {
    private Polygons() {
    }

    public static List<BlockVector2> polygonizeCylinder(BlockVector2 center, Vector2 radius, int maxPoints) {
        int nPoints = (int)Math.ceil(Math.PI * radius.length());
        if (maxPoints >= 0 && nPoints >= maxPoints) {
            nPoints = maxPoints - 1;
        }
        ArrayList<BlockVector2> points = new ArrayList<BlockVector2>(nPoints);
        for (int i = 0; i < nPoints; ++i) {
            double angle = (double)i * (Math.PI * 2) / (double)nPoints;
            Vector2 pos = Vector2.at(Math.cos(angle), Math.sin(angle));
            BlockVector2 blockVector2D = pos.multiply(radius).toBlockPoint().add(center);
            points.add(blockVector2D);
        }
        return points;
    }
}

