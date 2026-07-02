/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.jnbt;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NamedTag;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinRootEntry;

@Deprecated
public final class NBTInputStream
implements Closeable {
    final DataInputStream is;

    public NBTInputStream(InputStream is) {
        this.is = new DataInputStream(is);
    }

    public NamedTag readNamedTag() throws IOException {
        LinRootEntry named = LinBinaryIO.readUsing(this.is, LinRootEntry::readFrom);
        return new NamedTag(named.name(), new CompoundTag(named.value()));
    }

    @Override
    public void close() throws IOException {
        this.is.close();
    }
}

