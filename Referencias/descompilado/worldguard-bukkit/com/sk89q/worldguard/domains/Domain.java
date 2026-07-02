/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.domains;

import com.sk89q.worldguard.LocalPlayer;
import java.util.UUID;

public interface Domain {
    public boolean contains(LocalPlayer var1);

    public boolean contains(UUID var1);

    @Deprecated
    public boolean contains(String var1);

    public int size();

    public void clear();
}

