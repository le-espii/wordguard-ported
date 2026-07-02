/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.Vector3
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.HashMap;
import java.util.Map;

public class VectorFlag
extends Flag<Vector3> {
    public VectorFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public VectorFlag(String name) {
        super(name);
    }

    @Override
    public Vector3 parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if ("here".equalsIgnoreCase(input)) {
            return context.getPlayerSender().getLocation().toVector();
        }
        String[] split = input.split(",");
        if (split.length == 3) {
            try {
                return Vector3.at((double)Double.parseDouble(split[0]), (double)Double.parseDouble(split[1]), (double)Double.parseDouble(split[2]));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new InvalidFlagFormat("Expected 'here' or x,y,z.");
    }

    @Override
    public Vector3 unmarshal(Object o) {
        if (o instanceof Map) {
            Map map = (Map)o;
            Object rawX = map.get("x");
            Object rawY = map.get("y");
            Object rawZ = map.get("z");
            if (rawX == null || rawY == null || rawZ == null) {
                return null;
            }
            return Vector3.at((double)this.toNumber(rawX), (double)this.toNumber(rawY), (double)this.toNumber(rawZ));
        }
        return null;
    }

    @Override
    public Object marshal(Vector3 o) {
        HashMap<String, Double> vec = new HashMap<String, Double>();
        vec.put("x", o.x());
        vec.put("y", o.y());
        vec.put("z", o.z());
        return vec;
    }

    private double toNumber(Object o) {
        if (o instanceof Integer) {
            return ((Integer)o).intValue();
        }
        if (o instanceof Long) {
            return ((Long)o).longValue();
        }
        if (o instanceof Float) {
            return ((Float)o).floatValue();
        }
        if (o instanceof Double) {
            return (Double)o;
        }
        return 0.0;
    }
}

