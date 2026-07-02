/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.internal;

import com.sk89q.worldedit.event.platform.ConfigurationLoadEvent;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.apache.logging.log4j.Logger;

public class SchematicsEventListener {
    private static final Logger LOGGER = LogManagerCompat.getLogger();

    @Subscribe
    public void onConfigLoad(ConfigurationLoadEvent event) {
        Path config = event.getConfiguration().getWorkingDirectoryPath();
        try {
            Files.createDirectories(config.resolve(event.getConfiguration().saveDir), new FileAttribute[0]);
        }
        catch (FileAlreadyExistsException e) {
            LOGGER.debug("Schematic directory exists as file. Possible symlink.", (Throwable)e);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to create schematics directory", (Throwable)e);
        }
    }
}

