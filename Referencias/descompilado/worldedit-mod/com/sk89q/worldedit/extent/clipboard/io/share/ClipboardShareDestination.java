/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.share;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareMetadata;
import com.sk89q.worldedit.extent.clipboard.io.share.ShareOutputProvider;
import java.util.Set;
import java.util.function.Consumer;

public interface ClipboardShareDestination {
    public String getName();

    public Set<String> getAliases();

    public Consumer<Actor> share(ClipboardShareMetadata var1, ShareOutputProvider var2) throws Exception;

    public ClipboardFormat getDefaultFormat();

    public boolean supportsFormat(ClipboardFormat var1);
}

