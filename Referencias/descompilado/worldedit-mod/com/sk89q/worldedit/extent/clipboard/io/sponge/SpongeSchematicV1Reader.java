/*
 * Decompiled with CFR 0.152.
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
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockState;
import java.io.IOException;
import java.util.Map;
import java.util.OptionalInt;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class SpongeSchematicV1Reader
implements ClipboardReader {
    private final LinStream rootStream;

    public SpongeSchematicV1Reader(LinStream rootStream) {
        this.rootStream = rootStream;
    }

    @Override
    public Clipboard read() throws IOException {
        LinCompoundTag schematicTag = this.getBaseTag();
        ReaderUtil.checkSchematicVersion(1, schematicTag);
        return SpongeSchematicV1Reader.doRead(schematicTag);
    }

    public static BlockArrayClipboard doRead(LinCompoundTag schematicTag) throws IOException {
        Platform platform = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        VersionedDataFixer fixer = new VersionedDataFixer(1631, platform.getDataFixer());
        return SpongeSchematicV1Reader.readVersion1(schematicTag, fixer);
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            ReaderUtil.checkSchematicVersion(1, this.getBaseTag());
            return OptionalInt.of(1631);
        }
        catch (IOException e) {
            return OptionalInt.empty();
        }
    }

    private LinCompoundTag getBaseTag() throws IOException {
        return LinRootEntry.readFrom(this.rootStream).value();
    }

    static BlockArrayClipboard readVersion1(LinCompoundTag schematicTag, VersionedDataFixer fixer) throws IOException {
        LinIntTag offsetX;
        int width = schematicTag.getTag("Width", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        int height = schematicTag.getTag("Height", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        int length = schematicTag.getTag("Length", LinTagType.shortTag()).valueAsShort() & 0xFFFF;
        BlockVector3 min = ReaderUtil.decodeBlockVector3(schematicTag.findTag("Offset", LinTagType.intArrayTag()));
        BlockVector3 offset = BlockVector3.ZERO;
        LinCompoundTag metadataTag = schematicTag.findTag("Metadata", LinTagType.compoundTag());
        if (metadataTag != null && (offsetX = metadataTag.findTag("WEOffsetX", LinTagType.intTag())) != null) {
            int offsetY = metadataTag.getTag("WEOffsetY", LinTagType.intTag()).valueAsInt();
            int offsetZ = metadataTag.getTag("WEOffsetZ", LinTagType.intTag()).valueAsInt();
            offset = BlockVector3.at(offsetX.valueAsInt(), offsetY, offsetZ);
        }
        BlockVector3 origin = min.subtract(offset);
        CuboidRegion region = new CuboidRegion(min, min.add(width, height, length).subtract(BlockVector3.ONE));
        LinIntTag paletteMaxTag = schematicTag.findTag("PaletteMax", LinTagType.intTag());
        LinCompoundTag paletteObject = schematicTag.getTag("Palette", LinTagType.compoundTag());
        if (paletteMaxTag != null && paletteObject.value().size() != paletteMaxTag.valueAsInt()) {
            throw new IOException("Block palette size does not match expected size.");
        }
        Map<Integer, BlockState> palette = ReaderUtil.decodePalette(paletteObject, fixer);
        byte[] blocks = schematicTag.getTag("BlockData", LinTagType.byteArrayTag()).value();
        LinListTag<LinCompoundTag> tileEntities = schematicTag.findListTag("BlockEntities", LinTagType.compoundTag());
        if (tileEntities == null) {
            tileEntities = schematicTag.findListTag("TileEntities", LinTagType.compoundTag());
        }
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(origin);
        ReaderUtil.initializeClipboardFromBlocks(clipboard, palette, blocks, tileEntities, fixer, false);
        return clipboard;
    }

    @Override
    public void close() throws IOException {
    }
}

