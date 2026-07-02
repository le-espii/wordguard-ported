/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Set;

public interface ClipboardFormat {
    public String getName();

    public Set<String> getAliases();

    public ClipboardReader getReader(InputStream var1) throws IOException;

    public ClipboardWriter getWriter(OutputStream var1) throws IOException;

    default public boolean isFormat(File file) {
        boolean bl;
        block8: {
            InputStream stream = Files.newInputStream(file.toPath(), new OpenOption[0]);
            try {
                bl = this.isFormat(stream);
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return false;
                }
            }
            stream.close();
        }
        return bl;
    }

    default public boolean isFormat(InputStream inputStream) {
        return false;
    }

    public String getPrimaryFileExtension();

    public Set<String> getFileExtensions();
}

