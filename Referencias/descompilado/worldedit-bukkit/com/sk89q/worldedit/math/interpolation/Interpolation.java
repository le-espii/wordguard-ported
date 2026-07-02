/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.interpolation;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.interpolation.Node;
import java.util.List;

public interface Interpolation {
    public void setNodes(List<Node> var1);

    public Vector3 getPosition(double var1);

    public Vector3 get1stDerivative(double var1);

    public double arcLength(double var1, double var3);

    public int getSegment(double var1);
}

