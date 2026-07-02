/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.snapshot.experimental.fs;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotInfo;
import com.sk89q.worldedit.world.storage.LegacyChunkStore;
import com.sk89q.worldedit.world.storage.McRegionChunkStore;
import com.sk89q.worldedit.world.storage.McRegionReader;
import com.sk89q.worldedit.world.storage.MissingChunkException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinRootEntry;

public class FolderSnapshot
implements Snapshot {
    private static final Object NOT_FOUND_TOKEN = new Object();
    private final SnapshotInfo info;
    private final Path folder;
    private final AtomicReference<Object> regionFolder = new AtomicReference();
    @Nullable
    private final Closer closeCallback;

    private static Object getRegionFolder(Path folder) throws IOException {
        Path regionDir = folder.resolve("region");
        if (Files.exists(regionDir, new LinkOption[0])) {
            Preconditions.checkState((boolean)Files.isDirectory(regionDir, new LinkOption[0]), (Object)"Region folder is actually a file");
            return regionDir;
        }
        try (Stream<Path> paths = Files.list(folder);){
            Optional<Path> path = paths.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(p -> p.getFileName().toString().startsWith("DIM")).map(p -> p.resolve("region")).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).findFirst();
            if (path.isPresent()) {
                Path path2 = path.get();
                return path2;
            }
        }
        paths = Files.list(folder);
        try {
            if (paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).anyMatch(p -> {
                String fileName = p.getFileName().toString();
                return fileName.startsWith("r") && (fileName.endsWith(".mca") || fileName.endsWith(".mcr"));
            })) {
                Path path = folder;
                return path;
            }
        }
        finally {
            if (paths != null) {
                paths.close();
            }
        }
        return NOT_FOUND_TOKEN;
    }

    public FolderSnapshot(SnapshotInfo info, Path folder, @Nullable Closer closeCallback) {
        this.info = info;
        this.folder = folder.toAbsolutePath();
        this.closeCallback = closeCallback;
    }

    public Path getFolder() {
        return this.folder;
    }

    @Override
    public SnapshotInfo getInfo() {
        return this.info;
    }

    private Optional<Path> getRegionFolder() throws IOException {
        Object regFolder = this.regionFolder.get();
        if (regFolder == null) {
            Object update = FolderSnapshot.getRegionFolder(this.folder);
            regFolder = !this.regionFolder.compareAndSet(null, update) ? this.regionFolder.get() : update;
        }
        return regFolder == NOT_FOUND_TOKEN ? Optional.empty() : Optional.of((Path)regFolder);
    }

    @Override
    public LinCompoundTag getChunkTag(BlockVector3 position) throws DataException, IOException {
        BlockVector2 pos = position.toBlockVector2();
        Optional<Path> regFolder = this.getRegionFolder();
        if (regFolder.isEmpty()) {
            Path chunkFile = this.getFolder().resolve(LegacyChunkStore.getFilename(pos, "/"));
            if (!Files.exists(chunkFile, new LinkOption[0])) {
                throw new MissingChunkException();
            }
            try (DataInputStream chunkStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(chunkFile, new OpenOption[0])));){
                LinCompoundTag linCompoundTag = LinBinaryIO.readUsing(chunkStream, LinRootEntry::readFrom).value();
                return linCompoundTag;
            }
        }
        Path regionFile = regFolder.get().resolve(McRegionChunkStore.getFilename(pos));
        if (!Files.exists(regionFile, new LinkOption[0]) && !Files.exists(regionFile = regionFile.resolveSibling(regionFile.getFileName().toString().replace(".mca", ".mcr")), new LinkOption[0])) {
            throw new MissingChunkException();
        }
        try (InputStream stream = Files.newInputStream(regionFile, new OpenOption[0]);){
            LinCompoundTag linCompoundTag;
            McRegionReader regionReader = new McRegionReader(stream);
            try (DataInputStream chunkStream = new DataInputStream(regionReader.getChunkInputStream(pos));){
                linCompoundTag = LinBinaryIO.readUsing(chunkStream, LinRootEntry::readFrom).value();
            }
            return linCompoundTag;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.closeCallback != null) {
            this.closeCallback.close();
        }
    }
}

