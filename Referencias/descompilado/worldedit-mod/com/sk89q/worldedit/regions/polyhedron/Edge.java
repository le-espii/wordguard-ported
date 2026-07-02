/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions.polyhedron;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.polyhedron.Triangle;

public class Edge {
    private final Vector3 start;
    private final Vector3 end;

    public Edge(Vector3 start, Vector3 end) {
        Preconditions.checkNotNull((Object)((Object)start));
        Preconditions.checkNotNull((Object)((Object)end));
        this.start = start;
        this.end = end;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Edge)) {
            return false;
        }
        Edge otherEdge = (Edge)other;
        if (this.start.equals((Object)otherEdge.end) && this.end.equals((Object)otherEdge.start)) {
            return true;
        }
        return this.end.equals((Object)otherEdge.end) && this.start.equals((Object)otherEdge.start);
    }

    public int hashCode() {
        return this.start.hashCode() ^ this.end.hashCode();
    }

    public String toString() {
        return "(" + String.valueOf((Object)this.start) + "," + String.valueOf((Object)this.end) + ")";
    }

    public Triangle createTriangle(Vector3 vertex) {
        Preconditions.checkNotNull((Object)((Object)vertex));
        return new Triangle(this.start, this.end, vertex);
    }

    public Triangle createTriangle2(Vector3 vertex) {
        Preconditions.checkNotNull((Object)((Object)vertex));
        return new Triangle(this.start, vertex, this.end);
    }
}

