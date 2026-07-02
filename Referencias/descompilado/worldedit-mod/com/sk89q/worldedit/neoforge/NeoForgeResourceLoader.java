/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.io.WorldEditResourceLoader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class NeoForgeResourceLoader
extends WorldEditResourceLoader {
    public NeoForgeResourceLoader(WorldEdit worldEdit) {
        super(worldEdit);
    }

    private static URL getResourceForgeHack(String location) throws IOException {
        try {
            URL url = URI.create("modjar://worldedit/" + location).toURL();
            try {
                url.openStream().close();
            }
            catch (IOException e) {
                return null;
            }
            return url;
        }
        catch (Exception e) {
            throw new IOException("Could not find " + location);
        }
    }

    @Override
    public URL getRootResource(String pathName) throws IOException {
        URL url = super.getRootResource(pathName);
        if (url == null) {
            return NeoForgeResourceLoader.getResourceForgeHack(pathName);
        }
        return url;
    }
}

