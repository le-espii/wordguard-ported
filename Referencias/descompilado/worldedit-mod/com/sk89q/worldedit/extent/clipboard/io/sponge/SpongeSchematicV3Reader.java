/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.ReaderUtil;
import com.sk89q.worldedit.extent.clipboard.io.sponge.VersionedDataFixer;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.internal.util.VarIntIterator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class SpongeSchematicV3Reader
implements ClipboardReader {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final LinStream rootStream;

    public SpongeSchematicV3Reader(LinStream rootStream) {
        this.rootStream = rootStream;
    }

    @Override
    public Clipboard read() throws IOException {
        LinCompoundTag schematicTag = this.getBaseTag();
        ReaderUtil.checkSchematicVersion(3, schematicTag);
        Platform platform = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        int liveDataVersion = platform.getDataVersion();
        VersionedDataFixer fixer = ReaderUtil.getVersionedDataFixer(schematicTag, platform, liveDataVersion);
        return this.readVersion3(schematicTag, fixer);
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            LinCompoundTag schematicTag = this.getBaseTag();
            ReaderUtil.checkSchematicVersion(3, schematicTag);
            int dataVersion = schematicTag.getTag("DataVersion", LinTagType.intTag()).valueAsInt();
            if (dataVersion < 0) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(dataVersion);
        }
        catch (IOException e) {
            return OptionalInt.empty();
        }
    }

    private LinCompoundTag getBaseTag() throws IOException {
        return LinRootEntry.readFrom(this.rootStream).value().getTag("Schematic", LinTagType.compoundTag());
    }

    private Clipboard readVersion3(LinCompoundTag schematicTag, VersionedDataFixer fixer) throws IOException {
        LinListTag<LinCompoundTag> entities;
        LinCompoundTag worldeditMeta;
        int width = schematicTag.getTag("Width", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        int height = schematicTag.getTag("Height", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        int length = schematicTag.getTag("Length", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        BlockVector3 offset = ReaderUtil.decodeBlockVector3(schematicTag.findTag("Offset", LinTagType.intArrayTag()));
        BlockVector3 origin = BlockVector3.ZERO;
        LinCompoundTag metadataTag = schematicTag.findTag("Metadata", LinTagType.compoundTag());
        if (metadataTag != null && (worldeditMeta = metadataTag.findTag("WorldEdit", LinTagType.compoundTag())) != null) {
            origin = ReaderUtil.decodeBlockVector3(worldeditMeta.findTag("Origin", LinTagType.intArrayTag()));
        }
        BlockVector3 min = offset.add(origin);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, min.add(width, height, length).subtract(BlockVector3.ONE)));
        clipboard.setOrigin(origin);
        this.decodeBlocksIntoClipboard(fixer, schematicTag, clipboard);
        LinCompoundTag biomeContainer = schematicTag.findTag("Biomes", LinTagType.compoundTag());
        if (biomeContainer != null) {
            this.readBiomes3(clipboard, biomeContainer, fixer);
        }
        if ((entities = schematicTag.findListTag("Entities", LinTagType.compoundTag())) != null) {
            ReaderUtil.readEntities(clipboard, (List<? extends LinCompoundTag>)entities.value(), fixer, true);
        }
        return clipboard;
    }

    private void decodeBlocksIntoClipboard(VersionedDataFixer fixer, LinCompoundTag schematic, BlockArrayClipboard clipboard) throws IOException {
        LinCompoundTag blockContainer = schematic.getTag("Blocks", LinTagType.compoundTag());
        LinCompoundTag paletteObject = blockContainer.getTag("Palette", LinTagType.compoundTag());
        Map<Integer, BlockState> palette = ReaderUtil.decodePalette(paletteObject, fixer);
        byte[] blocks = blockContainer.getTag("Data", LinTagType.byteArrayTag()).value();
        LinListTag<LinCompoundTag> blockEntities = blockContainer.findListTag("BlockEntities", LinTagType.compoundTag());
        ReaderUtil.initializeClipboardFromBlocks(clipboard, palette, blocks, blockEntities, fixer, true);
    }

    private void readBiomes3(BlockArrayClipboard clipboard, LinCompoundTag biomeContainer, VersionedDataFixer fixer) throws IOException {
        LinCompoundTag paletteTag = biomeContainer.getTag("Palette", LinTagType.compoundTag());
        Int2ObjectMap<BiomeType> palette = ReaderUtil.readBiomePalette(fixer, paletteTag, LOGGER);
        int width = clipboard.getRegion().getWidth();
        int length = clipboard.getRegion().getLength();
        byte[] biomes = biomeContainer.getTag("Data", LinTagType.byteArrayTag()).value();
        BlockVector3 min = clipboard.getMinimumPoint();
        int index = 0;
        VarIntIterator iter = new VarIntIterator(biomes);
        while (iter.hasNext()) {
            int nextBiomeId = iter.nextInt();
            BiomeType type = (BiomeType)palette.get(nextBiomeId);
            BlockVector3 pos = ReaderUtil.decodePositionFromDataIndex(width, length, index);
            clipboard.setBiome(min.add(pos), type);
            ++index;
        }
    }

    @Override
    public void close() throws IOException {
    }
}

