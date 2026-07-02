/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extension.platform.Actor
 */
package com.sk89q.worldguard.util;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.CommandUtils;

public final class MessagingUtil {
    private MessagingUtil() {
    }

    public static void sendStringToChat(LocalPlayer player, String message) {
        String effective = CommandUtils.replaceColorMacros(message);
        effective = WorldGuard.getInstance().getPlatform().getMatcher().replaceMacros((Actor)player, effective);
        for (String mess : effective.replaceAll("\\\\n", "\n").split("\\n")) {
            player.printRaw(mess);
        }
    }

    public static void sendStringToTitle(LocalPlayer player, String message) {
        String[] parts = message.replaceAll("\\\\n", "\n").split("\\n", 2);
        String title = CommandUtils.replaceColorMacros(parts[0]);
        title = WorldGuard.getInstance().getPlatform().getMatcher().replaceMacros((Actor)player, title);
        if (parts.length > 1) {
            String subtitle = CommandUtils.replaceColorMacros(parts[1]);
            subtitle = WorldGuard.getInstance().getPlatform().getMatcher().replaceMacros((Actor)player, subtitle);
            player.sendTitle(title, subtitle);
        } else {
            player.sendTitle(title, null);
        }
    }
}

