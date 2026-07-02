/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.formatting.component.PaginationBox
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 */
package com.sk89q.worldguard.commands.task;

import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.Profile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegionLister
implements Callable<Integer> {
    private static final Logger log = Logger.getLogger(RegionLister.class.getCanonicalName());
    private final Actor sender;
    private final RegionManager manager;
    private final String world;
    private OwnerMatcher ownerMatcher;
    private String idFilter;
    private ProtectedRegion filterByIntersecting;
    private int page;
    private String playerName;
    private boolean nameOnly;

    public RegionLister(RegionManager manager, Actor sender, String world) {
        Preconditions.checkNotNull((Object)manager);
        Preconditions.checkNotNull((Object)sender);
        Preconditions.checkNotNull((Object)world);
        this.manager = manager;
        this.sender = sender;
        this.world = world;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void filterByIntersecting(ProtectedRegion region) {
        this.filterByIntersecting = region;
    }

    public void filterOwnedByName(String name, boolean nameOnly) {
        this.playerName = name;
        this.nameOnly = nameOnly;
        if (nameOnly) {
            this.filterOwnedByName(name);
        } else {
            this.filterOwnedByProfile(name);
        }
    }

    private void filterOwnedByName(final String name) {
        this.ownerMatcher = new OwnerMatcher(){

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isContainedWithin(DefaultDomain domain) {
                return domain.contains(name);
            }
        };
    }

    private void filterOwnedByProfile(final String name) {
        this.ownerMatcher = new OwnerMatcher(){
            private UUID uniqueId;

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isContainedWithin(DefaultDomain domain) throws CommandException {
                if (domain.contains(name)) {
                    return true;
                }
                if (this.uniqueId == null) {
                    Profile profile;
                    try {
                        profile = WorldGuard.getInstance().getProfileService().findByName(name);
                    }
                    catch (IOException e) {
                        log.log(Level.WARNING, "Failed UUID lookup of '" + name + "'", e);
                        throw new CommandException("Failed to lookup the UUID of '" + name + "'");
                    }
                    catch (InterruptedException e) {
                        log.log(Level.WARNING, "Failed UUID lookup of '" + name + "'", e);
                        throw new CommandException("The lookup the UUID of '" + name + "' was interrupted");
                    }
                    if (profile == null) {
                        throw new CommandException("A user by the name of '" + name + "' does not seem to exist.");
                    }
                    this.uniqueId = profile.getUniqueId();
                }
                return domain.contains(this.uniqueId);
            }
        };
    }

    public void filterIdByMatch(String idFilter) {
        this.idFilter = idFilter;
    }

    @Override
    public Integer call() throws Exception {
        RegionListEntry entry;
        Map<String, ProtectedRegion> regions = this.manager.getRegions();
        Collection<ProtectedRegion> iterableRegions = regions.values();
        if (this.filterByIntersecting != null) {
            iterableRegions = this.filterByIntersecting.getIntersectingRegions(iterableRegions);
        }
        ArrayList<RegionListEntry> entries = new ArrayList<RegionListEntry>();
        for (ProtectedRegion rg : iterableRegions) {
            RegionListEntry entry2;
            if (rg.getId().equals("__global__") || !(entry2 = new RegionListEntry(rg)).matches(this.idFilter) || !entry2.matches(this.ownerMatcher)) continue;
            entries.add(entry2);
        }
        if (this.ownerMatcher == null) {
            Collections.sort(entries);
        }
        if (regions.containsKey("__global__") && (entry = new RegionListEntry(regions.get("__global__"))).matches(this.idFilter) && entry.matches(this.ownerMatcher)) {
            entries.add(0, entry);
        }
        if (this.ownerMatcher != null) {
            Collections.sort(entries);
        }
        RegionPermissionModel perms = this.sender.isPlayer() ? new RegionPermissionModel(this.sender) : null;
        String title = this.ownerMatcher == null ? "Regions" : "Regions for " + this.ownerMatcher.getName();
        String cmd = "/rg list -w \"" + this.world + "\"" + (String)(this.playerName != null ? " -p " + this.playerName : "") + (this.nameOnly ? " -n" : "") + (this.filterByIntersecting != null ? " -s" : "") + (String)(this.idFilter != null ? " -i " + this.idFilter : "") + " %page%";
        RegionListBox box = new RegionListBox(title, cmd, perms, entries, this.world);
        this.sender.print(box.create(this.page));
        return this.page;
    }

    private static interface OwnerMatcher {
        public String getName();

        public boolean isContainedWithin(DefaultDomain var1) throws CommandException;
    }

    private static final class RegionListEntry
    implements Comparable<RegionListEntry> {
        private final ProtectedRegion region;
        private boolean isOwner;
        private boolean isMember;

        private RegionListEntry(ProtectedRegion rg) {
            this.region = rg;
        }

        public boolean matches(OwnerMatcher matcher) throws CommandException {
            return matcher == null || (this.isOwner = matcher.isContainedWithin(this.region.getOwners())) || (this.isMember = matcher.isContainedWithin(this.region.getMembers()));
        }

        public boolean matches(String idMatcher) {
            return idMatcher == null || this.region.getId().contains(idMatcher);
        }

        public ProtectedRegion getRegion() {
            return this.region;
        }

        public boolean isOwner() {
            return this.isOwner;
        }

        public boolean isMember() {
            return this.isMember;
        }

        @Override
        public int compareTo(RegionListEntry o) {
            if (this.isOwner != o.isOwner) {
                return this.isOwner ? -1 : 1;
            }
            if (this.isMember != o.isMember) {
                return this.isMember ? -1 : 1;
            }
            return this.region.getId().compareTo(o.region.getId());
        }
    }

    private static class RegionListBox
    extends PaginationBox {
        private final RegionPermissionModel perms;
        private final List<RegionListEntry> entries;
        private String world;

        RegionListBox(String title, String cmd, RegionPermissionModel perms, List<RegionListEntry> entries, String world) {
            super(title, cmd);
            this.perms = perms;
            this.entries = entries;
            this.world = world;
        }

        public Component getComponent(int number) {
            RegionListEntry entry = this.entries.get(number);
            TextComponent.Builder builder = (TextComponent.Builder)TextComponent.builder((String)(number + 1 + ".")).color(TextColor.LIGHT_PURPLE);
            if (entry.isOwner()) {
                ((TextComponent.Builder)builder.append((Component)TextComponent.space())).append(TextComponent.of((String)"+", (TextColor)TextColor.DARK_AQUA).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Region Owner", (TextColor)TextColor.GOLD))));
            } else if (entry.isMember()) {
                ((TextComponent.Builder)builder.append((Component)TextComponent.space())).append(TextComponent.of((String)"-", (TextColor)TextColor.AQUA).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Region Member", (TextColor)TextColor.GOLD))));
            }
            ((TextComponent.Builder)builder.append((Component)TextComponent.space())).append((Component)TextComponent.of((String)entry.getRegion().getId(), (TextColor)TextColor.GOLD));
            if (this.perms != null && this.perms.mayLookup(entry.region)) {
                builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Info]", (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click for info")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg info -w \"" + this.world + "\" " + entry.region.getId())))));
            }
            Location teleFlag = FlagValueCalculator.getEffectiveFlagOf(entry.region, Flags.TELE_LOC, this.perms != null && this.perms.getSender() instanceof RegionAssociable ? (RegionAssociable)this.perms.getSender() : null);
            if (this.perms != null && teleFlag != null && this.perms.mayTeleportTo(entry.region)) {
                builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[TP]", (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to teleport")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg tp -w \"" + this.world + "\" " + entry.region.getId())))));
            } else if (this.perms != null && this.perms.mayTeleportToCenter(entry.getRegion()) && entry.getRegion().isPhysicalArea()) {
                builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[TP-Center]", (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to teleport to the center")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg tp -c -w \"" + this.world + "\" " + entry.region.getId())))));
            }
            return builder.build();
        }

        public int getComponentsSize() {
            return this.entries.size();
        }
    }
}

