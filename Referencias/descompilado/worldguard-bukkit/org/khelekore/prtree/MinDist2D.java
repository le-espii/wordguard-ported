/*
 * Decompiled with CFR 0.152.
 */
package org.khelekore.prtree;

public class MinDist2D {
    private MinDist2D() {
    }

    public static double get(double minx, double miny, double maxx, double maxy, double x, double y) {
        double rx = MinDist2D.r(x, minx, maxx);
        double ry = MinDist2D.r(y, miny, maxy);
        double xd = x - rx;
        double yd = y - ry;
        return xd * xd + yd * yd;
    }

    private static double r(double x, double min, double max) {
        double r = x;
        if (x < min) {
            r = min;
        }
        if (x > max) {
            r = max;
        }
        return r;
    }
}

