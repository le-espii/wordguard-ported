/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions.polyhedron;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.polyhedron.Edge;

public class Triangle {
    private String tag = "Triangle";
    private final Vector3[] vertices;
    private final Vector3 normal;
    private final double maxDotProduct;

    public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) {
        Preconditions.checkNotNull((Object)((Object)v0));
        Preconditions.checkNotNull((Object)((Object)v1));
        Preconditions.checkNotNull((Object)((Object)v2));
        this.vertices = new Vector3[]{v0, v1, v2};
        this.normal = v1.subtract(v0).cross(v2.subtract(v0)).normalize();
        this.maxDotProduct = Math.max(Math.max(this.normal.dot(v0), this.normal.dot(v1)), this.normal.dot(v2));
    }

    public Vector3 getVertex(int index) {
        return this.vertices[index];
    }

    public Edge getEdge(int index) {
        if (index == this.vertices.length - 1) {
            return new Edge(this.vertices[index], this.vertices[0]);
        }
        return new Edge(this.vertices[index], this.vertices[index + 1]);
    }

    public boolean below(Vector3 pt) {
        Preconditions.checkNotNull((Object)((Object)pt));
        return this.normal.dot(pt) < this.maxDotProduct;
    }

    public boolean above(Vector3 pt) {
        Preconditions.checkNotNull((Object)((Object)pt));
        return this.normal.dot(pt) > this.maxDotProduct;
    }

    public Triangle tag(String tag) {
        Preconditions.checkNotNull((Object)tag);
        this.tag = tag;
        return this;
    }

    public String toString() {
        return this.tag + "(" + String.valueOf((Object)this.vertices[0]) + "," + String.valueOf((Object)this.vertices[1]) + "," + String.valueOf((Object)this.vertices[2]) + ")";
    }
}

