/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.LinStreamable;
import org.enginehub.linbus.stream.internal.SurroundingLinStream;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.ToLinTag;
import org.enginehub.linbus.tree.impl.LinTagReader;

public record LinRootEntry(String name, LinCompoundTag value) implements ToLinTag<LinCompoundTag>,
LinStreamable
{
    public LinRootEntry {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
    }

    public static LinRootEntry readFrom(LinStream tokens) throws IOException {
        return LinTagReader.readRoot(tokens);
    }

    public byte[] writeToArray() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(output);){
            LinBinaryIO.write(dataOutputStream, this);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return output.toByteArray();
    }

    @Override
    public LinCompoundTag toLinTag() {
        return LinCompoundTag.of(Map.of((Object)this.name, (Object)this.value));
    }

    @Override
    public LinStream linStream() {
        return new SurroundingLinStream(new LinToken.Name(this.name, LinTagId.COMPOUND), this.value.linStream(), null);
    }
}

