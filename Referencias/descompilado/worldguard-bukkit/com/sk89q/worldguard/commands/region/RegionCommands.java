/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.Sets
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissionsException
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.command.util.AsyncCommandBuilder
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.extent.Extent
 *  com.sk89q.worldedit.internal.command.exception.ExceptionConverter
 *  com.sk89q.worldedit.regions.Region
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.formatting.component.ErrorFormat
 *  com.sk89q.worldedit.util.formatting.component.LabelFormat
 *  com.sk89q.worldedit.util.formatting.component.SubtleFormat
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.formatting.text.format.TextDecoration
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.gamemode.GameModes
 */
package com.sk89q.worldguard.commands.region;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.LabelFormat;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.region.FlagHelperBox;
import com.sk89q.worldguard.commands.region.RegionCommandsBase;
import com.sk89q.worldguard.commands.region.RegionPrintoutBuilder;
import com.sk89q.worldguard.commands.task.RegionAdder;
import com.sk89q.worldguard.commands.task.RegionLister;
import com.sk89q.worldguard.commands.task.RegionManagerLoader;
import com.sk89q.worldguard.commands.task.RegionManagerSaver;
import com.sk89q.worldguard.commands.task.RegionRemover;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.migration.DriverMigration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.migration.UUIDMigration;
import com.sk89q.worldguard.protection.managers.migration.WorldHeightMigration;
import com.sk89q.worldguard.protection.managers.storage.DriverType;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.util.WorldEditRegionConverter;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.util.Enums;
import com.sk89q.worldguard.util.logging.LoggerToChatHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class RegionCommands
extends RegionCommandsBase {
    private static final Logger log = Logger.getLogger(RegionCommands.class.getCanonicalName());
    private final WorldGuard worldGuard;
    private static TextComponent passthroughFlagWarning = (TextComponent)((TextComponent)((TextComponent)((TextComponent)TextComponent.empty().append((Component)TextComponent.of((String)"WARNING:", (TextColor)TextColor.RED, (Set)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.BOLD})))).append((Component)ErrorFormat.wrap((String[])new String[]{" This flag is unrelated to moving through regions."}))).append((Component)TextComponent.newline())).append(((TextComponent)TextComponent.of((String)"It overrides build checks. If you're unsure what this means, see ").append(TextComponent.of((String)"[this documentation page]", (TextColor)TextColor.AQUA).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.OPEN_URL, (String)"https://worldguard.enginehub.org/en/latest/regions/flags/#overrides")))).append((Component)TextComponent.of((String)" for more info.")));
    private static TextComponent buildFlagWarning = (TextComponent)((TextComponent)((TextComponent)((TextComponent)((TextComponent)((TextComponent)TextComponent.empty().append((Component)TextComponent.of((String)"WARNING:", (TextColor)TextColor.RED, (Set)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.BOLD})))).append((Component)ErrorFormat.wrap((String[])new String[]{" Setting this flag is not required for protection."}))).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)"Setting this flag will completely override default protection, and apply to members, non-members, pistons, sand physics, and everything else that can modify blocks."))).append((Component)TextComponent.newline())).append(((TextComponent)TextComponent.of((String)"Only set this flag if you are sure you know what you are doing. See ").append(TextComponent.of((String)"[this documentation page]", (TextColor)TextColor.AQUA).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.OPEN_URL, (String)"https://worldguard.enginehub.org/en/latest/regions/flags/#protection-related")))).append((Component)TextComponent.of((String)" for more info.")));

    public RegionCommands(WorldGuard worldGuard) {
        Preconditions.checkNotNull((Object)worldGuard);
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"define", "def", "d", "create"}, usage="[-w <world>] <id> [<owner1> [<owner2> [<owners...>]]]", flags="ngw:", desc="Defines a region", min=1)
    public void define(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        if (!RegionCommands.getPermissionModel(sender).mayDefine()) {
            throw new CommandPermissionsException();
        }
        String id = RegionCommands.checkRegionId(args.getString(0), false);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        RegionCommands.checkRegionDoesNotExist(manager, id, true);
        ProtectedRegion region = args.hasFlag('g') ? new GlobalProtectedRegion(id) : RegionCommands.checkRegionFromSelection(sender, id);
        RegionAdder task = new RegionAdder(manager, region);
        task.addOwnersFromCommand(args, 2);
        String description = String.format("Adding region '%s'", region.getId());
        AsyncCommandBuilder.wrap((Callable)task, (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).onSuccess((Component)null, t -> {
            sender.print(String.format("A new region has been made named '%s'.", region.getId()));
            RegionCommands.warnAboutDimensions(sender, region);
            RegionCommands.informNewUser(sender, manager, region);
            RegionCommands.checkSpawnOverlap(sender, world, region);
        }).onFailure(String.format("Failed to add the region '%s'", region.getId()), (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }

    @Command(aliases={"redefine", "update", "move"}, usage="[-w <world>] <id>", desc="Re-defines the shape of a region", flags="gw:", min=1, max=1)
    public void redefine(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        String id = RegionCommands.checkRegionId(args.getString(0), false);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        ProtectedRegion existing = RegionCommands.checkExistingRegion(manager, id, false);
        if (!RegionCommands.getPermissionModel(sender).mayRedefine(existing)) {
            throw new CommandPermissionsException();
        }
        ProtectedRegion region = args.hasFlag('g') ? new GlobalProtectedRegion(id) : RegionCommands.checkRegionFromSelection(sender, id);
        region.copyFrom(existing);
        RegionAdder task = new RegionAdder(manager, region);
        String description = String.format("Updating region '%s'", region.getId());
        AsyncCommandBuilder.wrap((Callable)task, (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).sendMessageAfterDelay("(Please wait... " + description + ")").onSuccess((Component)null, t -> {
            sender.print(String.format("Region '%s' has been updated with a new area.", region.getId()));
            RegionCommands.warnAboutDimensions(sender, region);
            RegionCommands.informNewUser(sender, manager, region);
            RegionCommands.checkSpawnOverlap(sender, world, region);
        }).onFailure(String.format("Failed to update the region '%s'", region.getId()), (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }

    @Command(aliases={"claim"}, usage="<id>", desc="Claim a region", min=1, max=1)
    public void claim(CommandContext args, Actor sender) throws CommandException {
        ProtectedRegion templateRegion;
        int maxRegionCount;
        RegionCommands.warnAboutSaveFailures(sender);
        LocalPlayer player = this.worldGuard.checkPlayer(sender);
        RegionPermissionModel permModel = RegionCommands.getPermissionModel((Actor)player);
        if (!permModel.mayClaim()) {
            throw new CommandPermissionsException();
        }
        String id = RegionCommands.checkRegionId(args.getString(0), false);
        RegionManager manager = RegionCommands.checkRegionManager(player.getWorld());
        RegionCommands.checkRegionDoesNotExist(manager, id, false);
        ProtectedRegion region = RegionCommands.checkRegionFromSelection((Actor)player, id);
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(player.getWorld());
        if (!permModel.mayClaimRegionsUnbounded() && (maxRegionCount = wcfg.getMaxRegionCount(player)) >= 0 && manager.getRegionCountOfPlayer(player) >= maxRegionCount) {
            throw new CommandException("You own too many regions, delete one first to claim a new one.");
        }
        ProtectedRegion existing = manager.getRegion(id);
        if (existing != null && !existing.getOwners().contains(player)) {
            throw new CommandException("This region already exists and you don't own it.");
        }
        ApplicableRegionSet regions = manager.getApplicableRegions(region);
        if (regions.size() > 0) {
            if (!regions.isOwnerOfAll(player)) {
                throw new CommandException("This region overlaps with someone else's region.");
            }
        } else if (wcfg.claimOnlyInsideExistingRegions) {
            throw new CommandException("You may only claim regions inside existing regions that you or your group own.");
        }
        if (wcfg.maxClaimVolume >= Integer.MAX_VALUE) {
            throw new CommandException("The maximum claim volume get in the configuration is higher than is supported. Currently, it must be 2147483647 or smaller. Please contact a server administrator.");
        }
        if (!permModel.mayClaimRegionsUnbounded()) {
            if (region instanceof ProtectedPolygonalRegion) {
                throw new CommandException("Polygons are currently not supported for /rg claim.");
            }
            if (region.volume() > wcfg.maxClaimVolume) {
                player.printError("This region is too large to claim.");
                player.printError("Max. volume: " + wcfg.maxClaimVolume + ", your volume: " + region.volume());
                return;
            }
        }
        if (!Strings.isNullOrEmpty((String)wcfg.setParentOnClaim) && (templateRegion = manager.getRegion(wcfg.setParentOnClaim)) != null) {
            try {
                region.setParent(templateRegion);
            }
            catch (ProtectedRegion.CircularInheritanceException e) {
                throw new CommandException(e.getMessage());
            }
        }
        region.getOwners().addPlayer(player);
        manager.addRegion(region);
        player.print((Component)TextComponent.of((String)String.format("A new region has been claimed named '%s'.", id)));
    }

    @Command(aliases={"select", "sel", "s"}, usage="[-w <world>] [id]", desc="Load a region as a WorldEdit selection", min=0, max=1, flags="w:")
    public void select(CommandContext args, Actor sender) throws CommandException {
        ProtectedRegion existing;
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        if (args.argsLength() == 0) {
            LocalPlayer player = this.worldGuard.checkPlayer(sender);
            if (!player.getWorld().equals((Object)world)) {
                throw new CommandException("Please specify a region name.");
            }
            world = player.getWorld();
            existing = RegionCommands.checkRegionStandingIn(manager, player, "/rg select -w \"" + world.getName() + "\" %id%");
        } else {
            existing = RegionCommands.checkExistingRegion(manager, args.getString(0), false);
        }
        if (!RegionCommands.getPermissionModel(sender).maySelect(existing)) {
            throw new CommandPermissionsException();
        }
        RegionCommands.setPlayerSelection(sender, existing, world);
    }

    @Command(aliases={"info", "i"}, usage="[id]", flags="usw:", desc="Get information about a region", min=0, max=1)
    public void info(CommandContext args, Actor sender) throws CommandException {
        ProtectedRegion existing;
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionPermissionModel permModel = RegionCommands.getPermissionModel(sender);
        RegionManager manager = RegionCommands.checkRegionManager(world);
        if (args.argsLength() == 0) {
            if (!(sender instanceof LocalPlayer)) {
                throw new CommandException("Please specify the region with /region info -w world_name region_name.");
            }
            existing = RegionCommands.checkRegionStandingIn(manager, (LocalPlayer)sender, true, "/rg info -w \"" + world.getName() + "\" %id%" + (args.hasFlag('u') ? " -u" : "") + (args.hasFlag('s') ? " -s" : ""));
        } else {
            existing = RegionCommands.checkExistingRegion(manager, args.getString(0), true);
        }
        if (!permModel.mayLookup(existing)) {
            throw new CommandPermissionsException();
        }
        if (args.hasFlag('s')) {
            if (!permModel.maySelect(existing)) {
                throw new CommandPermissionsException();
            }
            RegionCommands.setPlayerSelection((Actor)this.worldGuard.checkPlayer(sender), existing, world);
        }
        RegionPrintoutBuilder printout = new RegionPrintoutBuilder(world.getName(), existing, args.hasFlag('u') ? null : WorldGuard.getInstance().getProfileCache(), sender);
        AsyncCommandBuilder.wrap((Callable)printout, (Actor)sender).registerWithSupervisor(WorldGuard.getInstance().getSupervisor(), "Fetching region info").sendMessageAfterDelay("(Please wait... fetching region information...)").onSuccess((Component)null, component -> {
            sender.print((Component)component);
            RegionCommands.checkSpawnOverlap(sender, world, existing);
        }).onFailure("Failed to fetch region information", (ExceptionConverter)WorldGuard.getInstance().getExceptionConverter()).buildAndExec(WorldGuard.getInstance().getExecutorService());
    }

    @Command(aliases={"list"}, usage="[-w world] [-p owner [-n]] [-s] [-i filter] [page]", desc="Get a list of regions", flags="np:w:i:s", max=1)
    public void list(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        int page = args.getInteger(0, 1);
        if (page < 1) {
            page = 1;
        }
        String ownedBy = args.hasFlag('p') ? args.getFlag('p') : null;
        if (!RegionCommands.getPermissionModel(sender).mayList(ownedBy)) {
            ownedBy = sender.getName();
            if (!RegionCommands.getPermissionModel(sender).mayList(ownedBy)) {
                throw new CommandPermissionsException();
            }
        }
        RegionManager manager = RegionCommands.checkRegionManager(world);
        RegionLister task = new RegionLister(manager, sender, world.getName());
        task.setPage(page);
        if (ownedBy != null) {
            task.filterOwnedByName(ownedBy, args.hasFlag('n'));
        }
        if (args.hasFlag('s')) {
            ProtectedRegion existing = RegionCommands.checkRegionFromSelection(sender, "tmp");
            task.filterByIntersecting(existing);
        }
        if (args.hasFlag('i')) {
            task.filterIdByMatch(args.getFlag('i'));
        }
        AsyncCommandBuilder.wrap((Callable)task, (Actor)sender).registerWithSupervisor(WorldGuard.getInstance().getSupervisor(), "Getting region list").sendMessageAfterDelay("(Please wait... fetching region list...)").onFailure("Failed to fetch region list", (ExceptionConverter)WorldGuard.getInstance().getExceptionConverter()).buildAndExec(WorldGuard.getInstance().getExecutorService());
    }

    @Command(aliases={"flag", "f"}, usage="<id> <flag> [-w world] [-g group] [value]", flags="g:w:eh:", desc="Set flags", min=2)
    public void flag(CommandContext args, Actor sender) throws CommandException {
        RegionGroupFlag groupFlag;
        RegionManager manager;
        ProtectedRegion existing;
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        String flagName = args.getString(1);
        String value = args.argsLength() >= 3 ? args.getJoinedStrings(2) : null;
        RegionGroup groupValue = null;
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        RegionPermissionModel permModel = RegionCommands.getPermissionModel(sender);
        if (args.hasFlag('e')) {
            if (value != null) {
                throw new CommandException("You cannot use -e(mpty) with a flag value.");
            }
            value = "";
        }
        if (!permModel.maySetFlag(existing = RegionCommands.checkExistingRegion(manager = RegionCommands.checkRegionManager(world), args.getString(0), true))) {
            throw new CommandPermissionsException();
        }
        String regionId = existing.getId();
        Flag<?> foundFlag = Flags.fuzzyMatchFlag(flagRegistry, flagName);
        if (foundFlag == null) {
            AsyncCommandBuilder.wrap((Callable)new FlagListBuilder(flagRegistry, permModel, existing, world, regionId, sender, flagName), (Actor)sender).registerWithSupervisor(WorldGuard.getInstance().getSupervisor(), "Flag list for invalid flag command.").onSuccess((Component)null, arg_0 -> ((Actor)sender).print(arg_0)).onFailure((Component)null, (ExceptionConverter)WorldGuard.getInstance().getExceptionConverter()).buildAndExec(WorldGuard.getInstance().getExecutorService());
            return;
        }
        if (value != null) {
            if (foundFlag == Flags.BUILD || foundFlag == Flags.BLOCK_BREAK || foundFlag == Flags.BLOCK_PLACE) {
                sender.print((Component)buildFlagWarning);
                if (!sender.isPlayer()) {
                    sender.printRaw("https://worldguard.enginehub.org/en/latest/regions/flags/#protection-related");
                }
            } else if (foundFlag == Flags.PASSTHROUGH) {
                sender.print((Component)passthroughFlagWarning);
                if (!sender.isPlayer()) {
                    sender.printRaw("https://worldguard.enginehub.org/en/latest/regions/flags/#overrides");
                }
            }
        }
        if (!permModel.maySetFlag(existing, foundFlag, value)) {
            throw new CommandPermissionsException();
        }
        if (args.hasFlag('g')) {
            String group = args.getFlag('g');
            RegionGroupFlag groupFlag2 = foundFlag.getRegionGroupFlag();
            if (groupFlag2 == null) {
                throw new CommandException("Region flag '" + foundFlag.getName() + "' does not have a group flag!");
            }
            try {
                groupValue = (RegionGroup)((Object)groupFlag2.parseInput(FlagContext.create().setSender(sender).setInput(group).setObject("region", existing).build()));
            }
            catch (InvalidFlagFormat e) {
                throw new CommandException(e.getMessage());
            }
        }
        if (value != null) {
            try {
                value = RegionCommands.setFlag(existing, foundFlag, sender, value).toString();
            }
            catch (InvalidFlagFormat e) {
                throw new CommandException(e.getMessage());
            }
            if (!args.hasFlag('h')) {
                sender.print("Region flag " + foundFlag.getName() + " set on '" + regionId + "' to '" + value + "'.");
            }
        } else if (!args.hasFlag('g')) {
            existing.setFlag(foundFlag, null);
            groupFlag = foundFlag.getRegionGroupFlag();
            if (groupFlag != null) {
                existing.setFlag(groupFlag, null);
            }
            if (!args.hasFlag('h')) {
                sender.print("Region flag " + foundFlag.getName() + " removed from '" + regionId + "'. (Any -g(roups) were also removed.)");
            }
        }
        if (groupValue != null) {
            groupFlag = foundFlag.getRegionGroupFlag();
            if (groupValue == groupFlag.getDefault()) {
                existing.setFlag(groupFlag, null);
                sender.print("Region group flag for '" + foundFlag.getName() + "' reset to default.");
            } else {
                existing.setFlag(groupFlag, groupValue);
                sender.print("Region group flag for '" + foundFlag.getName() + "' set.");
            }
        }
        if (args.hasFlag('h')) {
            int page = args.getFlagInteger('h');
            RegionCommands.sendFlagHelper(sender, world, existing, permModel, page);
        } else {
            RegionPrintoutBuilder printout = new RegionPrintoutBuilder(world.getName(), existing, null, sender);
            printout.append(SubtleFormat.wrap((String[])new String[]{"(Current flags: "}));
            printout.appendFlagsList(false);
            printout.append(SubtleFormat.wrap((String[])new String[]{")"}));
            printout.send(sender);
            RegionCommands.checkSpawnOverlap(sender, world, existing);
        }
    }

    @Command(aliases={"flags"}, usage="[-p <page>] [id]", flags="p:w:", desc="View region flags", min=0, max=2)
    public void flagHelper(CommandContext args, Actor sender) throws CommandException {
        ProtectedRegion region;
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        if (args.argsLength() == 0) {
            if (!(sender instanceof LocalPlayer)) {
                throw new CommandException("Please specify the region with /region flags -w world_name region_name.");
            }
            region = RegionCommands.checkRegionStandingIn(manager, (LocalPlayer)sender, true, "/rg flags -w \"" + world.getName() + "\" %id%");
        } else {
            region = RegionCommands.checkExistingRegion(manager, args.getString(0), true);
        }
        RegionPermissionModel perms = RegionCommands.getPermissionModel(sender);
        if (!perms.mayLookup(region)) {
            throw new CommandPermissionsException();
        }
        int page = args.hasFlag('p') ? args.getFlagInteger('p') : 1;
        RegionCommands.sendFlagHelper(sender, world, region, perms, page);
    }

    private static void sendFlagHelper(Actor sender, World world, ProtectedRegion region, RegionPermissionModel perms, int page) {
        FlagHelperBox flagHelperBox = new FlagHelperBox(world, region, perms);
        flagHelperBox.setComponentsPerPage(18);
        if (!sender.isPlayer()) {
            flagHelperBox.tryMonoSpacing();
        }
        AsyncCommandBuilder.wrap(() -> {
            if (RegionCommands.checkSpawnOverlap(sender, world, region)) {
                flagHelperBox.setComponentsPerPage(15);
            }
            return flagHelperBox.create(page);
        }, (Actor)sender).onSuccess((Component)null, arg_0 -> ((Actor)sender).print(arg_0)).onFailure("Failed to get region flags", (ExceptionConverter)WorldGuard.getInstance().getExceptionConverter()).buildAndExec(WorldGuard.getInstance().getExecutorService());
    }

    @Command(aliases={"setpriority", "priority", "pri"}, usage="<id> <priority>", flags="w:", desc="Set the priority of a region", min=2, max=2)
    public void setPriority(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        int priority = args.getInteger(1);
        RegionManager manager = RegionCommands.checkRegionManager(world);
        ProtectedRegion existing = RegionCommands.checkExistingRegion(manager, args.getString(0), false);
        if (!RegionCommands.getPermissionModel(sender).maySetPriority(existing)) {
            throw new CommandPermissionsException();
        }
        existing.setPriority(priority);
        sender.print("Priority of '" + existing.getId() + "' set to " + priority + " (higher numbers override).");
        RegionCommands.checkSpawnOverlap(sender, world, existing);
    }

    @Command(aliases={"setparent", "parent", "par"}, usage="<id> [parent-id]", flags="w:", desc="Set the parent of a region", min=1, max=2)
    public void setParent(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        ProtectedRegion child = RegionCommands.checkExistingRegion(manager, args.getString(0), false);
        ProtectedRegion parent = args.argsLength() == 2 ? RegionCommands.checkExistingRegion(manager, args.getString(1), false) : null;
        if (!RegionCommands.getPermissionModel(sender).maySetParent(child, parent)) {
            throw new CommandPermissionsException();
        }
        try {
            child.setParent(parent);
        }
        catch (ProtectedRegion.CircularInheritanceException e) {
            RegionPrintoutBuilder printout = new RegionPrintoutBuilder(world.getName(), parent, null, sender);
            assert (parent != null);
            printout.append(ErrorFormat.wrap((String[])new String[]{"Uh oh! Setting '", parent.getId(), "' to be the parent of '", child.getId(), "' would cause circular inheritance."})).newline();
            printout.append(SubtleFormat.wrap((String[])new String[]{"(Current inheritance on '", parent.getId(), "':"})).newline();
            printout.appendParentTree(true);
            printout.append(SubtleFormat.wrap((String[])new String[]{")"}));
            printout.send(sender);
            return;
        }
        RegionPrintoutBuilder printout = new RegionPrintoutBuilder(world.getName(), child, null, sender);
        printout.append(TextComponent.of((String)("Inheritance set for region '" + child.getId() + "'."), (TextColor)TextColor.LIGHT_PURPLE));
        if (parent != null) {
            printout.newline();
            printout.append(SubtleFormat.wrap((String[])new String[]{"(Current inheritance:"})).newline();
            printout.appendParentTree(true);
            printout.append(SubtleFormat.wrap((String[])new String[]{")"}));
        } else {
            printout.append(LabelFormat.wrap((String[])new String[]{" Region is now orphaned."}));
        }
        printout.send(sender);
    }

    @Command(aliases={"remove", "delete", "del", "rem"}, usage="<id>", flags="fuw:", desc="Remove a region", min=1, max=1)
    public void remove(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = RegionCommands.checkWorld(args, sender, 'w');
        boolean removeChildren = args.hasFlag('f');
        boolean unsetParent = args.hasFlag('u');
        RegionManager manager = RegionCommands.checkRegionManager(world);
        ProtectedRegion existing = RegionCommands.checkExistingRegion(manager, args.getString(0), true);
        if (!RegionCommands.getPermissionModel(sender).mayDelete(existing)) {
            throw new CommandPermissionsException();
        }
        RegionRemover task = new RegionRemover(manager, existing);
        if (removeChildren && unsetParent) {
            throw new CommandException("You cannot use both -u (unset parent) and -f (remove children) together.");
        }
        if (removeChildren) {
            task.setRemovalStrategy(RemovalStrategy.REMOVE_CHILDREN);
        } else if (unsetParent) {
            task.setRemovalStrategy(RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
        }
        String description = String.format("Removing region '%s' in '%s'", existing.getId(), world.getName());
        AsyncCommandBuilder.wrap((Callable)task, (Actor)sender).registerWithSupervisor(WorldGuard.getInstance().getSupervisor(), description).sendMessageAfterDelay("Please wait... removing region.").onSuccess((Component)null, removed -> sender.print((Component)TextComponent.of((String)("Successfully removed " + removed.stream().map(ProtectedRegion::getId).collect(Collectors.joining(", ")) + "."), (TextColor)TextColor.LIGHT_PURPLE))).onFailure("Failed to remove region", (ExceptionConverter)WorldGuard.getInstance().getExceptionConverter()).buildAndExec(WorldGuard.getInstance().getExecutorService());
    }

    @Command(aliases={"load", "reload"}, usage="[world]", desc="Reload regions from file", flags="w:")
    public void load(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = null;
        try {
            world = RegionCommands.checkWorld(args, sender, 'w');
        }
        catch (CommandException commandException) {
            // empty catch block
        }
        if (!RegionCommands.getPermissionModel(sender).mayForceLoadRegions()) {
            throw new CommandPermissionsException();
        }
        if (world != null) {
            RegionManager manager = RegionCommands.checkRegionManager(world);
            if (manager == null) {
                throw new CommandException("No region manager exists for world '" + world.getName() + "'.");
            }
            String description = String.format("Loading region data for '%s'.", world.getName());
            AsyncCommandBuilder.wrap((Callable)new RegionManagerLoader(manager), (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).sendMessageAfterDelay("Please wait... " + description).onSuccess(String.format("Loaded region data for '%s'", world.getName()), null).onFailure(String.format("Failed to load region data for '%s'", world.getName()), (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
        } else {
            ArrayList<RegionManager> managers = new ArrayList<RegionManager>();
            for (World w : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
                RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(w);
                if (manager == null) continue;
                managers.add(manager);
            }
            AsyncCommandBuilder.wrap((Callable)new RegionManagerLoader(managers), (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), "Loading regions for all worlds").sendMessageAfterDelay("(Please wait... loading region data for all worlds...)").onSuccess("Successfully load the region data for all worlds.", null).onFailure("Failed to load regions for all worlds", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
        }
    }

    @Command(aliases={"save", "write"}, usage="[world]", desc="Re-save regions to file", flags="w:")
    public void save(CommandContext args, Actor sender) throws CommandException {
        RegionCommands.warnAboutSaveFailures(sender);
        World world = null;
        try {
            world = RegionCommands.checkWorld(args, sender, 'w');
        }
        catch (CommandException commandException) {
            // empty catch block
        }
        if (!RegionCommands.getPermissionModel(sender).mayForceSaveRegions()) {
            throw new CommandPermissionsException();
        }
        if (world != null) {
            RegionManager manager = RegionCommands.checkRegionManager(world);
            if (manager == null) {
                throw new CommandException("No region manager exists for world '" + world.getName() + "'.");
            }
            String description = String.format("Saving region data for '%s'.", world.getName());
            AsyncCommandBuilder.wrap((Callable)new RegionManagerSaver(manager), (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).sendMessageAfterDelay("Please wait... " + description).onSuccess(String.format("Saving region data for '%s'", world.getName()), null).onFailure(String.format("Failed to save region data for '%s'", world.getName()), (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
        } else {
            ArrayList<RegionManager> managers = new ArrayList<RegionManager>();
            RegionContainer regionContainer = this.worldGuard.getPlatform().getRegionContainer();
            for (World w : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
                RegionManager manager = regionContainer.get(w);
                if (manager == null) continue;
                managers.add(manager);
            }
            AsyncCommandBuilder.wrap((Callable)new RegionManagerSaver(managers), (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), "Saving regions for all worlds").sendMessageAfterDelay("(Please wait... saving region data for all worlds...)").onSuccess("Successfully saved the region data for all worlds.", null).onFailure("Failed to save regions for all worlds", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
        }
    }

    @Command(aliases={"migratedb"}, usage="<from> <to>", flags="y", desc="Migrate from one Protection Database to another.", min=2, max=2)
    public void migrateDB(CommandContext args, Actor sender) throws CommandException {
        if (!RegionCommands.getPermissionModel(sender).mayMigrateRegionStore()) {
            throw new CommandPermissionsException();
        }
        DriverType from = Enums.findFuzzyByValue(DriverType.class, args.getString(0));
        DriverType to = Enums.findFuzzyByValue(DriverType.class, args.getString(1));
        if (from == null) {
            throw new CommandException("The value of 'from' is not a recognized type of region data database.");
        }
        if (to == null) {
            throw new CommandException("The value of 'to' is not a recognized type of region region data database.");
        }
        if (from == to) {
            throw new CommandException("It is not possible to migrate between the same types of region data databases.");
        }
        if (!args.hasFlag('y')) {
            throw new CommandException("This command is potentially dangerous.\nPlease ensure you have made a backup of your data, and then re-enter the command with -y tacked on at the end to proceed.");
        }
        ConfigurationManager config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        RegionDriver fromDriver = config.regionStoreDriverMap.get((Object)from);
        RegionDriver toDriver = config.regionStoreDriverMap.get((Object)to);
        if (fromDriver == null) {
            throw new CommandException("The driver specified as 'from' does not seem to be supported in your version of WorldGuard.");
        }
        if (toDriver == null) {
            throw new CommandException("The driver specified as 'to' does not seem to be supported in your version of WorldGuard.");
        }
        DriverMigration migration = new DriverMigration(fromDriver, toDriver, WorldGuard.getInstance().getFlagRegistry());
        LoggerToChatHandler handler = null;
        Logger minecraftLogger = null;
        if (sender instanceof LocalPlayer) {
            handler = new LoggerToChatHandler(sender);
            handler.setLevel(Level.ALL);
            minecraftLogger = Logger.getLogger("com.sk89q.worldguard");
            minecraftLogger.addHandler(handler);
        }
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            sender.print("Now performing migration... this may take a while.");
            container.migrate(migration);
            sender.print("Migration complete! This only migrated the data. If you already changed your settings to use the target driver, then WorldGuard is now using the new data. If not, you have to adjust your configuration to use the new driver and then restart your server.");
        }
        catch (MigrationException e) {
            log.log(Level.WARNING, "Failed to migrate", e);
            throw new CommandException("Error encountered while migrating: " + e.getMessage());
        }
        finally {
            if (minecraftLogger != null) {
                minecraftLogger.removeHandler(handler);
            }
        }
    }

    @Command(aliases={"migrateuuid"}, desc="Migrate loaded databases to use UUIDs", max=0)
    public void migrateUuid(CommandContext args, Actor sender) throws CommandException {
        if (!RegionCommands.getPermissionModel(sender).mayMigrateRegionNames()) {
            throw new CommandPermissionsException();
        }
        LoggerToChatHandler handler = null;
        Logger minecraftLogger = null;
        if (sender instanceof LocalPlayer) {
            handler = new LoggerToChatHandler(sender);
            handler.setLevel(Level.ALL);
            minecraftLogger = Logger.getLogger("com.sk89q.worldguard");
            minecraftLogger.addHandler(handler);
        }
        try {
            ConfigurationManager config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionDriver driver = container.getDriver();
            UUIDMigration migration = new UUIDMigration(driver, WorldGuard.getInstance().getProfileService(), WorldGuard.getInstance().getFlagRegistry());
            migration.setKeepUnresolvedNames(config.keepUnresolvedNames);
            sender.print("Now performing migration... this may take a while.");
            container.migrate(migration);
            sender.print("Migration complete!");
        }
        catch (MigrationException e) {
            log.log(Level.WARNING, "Failed to migrate", e);
            throw new CommandException("Error encountered while migrating: " + e.getMessage());
        }
        finally {
            if (minecraftLogger != null) {
                minecraftLogger.removeHandler(handler);
            }
        }
    }

    @Command(aliases={"migrateheights"}, usage="[world]", max=1, flags="yw:", desc="Migrate regions from old height limits to new height limits")
    public void migrateHeights(CommandContext args, Actor sender) throws CommandException {
        if (!RegionCommands.getPermissionModel(sender).mayMigrateRegionHeights()) {
            throw new CommandPermissionsException();
        }
        if (!args.hasFlag('y')) {
            throw new CommandException("This command is potentially dangerous.\nPlease ensure you have made a backup of your data, and then re-enter the command with -y tacked on at the end to proceed.");
        }
        World world = null;
        try {
            world = RegionCommands.checkWorld(args, sender, 'w');
        }
        catch (CommandException commandException) {
            // empty catch block
        }
        LoggerToChatHandler handler = null;
        Logger minecraftLogger = null;
        if (sender instanceof LocalPlayer) {
            handler = new LoggerToChatHandler(sender);
            handler.setLevel(Level.ALL);
            minecraftLogger = Logger.getLogger("com.sk89q.worldguard");
            minecraftLogger.addHandler(handler);
        }
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionDriver driver = container.getDriver();
            WorldHeightMigration migration = new WorldHeightMigration(driver, WorldGuard.getInstance().getFlagRegistry(), world);
            container.migrate(migration);
            sender.print("Migration complete!");
        }
        catch (MigrationException e) {
            log.log(Level.WARNING, "Failed to migrate", e);
            throw new CommandException("Error encountered while migrating: " + e.getMessage());
        }
        finally {
            if (minecraftLogger != null) {
                minecraftLogger.removeHandler(handler);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Command(aliases={"teleport", "tp"}, usage="[-w world] [-c|s] <id>", flags="csw:", desc="Teleports you to the location associated with the region.", min=1, max=1)
    public void teleport(CommandContext args, Actor sender) throws CommandException {
        String message;
        Location teleportLocation;
        LocalPlayer player = this.worldGuard.checkPlayer(sender);
        World world = RegionCommands.checkWorld(args, (Actor)player, 'w');
        RegionManager regionManager = RegionCommands.checkRegionManager(world);
        ProtectedRegion existing = RegionCommands.checkExistingRegion(regionManager, args.getString(0), true);
        if (!RegionCommands.getPermissionModel((Actor)player).mayTeleportTo(existing)) {
            throw new CommandPermissionsException();
        }
        if (args.hasFlag('s')) {
            teleportLocation = FlagValueCalculator.getEffectiveFlagOf(existing, Flags.SPAWN_LOC, player);
            if (teleportLocation == null) {
                throw new CommandException("The region has no spawn point associated.");
            }
        } else if (args.hasFlag('c')) {
            if (!RegionCommands.getPermissionModel((Actor)player).mayTeleportToCenter(existing)) {
                throw new CommandPermissionsException();
            }
            Region region = WorldEditRegionConverter.convertToRegion(existing);
            if (region == null || region.getCenter() == null) {
                throw new CommandException("The region has no center point.");
            }
            if (player.getGameMode() != GameModes.SPECTATOR) throw new CommandException("Center teleport is only available in Spectator gamemode.");
            teleportLocation = new Location((Extent)world, region.getCenter(), 0.0f, 0.0f);
        } else {
            teleportLocation = FlagValueCalculator.getEffectiveFlagOf(existing, Flags.TELE_LOC, player);
            if (teleportLocation == null) {
                throw new CommandException("The region has no teleport point associated.");
            }
        }
        if ((message = FlagValueCalculator.getEffectiveFlagOf(existing, Flags.TELE_MESSAGE, player)) == null) {
            message = Flags.TELE_MESSAGE.getDefault();
        }
        player.teleport(teleportLocation, message.replace("%id%", existing.getId()), "Unable to teleport to region '" + existing.getId() + "'.");
    }

    @Command(aliases={"toggle-bypass", "bypass"}, usage="[on|off]", desc="Toggle region bypassing, effectively ignoring bypass permissions.")
    public void toggleBypass(CommandContext args, Actor sender) throws CommandException {
        boolean shouldEnableBypass;
        LocalPlayer player = this.worldGuard.checkPlayer(sender);
        if (!player.hasPermission("worldguard.region.toggle-bypass")) {
            throw new CommandPermissionsException();
        }
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player);
        if (args.argsLength() > 0) {
            String arg1 = args.getString(0);
            if (!arg1.equalsIgnoreCase("on") && !arg1.equalsIgnoreCase("off")) {
                throw new CommandException("Allowed optional arguments are: on, off");
            }
            shouldEnableBypass = arg1.equalsIgnoreCase("on");
        } else {
            shouldEnableBypass = session.hasBypassDisabled();
        }
        if (shouldEnableBypass) {
            session.setBypassDisabled(false);
            player.print("You are now bypassing region protection (as long as you have permission).");
        } else {
            session.setBypassDisabled(true);
            player.print("You are no longer bypassing region protection.");
        }
    }

    private static class FlagListBuilder
    implements Callable<Component> {
        private final FlagRegistry flagRegistry;
        private final RegionPermissionModel permModel;
        private final ProtectedRegion existing;
        private final World world;
        private final String regionId;
        private final Actor sender;
        private final String flagName;

        FlagListBuilder(FlagRegistry flagRegistry, RegionPermissionModel permModel, ProtectedRegion existing, World world, String regionId, Actor sender, String flagName) {
            this.flagRegistry = flagRegistry;
            this.permModel = permModel;
            this.existing = existing;
            this.world = world;
            this.regionId = regionId;
            this.sender = sender;
            this.flagName = flagName;
        }

        @Override
        public Component call() {
            ArrayList<String> flagList = new ArrayList<String>();
            for (Flag flag : this.flagRegistry) {
                if (!this.permModel.maySetFlag(this.existing, flag)) continue;
                flagList.add(flag.getName());
            }
            Collections.sort(flagList);
            TextComponent.Builder builder = TextComponent.builder((String)"Available flags: ");
            HoverEvent clickToSet = HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to set"));
            for (int i = 0; i < flagList.size(); ++i) {
                String flag = (String)flagList.get(i);
                builder.append(((TextComponent)TextComponent.of((String)flag, (TextColor)(i % 2 == 0 ? TextColor.GRAY : TextColor.WHITE)).hoverEvent(clickToSet)).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg flag -w \"" + this.world.getName() + "\" " + this.regionId + " " + flag + " "))));
                if (i >= flagList.size() + 1) continue;
                builder.append((Component)TextComponent.of((String)", "));
            }
            Component ret = ((TextComponent)ErrorFormat.wrap((String[])new String[]{"Unknown flag specified: " + this.flagName}).append((Component)TextComponent.newline())).append((Component)builder.build());
            if (this.sender.isPlayer()) {
                return ret.append(TextComponent.of((String)"Or use the command ", (TextColor)TextColor.LIGHT_PURPLE).append(TextComponent.of((String)("/rg flags " + this.regionId), (TextColor)TextColor.AQUA).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg flags -w \"" + this.world.getName() + "\" " + this.regionId)))));
            }
            return ret;
        }
    }
}

