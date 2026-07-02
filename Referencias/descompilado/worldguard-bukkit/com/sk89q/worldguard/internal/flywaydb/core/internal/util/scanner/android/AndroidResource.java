/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.res.AssetManager
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.android;

import android.content.res.AssetManager;
import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FileCopyUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidResource
implements Resource {
    private final AssetManager assetManager;
    private final String path;
    private final String name;

    public AndroidResource(AssetManager assetManager, String path, String name) {
        this.assetManager = assetManager;
        this.path = path;
        this.name = name;
    }

    @Override
    public String getLocation() {
        return this.path + "/" + this.name;
    }

    @Override
    public String getLocationOnDisk() {
        return null;
    }

    @Override
    public String loadAsString(String encoding) {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(this.assetManager.open(this.getLocation()), encoding));
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load asset: " + this.getLocation(), e);
        }
    }

    @Override
    public byte[] loadAsBytes() {
        try {
            return FileCopyUtils.copyToByteArray(this.assetManager.open(this.getLocation()));
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load asset: " + this.getLocation(), e);
        }
    }

    @Override
    public String getFilename() {
        return this.name;
    }
}

