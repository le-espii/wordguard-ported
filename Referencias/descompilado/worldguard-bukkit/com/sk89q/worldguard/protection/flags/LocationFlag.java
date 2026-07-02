/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.math.Vector3
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.LazyLocation;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import java.util.Map;

public class LocationFlag
extends Flag<Location> {
    public LocationFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public LocationFlag(String name) {
        super(name);
    }

    @Override
    public Location parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        LocalPlayer player = context.getPlayerSender();
        LazyLocation loc = null;
        if ("here".equalsIgnoreCase(input)) {
            Location playerLoc = player.getLocation();
            loc = new LazyLocation(((World)playerLoc.getExtent()).getName(), playerLoc.toVector(), playerLoc.getYaw(), playerLoc.getPitch());
        } else {
            String[] split = input.split(",");
            if (split.length >= 3) {
                try {
                    World world = player.getWorld();
                    double x = Double.parseDouble(split[0]);
                    double y = Double.parseDouble(split[1]);
                    double z = Double.parseDouble(split[2]);
                    float yaw = split.length < 4 ? 0.0f : Float.parseFloat(split[3]);
                    float pitch = split.length < 5 ? 0.0f : Float.parseFloat(split[4]);
                    loc = new LazyLocation(world.getName(), Vector3.at((double)x, (double)y, (double)z), yaw, pitch);
                }
                catch (NumberFormatException world) {
                    // empty catch block
                }
            }
        }
        if (loc != null) {
            Object obj = context.get("region");
            if (obj instanceof ProtectedRegion) {
                ProtectedRegion rg = (ProtectedRegion)obj;
                if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().get((World)player.getWorld()).boundedLocationFlags) {
                    if (!rg.contains(loc.toVector().toBlockPoint())) {
                        if (new RegionPermissionModel((Actor)player).mayOverrideLocationFlagBounds(rg)) {
                            player.printDebug("WARNING: Flag location is outside of region.");
                        } else {
                            throw new InvalidFlagFormat("You can't set that flag outside of the region boundaries.");
                        }
                    }
                    loc.setPosition(loc.toVector().clampY(0, player.getWorld().getMaxY()));
                    return loc;
                }
            }
            return loc;
        }
        throw new InvalidFlagFormat("Expected 'here' or x,y,z.");
    }

    @Override
    public Location unmarshal(Object o) {
        if (o instanceof Map) {
            Map map = (Map)o;
            Object rawWorld = map.get("world");
            if (rawWorld == null) {
                return null;
            }
            Object rawX = map.get("x");
            if (rawX == null) {
                return null;
            }
            Object rawY = map.get("y");
            if (rawY == null) {
                return null;
            }
            Object rawZ = map.get("z");
            if (rawZ == null) {
                return null;
            }
            Object rawYaw = map.get("yaw");
            if (rawYaw == null) {
                return null;
            }
            Object rawPitch = map.get("pitch");
            if (rawPitch == null) {
                return null;
            }
            Vector3 position = Vector3.at((double)this.toNumber(rawX), (double)this.toNumber(rawY), (double)this.toNumber(rawZ));
            float yaw = (float)this.toNumber(rawYaw);
            float pitch = (float)this.toNumber(rawPitch);
            return new LazyLocation(String.valueOf(rawWorld), position, yaw, pitch);
        }
        return null;
    }

    @Override
    public Object marshal(Location o) {
        Vector3 position = o.toVector();
        HashMap<String, Object> vec = new HashMap<String, Object>();
        if (o instanceof LazyLocation) {
            vec.put("world", ((LazyLocation)o).getWorldName());
        } else {
            try {
                if (o.getExtent() instanceof World) {
                    vec.put("world", ((World)o.getExtent()).getName());
                }
            }
            catch (NullPointerException e) {
                return null;
            }
        }
        vec.put("x", position.x());
        vec.put("y", position.y());
        vec.put("z", position.z());
        vec.put("yaw", Float.valueOf(o.getYaw()));
        vec.put("pitch", Float.valueOf(o.getPitch()));
        return vec;
    }

    private double toNumber(Object o) {
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        return 0.0;
    }
}

