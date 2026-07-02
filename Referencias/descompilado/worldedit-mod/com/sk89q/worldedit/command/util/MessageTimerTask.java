/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.TimerTask;
import javax.annotation.Nullable;

public class MessageTimerTask
extends TimerTask {
    private final Actor sender;
    private final Component message;
    @Nullable
    private final Component workingMessage;
    private boolean hasRunBefore = false;

    @Deprecated
    MessageTimerTask(Actor sender, String message) {
        this(sender, TextComponent.of(message), null);
    }

    MessageTimerTask(Actor sender, Component message, @Nullable Component workingMessage) {
        Preconditions.checkNotNull((Object)sender);
        Preconditions.checkNotNull((Object)message);
        this.sender = sender;
        this.message = message;
        this.workingMessage = workingMessage;
    }

    @Override
    public void run() {
        if (!this.hasRunBefore) {
            this.sender.printDebug(this.message);
            this.hasRunBefore = true;
        } else if (this.workingMessage != null) {
            this.sender.printDebug(this.workingMessage);
        } else {
            this.cancel();
        }
    }
}

