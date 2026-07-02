/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.NBTSchematicReader;
import com.sk89q.worldedit.extent.clipboard.io.SchematicLoadException;
import com.sk89q.worldedit.extent.clipboard.io.sponge.ReaderUtil;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV1Reader;
import com.sk89q.worldedit.extent.clipboard.io.sponge.SpongeSchematicV2Reader;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.io.IOException;
import java.util.OptionalInt;
import org.enginehub.linbus.tree.LinCompoundTag;

@Deprecated
public class SpongeSchematicReader
extends NBTSchematicReader {
    private final NBTInputStream inputStream;

    public SpongeSchematicReader(NBTInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private CompoundTag getBaseTag() throws IOException {
        NamedTag rootTag = this.inputStream.readNamedTag();
        return (CompoundTag)rootTag.getTag();
    }

    @Override
    public Clipboard read() throws IOException {
        CompoundTag schematicTag = this.getBaseTag();
        int version = ReaderUtil.getSchematicVersion((LinCompoundTag)schematicTag.toLinTag());
        return switch (version) {
            case 1 -> SpongeSchematicV1Reader.doRead((LinCompoundTag)schematicTag.toLinTag());
            case 2 -> SpongeSchematicV2Reader.doRead((LinCompoundTag)schematicTag.toLinTag());
            default -> throw new SchematicLoadException(TranslatableComponent.of("worldedit.schematic.load.unsupported-version", TextComponent.of(version)));
        };
    }

    @Override
    public OptionalInt getDataVersion() {
        try {
            CompoundTag schematicTag = this.getBaseTag();
            int version = ReaderUtil.getSchematicVersion((LinCompoundTag)schematicTag.toLinTag());
            return switch (version) {
                case 1 -> OptionalInt.of(1631);
                case 2 -> {
                    int dataVersion = SpongeSchematicReader.requireTag(schematicTag.getValue(), "DataVersion", IntTag.class).getValue();
                    if (dataVersion < 0) {
                        yield OptionalInt.empty();
                    }
                    yield OptionalInt.of(dataVersion);
                }
                default -> OptionalInt.empty();
            };
        }
        catch (IOException e) {
            return OptionalInt.empty();
        }
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}

