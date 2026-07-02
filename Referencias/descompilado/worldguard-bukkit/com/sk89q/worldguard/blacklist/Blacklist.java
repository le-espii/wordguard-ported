/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package com.sk89q.worldguard.blacklist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.BlacklistLoggerHandler;
import com.sk89q.worldguard.blacklist.MatcherIndex;
import com.sk89q.worldguard.blacklist.TrackedEvent;
import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionType;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.event.EventType;
import com.sk89q.worldguard.blacklist.target.TargetMatcher;
import com.sk89q.worldguard.blacklist.target.TargetMatcherParseException;
import com.sk89q.worldguard.blacklist.target.TargetMatcherParser;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.util.formatting.component.BlacklistNotify;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Blacklist {
    private static final Logger log = Logger.getLogger(Blacklist.class.getCanonicalName());
    private MatcherIndex index = MatcherIndex.getEmptyInstance();
    private final BlacklistLoggerHandler blacklistLogger = new BlacklistLoggerHandler();
    private BlacklistEvent lastEvent;
    private boolean useAsWhitelist;
    private LoadingCache<String, TrackedEvent> repeatingEventCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterAccess(30L, TimeUnit.SECONDS).build(CacheLoader.from(TrackedEvent::new));

    public Blacklist(boolean useAsWhitelist) {
        this.useAsWhitelist = useAsWhitelist;
    }

    public boolean isEmpty() {
        return this.index.isEmpty();
    }

    public int getItemCount() {
        return this.index.size();
    }

    public boolean isWhitelist() {
        return this.useAsWhitelist;
    }

    public BlacklistLoggerHandler getLogger() {
        return this.blacklistLogger;
    }

    public boolean check(BlacklistEvent event, boolean forceRepeat, boolean silent) {
        List<BlacklistEntry> entries = this.index.getEntries(event.getTarget());
        if (entries == null) {
            return true;
        }
        boolean ret = true;
        for (BlacklistEntry entry : entries) {
            if (entry.check(this.useAsWhitelist, event, forceRepeat, silent)) continue;
            ret = false;
        }
        return ret;
    }

    public void load(File file) throws IOException {
        MatcherIndex.Builder builder = new MatcherIndex.Builder();
        TargetMatcherParser targetMatcherParser = new TargetMatcherParser();
        try (FileReader input = new FileReader(file);){
            String line;
            BufferedReader buff = new BufferedReader(input);
            ArrayList<BlacklistEntry> currentEntries = null;
            while ((line = buff.readLine()) != null) {
                if ((line = line.trim()).isEmpty() || line.charAt(0) == ';' || line.charAt(0) == '#') continue;
                if (line.matches("^\\[.*\\]$")) {
                    String[] items = line.substring(1, line.length() - 1).split(",");
                    currentEntries = new ArrayList<BlacklistEntry>();
                    for (String item : items) {
                        try {
                            TargetMatcher matcher = targetMatcherParser.fromInput(item.trim());
                            BlacklistEntry entry = new BlacklistEntry(this);
                            builder.add(matcher, entry);
                            currentEntries.add(entry);
                        }
                        catch (TargetMatcherParseException e) {
                            log.log(Level.WARNING, "Could not parse a block/item heading: " + e.getMessage());
                        }
                    }
                    continue;
                }
                if (currentEntries != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 1) {
                        log.log(Level.WARNING, "Found option with no value " + file.getName() + " for '" + line + "'");
                        continue;
                    }
                    boolean unknownOption = false;
                    for (BlacklistEntry entry : currentEntries) {
                        if (parts[0].equalsIgnoreCase("ignore-groups")) {
                            entry.setIgnoreGroups(parts[1].split(","));
                            continue;
                        }
                        if (parts[0].equalsIgnoreCase("ignore-perms")) {
                            entry.setIgnorePermissions(parts[1].split(","));
                            continue;
                        }
                        if (parts[0].equalsIgnoreCase("message")) {
                            entry.setMessage(CommandUtils.replaceColorMacros(parts[1].trim()));
                            continue;
                        }
                        if (parts[0].equalsIgnoreCase("comment")) {
                            entry.setComment(CommandUtils.replaceColorMacros(parts[1].trim()));
                            continue;
                        }
                        boolean found = false;
                        for (EventType type : EventType.values()) {
                            if (!type.getRuleName().equalsIgnoreCase(parts[0])) continue;
                            entry.getActions(type.getEventClass()).addAll(this.parseActions(entry, parts[1]));
                            found = true;
                            break;
                        }
                        if (found) continue;
                        unknownOption = true;
                    }
                    if (!unknownOption) continue;
                    log.log(Level.WARNING, "Unknown option '" + parts[0] + "' in " + file.getName() + " for '" + line + "'");
                    continue;
                }
                log.log(Level.WARNING, "Found option with no heading " + file.getName() + " for '" + line + "'");
            }
            this.index = builder.build();
        }
    }

    private List<Action> parseActions(BlacklistEntry entry, String raw) {
        String[] split = raw.split(",");
        ArrayList<Action> actions = new ArrayList<Action>();
        for (String name : split) {
            name = name.trim();
            boolean found = false;
            for (ActionType type : ActionType.values()) {
                if (!type.getActionName().equalsIgnoreCase(name)) continue;
                actions.add(type.parseInput(this, entry));
                found = true;
                break;
            }
            if (found) continue;
            log.log(Level.WARNING, "Unknown blacklist action: " + name);
        }
        return actions;
    }

    public BlacklistEvent getLastEvent() {
        return this.lastEvent;
    }

    public void notify(BlacklistEvent event, String comment) {
        this.lastEvent = event;
        WorldGuard.getInstance().getPlatform().broadcastNotification(new BlacklistNotify(event, comment).create());
    }

    public LoadingCache<String, TrackedEvent> getRepeatingEventCache() {
        return this.repeatingEventCache;
    }
}

