/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math.noise;

import com.sk89q.worldedit.jlibnoise.module.Module;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.noise.NoiseGenerator;
import java.util.Random;

abstract class JLibNoiseGenerator<V extends Module>
implements NoiseGenerator {
    private static final Random RANDOM = new Random();
    private final V module = this.createModule();

    JLibNoiseGenerator() {
        this.setSeed(RANDOM.nextInt());
    }

    protected abstract V createModule();

    protected V getModule() {
        return this.module;
    }

    public abstract void setSeed(int var1);

    public abstract int getSeed();

    @Override
    public float noise(Vector2 position) {
        return this.forceRange(((Module)this.module).GetValue(position.x(), 0.0, position.z()));
    }

    @Override
    public float noise(Vector3 position) {
        return this.forceRange(((Module)this.module).GetValue(position.x(), position.y(), position.z()));
    }

    private float forceRange(double value) {
        return (float)Math.max(0.0, Math.min(1.0, value / 2.0 + 0.5));
    }
}

