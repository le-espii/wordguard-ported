/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.net.UrlEscapers
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.snapshot.experimental.fs;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.net.UrlEscapers;
import com.sk89q.worldedit.util.function.IOFunction;
import com.sk89q.worldedit.util.function.IORunnable;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.ArchiveDir;
import com.sk89q.worldedit.util.io.file.ArchiveNioSupport;
import com.sk89q.worldedit.util.io.file.MorePaths;
import com.sk89q.worldedit.util.io.file.SafeFiles;
import com.sk89q.worldedit.util.time.FileNameDateTimeParser;
import com.sk89q.worldedit.util.time.ModificationDateTimeParser;
import com.sk89q.worldedit.util.time.SnapshotDateTimeParser;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotDatabase;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotInfo;
import com.sk89q.worldedit.world.snapshot.experimental.fs.FolderSnapshot;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class FileSystemSnapshotDatabase
implements SnapshotDatabase {
    private static final String SCHEME = "snapfs";
    private static final List<SnapshotDateTimeParser> DATE_TIME_PARSERS = new ImmutableList.Builder().add((Object)FileNameDateTimeParser.getInstance()).addAll(ServiceLoader.load(SnapshotDateTimeParser.class)).add((Object)ModificationDateTimeParser.getInstance()).build();
    private final Path root;
    private final ArchiveNioSupport archiveNioSupport;

    public static ZonedDateTime tryParseDate(Path path) {
        return FileSystemSnapshotDatabase.tryParseDateInternal(path).orElseThrow(() -> new IllegalStateException("Could not detect date of " + String.valueOf(path)));
    }

    private static Optional<ZonedDateTime> tryParseDateInternal(Path path) {
        return DATE_TIME_PARSERS.stream().map(parser -> parser.detectDateTime(path)).filter(Objects::nonNull).findFirst();
    }

    public static URI createUri(String name) {
        return URI.create("snapfs:" + UrlEscapers.urlFragmentEscaper().escape(name));
    }

    public static FileSystemSnapshotDatabase maybeCreate(Path root, ArchiveNioSupport archiveNioSupport) throws IOException {
        Files.createDirectories(root, new FileAttribute[0]);
        return new FileSystemSnapshotDatabase(root, archiveNioSupport);
    }

    public FileSystemSnapshotDatabase(Path root, ArchiveNioSupport archiveNioSupport) {
        Preconditions.checkArgument((boolean)Files.isDirectory(root, new LinkOption[0]), (Object)"Database root is not a directory");
        try {
            this.root = root.toRealPath(new LinkOption[0]);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to resolve snapshot database path", e);
        }
        this.archiveNioSupport = archiveNioSupport;
    }

    private SnapshotInfo createSnapshotInfo(Path idPath, Path ioPath) {
        ZonedDateTime date = FileSystemSnapshotDatabase.tryParseDateInternal(idPath).orElseGet(() -> FileSystemSnapshotDatabase.tryParseDate(ioPath));
        return SnapshotInfo.create(FileSystemSnapshotDatabase.createUri(idPath.toString()), date);
    }

    private Snapshot createSnapshot(Path idPath, Path ioPath, @Nullable Closer closeCallback) {
        return new FolderSnapshot(this.createSnapshotInfo(idPath, ioPath), ioPath, closeCallback);
    }

    public Path getRoot() {
        return this.root;
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public Optional<Snapshot> getSnapshot(URI name) throws IOException {
        if (!name.getScheme().equals(SCHEME)) {
            return Optional.empty();
        }
        return this.getSnapshot(name.getSchemeSpecificPart());
    }

    private Optional<Snapshot> getSnapshot(String id) throws IOException {
        Path rawResolved = this.root.resolve(id);
        Path ioPath = rawResolved.normalize();
        if (!ioPath.startsWith(this.root)) {
            return Optional.empty();
        }
        Path idPath = this.root.relativize(ioPath);
        Optional<Snapshot> result = this.tryRegularFileSnapshot(idPath);
        if (result.isPresent()) {
            return result;
        }
        if (!Files.isDirectory(ioPath, new LinkOption[0])) {
            return Optional.empty();
        }
        return Optional.of(this.createSnapshot(idPath, ioPath, null));
    }

    private Optional<Snapshot> tryRegularFileSnapshot(Path idPath) throws IOException {
        Closer closer = Closer.create();
        Path root = this.root;
        Path relative = idPath;
        Iterator iterator = null;
        try {
            while (true) {
                Optional<ArchiveDir> newRootOpt;
                if (iterator == null) {
                    iterator = MorePaths.iterPaths(relative).iterator();
                }
                if (!iterator.hasNext()) {
                    closer.close();
                    return Optional.empty();
                }
                Path relativeNext = (Path)iterator.next();
                Path next = root.resolve(relativeNext);
                if (!Files.isRegularFile(next, new LinkOption[0]) || !(newRootOpt = this.archiveNioSupport.tryOpenAsDir(next)).isPresent()) continue;
                ArchiveDir archiveDir = newRootOpt.get();
                root = archiveDir.getPath();
                closer.register(archiveDir);
                relative = root.resolve(relativeNext.relativize(relative).toString());
                iterator = null;
                if (Files.exists(relative, new LinkOption[0])) break;
            }
            return Optional.of(this.createSnapshot(idPath, relative, closer));
        }
        catch (Throwable t) {
            throw closer.rethrowAndClose(t);
        }
    }

    @Override
    public Stream<Snapshot> getSnapshots(String worldName) throws IOException {
        return SafeFiles.noLeakFileList(this.root).flatMap(IOFunction.unchecked(entry -> {
            String worldEntry = this.getWorldEntry(worldName, (Path)entry);
            if (worldEntry != null) {
                return Stream.of(worldEntry);
            }
            String fileName = SafeFiles.canonicalFileName(entry);
            if (fileName.equals(worldName) && Files.isDirectory(entry, new LinkOption[0]) && !Files.exists(entry.resolve("level.dat"), new LinkOption[0])) {
                return this.listTimestampedEntries(worldName, (Path)entry).map(id -> worldName + "/" + id);
            }
            return this.getTimestampedEntries(worldName, (Path)entry);
        })).map(IOFunction.unchecked(id -> this.getSnapshot((String)id).orElseThrow(() -> new AssertionError((Object)("Could not find discovered snapshot: " + id)))));
    }

    private Stream<String> listTimestampedEntries(String worldName, Path directory) throws IOException {
        return SafeFiles.noLeakFileList(directory).flatMap(IOFunction.unchecked(entry -> this.getTimestampedEntries(worldName, (Path)entry)));
    }

    private Stream<String> getTimestampedEntries(String worldName, Path entry) throws IOException {
        ZonedDateTime dateTime = FileNameDateTimeParser.getInstance().detectDateTime(entry);
        if (dateTime == null) {
            return Stream.of(new String[0]);
        }
        String fileName = SafeFiles.canonicalFileName(entry);
        if (Files.isDirectory(entry, new LinkOption[0])) {
            return this.listWorldEntries(worldName, entry).map(id -> fileName + "/" + id);
        }
        if (!Files.isRegularFile(entry, new LinkOption[0])) {
            return Stream.of(new String[0]);
        }
        Optional<ArchiveDir> asArchive = this.archiveNioSupport.tryOpenAsDir(entry);
        if (asArchive.isPresent()) {
            ArchiveDir dir = asArchive.get();
            return (Stream)this.listWorldEntries(worldName, dir.getPath()).map(id -> fileName + "/" + id).onClose(IORunnable.unchecked(dir::close));
        }
        return Stream.of(new String[0]);
    }

    private Stream<String> listWorldEntries(String worldName, Path directory) throws IOException {
        return SafeFiles.noLeakFileList(directory).map(IOFunction.unchecked(entry -> this.getWorldEntry(worldName, (Path)entry))).filter(Objects::nonNull);
    }

    private String getWorldEntry(String worldName, Path entry) throws IOException {
        Optional<ArchiveDir> asArchive;
        String fileName = SafeFiles.canonicalFileName(entry);
        if (fileName.equals(worldName) && Files.exists(entry.resolve("level.dat"), new LinkOption[0])) {
            return worldName;
        }
        if (fileName.startsWith(worldName + ".") && Files.isRegularFile(entry, new LinkOption[0]) && (asArchive = this.archiveNioSupport.tryOpenAsDir(entry)).isPresent()) {
            asArchive.get().close();
            return fileName;
        }
        return null;
    }
}

