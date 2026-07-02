/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableSetMultimap;
import com.sk89q.worldedit.regions.factory.CuboidRegionFactory;
import com.sk89q.worldedit.regions.factory.CylinderRegionFactory;
import com.sk89q.worldedit.regions.factory.RegionFactory;
import com.sk89q.worldedit.regions.factory.SphereRegionFactory;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.MultiKeyConverter;
import org.enginehub.piston.inject.Key;

public class RegionFactoryConverter {
    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(RegionFactory.class), MultiKeyConverter.builder(ImmutableSetMultimap.builder().put((Object)new CuboidRegionFactory(), (Object)"cuboid").put((Object)new SphereRegionFactory(), (Object)"sphere").putAll((Object)new CylinderRegionFactory(1.0), (Object[])new String[]{"cyl", "cylinder"}).build()).errorMessage(arg -> "Not a known region type: " + arg).build());
    }

    private RegionFactoryConverter() {
    }
}

