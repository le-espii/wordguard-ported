/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.extension.platform.Actor
 */
package com.sk89q.worldguard.internal.permission;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.internal.PermissionModel;

public abstract class AbstractPermissionModel
implements PermissionModel {
    private final Actor sender;

    protected AbstractPermissionModel(Actor sender) {
        Preconditions.checkNotNull((Object)sender);
        this.sender = sender;
    }

    public Actor getSender() {
        return this.sender;
    }

    protected boolean hasPluginPermission(String permission) {
        return this.getSender().hasPermission("worldguard." + permission);
    }
}

