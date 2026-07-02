/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import javax.annotation.Nullable;

public interface SessionManager {
    public boolean hasBypass(LocalPlayer var1, World var2);

    public void resetAllStates();

    public void resetState(LocalPlayer var1);

    public boolean customHandlersRegistered();

    public boolean registerHandler(Handler.Factory<? extends Handler> var1, @Nullable Handler.Factory<? extends Handler> var2);

    public boolean unregisterHandler(Handler.Factory<? extends Handler> var1);

    @Deprecated
    public Session createSession(LocalPlayer var1);

    @Nullable
    public Session getIfPresent(LocalPlayer var1);

    public Session get(LocalPlayer var1);
}

