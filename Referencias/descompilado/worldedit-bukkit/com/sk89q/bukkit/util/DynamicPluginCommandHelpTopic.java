/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.help.HelpTopic
 *  org.bukkit.help.HelpTopicFactory
 */
package com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.CommandInspector;
import com.sk89q.bukkit.util.DynamicPluginCommand;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.wepif.WEPIFRuntimeException;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

public class DynamicPluginCommandHelpTopic
extends HelpTopic {
    private final DynamicPluginCommand cmd;

    public DynamicPluginCommandHelpTopic(DynamicPluginCommand cmd) {
        this.cmd = cmd;
        this.name = "/" + cmd.getName();
        if (cmd.getRegisteredWith() instanceof CommandInspector) {
            CommandInspector resolver = (CommandInspector)cmd.getRegisteredWith();
            this.shortText = resolver.getShortText(cmd);
            this.fullText = resolver.getFullText(cmd);
        } else {
            Object fullTextTemp = null;
            StringBuilder fullText = new StringBuilder();
            if (cmd.getRegisteredWith() instanceof CommandsManager) {
                String lookupName;
                Map<String, String> helpText = ((CommandsManager)cmd.getRegisteredWith()).getHelpMessages();
                if (helpText.containsKey(lookupName = cmd.getName().replaceAll("/", ""))) {
                    fullTextTemp = helpText.get(lookupName);
                }
                if ((helpText = ((CommandsManager)cmd.getRegisteredWith()).getCommands()).containsKey(cmd.getName())) {
                    String shortText = helpText.get(cmd.getName());
                    if (fullTextTemp == null) {
                        fullTextTemp = this.name + " " + shortText;
                    }
                    this.shortText = shortText;
                }
            } else {
                this.shortText = cmd.getDescription();
            }
            String[] split = fullTextTemp == null ? new String[2] : ((String)fullTextTemp).split("\n", 2);
            fullText.append(ChatColor.BOLD).append(ChatColor.GOLD).append("Usage: ").append(ChatColor.WHITE);
            fullText.append(split[0]).append("\n");
            if (!cmd.getAliases().isEmpty()) {
                fullText.append(ChatColor.BOLD).append(ChatColor.GOLD).append("Aliases: ").append(ChatColor.WHITE);
                boolean first = true;
                for (String alias : cmd.getAliases()) {
                    if (!first) {
                        fullText.append(", ");
                    }
                    fullText.append(alias);
                    first = false;
                }
                fullText.append("\n");
            }
            if (split.length > 1) {
                fullText.append(split[1]);
            }
            this.fullText = fullText.toString();
        }
    }

    public boolean canSee(CommandSender player) {
        if (this.cmd.getRegisteredWith() instanceof CommandInspector) {
            CommandInspector resolver = (CommandInspector)this.cmd.getRegisteredWith();
            return resolver.testPermission(player, this.cmd);
        }
        if (this.cmd.getPermissions() != null && this.cmd.getPermissions().length > 0) {
            if (this.cmd.getRegisteredWith() instanceof CommandsManager) {
                try {
                    for (String perm : this.cmd.getPermissions()) {
                        if (!((CommandsManager)this.cmd.getRegisteredWith()).hasPermission(player, perm)) continue;
                        return true;
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (player instanceof OfflinePlayer) {
                try {
                    for (String perm : this.cmd.getPermissions()) {
                        if (!PermissionsResolverManager.getInstance().hasPermission((OfflinePlayer)player, perm)) continue;
                        return true;
                    }
                }
                catch (WEPIFRuntimeException wEPIFRuntimeException) {
                    // empty catch block
                }
            }
            for (String perm : this.cmd.getPermissions()) {
                if (!player.hasPermission(perm)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public String getFullText(CommandSender forWho) {
        if (this.fullText == null || this.fullText.isEmpty()) {
            return this.getShortText();
        }
        return this.fullText;
    }

    public static class Factory
    implements HelpTopicFactory<DynamicPluginCommand> {
        public HelpTopic createTopic(DynamicPluginCommand command) {
            return new DynamicPluginCommandHelpTopic(command);
        }
    }
}

