/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.sk89q.worldedit.internal.util;

import com.google.common.base.Throwables;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;

public class ErrorReporting {
    public static void trigger(Actor actor, Throwable error) {
        actor.printError(TranslatableComponent.of("worldedit.command.error.report"));
        TextComponent.Builder errorBuilder = TextComponent.builder(error.getClass().getName() + ": " + error.getMessage());
        if (actor.hasPermission("worldedit.error.detailed")) {
            errorBuilder = (TextComponent.Builder)errorBuilder.hoverEvent(HoverEvent.showText(TextComponent.of(Throwables.getStackTraceAsString((Throwable)error))));
        }
        actor.print((Component)errorBuilder.build());
    }

    private ErrorReporting() {
    }
}

