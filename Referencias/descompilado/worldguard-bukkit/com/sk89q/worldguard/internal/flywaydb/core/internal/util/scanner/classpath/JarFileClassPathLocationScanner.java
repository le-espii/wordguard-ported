/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileClassPathLocationScanner
implements ClassPathLocationScanner {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> findResourceNames(String location, URL locationUrl) throws IOException {
        JarFile jarFile = this.getJarFromUrl(locationUrl);
        try {
            Set<String> set = this.findResourceNamesFromJarFile(jarFile, location);
            return set;
        }
        finally {
            jarFile.close();
        }
    }

    private JarFile getJarFromUrl(URL locationUrl) throws IOException {
        URLConnection con = locationUrl.openConnection();
        if (con instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection)con;
            jarCon.setUseCaches(false);
            return jarCon.getJarFile();
        }
        String urlFile = locationUrl.getFile();
        int separatorIndex = urlFile.indexOf("!/");
        if (separatorIndex != -1) {
            String jarFileUrl = urlFile.substring(0, separatorIndex);
            if (jarFileUrl.startsWith("file:")) {
                try {
                    return new JarFile(new URL(jarFileUrl).toURI().getSchemeSpecificPart());
                }
                catch (URISyntaxException ex) {
                    return new JarFile(jarFileUrl.substring("file:".length()));
                }
            }
            return new JarFile(jarFileUrl);
        }
        return new JarFile(urlFile);
    }

    private Set<String> findResourceNamesFromJarFile(JarFile jarFile, String location) throws IOException {
        location = location + (location.endsWith("/") ? "" : "/");
        TreeSet<String> resourceNames = new TreeSet<String>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();
            if (!entryName.startsWith(location)) continue;
            resourceNames.add(entryName);
        }
        return resourceNames;
    }
}

