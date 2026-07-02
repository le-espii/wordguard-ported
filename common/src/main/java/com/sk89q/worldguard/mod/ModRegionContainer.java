package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ModRegionContainer extends RegionContainer {
    @Override
    protected RegionManager load(World world) {
        if (this.container == null) {
            return null;
        }
        return this.container.load(world.getName());
    }

    @Override
    public RegionManager get(World world) {
        if (this.container == null) {
            return null;
        }
        return super.get(world);
    }

    @Override
    public List<RegionManager> getLoaded() {
        if (this.container == null) {
            return Collections.emptyList();
        }
        return super.getLoaded();
    }

    @Override
    public Set<RegionManager> getSaveFailures() {
        if (this.container == null) {
            return Collections.emptySet();
        }
        return super.getSaveFailures();
    }

    @Override
    public RegionDriver getDriver() {
        if (this.container == null) {
            return null;
        }
        return super.getDriver();
    }

    @Override
    public void unload(World world) {
        if (this.container == null) {
            return;
        }
        super.unload(world);
    }

    @Override
    public void unload() {
        if (this.container == null) {
            return;
        }
        super.unload();
    }
}
