/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.internal.anvil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sk89q.worldedit.internal.anvil.ChunkDeletionInfo;
import com.sk89q.worldedit.internal.anvil.RegionAccess;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.BlockVector2;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;

public final class ChunkDeleter {
    public static final String DELCHUNKS_FILE_NAME = "delete_chunks.json";
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final Comparator<BlockVector2> chunkSorter = Comparator.comparing(pos -> (pos.x() & 0x1F) + (pos.z() & 0x1F) * 32);
    private static final Gson chunkDeleterGson = new GsonBuilder().registerTypeAdapter(BlockVector2.class, (Object)new BlockVector2Adapter().nullSafe()).setPrettyPrinting().create();
    private final ChunkDeletionInfo chunkDeletionInfo;
    private final Set<Path> backedUpRegions = new HashSet<Path>();
    private boolean shouldPreload;
    private int debugRate = 100;
    private int totalChunksDeleted = 0;
    private int deletionsRequested = 0;

    public static ChunkDeletionInfo readInfo(Path chunkFile) throws IOException, JsonSyntaxException {
        String json = Files.readString((Path)chunkFile, (Charset)StandardCharsets.UTF_8);
        return (ChunkDeletionInfo)chunkDeleterGson.fromJson(json, ChunkDeletionInfo.class);
    }

    public static void writeInfo(ChunkDeletionInfo info, Path chunkFile) throws IOException, JsonIOException {
        String json = chunkDeleterGson.toJson((Object)info, new TypeToken<ChunkDeletionInfo>(){}.getType());
        try (BufferedWriter writer = Files.newBufferedWriter(chunkFile, new OpenOption[0]);){
            writer.write(json);
        }
    }

    public static void runFromFile(Path chunkFile, boolean deleteOnSuccess) {
        ChunkDeleter chunkDeleter;
        try {
            chunkDeleter = ChunkDeleter.createFromFile(chunkFile);
        }
        catch (JsonSyntaxException | IOException e) {
            LOGGER.error("Could not parse chunk deletion file. Invalid file?", e);
            return;
        }
        LOGGER.info("Found chunk deletions. Proceeding with deletion...");
        long start = System.currentTimeMillis();
        if (chunkDeleter.runDeleter()) {
            LOGGER.info("Successfully deleted {} matching chunks (out of {}, taking {} ms).", (Object)chunkDeleter.getDeletedChunkCount(), (Object)chunkDeleter.getDeletionsRequested(), (Object)(System.currentTimeMillis() - start));
            if (deleteOnSuccess) {
                boolean deletedFile = false;
                try {
                    deletedFile = Files.deleteIfExists(chunkFile);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (!deletedFile) {
                    LOGGER.warn("Chunk deletion file could not be cleaned up. This may have unintended consequences on next startup, or if /delchunks is used again.");
                }
            }
        } else {
            LOGGER.error("Error occurred while deleting chunks. If world errors occur, stop the server and restore the *.bak backup files.");
        }
    }

    private ChunkDeleter(ChunkDeletionInfo chunkDeletionInfo) {
        this.chunkDeletionInfo = chunkDeletionInfo;
    }

    private static ChunkDeleter createFromFile(Path chunkFile) throws IOException {
        ChunkDeletionInfo info = ChunkDeleter.readInfo(chunkFile);
        if (info == null) {
            throw new IOException("Read null json. Empty file?");
        }
        return new ChunkDeleter(info);
    }

    private boolean runDeleter() {
        return this.chunkDeletionInfo.batches.stream().allMatch(this::runBatch);
    }

    private boolean runBatch(ChunkDeletionInfo.ChunkBatch chunkBatch) {
        int chunkCount = chunkBatch.getChunkCount();
        LOGGER.debug("Processing deletion batch with {} chunks.", (Object)chunkCount);
        Map<Path, Stream<BlockVector2>> regionToChunkList = this.groupChunks(chunkBatch);
        BiPredicate<RegionAccess, BlockVector2> predicate = this.createPredicates(chunkBatch.deletionPredicates);
        this.shouldPreload = chunkBatch.chunks == null;
        this.deletionsRequested += chunkCount;
        this.debugRate = chunkCount / 10;
        return regionToChunkList.entrySet().stream().allMatch(entry -> {
            Path regionPath = (Path)entry.getKey();
            if (!Files.exists(regionPath, new LinkOption[0])) {
                return true;
            }
            if (chunkBatch.backup && !this.backedUpRegions.contains(regionPath)) {
                try {
                    this.backupRegion(regionPath);
                }
                catch (IOException e) {
                    LOGGER.warn("Error backing up region file: " + String.valueOf(regionPath) + ". Aborting the process.", (Throwable)e);
                    return false;
                }
            }
            return this.deleteChunks(regionPath, (Stream)entry.getValue(), predicate);
        });
    }

    private Map<Path, Stream<BlockVector2>> groupChunks(ChunkDeletionInfo.ChunkBatch chunkBatch) {
        Path worldPath = Paths.get(chunkBatch.worldPath, new String[0]);
        if (chunkBatch.chunks != null) {
            return chunkBatch.chunks.stream().collect(Collectors.groupingBy(RegionFilePos::new)).entrySet().stream().collect(Collectors.toMap(e -> worldPath.resolve("region").resolve(((RegionFilePos)e.getKey()).getFileName()), e -> ((List)e.getValue()).stream().sorted(chunkSorter)));
        }
        BlockVector2 minChunk = chunkBatch.minChunk;
        BlockVector2 maxChunk = chunkBatch.maxChunk;
        RegionFilePos minRegion = new RegionFilePos(minChunk);
        RegionFilePos maxRegion = new RegionFilePos(maxChunk);
        HashMap<Path, Stream<BlockVector2>> groupedChunks = new HashMap<Path, Stream<BlockVector2>>();
        for (int regX = minRegion.getX(); regX <= maxRegion.getX(); ++regX) {
            for (int regZ = minRegion.getZ(); regZ <= maxRegion.getZ(); ++regZ) {
                Path regionPath = worldPath.resolve("region").resolve(new RegionFilePos(regX, regZ).getFileName());
                if (!Files.exists(regionPath, new LinkOption[0])) continue;
                int startX = regX << 5;
                int endX = (regX << 5) + 31;
                int startZ = regZ << 5;
                int endZ = (regZ << 5) + 31;
                int minX = Math.max(Math.min(startX, endX), minChunk.x());
                int minZ = Math.max(Math.min(startZ, endZ), minChunk.z());
                int maxX = Math.min(Math.max(startX, endX), maxChunk.x());
                int maxZ = Math.min(Math.max(startZ, endZ), maxChunk.z());
                Stream<BlockVector2> stream = Stream.iterate(BlockVector2.at(minX, minZ), bv2 -> {
                    int nextX = bv2.x();
                    int nextZ = bv2.z();
                    if (++nextX > maxX) {
                        nextX = minX;
                        if (++nextZ > maxZ) {
                            return null;
                        }
                    }
                    return BlockVector2.at(nextX, nextZ);
                });
                groupedChunks.put(regionPath, stream);
            }
        }
        return groupedChunks;
    }

    private BiPredicate<RegionAccess, BlockVector2> createPredicates(List<ChunkDeletionInfo.DeletionPredicate> deletionPredicates) {
        if (deletionPredicates == null) {
            return (r, p) -> true;
        }
        return deletionPredicates.stream().map(this::createPredicate).reduce(BiPredicate::and).orElse((r, p) -> true);
    }

    private BiPredicate<RegionAccess, BlockVector2> createPredicate(ChunkDeletionInfo.DeletionPredicate deletionPredicate) {
        if ("modification".equals(deletionPredicate.property)) {
            int time;
            try {
                time = Integer.parseInt(deletionPredicate.value);
            }
            catch (NumberFormatException e) {
                throw new IllegalStateException("Modification time predicate specified invalid time: " + deletionPredicate.value);
            }
            return switch (deletionPredicate.comparison) {
                case "<" -> (r, p) -> {
                    try {
                        return r.getModificationTime((BlockVector2)((Object)p)) < time;
                    }
                    catch (IOException e) {
                        return false;
                    }
                };
                case ">" -> (r, p) -> {
                    try {
                        return r.getModificationTime((BlockVector2)((Object)p)) > time;
                    }
                    catch (IOException e) {
                        return false;
                    }
                };
                default -> throw new IllegalStateException("Unexpected comparison value: " + deletionPredicate.comparison);
            };
        }
        throw new IllegalStateException("Unexpected property value: " + deletionPredicate.property);
    }

    private void backupRegion(Path regionFile) throws IOException {
        Path backupFile = regionFile.resolveSibling(String.valueOf(regionFile.getFileName()) + ".bak");
        Files.copy(regionFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        this.backedUpRegions.add(backupFile);
    }

    private boolean deleteChunks(Path regionFile, Stream<BlockVector2> chunks, BiPredicate<RegionAccess, BlockVector2> deletionPredicate) {
        boolean bl;
        RegionAccess region = new RegionAccess(regionFile, this.shouldPreload);
        try {
            BlockVector2 chunk;
            Iterator iterator = chunks.iterator();
            while (iterator.hasNext() && (chunk = (BlockVector2)((Object)iterator.next())) != null) {
                if (deletionPredicate.test(region, chunk)) {
                    region.deleteChunk(chunk);
                    ++this.totalChunksDeleted;
                    if (this.debugRate == 0 || this.totalChunksDeleted % this.debugRate != 0) continue;
                    LOGGER.debug("Deleted {} chunks so far.", (Object)this.totalChunksDeleted);
                    continue;
                }
                LOGGER.debug("Chunk did not match predicates: " + String.valueOf((Object)chunk));
            }
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    region.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                LOGGER.warn("Error deleting chunks from region: " + String.valueOf(regionFile) + ". Aborting the process.", (Throwable)e);
                return false;
            }
        }
        region.close();
        return bl;
    }

    public int getDeletedChunkCount() {
        return this.totalChunksDeleted;
    }

    public int getDeletionsRequested() {
        return this.deletionsRequested;
    }

    private static class RegionFilePos {
        private final int x;
        private final int z;

        RegionFilePos(BlockVector2 chunk) {
            this.x = chunk.x() >> 5;
            this.z = chunk.z() >> 5;
        }

        RegionFilePos(int regX, int regZ) {
            this.x = regX;
            this.z = regZ;
        }

        public int getX() {
            return this.x;
        }

        public int getZ() {
            return this.z;
        }

        public String getFileName() {
            return "r." + this.x + "." + this.z + ".mca";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            RegionFilePos that = (RegionFilePos)o;
            if (this.x != that.x) {
                return false;
            }
            return this.z == that.z;
        }

        public int hashCode() {
            int result = this.x;
            result = 31 * result + this.z;
            return result;
        }

        public String toString() {
            return "(" + this.x + ", " + this.z + ")";
        }
    }

    private static class BlockVector2Adapter
    extends TypeAdapter<BlockVector2> {
        private BlockVector2Adapter() {
        }

        public void write(JsonWriter out, BlockVector2 value) throws IOException {
            out.beginArray();
            out.value((long)value.x());
            out.value((long)value.z());
            out.endArray();
        }

        public BlockVector2 read(JsonReader in) throws IOException {
            in.beginArray();
            int x = in.nextInt();
            int z = in.nextInt();
            in.endArray();
            return BlockVector2.at(x, z);
        }
    }
}

