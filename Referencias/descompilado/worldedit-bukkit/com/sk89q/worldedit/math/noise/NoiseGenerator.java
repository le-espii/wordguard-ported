/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.noise;

import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;

public interface NoiseGenerator {
    public float noise(Vector2 var1);

    public float noise(Vector3 var1);
}

