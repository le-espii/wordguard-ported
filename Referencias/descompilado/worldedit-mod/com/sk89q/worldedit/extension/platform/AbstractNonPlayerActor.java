/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import java.io.File;

public abstract class AbstractNonPlayerActor
implements Actor {
    @Override
    public boolean canDestroyBedrock() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public File openFileOpenDialog(String[] extensions) {
        return null;
    }

    @Override
    public File openFileSaveDialog(String[] extensions) {
        return null;
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
    }
}

