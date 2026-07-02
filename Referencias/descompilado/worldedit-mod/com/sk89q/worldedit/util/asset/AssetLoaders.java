/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Table
 *  com.google.common.io.MoreFiles
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util.asset;

import com.google.common.annotations.Beta;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.io.MoreFiles;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.asset.AssetLoader;
import com.sk89q.worldedit.util.asset.ImageHeightmapLoader;
import com.sk89q.worldedit.util.asset.holder.ImageHeightmap;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.Logger;

@Beta
public class AssetLoaders {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final List<AssetLoader<?>> assetLoaders = Lists.newArrayList();
    private final Table<Class<?>, String, AssetLoader<?>> assetLoaderRegistration = HashBasedTable.create();
    private final WorldEdit worldEdit;
    private Path assetsDir;

    public AssetLoaders(WorldEdit worldEdit) {
        this.worldEdit = worldEdit;
    }

    public void init() {
        this.assetsDir = this.worldEdit.getWorkingDirectoryPath("assets");
        try {
            Files.createDirectories(this.assetsDir, new FileAttribute[0]);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to create asset directory", (Throwable)e);
        }
        this.registerAssetLoader(new ImageHeightmapLoader(this.worldEdit, this.assetsDir), ImageHeightmap.class);
    }

    public void uninit() {
        this.assetsDir = null;
        this.assetLoaderRegistration.clear();
    }

    public <T> void registerAssetLoader(AssetLoader<T> loader, Class<T> assetClass) {
        this.assetLoaders.add(loader);
        for (String extension : loader.getAllowedExtensions()) {
            if (this.assetLoaderRegistration.contains(assetClass, (Object)extension)) {
                LOGGER.warn(String.format("Tried to register asset loader '%s' with extension '%s' and asset class '%s', but it is already registered to '%s'", loader.getClass().getName(), extension, assetClass.getName(), ((AssetLoader)this.assetLoaderRegistration.get(assetClass, (Object)extension)).getClass().getName()));
                continue;
            }
            this.assetLoaderRegistration.put(assetClass, (Object)extension, loader);
        }
    }

    public <T> Optional<AssetLoader<T>> getAssetLoader(Class<T> assetClass, String filename) {
        if (!this.assetLoaderRegistration.containsRow(assetClass)) {
            return Optional.empty();
        }
        Path directPath = this.assetsDir.resolve(filename);
        String ext = MoreFiles.getFileExtension((Path)directPath);
        if (Files.exists(directPath, new LinkOption[0]) && this.assetLoaderRegistration.contains(assetClass, (Object)ext)) {
            return Optional.ofNullable((AssetLoader)this.assetLoaderRegistration.get(assetClass, (Object)ext));
        }
        for (Map.Entry entry : this.assetLoaderRegistration.row(assetClass).entrySet()) {
            Path extensionPath = this.assetsDir.resolve(filename + "." + (String)entry.getKey());
            if (!Files.exists(extensionPath, new LinkOption[0])) continue;
            return Optional.ofNullable((AssetLoader)entry.getValue());
        }
        return Optional.empty();
    }

    public <T> List<AssetLoader<T>> getAssetLoaders(Class<T> assetClass) {
        if (!this.assetLoaderRegistration.containsRow(assetClass)) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(this.assetLoaderRegistration.row(assetClass).values());
    }

    public List<Path> getFilesForAsset(Class<?> assetClass) {
        ImmutableList immutableList;
        block8: {
            Set extensions = this.assetLoaderRegistration.row(assetClass).keySet();
            DirectoryStream<Path> stream = Files.newDirectoryStream(this.assetsDir, entry -> extensions.contains(MoreFiles.getFileExtension((Path)entry)));
            try {
                immutableList = ImmutableList.copyOf(stream);
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    LOGGER.warn("Failed to get files for asset type " + assetClass.getName(), (Throwable)e);
                    return ImmutableList.of();
                }
            }
            stream.close();
        }
        return immutableList;
    }

    public List<AssetLoader<?>> getAssetLoaders() {
        return ImmutableList.copyOf(this.assetLoaders);
    }
}

