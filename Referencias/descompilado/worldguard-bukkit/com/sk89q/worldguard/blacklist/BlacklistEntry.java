/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist;

import com.google.common.cache.LoadingCache;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.blacklist.TrackedEvent;
import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class BlacklistEntry {
    private Blacklist blacklist;
    private Set<String> ignoreGroups;
    private Set<String> ignorePermissions;
    private Map<Class<? extends BlacklistEvent>, List<Action>> actions = new HashMap<Class<? extends BlacklistEvent>, List<Action>>();
    private String message;
    private String comment;

    public BlacklistEntry(Blacklist blacklist) {
        this.blacklist = blacklist;
    }

    public String[] getIgnoreGroups() {
        return this.ignoreGroups.toArray(new String[this.ignoreGroups.size()]);
    }

    public String[] getIgnorePermissions() {
        return this.ignorePermissions.toArray(new String[this.ignorePermissions.size()]);
    }

    public void setIgnoreGroups(String[] ignoreGroups) {
        HashSet<String> ignoreGroupsSet = new HashSet<String>();
        for (String group : ignoreGroups) {
            ignoreGroupsSet.add(group.toLowerCase());
        }
        this.ignoreGroups = ignoreGroupsSet;
    }

    public void setIgnorePermissions(String[] ignorePermissions) {
        HashSet<String> ignorePermissionsSet = new HashSet<String>();
        Collections.addAll(ignorePermissionsSet, ignorePermissions);
        this.ignorePermissions = ignorePermissionsSet;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean shouldIgnore(@Nullable LocalPlayer player) {
        if (player == null) {
            return false;
        }
        if (this.ignoreGroups != null) {
            for (String group : player.getGroups()) {
                if (!this.ignoreGroups.contains(group.toLowerCase())) continue;
                return true;
            }
        }
        if (this.ignorePermissions != null) {
            for (String perm : this.ignorePermissions) {
                if (!player.hasPermission(perm)) continue;
                return true;
            }
        }
        return false;
    }

    public List<Action> getActions(Class<? extends BlacklistEvent> eventCls) {
        return this.actions.computeIfAbsent(eventCls, k -> new ArrayList());
    }

    public boolean check(boolean useAsWhitelist, BlacklistEvent event, boolean forceRepeat, boolean silent) {
        boolean ret;
        LocalPlayer player = event.getPlayer();
        if (this.shouldIgnore(player)) {
            return true;
        }
        boolean repeating = false;
        String eventCacheKey = event.getCauseName();
        LoadingCache<String, TrackedEvent> repeatingEventCache = this.blacklist.getRepeatingEventCache();
        TrackedEvent tracked = (TrackedEvent)repeatingEventCache.getUnchecked((Object)eventCacheKey);
        if (tracked.matches(event)) {
            repeating = true;
        } else {
            tracked.setLastEvent(event);
            tracked.resetTimer();
        }
        List<Action> actions = this.getActions(event.getClass());
        boolean bl = ret = !useAsWhitelist;
        if (actions == null) {
            return ret;
        }
        for (Action action : actions) {
            ActionResult result = action.apply(event, silent, repeating, forceRepeat);
            switch (result) {
                case INHERIT: {
                    break;
                }
                case ALLOW: {
                    ret = true;
                    break;
                }
                case DENY: {
                    ret = false;
                    break;
                }
                case ALLOW_OVERRIDE: {
                    return true;
                }
                case DENY_OVERRIDE: {
                    return false;
                }
            }
        }
        return ret;
    }
}

