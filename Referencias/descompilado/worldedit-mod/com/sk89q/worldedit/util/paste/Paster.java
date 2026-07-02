/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.paste;

import com.sk89q.worldedit.util.paste.PasteMetadata;
import java.net.URL;
import java.util.concurrent.Callable;

public interface Paster {
    default public Callable<URL> paste(String content) {
        return this.paste(content, new PasteMetadata());
    }

    public Callable<URL> paste(String var1, PasteMetadata var2);
}

