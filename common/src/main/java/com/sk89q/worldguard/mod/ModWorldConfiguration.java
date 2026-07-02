package com.sk89q.worldguard.mod;

import com.sk89q.worldguard.config.WorldConfiguration;
import java.io.File;

public class ModWorldConfiguration extends WorldConfiguration {
    private final String worldName;

    public ModWorldConfiguration(String worldName) {
        this.worldName = worldName;
        this.useRegions = true;
        this.summaryOnStart = true;
        this.opPermissions = true;
        this.buildPermissions = false;
        
        // Archivo de lista negra por defecto
        this.blacklistFile = new File("config/worldguard/worlds/" + worldName + "/blacklist.txt");
    }

    @Override
    public void loadConfiguration() {
        // Aquí se pueden cargar configuraciones específicas del mundo si es necesario.
    }
}
