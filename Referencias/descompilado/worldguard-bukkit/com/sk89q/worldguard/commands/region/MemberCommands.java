/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissionsException
 *  com.sk89q.worldedit.command.util.AsyncCommandBuilder
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.internal.command.exception.ExceptionConverter
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.commands.region;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.region.RegionCommandsBase;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import java.util.concurrent.Callable;

public class MemberCommands
extends RegionCommandsBase {
    private final WorldGuard worldGuard;

    public MemberCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"addmember", "addmember", "addmem", "am"}, usage="<id> <members...>", flags="nw:", desc="Add a member to a region", min=2)
    public void addMember(CommandContext args, Actor sender) throws CommandException {
        MemberCommands.warnAboutSaveFailures(sender);
        World world = MemberCommands.checkWorld(args, sender, 'w');
        String id = args.getString(0);
        RegionManager manager = MemberCommands.checkRegionManager(world);
        ProtectedRegion region = MemberCommands.checkExistingRegion(manager, id, true);
        if (!MemberCommands.getPermissionModel(sender).mayAddMembers(region)) {
            throw new CommandPermissionsException();
        }
        DomainInputResolver resolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), args.getParsedPaddedSlice(1, 0));
        resolver.setLocatorPolicy(args.hasFlag('n') ? DomainInputResolver.UserLocatorPolicy.NAME_ONLY : DomainInputResolver.UserLocatorPolicy.UUID_ONLY);
        String description = String.format("Adding members to the region '%s' on '%s'", region.getId(), world.getName());
        AsyncCommandBuilder.wrap((Callable)resolver, (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).onSuccess(String.format("Region '%s' updated with new members.", region.getId()), region.getMembers()::addAll).onFailure("Failed to add new members", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }

    @Command(aliases={"addowner", "addowner", "ao"}, usage="<id> <owners...>", flags="nw:", desc="Add an owner to a region", min=2)
    public void addOwner(CommandContext args, Actor sender) throws CommandException {
        MemberCommands.warnAboutSaveFailures(sender);
        World world = MemberCommands.checkWorld(args, sender, 'w');
        String id = args.getString(0);
        RegionManager manager = MemberCommands.checkRegionManager(world);
        ProtectedRegion region = MemberCommands.checkExistingRegion(manager, id, true);
        if (!MemberCommands.getPermissionModel(sender).mayAddOwners(region)) {
            throw new CommandPermissionsException();
        }
        DomainInputResolver resolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), args.getParsedPaddedSlice(1, 0));
        resolver.setLocatorPolicy(args.hasFlag('n') ? DomainInputResolver.UserLocatorPolicy.NAME_ONLY : DomainInputResolver.UserLocatorPolicy.UUID_ONLY);
        String description = String.format("Adding owners to the region '%s' on '%s'", region.getId(), world.getName());
        AsyncCommandBuilder.wrap(MemberCommands.checkedAddOwners(sender, manager, region, world, resolver), (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).onSuccess(String.format("Region '%s' updated with new owners.", region.getId()), region.getOwners()::addAll).onFailure("Failed to add new owners", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }

    private static Callable<DefaultDomain> checkedAddOwners(Actor sender, RegionManager manager, ProtectedRegion region, World world, DomainInputResolver resolver) {
        return () -> {
            int maxRegionCount;
            LocalPlayer player;
            DefaultDomain owners = resolver.call();
            if (sender instanceof LocalPlayer && owners.contains(player = (LocalPlayer)sender) && !sender.hasPermission("worldguard.region.unlimited") && (maxRegionCount = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world).getMaxRegionCount(player)) >= 0 && manager.getRegionCountOfPlayer(player) >= maxRegionCount) {
                throw new CommandException("You already own the maximum allowed amount of regions.");
            }
            if (region.getOwners().size() == 0) {
                boolean anyOwners = false;
                ProtectedRegion parent = region;
                while ((parent = parent.getParent()) != null) {
                    if (parent.getOwners().size() <= 0) continue;
                    anyOwners = true;
                    break;
                }
                if (!anyOwners) {
                    sender.checkPermission("worldguard.region.addowner.unclaimed." + region.getId().toLowerCase());
                }
            }
            return owners;
        };
    }

    @Command(aliases={"removemember", "remmember", "removemem", "remmem", "rm"}, usage="<id> <owners...>", flags="naw:", desc="Remove an owner to a region", min=1)
    public void removeMember(CommandContext args, Actor sender) throws CommandException {
        DomainInputResolver callable;
        MemberCommands.warnAboutSaveFailures(sender);
        World world = MemberCommands.checkWorld(args, sender, 'w');
        String id = args.getString(0);
        RegionManager manager = MemberCommands.checkRegionManager(world);
        ProtectedRegion region = MemberCommands.checkExistingRegion(manager, id, true);
        if (!MemberCommands.getPermissionModel(sender).mayRemoveMembers(region)) {
            throw new CommandPermissionsException();
        }
        if (args.hasFlag('a')) {
            callable = region::getMembers;
        } else {
            if (args.argsLength() < 2) {
                throw new CommandException("List some names to remove, or use -a to remove all.");
            }
            DomainInputResolver resolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), args.getParsedPaddedSlice(1, 0));
            resolver.setLocatorPolicy(args.hasFlag('n') ? DomainInputResolver.UserLocatorPolicy.NAME_ONLY : DomainInputResolver.UserLocatorPolicy.UUID_AND_NAME);
            callable = resolver;
        }
        String description = String.format("Removing members from the region '%s' on '%s'", region.getId(), world.getName());
        AsyncCommandBuilder.wrap((Callable)callable, (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).sendMessageAfterDelay("(Please wait... querying player names...)").onSuccess(String.format("Region '%s' updated with members removed.", region.getId()), region.getMembers()::removeAll).onFailure("Failed to remove members", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }

    @Command(aliases={"removeowner", "remowner", "ro"}, usage="<id> <owners...>", flags="naw:", desc="Remove an owner to a region", min=1)
    public void removeOwner(CommandContext args, Actor sender) throws CommandException {
        DomainInputResolver callable;
        MemberCommands.warnAboutSaveFailures(sender);
        World world = MemberCommands.checkWorld(args, sender, 'w');
        String id = args.getString(0);
        RegionManager manager = MemberCommands.checkRegionManager(world);
        ProtectedRegion region = MemberCommands.checkExistingRegion(manager, id, true);
        if (!MemberCommands.getPermissionModel(sender).mayRemoveOwners(region)) {
            throw new CommandPermissionsException();
        }
        if (args.hasFlag('a')) {
            callable = region::getOwners;
        } else {
            if (args.argsLength() < 2) {
                throw new CommandException("List some names to remove, or use -a to remove all.");
            }
            DomainInputResolver resolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), args.getParsedPaddedSlice(1, 0));
            resolver.setLocatorPolicy(args.hasFlag('n') ? DomainInputResolver.UserLocatorPolicy.NAME_ONLY : DomainInputResolver.UserLocatorPolicy.UUID_AND_NAME);
            callable = resolver;
        }
        String description = String.format("Removing owners from the region '%s' on '%s'", region.getId(), world.getName());
        AsyncCommandBuilder.wrap((Callable)callable, (Actor)sender).registerWithSupervisor(this.worldGuard.getSupervisor(), description).sendMessageAfterDelay("(Please wait... querying player names...)").onSuccess(String.format("Region '%s' updated with owners removed.", region.getId()), region.getOwners()::removeAll).onFailure("Failed to remove owners", (ExceptionConverter)this.worldGuard.getExceptionConverter()).buildAndExec(this.worldGuard.getExecutorService());
    }
}

