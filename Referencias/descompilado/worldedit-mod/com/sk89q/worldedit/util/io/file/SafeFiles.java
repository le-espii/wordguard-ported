/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.io.file;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.util.io.file.AttributeTarget;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class SafeFiles {
    private static final FileAttribute<?>[] OWNER_ONLY_FILE_ATTRS;
    private static final FileAttribute<?>[] OWNER_ONLY_DIR_ATTRS;

    public static Stream<Path> noLeakFileList(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir);){
            Stream<Path> stream2 = stream.toList().stream();
            return stream2;
        }
    }

    public static String canonicalFileName(Path path) {
        return SafeFiles.dropSlash(path.getFileName().toString());
    }

    private static String dropSlash(String name) {
        if (name.isEmpty() || name.codePointBefore(name.length()) != 47) {
            return name;
        }
        return name.substring(0, name.length() - 1);
    }

    public static void tryHardToDeleteDir(Path directory) throws IOException {
        if (!Files.isDirectory(directory, new LinkOption[0])) {
            if (!Files.exists(directory, new LinkOption[0])) {
                return;
            }
            throw new IOException(String.valueOf(directory) + " is not a directory");
        }
        try (Stream<Path> files = Files.list(directory);){
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                Path next = (Path)iter.next();
                if (Files.isDirectory(next, new LinkOption[0])) {
                    SafeFiles.tryHardToDeleteDir(next);
                    continue;
                }
                SafeFiles.tryHardToDelete(next);
            }
        }
        SafeFiles.tryHardToDelete(directory);
    }

    public static void tryHardToDelete(Path path) throws IOException {
        IOException suppressed = SafeFiles.tryDelete(path);
        if (suppressed == null) {
            return;
        }
        System.gc();
        try {
            Thread.sleep(10L);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        IOException suppressed2 = SafeFiles.tryDelete(path);
        if (suppressed2 == null) {
            return;
        }
        IOException ex = new IOException("Failed to delete " + String.valueOf(path), suppressed2);
        ex.addSuppressed(suppressed);
        throw ex;
    }

    @Nullable
    private static IOException tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
            if (Files.exists(path, new LinkOption[0])) {
                return new IOException(String.valueOf(path) + " still exists after deleting");
            }
            return null;
        }
        catch (IOException e) {
            return e;
        }
    }

    public static FileAttribute<?>[] getOwnerOnlyFileAttributes(AttributeTarget attributeTarget) {
        return switch (attributeTarget) {
            case AttributeTarget.FILE -> OWNER_ONLY_FILE_ATTRS;
            case AttributeTarget.DIRECTORY -> OWNER_ONLY_DIR_ATTRS;
            default -> throw new IllegalStateException("Unknown attribute target " + String.valueOf((Object)attributeTarget));
        };
    }

    private SafeFiles() {
    }

    static {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            OWNER_ONLY_FILE_ATTRS = new FileAttribute[]{PosixFilePermissions.asFileAttribute((Set<PosixFilePermission>)ImmutableSet.of((Object)((Object)PosixFilePermission.OWNER_READ), (Object)((Object)PosixFilePermission.OWNER_WRITE)))};
            OWNER_ONLY_DIR_ATTRS = new FileAttribute[]{PosixFilePermissions.asFileAttribute((Set<PosixFilePermission>)ImmutableSet.of((Object)((Object)PosixFilePermission.OWNER_READ), (Object)((Object)PosixFilePermission.OWNER_WRITE), (Object)((Object)PosixFilePermission.OWNER_EXECUTE)))};
        } else {
            OWNER_ONLY_FILE_ATTRS = new FileAttribute[0];
            OWNER_ONLY_DIR_ATTRS = new FileAttribute[0];
        }
    }
}

