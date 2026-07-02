/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.Tag;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinRootEntry;

@Deprecated
public final class NBTOutputStream
implements Closeable {
    final DataOutputStream os;

    public NBTOutputStream(OutputStream os) {
        this.os = new DataOutputStream(os);
    }

    public void writeNamedTag(String name, Tag<?, ?> tag) throws IOException {
        LinBinaryIO.write(this.os, new LinRootEntry(name, (LinCompoundTag)tag.toLinTag()));
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }
}

