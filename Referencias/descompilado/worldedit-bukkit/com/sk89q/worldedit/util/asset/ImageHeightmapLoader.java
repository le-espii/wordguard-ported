/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.asset;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.asset.AssetLoader;
import com.sk89q.worldedit.util.asset.holder.ImageHeightmap;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

@Beta
public class ImageHeightmapLoader
extends AssetLoader<ImageHeightmap> {
    public ImageHeightmapLoader(WorldEdit worldEdit, Path assetDir) {
        super(worldEdit, assetDir);
    }

    @Override
    @Nullable
    public ImageHeightmap loadAssetFromPath(Path path) throws Exception {
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        return new ImageHeightmap(ImageIO.read(path.toFile()));
    }

    @Override
    public Set<String> getAllowedExtensions() {
        return ImmutableSet.copyOf((Object[])ImageIO.getReaderFileSuffixes());
    }
}

