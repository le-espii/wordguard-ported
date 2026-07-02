/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.PaperPlayerService;
import com.sk89q.worldguard.util.profile.resolver.SingleRequestService;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;

@Deprecated(forRemoval=true)
public final class PaperProfileService
extends SingleRequestService {
    private static final PaperPlayerService SUPER = PaperPlayerService.getInstance();
    private static final PaperProfileService INSTANCE = new PaperProfileService();

    private PaperProfileService() {
        if (!PaperLib.isPaper()) {
            throw new IllegalStateException("Attempt to access PaperProfileService on non-Paper server.");
        }
    }

    @Override
    public int getIdealRequestLimit() {
        return SUPER.getIdealRequestLimit();
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        return SUPER.findByName(name);
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        return SUPER.findByUuid(uuid);
    }

    public static PaperProfileService getInstance() {
        return INSTANCE;
    }
}

