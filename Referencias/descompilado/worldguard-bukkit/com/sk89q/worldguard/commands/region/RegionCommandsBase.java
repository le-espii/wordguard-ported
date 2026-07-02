/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.IncompleteRegionException
 *  com.sk89q.worldedit.LocalSession
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.regions.CuboidRegion
 *  com.sk89q.worldedit.regions.Polygonal2DRegion
 *  com.sk89q.worldedit.regions.Region
 *  com.sk89q.worldedit.regions.RegionSelector
 *  com.sk89q.worldedit.session.SessionOwner
 *  com.sk89q.worldedit.util.formatting.component.ErrorFormat
 *  com.sk89q.worldedit.util.formatting.component.SubtleFormat
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.commands.region;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.util.WorldEditRegionConverter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class RegionCommandsBase {
    protected RegionCommandsBase() {
    }

    protected static RegionPermissionModel getPermissionModel(Actor sender) {
        return new RegionPermissionModel(sender);
    }

    protected static World checkWorld(CommandContext args, Actor sender, char flag) throws CommandException {
        return RegionCommandsBase.checkWorld(args, sender, flag, true);
    }

    protected static World checkWorld(CommandContext args, Actor sender, char flag, boolean allowWorldEditOverride) throws CommandException {
        if (args.hasFlag(flag)) {
            return WorldGuard.getInstance().getPlatform().getMatcher().matchWorld(sender, args.getFlag(flag));
        }
        if (allowWorldEditOverride) {
            try {
                World override = WorldEdit.getInstance().getSessionManager().get((SessionOwner)sender).getWorldOverride();
                if (override != null) {
                    if (sender instanceof LocalPlayer && !override.equals((Object)((LocalPlayer)sender).getWorld())) {
                        sender.printDebug((Component)TextComponent.of((String)("Using //world override for region command: " + override.getName())));
                    }
                    return override;
                }
            }
            catch (NoSuchMethodError noSuchMethodError) {
                // empty catch block
            }
        }
        if (sender instanceof LocalPlayer) {
            return ((LocalPlayer)sender).getWorld();
        }
        throw new CommandException("Please specify the world with -" + flag + " world_name.");
    }

    protected static String checkRegionId(String id, boolean allowGlobal) throws CommandException {
        if (!ProtectedRegion.isValidId(id)) {
            throw new CommandException("The region name of '" + id + "' contains characters that are not allowed.");
        }
        if (!allowGlobal && id.equalsIgnoreCase("__global__")) {
            throw new CommandException("Sorry, you can't use __global__ here.");
        }
        return id;
    }

    protected static ProtectedRegion checkExistingRegion(RegionManager regionManager, String id, boolean allowGlobal) throws CommandException {
        RegionCommandsBase.checkRegionId(id, allowGlobal);
        ProtectedRegion region = regionManager.getRegion(id);
        if (region == null) {
            if (id.equalsIgnoreCase("__global__")) {
                region = new GlobalProtectedRegion(id);
                regionManager.addRegion(region);
                return region;
            }
            throw new CommandException("No region could be found with the name of '" + id + "'.");
        }
        return region;
    }

    protected static ProtectedRegion checkRegionStandingIn(RegionManager regionManager, LocalPlayer player, String rgCmd) throws CommandException {
        return RegionCommandsBase.checkRegionStandingIn(regionManager, player, false, rgCmd);
    }

    protected static ProtectedRegion checkRegionStandingIn(RegionManager regionManager, LocalPlayer player, boolean allowGlobal, String rgCmd) throws CommandException {
        ApplicableRegionSet set = regionManager.getApplicableRegions(player.getLocation().toVector().toBlockPoint(), RegionQuery.QueryOption.SORT);
        if (set.size() == 0) {
            if (allowGlobal) {
                ProtectedRegion global = RegionCommandsBase.checkExistingRegion(regionManager, "__global__", true);
                player.printDebug("You're not standing in any regions. Using the global region for this world instead.");
                return global;
            }
            throw new CommandException("You're not standing in a region. Specify an ID if you want to select a specific region.");
        }
        if (set.size() > 1) {
            boolean first = true;
            TextComponent.Builder builder = TextComponent.builder((String)"");
            builder.append((Component)TextComponent.of((String)"Current regions: ", (TextColor)TextColor.GOLD));
            for (ProtectedRegion region : set) {
                if (!first) {
                    builder.append((Component)TextComponent.of((String)", "));
                }
                first = false;
                TextComponent regionComp = TextComponent.of((String)region.getId(), (TextColor)TextColor.AQUA);
                if (rgCmd != null && rgCmd.contains("%id%")) {
                    regionComp = (TextComponent)((TextComponent)regionComp.hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to pick this region")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)rgCmd.replace("%id%", region.getId())));
                }
                builder.append((Component)regionComp);
            }
            player.print((Component)builder.build());
            throw new CommandException("You're standing in several regions (please pick one).");
        }
        return (ProtectedRegion)set.iterator().next();
    }

    protected static Region checkSelection(Actor actor) throws CommandException {
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get((SessionOwner)actor);
        try {
            if (localSession == null || localSession.getSelectionWorld() == null) {
                throw new IncompleteRegionException();
            }
            return localSession.getRegionSelector(localSession.getSelectionWorld()).getRegion();
        }
        catch (IncompleteRegionException e) {
            throw new CommandException("Please select an area first. Use WorldEdit to make a selection! (see: https://worldedit.enginehub.org/en/latest/usage/regions/selections/).");
        }
    }

    protected static void checkRegionDoesNotExist(RegionManager manager, String id, boolean mayRedefine) throws CommandException {
        if (manager.hasRegion(id)) {
            throw new CommandException("A region with that name already exists. Please choose another name." + (String)(mayRedefine ? " To change the shape, use /region redefine " + id + "." : ""));
        }
    }

    protected static RegionManager checkRegionManager(World world) throws CommandException {
        if (!WorldGuard.getInstance().getPlatform().getGlobalStateManager().get((World)world).useRegions) {
            throw new CommandException("Region support is disabled in the target world. It can be enabled per-world in WorldGuard's configuration files. However, you may need to restart your server afterwards.");
        }
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (manager == null) {
            throw new CommandException("Region data failed to load for this world. Please ask a server administrator to read the logs to identify the reason.");
        }
        return manager;
    }

    protected static ProtectedRegion checkRegionFromSelection(Actor actor, String id) throws CommandException {
        Region selection = RegionCommandsBase.checkSelection(actor);
        if (selection instanceof Polygonal2DRegion) {
            Polygonal2DRegion polySel = (Polygonal2DRegion)selection;
            int minY = polySel.getMinimumPoint().y();
            int maxY = polySel.getMaximumPoint().y();
            return new ProtectedPolygonalRegion(id, polySel.getPoints(), minY, maxY);
        }
        if (selection instanceof CuboidRegion) {
            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();
            return new ProtectedCuboidRegion(id, min, max);
        }
        throw new CommandException("Sorry, you can only use cuboids and polygons for WorldGuard regions.");
    }

    protected static void warnAboutSaveFailures(Actor sender) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Set<RegionManager> failures = container.getSaveFailures();
        if (!failures.isEmpty()) {
            String failingList = Joiner.on((String)", ").join((Iterable)failures.stream().map(regionManager -> "'" + regionManager.getName() + "'").collect(Collectors.toList()));
            sender.print((Component)TextComponent.of((String)("(Warning: The background saving of region data is failing for these worlds: " + failingList + ". Your changes are getting lost. See the server log for more information.)"), (TextColor)TextColor.GOLD));
        }
    }

    protected static void warnAboutDimensions(Actor sender, ProtectedRegion region) {
        if (region instanceof GlobalProtectedRegion) {
            return;
        }
        int height = region.getMaximumPoint().y() - region.getMinimumPoint().y();
        if (height <= 2) {
            sender.printDebug("(Warning: The height of the region was " + (height + 1) + " block(s).)");
        }
    }

    protected static void informNewUser(Actor sender, RegionManager manager, ProtectedRegion region) {
        if (manager.size() <= 2) {
            sender.print(((TextComponent)SubtleFormat.wrap((String[])new String[]{"(This region is NOW PROTECTED from modification from others. Don't want that? Use "}).append((Component)TextComponent.of((String)("/rg flag " + region.getId() + " passthrough allow"), (TextColor)TextColor.AQUA))).append((Component)TextComponent.of((String)")", (TextColor)TextColor.GRAY)));
        }
    }

    protected static boolean checkSpawnOverlap(Actor sender, World world, ProtectedRegion region) {
        ProtectedRegion spawn = WorldGuard.getInstance().getPlatform().getSpawnProtection(world);
        if (spawn != null && !spawn.getIntersectingRegions((Collection<ProtectedRegion>)ImmutableList.of((Object)region)).isEmpty()) {
            sender.print(ErrorFormat.wrap((String[])new String[]{"Warning!"}).append((Component)TextComponent.of((String)" This region overlaps vanilla's spawn protection. WorldGuard cannot override this, and only server operators will be able to interact with this area.", (TextColor)TextColor.WHITE)));
            return true;
        }
        return false;
    }

    protected static void setPlayerSelection(Actor actor, ProtectedRegion region, World world) throws CommandException {
        LocalSession session = WorldEdit.getInstance().getSessionManager().get((SessionOwner)actor);
        RegionSelector selector = WorldEditRegionConverter.convertToSelector(region);
        if (selector == null) {
            throw new CommandException("Can't select that region! The region type '" + region.getType().getName() + "' can't be selected.");
        }
        selector.setWorld(world);
        session.setRegionSelector(world, selector);
        selector.explainRegionAdjust(actor, session);
        actor.print("Region selected as " + region.getType().getName());
    }

    protected static <V> V setFlag(ProtectedRegion region, Flag<V> flag, Actor sender, String value) throws InvalidFlagFormat {
        V val = flag.parseInput(FlagContext.create().setSender(sender).setInput(value).setObject("region", region).build());
        region.setFlag(flag, val);
        return val;
    }
}

