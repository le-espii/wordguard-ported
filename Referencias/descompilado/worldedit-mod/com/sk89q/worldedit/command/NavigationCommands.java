/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class NavigationCommands {
    private final WorldEdit worldEdit;

    public NavigationCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="unstuck", aliases={"!"}, desc="Escape from being stuck inside a block")
    @CommandPermissions(value={"worldedit.navigation.unstuck"})
    public void unstuck(Player player) throws WorldEditException {
        player.findFreePosition();
        player.printInfo(TranslatableComponent.of("worldedit.unstuck.moved"));
    }

    @Command(name="ascend", aliases={"asc"}, desc="Go up a floor")
    @CommandPermissions(value={"worldedit.navigation.ascend"})
    public void ascend(Player player, @Arg(desc="# of levels to ascend", def={"1"}) int levels) throws WorldEditException {
        int ascentLevels = 0;
        while (player.ascendLevel() && levels != ++ascentLevels) {
        }
        if (ascentLevels == 0) {
            player.printError(TranslatableComponent.of("worldedit.ascend.obstructed"));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.ascend.moved", TextComponent.of(ascentLevels)));
        }
    }

    @Command(name="descend", aliases={"desc"}, desc="Go down a floor")
    @CommandPermissions(value={"worldedit.navigation.descend"})
    public void descend(Player player, @Arg(desc="# of levels to descend", def={"1"}) int levels) throws WorldEditException {
        int descentLevels = 0;
        while (player.descendLevel() && levels != ++descentLevels) {
        }
        if (descentLevels == 0) {
            player.printError(TranslatableComponent.of("worldedit.descend.obstructed"));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.descend.moved", TextComponent.of(descentLevels)));
        }
    }

    @Command(name="ceil", desc="Go to the ceiling")
    @CommandPermissions(value={"worldedit.navigation.ceiling"})
    @Logging(value=Logging.LogMode.POSITION)
    public void ceiling(Player player, @Arg(desc="# of blocks to leave above you", def={"0"}) int clearance, @Switch(name=102, desc="Force using flight to keep you still") boolean forceFlight, @Switch(name=103, desc="Force using glass to keep you still") boolean forceGlass) throws WorldEditException {
        boolean alwaysGlass;
        if (player.ascendToCeiling(clearance = Math.max(0, clearance), alwaysGlass = this.getAlwaysGlass(forceFlight, forceGlass))) {
            player.printInfo(TranslatableComponent.of("worldedit.ceil.moved"));
        } else {
            player.printError(TranslatableComponent.of("worldedit.ceil.obstructed"));
        }
    }

    @Command(name="thru", desc="Pass through walls")
    @CommandPermissions(value={"worldedit.navigation.thru.command"})
    public void thru(Player player) throws WorldEditException {
        if (player.passThroughForwardWall(6)) {
            player.printInfo(TranslatableComponent.of("worldedit.thru.moved"));
        } else {
            player.printError(TranslatableComponent.of("worldedit.thru.obstructed"));
        }
    }

    @Command(name="jumpto", aliases={"j"}, desc="Teleport to a location")
    @CommandPermissions(value={"worldedit.navigation.jumpto.command"})
    public void jumpTo(Player player) throws WorldEditException {
        Location pos = player.getSolidBlockTrace(300);
        if (pos != null) {
            player.findFreePosition(pos);
            player.printInfo(TranslatableComponent.of("worldedit.jumpto.moved"));
        } else {
            player.printError(TranslatableComponent.of("worldedit.jumpto.none"));
        }
    }

    @Command(name="up", desc="Go upwards some distance")
    @CommandPermissions(value={"worldedit.navigation.up"})
    @Logging(value=Logging.LogMode.POSITION)
    public void up(Player player, @Arg(desc="Distance to go upwards") int distance, @Switch(name=102, desc="Force using flight to keep you still") boolean forceFlight, @Switch(name=103, desc="Force using glass to keep you still") boolean forceGlass) throws WorldEditException {
        boolean alwaysGlass = this.getAlwaysGlass(forceFlight, forceGlass);
        if (player.ascendUpwards(distance, alwaysGlass)) {
            player.printInfo(TranslatableComponent.of("worldedit.up.moved"));
        } else {
            player.printError(TranslatableComponent.of("worldedit.up.obstructed"));
        }
    }

    private boolean getAlwaysGlass(boolean forceFlight, boolean forceGlass) {
        LocalConfiguration config = this.worldEdit.getConfiguration();
        return forceGlass || config.navigationUseGlass && !forceFlight;
    }
}

