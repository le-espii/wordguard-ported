package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.internal.platform.DebugHandler;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.minecraft.util.commands.CommandException;

public class ModDebugHandler implements DebugHandler {
    @Override
    public void testBreak(Actor actor, LocalPlayer player, boolean b1, boolean b2) throws CommandException {}

    @Override
    public void testPlace(Actor actor, LocalPlayer player, boolean b1, boolean b2) throws CommandException {}

    @Override
    public void testInteract(Actor actor, LocalPlayer player, boolean b1, boolean b2) throws CommandException {}

    @Override
    public void testDamage(Actor actor, LocalPlayer player, boolean b1, boolean b2) throws CommandException {}
}
