/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.sk89q.worldedit.extension.platform.Actor
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class FlagContext {
    private final Actor sender;
    private final String input;
    private Map<String, Object> context;

    private FlagContext(Actor sender, String input, Map<String, Object> values) {
        this.sender = sender;
        this.input = input;
        this.context = values;
    }

    public static FlagContextBuilder create() {
        return new FlagContextBuilder();
    }

    public void put(String name, Object value) {
        this.context.put(name, value);
    }

    public Actor getSender() {
        return this.sender;
    }

    public String getUserInput() {
        return this.input;
    }

    public LocalPlayer getPlayerSender() throws InvalidFlagFormat {
        if (this.sender.isPlayer() && this.sender instanceof LocalPlayer) {
            return (LocalPlayer)this.sender;
        }
        throw new InvalidFlagFormat("Not a player");
    }

    public Integer getUserInputAsInt() throws InvalidFlagFormat {
        try {
            return Integer.parseInt(this.input);
        }
        catch (NumberFormatException e) {
            throw new InvalidFlagFormat("Not a number: " + this.input);
        }
    }

    public Double getUserInputAsDouble() throws InvalidFlagFormat {
        try {
            return Double.parseDouble(this.input);
        }
        catch (NumberFormatException e) {
            throw new InvalidFlagFormat("Not a number: " + this.input);
        }
    }

    @Nullable
    public Object get(String name) {
        return this.get(name, null);
    }

    @Nullable
    public Object get(String name, Object defaultValue) {
        Object obj = this.context.get(name);
        return obj != null || this.context.containsKey(name) ? obj : defaultValue;
    }

    public FlagContext copyWith(@Nullable Actor commandSender, @Nullable String s, @Nullable Map<String, Object> values) {
        HashMap map = Maps.newHashMap();
        map.putAll(this.context);
        if (values != null) {
            map.putAll(values);
        }
        return new FlagContext(commandSender == null ? this.sender : commandSender, s == null ? this.input : s, map);
    }

    public static class FlagContextBuilder {
        private Actor sender;
        private String input;
        private Map<String, Object> map = Maps.newHashMap();

        public FlagContextBuilder setSender(Actor sender) {
            this.sender = sender;
            return this;
        }

        public FlagContextBuilder setInput(String input) {
            this.input = input;
            return this;
        }

        public FlagContextBuilder setObject(String key, Object value) {
            this.map.put(key, value);
            return this;
        }

        public boolean tryAddToMap(String key, Object value) {
            if (this.map.containsKey(key)) {
                return false;
            }
            this.map.put(key, value);
            return true;
        }

        public FlagContext build() {
            WorldGuard.getInstance().getPlatform().notifyFlagContextCreate(this);
            return new FlagContext(this.sender, this.input, this.map);
        }
    }
}

