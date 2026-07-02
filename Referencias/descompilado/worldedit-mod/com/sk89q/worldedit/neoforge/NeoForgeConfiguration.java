/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.util.PropertiesConfiguration;
import java.nio.file.Path;

public class NeoForgeConfiguration
extends PropertiesConfiguration {
    public boolean creativeEnable = false;
    public boolean cheatMode = false;

    public NeoForgeConfiguration(NeoForgeWorldEdit mod) {
        super(mod.getWorkingDir().resolve("worldedit.properties"));
    }

    @Override
    protected void loadExtra() {
        this.creativeEnable = this.getBool("use-in-creative", false);
        this.cheatMode = this.getBool("cheat-mode", false);
    }

    @Override
    public Path getWorkingDirectoryPath() {
        return NeoForgeWorldEdit.inst.getWorkingDir();
    }
}

