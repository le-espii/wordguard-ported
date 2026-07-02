/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.sk89q.worldedit.util.io.file;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.io.file.ArchiveDir;
import com.sk89q.worldedit.util.io.file.ArchiveNioSupport;
import com.sk89q.worldedit.util.io.file.ZipArchiveNioSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class ArchiveNioSupports {
    private static final List<ArchiveNioSupport> SUPPORTS;
    private static final ArchiveNioSupport COMBINED;

    public static Optional<ArchiveDir> tryOpenAsDir(Path archive) throws IOException {
        for (ArchiveNioSupport support : SUPPORTS) {
            Optional<ArchiveDir> fs = support.tryOpenAsDir(archive);
            if (!fs.isPresent()) continue;
            return fs;
        }
        return Optional.empty();
    }

    public static ArchiveNioSupport combined() {
        return COMBINED;
    }

    public static Path skipRootSameName(Path root, String name) throws IOException {
        Path innerDir = root.resolve(name);
        if (Files.isDirectory(innerDir, new LinkOption[0])) {
            try (Stream<Path> files = Files.list(root);){
                if (files.allMatch(x$0 -> Files.isDirectory(x$0, new LinkOption[0]))) {
                    Path path = innerDir;
                    return path;
                }
            }
        }
        return root;
    }

    private ArchiveNioSupports() {
    }

    static {
        ImmutableList.Builder builder = ImmutableList.builder();
        SUPPORTS = builder.add((Object)ZipArchiveNioSupport.getInstance()).addAll(ServiceLoader.load(ArchiveNioSupport.class)).build();
        COMBINED = ArchiveNioSupports::tryOpenAsDir;
    }
}

