/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.snapshot.experimental;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.MissingChunkException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SnapshotRestore {
    private final Map<BlockVector2, ArrayList<BlockVector3>> neededChunks = new LinkedHashMap<BlockVector2, ArrayList<BlockVector3>>();
    private final Snapshot snapshot;
    private final EditSession editSession;
    private ArrayList<BlockVector2> missingChunks;
    private ArrayList<BlockVector2> errorChunks;
    private String lastErrorMessage;

    public SnapshotRestore(Snapshot snapshot, EditSession editSession, Region region) {
        this.snapshot = snapshot;
        this.editSession = editSession;
        if (region instanceof CuboidRegion) {
            this.findNeededCuboidChunks(region);
        } else {
            this.findNeededChunks(region);
        }
    }

    private void findNeededCuboidChunks(Region region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        for (int x = min.x(); x <= max.x(); ++x) {
            for (int y = min.y(); y <= max.y(); ++y) {
                for (int z = min.z(); z <= max.z(); ++z) {
                    BlockVector3 pos = BlockVector3.at(x, y, z);
                    this.checkAndAddBlock(pos);
                }
            }
        }
    }

    private void findNeededChunks(Region region) {
        for (BlockVector3 pos : region) {
            this.checkAndAddBlock(pos);
        }
    }

    private void checkAndAddBlock(BlockVector3 pos) {
        if (this.editSession.getMask() != null && !this.editSession.getMask().test(pos)) {
            return;
        }
        BlockVector2 chunkPos = ChunkStore.toChunk(pos);
        if (!this.neededChunks.containsKey((Object)chunkPos)) {
            this.neededChunks.put(chunkPos, new ArrayList());
        }
        this.neededChunks.get((Object)chunkPos).add(pos);
    }

    public int getChunksAffected() {
        return this.neededChunks.size();
    }

    public void restore() throws MaxChangedBlocksException {
        this.missingChunks = new ArrayList();
        this.errorChunks = new ArrayList();
        for (Map.Entry<BlockVector2, ArrayList<BlockVector3>> entry : this.neededChunks.entrySet()) {
            BlockVector2 chunkPos = entry.getKey();
            try {
                Chunk chunk = this.snapshot.getChunk(chunkPos.toBlockVector3());
                for (BlockVector3 pos : entry.getValue()) {
                    try {
                        this.editSession.setBlock(pos, chunk.getBlock(pos));
                    }
                    catch (DataException dataException) {}
                }
            }
            catch (MissingChunkException me) {
                this.missingChunks.add(chunkPos);
            }
            catch (DataException | IOException me) {
                this.errorChunks.add(chunkPos);
                this.lastErrorMessage = me.getMessage();
            }
        }
    }

    public List<BlockVector2> getMissingChunks() {
        return this.missingChunks;
    }

    public List<BlockVector2> getErrorChunks() {
        return this.errorChunks;
    }

    public boolean hadTotalFailure() {
        return this.missingChunks.size() + this.errorChunks.size() == this.getChunksAffected();
    }

    public String getLastErrorMessage() {
        return this.lastErrorMessage;
    }
}

