/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.asset;

import com.sk89q.worldedit.util.asset.AssetLoader;
import java.util.concurrent.Callable;

public class AssetLoadTask<T>
implements Callable<T> {
    private final String assetName;
    private final AssetLoader<T> loader;

    public AssetLoadTask(AssetLoader<T> loader, String assetName) {
        this.loader = loader;
        this.assetName = assetName;
    }

    @Override
    public T call() {
        return this.loader.getAsset(this.assetName);
    }
}

