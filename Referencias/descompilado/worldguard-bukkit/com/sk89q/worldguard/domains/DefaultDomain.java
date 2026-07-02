/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.domains;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.Domain;
import com.sk89q.worldguard.domains.GroupDomain;
import com.sk89q.worldguard.domains.PlayerDomain;
import com.sk89q.worldguard.util.ChangeTracked;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class DefaultDomain
implements Domain,
ChangeTracked {
    private PlayerDomain playerDomain = new PlayerDomain();
    private GroupDomain groupDomain = new GroupDomain();

    public DefaultDomain() {
    }

    public DefaultDomain(DefaultDomain existing) {
        this.setPlayerDomain(existing.getPlayerDomain());
        this.setGroupDomain(existing.getGroupDomain());
    }

    public PlayerDomain getPlayerDomain() {
        return this.playerDomain;
    }

    public void setPlayerDomain(PlayerDomain playerDomain) {
        Preconditions.checkNotNull((Object)playerDomain);
        this.playerDomain = new PlayerDomain(playerDomain);
    }

    public GroupDomain getGroupDomain() {
        return this.groupDomain;
    }

    public void setGroupDomain(GroupDomain groupDomain) {
        Preconditions.checkNotNull((Object)groupDomain);
        this.groupDomain = new GroupDomain(groupDomain);
    }

    public void addPlayer(String name) {
        this.playerDomain.addPlayer(name);
    }

    public void removePlayer(String name) {
        this.playerDomain.removePlayer(name);
    }

    public void removePlayer(UUID uuid) {
        this.playerDomain.removePlayer(uuid);
    }

    public void addPlayer(UUID uniqueId) {
        this.playerDomain.addPlayer(uniqueId);
    }

    public void removePlayer(LocalPlayer player) {
        this.playerDomain.removePlayer(player);
    }

    public void addPlayer(LocalPlayer player) {
        this.playerDomain.addPlayer(player);
    }

    public void addAll(DefaultDomain other) {
        Preconditions.checkNotNull((Object)other);
        for (String player : other.getPlayers()) {
            this.addPlayer(player);
        }
        for (UUID uuid : other.getUniqueIds()) {
            this.addPlayer(uuid);
        }
        for (String group : other.getGroups()) {
            this.addGroup(group);
        }
    }

    public void removeAll(DefaultDomain other) {
        Preconditions.checkNotNull((Object)other);
        for (String player : other.getPlayers()) {
            this.removePlayer(player);
        }
        for (UUID uuid : other.getUniqueIds()) {
            this.removePlayer(uuid);
        }
        for (String group : other.getGroups()) {
            this.removeGroup(group);
        }
    }

    public Set<String> getPlayers() {
        return this.playerDomain.getPlayers();
    }

    public Set<UUID> getUniqueIds() {
        return this.playerDomain.getUniqueIds();
    }

    public void addGroup(String name) {
        this.groupDomain.addGroup(name);
    }

    public void removeGroup(String name) {
        this.groupDomain.removeGroup(name);
    }

    public Set<String> getGroups() {
        return this.groupDomain.getGroups();
    }

    @Override
    public boolean contains(LocalPlayer player) {
        return this.playerDomain.contains(player) || this.groupDomain.contains(player);
    }

    @Override
    public boolean contains(UUID uniqueId) {
        return this.playerDomain.contains(uniqueId);
    }

    @Override
    public boolean contains(String playerName) {
        return this.playerDomain.contains(playerName);
    }

    @Override
    public int size() {
        return this.groupDomain.size() + this.playerDomain.size();
    }

    @Override
    public void clear() {
        this.playerDomain.clear();
        this.groupDomain.clear();
    }

    public void removeAll() {
        this.clear();
    }

    public String toPlayersString() {
        return this.toPlayersString(null);
    }

    public String toPlayersString(@Nullable ProfileCache cache) {
        StringBuilder str = new StringBuilder();
        ArrayList<CallSite> output = new ArrayList<CallSite>();
        for (String string : this.playerDomain.getPlayers()) {
            output.add((CallSite)((Object)("name:" + string)));
        }
        if (cache != null) {
            Iterator<UUID> results = cache.getAllPresent(this.playerDomain.getUniqueIds());
            for (UUID uuid : this.playerDomain.getUniqueIds()) {
                Profile profile = (Profile)results.get(uuid);
                if (profile != null) {
                    output.add((CallSite)((Object)(profile.getName() + "*")));
                    continue;
                }
                output.add((CallSite)((Object)("uuid:" + String.valueOf(uuid))));
            }
        } else {
            for (UUID uUID : this.playerDomain.getUniqueIds()) {
                output.add((CallSite)((Object)("uuid:" + String.valueOf(uUID))));
            }
        }
        output.sort(String.CASE_INSENSITIVE_ORDER);
        Iterator it = output.iterator();
        while (it.hasNext()) {
            str.append((String)it.next());
            if (!it.hasNext()) continue;
            str.append(", ");
        }
        return str.toString();
    }

    public String toGroupsString() {
        StringBuilder str = new StringBuilder();
        Iterator<String> it = this.groupDomain.getGroups().iterator();
        while (it.hasNext()) {
            str.append("g:");
            str.append(it.next());
            if (!it.hasNext()) continue;
            str.append(", ");
        }
        return str.toString();
    }

    public String toUserFriendlyString() {
        StringBuilder str = new StringBuilder();
        if (this.playerDomain.size() > 0) {
            str.append(this.toPlayersString());
        }
        if (this.groupDomain.size() > 0) {
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(this.toGroupsString());
        }
        return str.toString();
    }

    public String toUserFriendlyString(ProfileCache cache) {
        StringBuilder str = new StringBuilder();
        if (this.playerDomain.size() > 0) {
            str.append(this.toPlayersString(cache));
        }
        if (this.groupDomain.size() > 0) {
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(this.toGroupsString());
        }
        return str.toString();
    }

    public Component toUserFriendlyComponent(@Nullable ProfileCache cache) {
        TextComponent.Builder builder = TextComponent.builder((String)"");
        if (this.playerDomain.size() > 0) {
            builder.append(this.toPlayersComponent(cache));
        }
        if (this.groupDomain.size() > 0) {
            if (this.playerDomain.size() > 0) {
                builder.append((Component)TextComponent.of((String)"; "));
            }
            builder.append(this.toGroupsComponent());
        }
        return builder.build();
    }

    private Component toGroupsComponent() {
        TextComponent.Builder builder = TextComponent.builder((String)"");
        Iterator<String> it = this.groupDomain.getGroups().iterator();
        while (it.hasNext()) {
            ((TextComponent.Builder)builder.append((Component)TextComponent.of((String)"g:", (TextColor)TextColor.GRAY))).append((Component)TextComponent.of((String)it.next(), (TextColor)TextColor.GOLD));
            if (!it.hasNext()) continue;
            builder.append((Component)TextComponent.of((String)", "));
        }
        return ((TextComponent)builder.build()).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Groups")));
    }

    private Component toPlayersComponent(ProfileCache cache) {
        ArrayList uuids = Lists.newArrayList();
        HashMap profileMap = Maps.newHashMap();
        for (String string : this.playerDomain.getPlayers()) {
            profileMap.put(string, null);
        }
        if (cache != null) {
            Iterator<UUID> results = cache.getAllPresent(this.playerDomain.getUniqueIds());
            for (UUID uuid : this.playerDomain.getUniqueIds()) {
                Profile profile = (Profile)results.get(uuid);
                if (profile != null) {
                    profileMap.put(profile.getName(), uuid);
                    continue;
                }
                uuids.add(uuid.toString());
            }
        } else {
            for (UUID uUID : this.playerDomain.getUniqueIds()) {
                uuids.add(uUID.toString());
            }
        }
        TextComponent.Builder builder = TextComponent.builder((String)"");
        Iterator iterator = profileMap.keySet().stream().sorted().map(name -> {
            UUID uuid = (UUID)profileMap.get(name);
            if (uuid == null) {
                return (TextComponent)((TextComponent)TextComponent.of((String)name, (TextColor)TextColor.YELLOW).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)((TextComponent)TextComponent.of((String)"Name only", (TextColor)TextColor.GRAY).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)"Click to copy"))))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.COPY_TO_CLIPBOARD, (String)name));
            }
            return (TextComponent)((TextComponent)TextComponent.of((String)name, (TextColor)TextColor.YELLOW).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)((TextComponent)((TextComponent)TextComponent.of((String)"Last known name of uuid: ", (TextColor)TextColor.GRAY).append((Component)TextComponent.of((String)uuid.toString(), (TextColor)TextColor.WHITE))).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)"Click to copy"))))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.COPY_TO_CLIPBOARD, (String)uuid.toString()));
        }).iterator();
        while (iterator.hasNext()) {
            builder.append((Component)iterator.next());
            if (!iterator.hasNext() && uuids.isEmpty()) continue;
            builder.append((Component)TextComponent.of((String)", "));
        }
        if (!uuids.isEmpty()) {
            builder.append(((TextComponent)TextComponent.of((String)(uuids.size() + " unknown uuid" + (uuids.size() == 1 ? "" : "s")), (TextColor)TextColor.GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)((TextComponent)((TextComponent)TextComponent.of((String)"Unable to resolve the name for:", (TextColor)TextColor.GRAY).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)String.join((CharSequence)"\n", uuids), (TextColor)TextColor.WHITE))).append(TextComponent.newline().append((Component)TextComponent.of((String)"Click to copy")))))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.COPY_TO_CLIPBOARD, (String)String.join((CharSequence)",", uuids))));
        }
        return builder.build();
    }

    @Override
    public boolean isDirty() {
        return this.playerDomain.isDirty() || this.groupDomain.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.playerDomain.setDirty(dirty);
        this.groupDomain.setDirty(dirty);
    }

    public String toString() {
        return "{players=" + String.valueOf(this.playerDomain) + ", groups=" + String.valueOf(this.groupDomain) + "}";
    }
}

