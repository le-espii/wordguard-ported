/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.collect.Maps;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public class MapFlag<K, V>
extends Flag<Map<K, V>> {
    private final Flag<K> keyFlag;
    private final Flag<V> valueFlag;

    public MapFlag(String name, Flag<K> keyFlag, Flag<V> valueFlag) {
        super(name);
        Objects.requireNonNull(keyFlag, "keyFlag cannot be null.");
        Objects.requireNonNull(valueFlag, "valueFlag cannot be null.");
        this.keyFlag = keyFlag;
        this.valueFlag = valueFlag;
    }

    public MapFlag(String name, @Nullable RegionGroup defaultGroup, Flag<K> keyFlag, Flag<V> valueFlag) {
        super(name, defaultGroup);
        Objects.requireNonNull(keyFlag, "keyFlag cannot be null.");
        Objects.requireNonNull(valueFlag, "valueFlag cannot be null.");
        this.keyFlag = keyFlag;
        this.valueFlag = valueFlag;
    }

    public Flag<K> getKeyFlag() {
        return this.keyFlag;
    }

    public Flag<V> getValueFlag() {
        return this.valueFlag;
    }

    @Override
    public Map<K, V> parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if (input.isEmpty()) {
            return Maps.newHashMap();
        }
        HashMap items = Maps.newHashMap();
        String[] stringArray = input.split(",");
        int n = stringArray.length;
        for (int i = 0; i < n; ++i) {
            String str;
            char split = (str = stringArray[i]).indexOf(61) == -1 ? (char)':' : '=';
            String[] keyVal = str.split(String.valueOf(split));
            if (keyVal.length != 2) {
                throw new InvalidFlagFormat("Input must be in a 'key:value,key1=value1' format. Either ':' or '=' can be used.");
            }
            FlagContext key = context.copyWith(null, keyVal[0], null);
            FlagContext value = context.copyWith(null, keyVal[1], null);
            items.put(this.keyFlag.parseInput(key), this.valueFlag.parseInput(value));
        }
        return items;
    }

    @Override
    public Map<K, V> unmarshal(@Nullable Object o) {
        if (o instanceof Map) {
            Map map = (Map)o;
            HashMap items = Maps.newHashMap();
            for (Map.Entry entry : map.entrySet()) {
                K keyItem = this.keyFlag.unmarshal(entry.getKey());
                V valueItem = this.valueFlag.unmarshal(entry.getValue());
                if (keyItem == null || valueItem == null) continue;
                items.put(keyItem, valueItem);
            }
            return items;
        }
        return null;
    }

    @Override
    public Object marshal(Map<K, V> o) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<K, V> entry : o.entrySet()) {
            map.put(this.keyFlag.marshal(entry.getKey()), this.valueFlag.marshal(entry.getValue()));
        }
        return map;
    }
}

