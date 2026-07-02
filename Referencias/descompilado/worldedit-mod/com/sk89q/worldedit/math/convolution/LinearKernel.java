/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.convolution;

import com.sk89q.worldedit.math.convolution.Kernel;
import java.util.Arrays;

public class LinearKernel
extends Kernel {
    public LinearKernel(int radius) {
        super(radius * 2 + 1, radius * 2 + 1, LinearKernel.createKernel(radius));
    }

    private static float[] createKernel(int radius) {
        int diameter = radius * 2 + 1;
        float[] data = new float[diameter * diameter];
        Arrays.fill(data, 1.0f / (float)data.length);
        return data;
    }
}

