/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.SingleRequestService;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;

public class PaperPlayerService
extends SingleRequestService {
    private static PaperPlayerService INSTANCE;

    private PaperPlayerService() {
    }

    public static PaperPlayerService getInstance() {
        return INSTANCE;
    }

    @Override
    public int getIdealRequestLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        PlayerProfile profile = Bukkit.createProfile((String)name);
        if (profile.completeFromCache()) {
            return new Profile(profile.getId(), profile.getName());
        }
        return null;
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        PlayerProfile profile = Bukkit.createProfile((UUID)uuid);
        if (profile.completeFromCache()) {
            return new Profile(profile.getId(), profile.getName());
        }
        return null;
    }

    static {
        try {
            Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            INSTANCE = new PaperPlayerService();
        }
        catch (ClassNotFoundException e) {
            INSTANCE = null;
        }
    }
}

