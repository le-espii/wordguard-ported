/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.internal.block;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectOpenHashMap;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.BitSet;
import javax.annotation.Nullable;

public final class BlockStateIdAccess {
    private static final int INVALID_ID = -1;
    private static final int EXPECTED_BLOCK_COUNT = 32768;
    private static final Int2ObjectOpenHashMap<BlockState> TO_STATE = new Int2ObjectOpenHashMap(32768);
    private static BlockStateInternalId blockStateInternalId;
    private static final BitSet usedIds;

    public static void setBlockStateInternalId(BlockStateInternalId blockStateInternalId) {
        BlockStateIdAccess.blockStateInternalId = blockStateInternalId;
    }

    public static int invalidId() {
        return -1;
    }

    public static boolean isValidInternalId(int internalId) {
        return internalId != -1;
    }

    public static int getBlockStateId(BlockState holder) {
        return blockStateInternalId.getInternalId(holder);
    }

    @Nullable
    public static BlockState getBlockStateById(int id) {
        return TO_STATE.get(id);
    }

    private static int provideUnusedWorldEditId() {
        return usedIds.nextClearBit(0);
    }

    public static void register(BlockState blockState, int id) {
        int i = BlockStateIdAccess.isValidInternalId(id) ? id : BlockStateIdAccess.provideUnusedWorldEditId();
        BlockState existing = BlockStateIdAccess.getBlockStateById(id);
        Preconditions.checkState((existing == null || existing == blockState ? 1 : 0) != 0, (String)"BlockState %s is using the same block ID (%s) as BlockState %s", (Object)blockState, (Object)i, (Object)existing);
        blockStateInternalId.setInternalId(blockState, i);
        TO_STATE.put(i, blockState);
        usedIds.set(i);
    }

    public static void clear() {
        for (BlockState value : TO_STATE.values()) {
            blockStateInternalId.setInternalId(value, BlockStateIdAccess.invalidId());
        }
        TO_STATE.clear();
        usedIds.clear();
    }

    private BlockStateIdAccess() {
    }

    static {
        TO_STATE.defaultReturnValue(null);
        usedIds = new BitSet();
    }

    public static interface BlockStateInternalId {
        public int getInternalId(BlockState var1);

        public void setInternalId(BlockState var1, int var2);
    }
}

