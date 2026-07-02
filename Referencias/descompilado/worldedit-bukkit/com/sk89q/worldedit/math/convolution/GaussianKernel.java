/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.convolution;

import com.sk89q.worldedit.math.convolution.Kernel;

public class GaussianKernel
extends Kernel {
    public GaussianKernel(int radius, double sigma) {
        super(radius * 2 + 1, radius * 2 + 1, GaussianKernel.createKernel(radius, sigma));
    }

    private static float[] createKernel(int radius, double sigma) {
        int diameter = radius * 2 + 1;
        float[] data = new float[diameter * diameter];
        double sigma22 = 2.0 * sigma * sigma;
        double constant = Math.PI * sigma22;
        float sum = 0.0f;
        for (int y = -radius; y <= radius; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                float value;
                data[(y + radius) * diameter + x + radius] = value = (float)(Math.exp((double)(-(x * x + y * y)) / sigma22) / constant);
                sum += value;
            }
        }
        for (int i = 0; i < data.length; ++i) {
            data[i] = data[i] / sum;
        }
        return data;
    }
}

