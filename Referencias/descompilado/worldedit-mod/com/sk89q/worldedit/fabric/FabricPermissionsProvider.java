/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.lucko.fabric.api.permissions.v0.Permissions
 *  net.minecraft.class_1297
 *  net.minecraft.class_1934
 *  net.minecraft.class_3222
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.fabric.FabricConfiguration;
import com.sk89q.worldedit.fabric.FabricPlatform;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.class_1297;
import net.minecraft.class_1934;
import net.minecraft.class_3222;

public interface FabricPermissionsProvider {
    public boolean hasPermission(class_3222 var1, String var2);

    public void registerPermission(String var1);

    public static class LuckoFabricPermissionsProvider
    extends VanillaPermissionsProvider {
        public LuckoFabricPermissionsProvider(FabricPlatform platform) {
            super(platform);
        }

        @Override
        public boolean hasPermission(class_3222 player, String permission) {
            return Permissions.getPermissionValue((class_1297)player, (String)permission).orElseGet(() -> super.hasPermission(player, permission));
        }

        @Override
        public void registerPermission(String permission) {
        }
    }

    public static class VanillaPermissionsProvider
    implements FabricPermissionsProvider {
        private final FabricPlatform platform;

        public VanillaPermissionsProvider(FabricPlatform platform) {
            this.platform = platform;
        }

        @Override
        public boolean hasPermission(class_3222 player, String permission) {
            FabricConfiguration configuration = this.platform.getConfiguration();
            return configuration.cheatMode || player.field_13995.method_3760().method_14569(player.method_7334()) || configuration.creativeEnable && player.field_13974.method_14257() == class_1934.field_9220;
        }

        @Override
        public void registerPermission(String permission) {
        }
    }
}

