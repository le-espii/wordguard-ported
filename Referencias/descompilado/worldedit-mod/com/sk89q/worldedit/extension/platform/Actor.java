/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.Identifiable;
import com.sk89q.worldedit.util.auth.Subject;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.io.File;
import java.util.Locale;

public interface Actor
extends Identifiable,
SessionOwner,
Subject {
    public String getName();

    default public String getDisplayName() {
        return this.getName();
    }

    @Deprecated
    public void printRaw(String var1);

    @Deprecated
    public void printDebug(String var1);

    @Deprecated
    public void print(String var1);

    @Deprecated
    public void printError(String var1);

    default public void printError(Component component) {
        this.print(component.color(TextColor.RED));
    }

    default public void printInfo(Component component) {
        this.print(component.color(TextColor.LIGHT_PURPLE));
    }

    default public void printDebug(Component component) {
        this.print(component.color(TextColor.GRAY));
    }

    public void print(Component var1);

    public boolean canDestroyBedrock();

    public boolean isPlayer();

    public File openFileOpenDialog(String[] var1);

    public File openFileSaveDialog(String[] var1);

    public void dispatchCUIEvent(CUIEvent var1);

    public Locale getLocale();

    default public void sendAnnouncements() {
    }
}

