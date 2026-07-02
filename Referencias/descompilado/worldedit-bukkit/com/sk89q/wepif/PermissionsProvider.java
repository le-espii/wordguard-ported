/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 */
package com.sk89q.wepif;

import org.bukkit.OfflinePlayer;

public interface PermissionsProvider {
    public boolean hasPermission(String var1, String var2);

    public boolean hasPermission(String var1, String var2, String var3);

    public boolean inGroup(String var1, String var2);

    public String[] getGroups(String var1);

    public boolean hasPermission(OfflinePlayer var1, String var2);

    public boolean hasPermission(String var1, OfflinePlayer var2, String var3);

    public boolean inGroup(OfflinePlayer var1, String var2);

    public String[] getGroups(OfflinePlayer var1);
}

