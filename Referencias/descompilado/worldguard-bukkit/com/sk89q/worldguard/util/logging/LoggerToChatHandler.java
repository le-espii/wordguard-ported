/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.formatting.component.SubtleFormat
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 */
package com.sk89q.worldguard.util.logging;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerToChatHandler
extends Handler {
    private final Formatter formatter = new Formatter(this){

        @Override
        public String format(LogRecord record) {
            return this.formatMessage(record);
        }
    };
    private Actor player;

    public LoggerToChatHandler(Actor player) {
        this.player = player;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {
        this.player.print(SubtleFormat.wrap((String[])new String[]{record.getLevel().getName() + ": "}).append((Component)TextComponent.of((String)this.formatter.format(record), (TextColor)TextColor.WHITE)));
    }
}

