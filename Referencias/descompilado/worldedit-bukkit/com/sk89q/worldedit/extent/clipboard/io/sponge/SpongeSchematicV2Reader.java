/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectMap;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.ReaderUtil;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV1Reader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.VersionedDataFixer;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.internal.util.VarIntIterator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class SpongeSchematicV2Reader
implements ClipboardReader {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final LinStream rootStream;

    public SpongeSchematicV2Reader(LinStream rootStream) {
        this.rootStream = rootStream;
    }

    @Override
    public Clipboard read() throws IOException {
        LinCompoundTag schematicTag = this.getBaseTag();
        ReaderUtil.checkSchematicVersion(2, schematicTag);
        return SpongeSchematicV2Reader.doRead(schematicTag);
    }

    public static Clipboard doRead(LinCompoundTag schematicTag) throws IOException {
        Platform platform = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        int liveDataVersion = platform.getDataVersion();
        VersionedDataFixer fixer = ReaderUtil.getVersionedDataFixer(schematicTag, platform, liveDataVersion);
        BlockArrayClipboard clip = SpongeSchematicV1Reader.readVersion1(schematicTag, fixer);
        return SpongeSchematicV2Reader.readVersion2(clip, schematicTag, fixer);
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            LinCompoundTag schematicTag = this.getBaseTag();
            ReaderUtil.checkSchematicVersion(2, schematicTag);
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
        return LinRootEntry.readFrom(this.rootStream).value();
    }

    private static Clipboard readVersion2(BlockArrayClipboard version1, LinCompoundTag schematicTag, VersionedDataFixer fixer) throws IOException {
        LinListTag<LinCompoundTag> entities;
        if (schematicTag.value().containsKey("BiomeData")) {
            SpongeSchematicV2Reader.readBiomes2(version1, schematicTag, fixer);
        }
        if ((entities = schematicTag.findListTag("Entities", LinTagType.compoundTag())) != null) {
            ReaderUtil.readEntities(version1, (List<? extends LinCompoundTag>)entities.value(), fixer, false);
        }
        return version1;
    }

    private static void readBiomes2(BlockArrayClipboard clipboard, LinCompoundTag schematic, VersionedDataFixer fixer) throws IOException {
        LinByteArrayTag dataTag = schematic.getTag("BiomeData", LinTagType.byteArrayTag());
        LinIntTag maxTag = schematic.getTag("BiomePaletteMax", LinTagType.intTag());
        LinCompoundTag paletteTag = schematic.getTag("BiomePalette", LinTagType.compoundTag());
        if (maxTag.valueAsInt() != paletteTag.value().size()) {
            throw new IOException("Biome palette size does not match expected size.");
        }
        Int2ObjectMap<BiomeType> palette = ReaderUtil.readBiomePalette(fixer, paletteTag, LOGGER);
        int width = clipboard.getDimensions().x();
        byte[] biomes = dataTag.value();
        BlockVector3 min = clipboard.getMinimumPoint();
        int index = 0;
        VarIntIterator iter = new VarIntIterator(biomes);
        while (iter.hasNext()) {
            int nextBiomeId = iter.nextInt();
            BiomeType type = (BiomeType)palette.get(nextBiomeId);
            BlockVector3 hackDecode = ReaderUtil.decodePositionFromDataIndex(width, 1, index);
            int x = hackDecode.x();
            int z = hackDecode.y();
            for (int y = 0; y < clipboard.getRegion().getHeight(); ++y) {
                clipboard.setBiome(min.add(x, y, z), type);
            }
            ++index;
        }
    }

    @Override
    public void close() throws IOException {
    }
}

