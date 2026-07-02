/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.wepif;

import com.sk89q.wepif.PermissionsProvider;

public interface PermissionsResolver
extends PermissionsProvider {
    public void load();

    public String getDetectionMessage();
}

