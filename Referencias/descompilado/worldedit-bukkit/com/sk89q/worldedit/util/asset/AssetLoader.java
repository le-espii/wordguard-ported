/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.asset;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.io.file.FilenameException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@Beta
public abstract class AssetLoader<T> {
    private final Cache<String, T> assets = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build();
    private final WorldEdit worldEdit;
    private final Path assetDir;

    public AssetLoader(WorldEdit worldEdit, Path assetDir) {
        this.worldEdit = worldEdit;
        this.assetDir = assetDir;
    }

    @Nullable
    public T getAsset(String path) {
        T asset;
        Path file;
        Object cached = this.assets.getIfPresent((Object)path);
        if (cached != null) {
            return (T)cached;
        }
        if (!Files.isDirectory(this.assetDir, new LinkOption[0])) {
            return null;
        }
        String[] extensions = this.getAllowedExtensions().toArray(new String[0]);
        try {
            file = this.worldEdit.getSafeOpenFile(null, this.assetDir.toFile(), path, extensions[0], extensions).toPath();
        }
        catch (FilenameException e) {
            return null;
        }
        try {
            asset = this.loadAssetFromPath(file);
            if (asset == null) {
                return null;
            }
        }
        catch (Exception e) {
            WorldEdit.logger.error("Error reading asset file directory", (Throwable)e);
            return null;
        }
        this.assets.put((Object)path, asset);
        return asset;
    }

    @Nullable
    protected abstract T loadAssetFromPath(Path var1) throws Exception;

    public abstract Set<String> getAllowedExtensions();
}

