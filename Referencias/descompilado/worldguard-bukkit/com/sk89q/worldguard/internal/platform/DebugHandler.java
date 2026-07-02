/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.extension.platform.Actor
 */
package com.sk89q.worldguard.internal.platform;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;

public interface DebugHandler {
    public void testBreak(Actor var1, LocalPlayer var2, boolean var3, boolean var4) throws CommandException;

    public void testPlace(Actor var1, LocalPlayer var2, boolean var3, boolean var4) throws CommandException;

    public void testInteract(Actor var1, LocalPlayer var2, boolean var3, boolean var4) throws CommandException;

    public void testDamage(Actor var1, LocalPlayer var2, boolean var3, boolean var4) throws CommandException;
}

