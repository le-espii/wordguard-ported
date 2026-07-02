/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.session;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Identity;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.PasteBuilder;

public class ClipboardHolder {
    private final Clipboard clipboard;
    private Transform transform = new Identity();

    public ClipboardHolder(Clipboard clipboard) {
        Preconditions.checkNotNull((Object)clipboard);
        this.clipboard = clipboard;
    }

    public Clipboard getClipboard() {
        return this.clipboard;
    }

    public void setTransform(Transform transform) {
        Preconditions.checkNotNull((Object)transform);
        this.transform = transform;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public PasteBuilder createPaste(Extent targetExtent) {
        return new PasteBuilder(this, targetExtent);
    }
}

