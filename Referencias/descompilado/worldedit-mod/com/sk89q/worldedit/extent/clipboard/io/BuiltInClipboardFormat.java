/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.MCEditSchematicReader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV1Reader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV2Reader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV2Writer;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV3Reader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV3Writer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.stream.LinReadOptions;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public enum BuiltInClipboardFormat implements ClipboardFormat
{
    MCEDIT_SCHEMATIC(new String[]{"mcedit", "mce", "schematic"}){

        @Override
        public String getPrimaryFileExtension() {
            return "schematic";
        }

        @Override
        public ClipboardReader getReader(InputStream inputStream) throws IOException {
            return new MCEditSchematicReader(LinBinaryIO.read(new DataInputStream(new GZIPInputStream(inputStream)), LEGACY_OPTIONS));
        }

        @Override
        public ClipboardWriter getWriter(OutputStream outputStream) throws IOException {
            throw new IOException("This format does not support saving");
        }

        @Override
        public boolean isFormat(InputStream inputStream) {
            LinRootEntry rootEntry;
            try {
                DataInputStream stream = new DataInputStream(new GZIPInputStream(inputStream));
                rootEntry = LinBinaryIO.readUsing(stream, LEGACY_OPTIONS, LinRootEntry::readFrom);
            }
            catch (Exception e) {
                return false;
            }
            if (!rootEntry.name().equals("Schematic")) {
                return false;
            }
            return rootEntry.value().value().containsKey("Materials");
        }
    }
    ,
    SPONGE_V1_SCHEMATIC(new String[]{"sponge.1"}){

        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public ClipboardReader getReader(InputStream inputStream) throws IOException {
            return new SpongeSchematicV1Reader(LinBinaryIO.read(new DataInputStream(new GZIPInputStream(inputStream)), LEGACY_OPTIONS));
        }

        @Override
        public ClipboardWriter getWriter(OutputStream outputStream) throws IOException {
            throw new IOException("This format does not support saving");
        }

        @Override
        public boolean isFormat(InputStream inputStream) {
            return BuiltInClipboardFormat.detectOldSpongeSchematic(inputStream, 1);
        }
    }
    ,
    SPONGE_V2_SCHEMATIC(new String[]{"sponge.2"}){

        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public ClipboardReader getReader(InputStream inputStream) throws IOException {
            return new SpongeSchematicV2Reader(LinBinaryIO.read(new DataInputStream(new GZIPInputStream(inputStream)), LEGACY_OPTIONS));
        }

        @Override
        public ClipboardWriter getWriter(OutputStream outputStream) throws IOException {
            return new SpongeSchematicV2Writer(new DataOutputStream(new GZIPOutputStream(outputStream)));
        }

        @Override
        public boolean isFormat(InputStream inputStream) {
            return BuiltInClipboardFormat.detectOldSpongeSchematic(inputStream, 2);
        }
    }
    ,
    SPONGE_V3_SCHEMATIC(new String[]{"sponge.3", "sponge", "schem"}){

        @Override
        public String getPrimaryFileExtension() {
            return "schem";
        }

        @Override
        public ClipboardReader getReader(InputStream inputStream) throws IOException {
            return new SpongeSchematicV3Reader(LinBinaryIO.read(new DataInputStream(new GZIPInputStream(inputStream))));
        }

        @Override
        public ClipboardWriter getWriter(OutputStream outputStream) throws IOException {
            return new SpongeSchematicV3Writer(new DataOutputStream(new GZIPOutputStream(outputStream)));
        }

        @Override
        public boolean isFormat(InputStream inputStream) {
            LinCompoundTag root;
            try {
                DataInputStream stream = new DataInputStream(new GZIPInputStream(inputStream));
                root = LinBinaryIO.readUsing(stream, LinRootEntry::readFrom).value();
            }
            catch (Exception e) {
                return false;
            }
            LinCompoundTag schematicTag = root.findTag("Schematic", LinTagType.compoundTag());
            if (schematicTag == null) {
                return false;
            }
            LinIntTag versionTag = schematicTag.findTag("Version", LinTagType.intTag());
            if (versionTag == null) {
                return false;
            }
            return versionTag.valueAsInt() == 3;
        }
    };

    @Deprecated
    public static final BuiltInClipboardFormat SPONGE_SCHEMATIC;
    private static final LinReadOptions LEGACY_OPTIONS;
    private final ImmutableSet<String> aliases;

    private static boolean detectOldSpongeSchematic(InputStream inputStream, int version) {
        LinRootEntry rootEntry;
        try {
            DataInputStream stream = new DataInputStream(new GZIPInputStream(inputStream));
            rootEntry = LinBinaryIO.readUsing(stream, LEGACY_OPTIONS, LinRootEntry::readFrom);
        }
        catch (Exception e) {
            return false;
        }
        if (!rootEntry.name().equals("Schematic")) {
            return false;
        }
        LinCompoundTag schematicTag = rootEntry.value();
        LinIntTag versionTag = schematicTag.findTag("Version", LinTagType.intTag());
        if (versionTag == null) {
            return false;
        }
        return versionTag.valueAsInt() == version;
    }

    private BuiltInClipboardFormat(String ... aliases) {
        this.aliases = ImmutableSet.copyOf((Object[])aliases);
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public Set<String> getAliases() {
        return this.aliases;
    }

    @Override
    public Set<String> getFileExtensions() {
        return ImmutableSet.of((Object)this.getPrimaryFileExtension());
    }

    static {
        SPONGE_SCHEMATIC = SPONGE_V2_SCHEMATIC;
        LEGACY_OPTIONS = LinReadOptions.builder().allowNormalUtf8Encoding(true).build();
    }
}

