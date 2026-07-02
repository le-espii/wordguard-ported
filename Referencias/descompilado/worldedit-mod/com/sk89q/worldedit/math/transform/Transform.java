/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.transform;

import com.sk89q.worldedit.math.Vector3;

public interface Transform {
    public boolean isIdentity();

    public Vector3 apply(Vector3 var1);

    public Transform inverse();

    public Transform combine(Transform var1);
}

