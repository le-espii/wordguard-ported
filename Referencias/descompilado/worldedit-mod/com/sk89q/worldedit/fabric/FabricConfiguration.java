/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.util.PropertiesConfiguration;
import java.nio.file.Path;

public class FabricConfiguration
extends PropertiesConfiguration {
    public boolean creativeEnable = false;
    public boolean cheatMode = false;

    public FabricConfiguration(FabricWorldEdit mod) {
        super(mod.getWorkingDir().resolve("worldedit.properties"));
    }

    @Override
    protected void loadExtra() {
        this.creativeEnable = this.getBool("use-in-creative", false);
        this.cheatMode = this.getBool("cheat-mode", false);
    }

    @Override
    public Path getWorkingDirectoryPath() {
        return FabricWorldEdit.inst.getWorkingDir();
    }
}

