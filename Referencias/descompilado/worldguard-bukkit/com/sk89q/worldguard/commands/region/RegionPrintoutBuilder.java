/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.formatting.component.ErrorFormat
 *  com.sk89q.worldedit.util.formatting.component.LabelFormat
 *  com.sk89q.worldedit.util.formatting.component.MessageBox
 *  com.sk89q.worldedit.util.formatting.component.SubtleFormat
 *  com.sk89q.worldedit.util.formatting.component.TextComponentProducer
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands.region;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.LabelFormat;
import com.sk89q.worldedit.util.formatting.component.MessageBox;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.region.FlagHelperBox;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.registry.UnknownFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public class RegionPrintoutBuilder
implements Callable<TextComponent> {
    private final String world;
    private final ProtectedRegion region;
    @Nullable
    private final ProfileCache cache;
    private final TextComponentProducer builder = new TextComponentProducer();
    private final RegionPermissionModel perms;

    public RegionPrintoutBuilder(String world, ProtectedRegion region, @Nullable ProfileCache cache, @Nullable Actor actor) {
        this.world = world;
        this.region = region;
        this.cache = cache;
        this.perms = actor != null && actor.isPlayer() ? new RegionPermissionModel(actor) : null;
    }

    public void newline() {
        this.builder.append((Component)TextComponent.newline());
    }

    public void appendBasics() {
        this.builder.append((Component)TextComponent.of((String)"Region: ", (TextColor)TextColor.BLUE));
        this.builder.append(TextComponent.of((String)this.region.getId(), (TextColor)TextColor.YELLOW).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg info -w \"" + this.world + "\" " + this.region.getId()))));
        this.builder.append((Component)TextComponent.of((String)" (type=", (TextColor)TextColor.GRAY));
        this.builder.append((Component)TextComponent.of((String)this.region.getType().getName()));
        this.builder.append((Component)TextComponent.of((String)", priority=", (TextColor)TextColor.GRAY));
        this.appendPriorityComponent(this.region);
        this.builder.append((Component)TextComponent.of((String)")", (TextColor)TextColor.GRAY));
        this.newline();
    }

    public void appendFlags() {
        this.builder.append((Component)TextComponent.of((String)"Flags: ", (TextColor)TextColor.BLUE));
        this.appendFlagsList(true);
        this.newline();
    }

    public void appendFlagsList(boolean useColors) {
        boolean hasFlags = false;
        for (Flag flag : WorldGuard.getInstance().getFlagRegistry()) {
            Object val = this.region.getFlag(flag);
            if (val == null) continue;
            if (hasFlags) {
                this.builder.append((Component)TextComponent.of((String)", "));
            }
            RegionGroupFlag groupFlag = flag.getRegionGroupFlag();
            Object group = null;
            if (groupFlag != null) {
                group = this.region.getFlag(groupFlag);
            }
            String flagString = group == null ? flag.getName() + ": " : flag.getName() + " -g " + String.valueOf(group) + ": ";
            TextColor flagColor = TextColor.WHITE;
            if (useColors) {
                flagColor = FlagHelperBox.DANGER_ZONE.contains(flag) && (!this.region.getId().equals("__global__") || flag != Flags.PASSTHROUGH) ? TextColor.DARK_RED : (Flags.INBUILT_FLAGS.contains(flag.getName()) ? TextColor.GOLD : (flag instanceof UnknownFlag ? TextColor.GRAY : TextColor.LIGHT_PURPLE));
            }
            TextComponent flagText = (TextComponent)TextComponent.of((String)flagString, (TextColor)flagColor).append((Component)TextComponent.of((String)String.valueOf(val), (TextColor)(useColors ? TextColor.YELLOW : TextColor.WHITE)));
            if (this.perms != null && this.perms.maySetFlag(this.region, flag)) {
                flagText = (TextComponent)((TextComponent)flagText.hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to set flag")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg flag -w \"" + this.world + "\" " + this.region.getId() + " " + flag.getName() + " ")));
            }
            this.builder.append((Component)flagText);
            hasFlags = true;
        }
        if (!hasFlags) {
            TextComponent noFlags = TextComponent.of((String)"(none)", (TextColor)(useColors ? TextColor.RED : TextColor.WHITE));
            this.builder.append((Component)noFlags);
        }
        if (this.perms != null && this.perms.maySetFlag(this.region)) {
            this.builder.append((Component)TextComponent.space()).append(((TextComponent)TextComponent.of((String)"[Flags]", (TextColor)(useColors ? TextColor.GREEN : TextColor.GRAY)).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to set a flag")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg flags -w \"" + this.world + "\" " + this.region.getId()))));
        }
    }

    public void appendParents() {
        this.appendParentTree(true);
    }

    public void appendParentTree(boolean useColors) {
        if (this.region.getParent() == null) {
            return;
        }
        ArrayList<ProtectedRegion> inheritance = new ArrayList<ProtectedRegion>();
        ProtectedRegion r = this.region;
        inheritance.add(r);
        while (r.getParent() != null) {
            r = r.getParent();
            inheritance.add(r);
        }
        ListIterator it = inheritance.listIterator(inheritance.size());
        ProtectedRegion last = null;
        int indent = 0;
        while (it.hasPrevious()) {
            ProtectedRegion cur = (ProtectedRegion)it.previous();
            StringBuilder namePrefix = new StringBuilder();
            if (indent != 0) {
                for (int i = 0; i < indent; ++i) {
                    namePrefix.append(" ");
                }
                namePrefix.append("\u2937");
            }
            this.builder.append((Component)TextComponent.of((String)namePrefix.toString(), (TextColor)(useColors ? TextColor.GREEN : TextColor.WHITE)));
            if (this.perms != null && this.perms.mayLookup(cur)) {
                this.builder.append(((TextComponent)TextComponent.of((String)cur.getId(), (TextColor)(useColors ? TextColor.GREEN : TextColor.WHITE)).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click for info")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg info -w \"" + this.world + "\" " + cur.getId()))));
            } else {
                this.builder.append((Component)TextComponent.of((String)cur.getId(), (TextColor)(useColors ? TextColor.GREEN : TextColor.WHITE)));
            }
            if (!cur.equals(this.region)) {
                this.builder.append((Component)TextComponent.of((String)" (parent, priority=", (TextColor)(useColors ? TextColor.GRAY : TextColor.WHITE)));
                this.appendPriorityComponent(cur);
                this.builder.append((Component)TextComponent.of((String)")", (TextColor)(useColors ? TextColor.GRAY : TextColor.WHITE)));
            }
            if (last != null && cur.equals(this.region) && this.perms != null && this.perms.maySetParent(cur, last)) {
                this.builder.append((Component)TextComponent.space());
                this.builder.append(((TextComponent)TextComponent.of((String)"[X]", (TextColor)TextColor.RED).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to unlink parent")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg setparent -w \"" + this.world + "\" " + cur.getId()))));
            }
            last = cur;
            ++indent;
            this.newline();
        }
    }

    public void appendDomain() {
        this.builder.append((Component)TextComponent.of((String)"Owners: ", (TextColor)TextColor.BLUE));
        this.addDomainString(this.region.getOwners(), this.perms != null && this.perms.mayAddOwners(this.region) ? "addowner" : null, this.perms != null && this.perms.mayRemoveOwners(this.region) ? "removeowner" : null);
        this.newline();
        this.builder.append((Component)TextComponent.of((String)"Members: ", (TextColor)TextColor.BLUE));
        this.addDomainString(this.region.getMembers(), this.perms != null && this.perms.mayAddMembers(this.region) ? "addmember" : null, this.perms != null && this.perms.mayRemoveMembers(this.region) ? "removemember" : null);
        this.newline();
    }

    private void addDomainString(DefaultDomain domain, String addCommand, String removeCommand) {
        if (domain.size() == 0) {
            this.builder.append((Component)ErrorFormat.wrap((String[])new String[]{"(none)"}));
        } else if (this.perms != null) {
            this.builder.append(domain.toUserFriendlyComponent(this.cache));
        } else {
            this.builder.append((Component)LabelFormat.wrap((String[])new String[]{domain.toUserFriendlyString(this.cache)}));
        }
        if (addCommand != null) {
            this.builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Add]", (TextColor)TextColor.GREEN).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to add a player or group")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg " + addCommand + " -w \"" + this.world + "\" " + this.region.getId() + " ")))));
        }
        if (removeCommand != null && domain.size() > 0) {
            this.builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Rem]", (TextColor)TextColor.RED).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to remove a player or group")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg " + removeCommand + " -w \"" + this.world + "\" " + this.region.getId() + " ")))));
            this.builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Clr]", (TextColor)TextColor.RED).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to clear")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg " + removeCommand + " -w \"" + this.world + "\" -a " + this.region.getId())))));
        }
    }

    public void appendBounds() {
        BlockVector3 min = this.region.getMinimumPoint();
        BlockVector3 max = this.region.getMaximumPoint();
        this.builder.append((Component)TextComponent.of((String)"Bounds:", (TextColor)TextColor.BLUE));
        TextComponent bound = TextComponent.of((String)(" " + String.valueOf(min) + " -> " + String.valueOf(max)), (TextColor)TextColor.YELLOW);
        if (this.perms != null && this.perms.maySelect(this.region)) {
            bound = (TextComponent)((TextComponent)bound.hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to select")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg select " + this.region.getId())));
        }
        this.builder.append((Component)bound);
        Location teleFlag = FlagValueCalculator.getEffectiveFlagOf(this.region, Flags.TELE_LOC, this.perms != null && this.perms.getSender() instanceof RegionAssociable ? (RegionAssociable)this.perms.getSender() : null);
        if (teleFlag != null && this.perms != null && this.perms.mayTeleportTo(this.region)) {
            this.builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Teleport]", (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)((TextComponent)TextComponent.of((String)"Click to teleport").append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)(teleFlag.getBlockX() + ", " + teleFlag.getBlockY() + ", " + teleFlag.getBlockZ())))))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg tp -w \"" + this.world + "\" " + this.region.getId())))));
        } else if (this.perms != null && this.perms.mayTeleportToCenter(this.region) && this.region.isPhysicalArea()) {
            this.builder.append(TextComponent.space().append(((TextComponent)TextComponent.of((String)"[Center Teleport]", (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to teleport to the center of the region")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)("/rg tp -c -w \"" + this.world + "\" " + this.region.getId())))));
        }
        this.newline();
    }

    private void appendPriorityComponent(ProtectedRegion rg) {
        String content = String.valueOf(rg.getPriority());
        if (this.perms != null && this.perms.maySetPriority(rg)) {
            this.builder.append(((TextComponent)TextComponent.of((String)content, (TextColor)TextColor.GOLD).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to change")))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)("/rg setpriority -w \"" + this.world + "\" " + rg.getId() + " "))));
        } else {
            this.builder.append((Component)TextComponent.of((String)content, (TextColor)TextColor.WHITE));
        }
    }

    private void appendRegionInformation() {
        this.appendBasics();
        this.appendFlags();
        this.appendParents();
        this.appendDomain();
        this.appendBounds();
        if (this.cache != null && this.perms == null) {
            this.builder.append((Component)SubtleFormat.wrap((String[])new String[]{"Any names suffixed by * are 'last seen names' and may not be up to date."}));
        }
    }

    @Override
    public TextComponent call() {
        MessageBox box = new MessageBox("Region Info", this.builder);
        this.appendRegionInformation();
        return box.create();
    }

    public void send(Actor sender) {
        sender.print((Component)this.toComponent());
    }

    public TextComponentProducer append(String str) {
        return this.builder.append((Component)TextComponent.of((String)str));
    }

    public TextComponentProducer append(TextComponent component) {
        return this.builder.append((Component)component);
    }

    public TextComponent toComponent() {
        return this.builder.create();
    }

    public String toString() {
        return this.builder.toString().trim();
    }
}

