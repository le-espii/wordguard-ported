package com.sk89q.worldguard.mod;

import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Predicate;

public class MinecraftProfileService implements ProfileService {

    @Override
    public int getIdealRequestLimit() {
        return 100;
    }

    @Nullable
    @Override
    public Profile findByName(String name) throws IOException, InterruptedException {
        net.minecraft.server.MinecraftServer server = WorldGuardModCommon.getServer();
        if (server != null) {
            net.minecraft.server.players.GameProfileCache mcCache = server.getProfileCache();
            if (mcCache != null) {
                java.util.Optional<com.mojang.authlib.GameProfile> gpOpt = mcCache.get(name);
                if (gpOpt.isPresent()) {
                    com.mojang.authlib.GameProfile gp = gpOpt.get();
                    return new Profile(gp.getId(), gp.getName());
                }
            }
        }
        
        // Fallback: generar UUID offline de manera segura para evitar crasheos si el jugador no existe/está en caché
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        return new Profile(offlineUuid, name);
    }

    @Override
    public ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ImmutableList.Builder<Profile> builder = ImmutableList.builder();
        for (String name : names) {
            Profile profile = findByName(name);
            if (profile != null) {
                builder.add(profile);
            }
        }
        return builder.build();
    }

    @Override
    public void findAllByName(Iterable<String> names, Predicate<Profile> callback) throws IOException, InterruptedException {
        for (String name : names) {
            Profile profile = findByName(name);
            if (profile != null) {
                if (!callback.test(profile)) {
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        net.minecraft.server.MinecraftServer server = WorldGuardModCommon.getServer();
        if (server != null) {
            net.minecraft.server.players.GameProfileCache mcCache = server.getProfileCache();
            if (mcCache != null) {
                java.util.Optional<com.mojang.authlib.GameProfile> gpOpt = mcCache.get(uuid);
                if (gpOpt.isPresent()) {
                    com.mojang.authlib.GameProfile gp = gpOpt.get();
                    return new Profile(gp.getId(), gp.getName());
                }
            }
        }
        return null;
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ImmutableList.Builder<Profile> builder = ImmutableList.builder();
        for (UUID uuid : uuids) {
            Profile profile = findByUuid(uuid);
            if (profile != null) {
                builder.add(profile);
            }
        }
        return builder.build();
    }

    @Override
    public void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> callback) throws IOException, InterruptedException {
        for (UUID uuid : uuids) {
            Profile profile = findByUuid(uuid);
            if (profile != null) {
                if (!callback.test(profile)) {
                    break;
                }
            }
        }
    }
}
