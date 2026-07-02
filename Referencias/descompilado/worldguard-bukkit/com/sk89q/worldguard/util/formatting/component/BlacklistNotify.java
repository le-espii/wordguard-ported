/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 */
package com.sk89q.worldguard.util.formatting.component;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.util.formatting.component.Notify;

public class BlacklistNotify
extends Notify {
    public BlacklistNotify(BlacklistEvent event, String comment) {
        super(event.getCauseName(), " (" + event.getDescription() + ") ");
        this.append((Component)TextComponent.of((String)(event.getTarget().getFriendlyName() + (String)(comment != null ? " (" + comment + ")" : "") + "."), (TextColor)TextColor.WHITE));
    }
}

