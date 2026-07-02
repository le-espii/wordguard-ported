/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.chunk;

import com.google.common.collect.ImmutableMap;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class AnvilChunk13
implements Chunk {
    private final LinCompoundTag rootTag;
    private final BlockState[][] blocks;
    private final int rootX;
    private final int rootZ;
    private Map<BlockVector3, LinCompoundTag> tileEntities;

    @Deprecated
    public AnvilChunk13(CompoundTag tag) throws DataException {
        this((LinCompoundTag)tag.toLinTag());
    }

    public AnvilChunk13(LinCompoundTag tag) throws DataException {
        this.rootTag = tag;
        this.rootX = this.rootTag.getTag("xPos", LinTagType.intTag()).valueAsInt();
        this.rootZ = this.rootTag.getTag("zPos", LinTagType.intTag()).valueAsInt();
        this.blocks = new BlockState[16][];
        LinListTag sections = this.rootTag.getTag("Sections", LinTagType.listTag());
        Iterator iterator = sections.value().iterator();
        while (iterator.hasNext()) {
            LinListTag<LinCompoundTag> paletteEntries;
            int paletteSize;
            byte y;
            LinCompoundTag sectionTag;
            LinByteTag sectionYTag;
            LinTag rawSectionTag = (LinTag)iterator.next();
            if (!(rawSectionTag instanceof LinCompoundTag) || (sectionYTag = (sectionTag = (LinCompoundTag)rawSectionTag).findTag("Y", LinTagType.byteTag())) == null || (y = sectionYTag.value().byteValue()) < 0 || y >= 16 || (paletteSize = (paletteEntries = sectionTag.getTag("Palette", LinTagType.listTag()).asTypeChecked(LinTagType.compoundTag())).value().size()) == 0) continue;
            BlockState[] palette = new BlockState[paletteSize];
            for (int paletteEntryId = 0; paletteEntryId < paletteSize; ++paletteEntryId) {
                LinCompoundTag paletteEntry = paletteEntries.get(paletteEntryId);
                String blockType = paletteEntry.getTag("Name", LinTagType.stringTag()).value();
                BlockType type = BlockTypes.get(blockType);
                if (type == null) {
                    throw new InvalidFormatException("Invalid block type: " + blockType);
                }
                BlockState blockState = type.getDefaultState();
                LinCompoundTag propertiesTag = paletteEntry.findTag("Properties", LinTagType.compoundTag());
                if (propertiesTag != null) {
                    for (Property<?> property : blockState.getStates().keySet()) {
                        LinStringTag propertyName = propertiesTag.findTag(property.getName(), LinTagType.stringTag());
                        if (propertyName == null) continue;
                        String value = propertyName.value();
                        try {
                            blockState = this.getBlockStateWith(blockState, property, value);
                        }
                        catch (IllegalArgumentException e) {
                            throw new InvalidFormatException("Invalid block state for " + blockState.getBlockType().id() + ", " + property.getName() + ": " + value);
                        }
                    }
                }
                palette[paletteEntryId] = blockState;
            }
            long[] blockStatesSerialized = sectionTag.getTag("BlockStates", LinTagType.longArrayTag()).value();
            BlockState[] chunkSectionBlocks = new BlockState[4096];
            this.blocks[y] = chunkSectionBlocks;
            this.readBlockStates(palette, blockStatesSerialized, chunkSectionBlocks);
        }
    }

    protected void readBlockStates(BlockState[] palette, long[] blockStatesSerialized, BlockState[] chunkSectionBlocks) throws InvalidFormatException {
        int paletteBits = 4;
        while (1 << paletteBits < palette.length) {
            ++paletteBits;
        }
        int paletteMask = (1 << paletteBits) - 1;
        long currentSerializedValue = 0L;
        int nextSerializedItem = 0;
        int remainingBits = 0;
        for (int blockPos = 0; blockPos < chunkSectionBlocks.length; ++blockPos) {
            int localBlockId;
            if (remainingBits < paletteBits) {
                int bitsNextLong = paletteBits - remainingBits;
                localBlockId = (int)currentSerializedValue;
                if (nextSerializedItem >= blockStatesSerialized.length) {
                    throw new InvalidFormatException("Too short block state table");
                }
                currentSerializedValue = blockStatesSerialized[nextSerializedItem++];
                localBlockId |= (int)(currentSerializedValue & (1L << bitsNextLong) - 1L) << remainingBits;
                currentSerializedValue >>>= bitsNextLong;
                remainingBits = 64 - bitsNextLong;
            } else {
                localBlockId = (int)(currentSerializedValue & (long)paletteMask);
                currentSerializedValue >>>= paletteBits;
                remainingBits -= paletteBits;
            }
            if (localBlockId >= palette.length) {
                throw new InvalidFormatException("Invalid block state table entry: " + localBlockId);
            }
            chunkSectionBlocks[blockPos] = palette[localBlockId];
        }
    }

    private <T> BlockState getBlockStateWith(BlockState source, Property<T> property, String value) {
        return source.with(property, property.getValueFor(value));
    }

    private Map<BlockVector3, LinCompoundTag> populateTileEntities() {
        LinListTag<LinCompoundTag> tags = this.rootTag.findListTag("TileEntities", LinTagType.compoundTag());
        if (tags == null) {
            return ImmutableMap.of();
        }
        ImmutableMap.Builder tileEntities = ImmutableMap.builderWithExpectedSize((int)tags.value().size());
        Iterator iterator = tags.value().iterator();
        while (iterator.hasNext()) {
            LinCompoundTag tag = (LinCompoundTag)iterator.next();
            int x = tag.getTag("x", LinTagType.intTag()).valueAsInt();
            int y = tag.getTag("y", LinTagType.intTag()).valueAsInt();
            int z = tag.getTag("z", LinTagType.intTag()).valueAsInt();
            BlockVector3 vec = BlockVector3.at(x, y, z);
            tileEntities.put((Object)vec, (Object)tag);
        }
        return tileEntities.build();
    }

    @Nullable
    private LinCompoundTag getBlockTileEntity(BlockVector3 position) {
        if (this.tileEntities == null) {
            this.tileEntities = this.populateTileEntities();
        }
        return this.tileEntities.get((Object)position);
    }

    @Override
    public BaseBlock getBlock(BlockVector3 position) throws DataException {
        int x = position.x() - this.rootX * 16;
        int y = position.y();
        int z = position.z() - this.rootZ * 16;
        int section = y >> 4;
        int yIndex = y & 0xF;
        if (section < 0 || section >= this.blocks.length) {
            throw new DataException("Chunk does not contain position " + String.valueOf((Object)position));
        }
        BlockState[] sectionBlocks = this.blocks[section];
        BlockState state = sectionBlocks != null ? sectionBlocks[yIndex << 8 | z << 4 | x] : BlockTypes.AIR.getDefaultState();
        LinCompoundTag tileEntity = this.getBlockTileEntity(position);
        return state.toBaseBlock(tileEntity);
    }
}

