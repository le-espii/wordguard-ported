/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.anvil;

import com.sk89q.worldedit.math.BlockVector2;
import java.util.List;

public class ChunkDeletionInfo {
    public List<ChunkBatch> batches;

    public static class DeletionPredicate {
        public String property;
        public String comparison;
        public String value;
    }

    public static class ChunkBatch {
        public String worldPath;
        public boolean backup;
        public List<DeletionPredicate> deletionPredicates;
        public List<BlockVector2> chunks;
        public BlockVector2 minChunk;
        public BlockVector2 maxChunk;

        public int getChunkCount() {
            if (this.chunks != null) {
                return this.chunks.size();
            }
            BlockVector2 dist = this.maxChunk.subtract(this.minChunk).add(1, 1);
            return dist.x() * dist.z();
        }
    }
}

