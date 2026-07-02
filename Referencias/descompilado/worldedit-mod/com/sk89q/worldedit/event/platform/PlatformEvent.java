/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.event.platform;

import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.extension.platform.Platform;

public abstract class PlatformEvent
extends Event {
    private final Platform platform;

    protected PlatformEvent(Platform platform) {
        this.platform = platform;
    }

    public Platform getPlatform() {
        return this.platform;
    }
}

