/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.math.convolution;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.convolution.Kernel;

public class HeightMapFilter {
    private Kernel kernel;

    public HeightMapFilter(Kernel kernel) {
        Preconditions.checkNotNull((Object)kernel);
        this.kernel = kernel;
    }

    public HeightMapFilter(int kernelWidth, int kernelHeight, float[] kernelData) {
        Preconditions.checkNotNull((Object)kernelData);
        this.kernel = new Kernel(kernelWidth, kernelHeight, kernelData);
    }

    public Kernel getKernel() {
        return this.kernel;
    }

    public void setKernel(Kernel kernel) {
        Preconditions.checkNotNull((Object)kernel);
        this.kernel = kernel;
    }

    public int[] filter(int[] inData, int width, int height) {
        return this.filter(inData, width, height, 0.5f);
    }

    public int[] filter(int[] inData, int width, int height, float offset) {
        Preconditions.checkNotNull((Object)inData);
        float[] inDataFloat = new float[inData.length];
        for (int i = 0; i < inData.length; ++i) {
            inDataFloat[i] = inData[i];
        }
        int index = 0;
        float[] matrix = this.kernel.getKernelData(null);
        int[] outData = new int[inData.length];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                outData[index++] = (int)Math.floor(this.calculateHeight(inDataFloat, width, height, offset, matrix, x, y));
            }
        }
        return outData;
    }

    public float[] filter(float[] inData, int width, int height, float offset) {
        Preconditions.checkNotNull((Object)inData);
        int index = 0;
        float[] matrix = this.kernel.getKernelData(null);
        float[] outData = new float[inData.length];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                outData[index++] = this.calculateHeight(inData, width, height, offset, matrix, x, y);
            }
        }
        return outData;
    }

    private float calculateHeight(float[] inData, int width, int height, float offset, float[] matrix, int x, int y) {
        int kh = this.kernel.getHeight();
        int kw = this.kernel.getWidth();
        int kox = this.kernel.getXOrigin();
        int koy = this.kernel.getYOrigin();
        float z = 0.0f;
        for (int ky = 0; ky < kh; ++ky) {
            int offsetY = y + ky - koy;
            if (offsetY < 0 || offsetY >= height) {
                offsetY = y;
            }
            offsetY *= width;
            int matrixOffset = ky * kw;
            for (int kx = 0; kx < kw; ++kx) {
                float f = matrix[matrixOffset + kx];
                if (f == 0.0f) continue;
                int offsetX = x + kx - kox;
                if (offsetX < 0 || offsetX >= width) {
                    offsetX = x;
                }
                z += f * inData[offsetY + offsetX];
            }
        }
        return z + offset;
    }
}

