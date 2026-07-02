/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.SingleRequestService;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitPlayerService
extends SingleRequestService {
    private static final BukkitPlayerService INSTANCE = new BukkitPlayerService();

    private BukkitPlayerService() {
    }

    public static BukkitPlayerService getInstance() {
        return INSTANCE;
    }

    @Override
    public int getIdealRequestLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        Player player = Bukkit.getServer().getPlayerExact(name);
        if (player != null) {
            return new Profile(player.getUniqueId(), player.getName());
        }
        return null;
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        OfflinePlayer player = Bukkit.getOfflinePlayer((UUID)uuid);
        if (player.getName() != null) {
            return new Profile(player.getUniqueId(), player.getName());
        }
        return null;
    }
}

