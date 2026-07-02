/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.noise;

import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.noise.NoiseGenerator;
import java.util.Random;

public class RandomNoise
implements NoiseGenerator {
    private final Random random;

    public RandomNoise(Random random) {
        this.random = random;
    }

    public RandomNoise() {
        this(new Random());
    }

    @Override
    public float noise(Vector2 position) {
        return this.random.nextFloat();
    }

    @Override
    public float noise(Vector3 position) {
        return this.random.nextFloat();
    }
}

