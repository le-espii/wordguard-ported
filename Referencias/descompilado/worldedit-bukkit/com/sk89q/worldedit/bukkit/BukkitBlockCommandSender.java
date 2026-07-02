/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.BlockCommandSender
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.AbstractCommandBlockActor;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.TextAdapter;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class BukkitBlockCommandSender
extends AbstractCommandBlockActor {
    private final BlockCommandSender sender;
    private final WorldEditPlugin plugin;
    private final UUID uuid;

    public BukkitBlockCommandSender(WorldEditPlugin plugin, BlockCommandSender sender) {
        super(BukkitAdapter.adapt(((BlockCommandSender)Preconditions.checkNotNull((Object)sender)).getBlock().getLocation()));
        Preconditions.checkNotNull((Object)((Object)plugin));
        this.plugin = plugin;
        this.sender = sender;
        this.uuid = UUID.nameUUIDFromBytes(("CMD" + sender.getName()).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            this.sender.sendMessage(part);
        }
    }

    @Override
    @Deprecated
    public void print(String msg) {
        for (String part : msg.split("\n")) {
            this.print(TextComponent.of(part, TextColor.LIGHT_PURPLE));
        }
    }

    @Override
    @Deprecated
    public void printDebug(String msg) {
        for (String part : msg.split("\n")) {
            this.print(TextComponent.of(part, TextColor.GRAY));
        }
    }

    @Override
    @Deprecated
    public void printError(String msg) {
        for (String part : msg.split("\n")) {
            this.print(TextComponent.of(part, TextColor.RED));
        }
    }

    @Override
    public void print(Component component) {
        TextAdapter.sendMessage((CommandSender)this.sender, WorldEditText.format(component, this.getLocale()));
    }

    @Override
    public Locale getLocale() {
        return WorldEdit.getInstance().getConfiguration().defaultLocale;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public void checkPermission(String permission) throws AuthorizationException {
        if (!this.hasPermission(permission)) {
            throw new AuthorizationException();
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    public BlockCommandSender getSender() {
        return this.sender;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey(){
            private volatile boolean active = true;

            private void updateActive() {
                Block block = BukkitBlockCommandSender.this.sender.getBlock();
                if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                    this.active = false;
                    return;
                }
                Material type = block.getType();
                this.active = type == Material.COMMAND_BLOCK || type == Material.CHAIN_COMMAND_BLOCK || type == Material.REPEATING_COMMAND_BLOCK;
            }

            @Override
            public String getName() {
                return BukkitBlockCommandSender.this.sender.getName();
            }

            @Override
            public boolean isActive() {
                if (Bukkit.isPrimaryThread()) {
                    this.updateActive();
                } else {
                    Bukkit.getScheduler().callSyncMethod((Plugin)BukkitBlockCommandSender.this.plugin, () -> {
                        this.updateActive();
                        return null;
                    });
                }
                return this.active;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return BukkitBlockCommandSender.this.uuid;
            }
        };
    }
}

