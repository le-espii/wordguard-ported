/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.session.request;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.world.World;
import javax.annotation.Nullable;

public final class Request {
    private static final ThreadLocal<Request> threadLocal = ThreadLocal.withInitial(Request::new);
    @Nullable
    private World world;
    @Nullable
    private LocalSession session;
    @Nullable
    private EditSession editSession;
    private boolean valid;

    private Request() {
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
    }

    @Nullable
    public LocalSession getSession() {
        return this.session;
    }

    public void setSession(@Nullable LocalSession session) {
        this.session = session;
    }

    @Nullable
    public EditSession getEditSession() {
        return this.editSession;
    }

    public void setEditSession(@Nullable EditSession editSession) {
        this.editSession = editSession;
    }

    public static Request request() {
        return threadLocal.get();
    }

    public static void reset() {
        Request.request().invalidate();
        threadLocal.remove();
    }

    public boolean isValid() {
        return this.valid;
    }

    private void invalidate() {
        this.valid = false;
    }
}

