/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class OsgiClassPathLocationScanner
implements ClassPathLocationScanner {
    @Override
    public Set<String> findResourceNames(String location, URL locationUrl) throws IOException {
        TreeSet<String> resourceNames = new TreeSet<String>();
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        Enumeration entries = bundle.findEntries(locationUrl.getPath(), "*", true);
        if (entries != null) {
            while (entries.hasMoreElements()) {
                URL entry = (URL)entries.nextElement();
                String resourceName = this.getPathWithoutLeadingSlash(entry);
                resourceNames.add(resourceName);
            }
        }
        return resourceNames;
    }

    private String getPathWithoutLeadingSlash(URL entry) {
        String path = entry.getPath();
        return path.startsWith("/") ? path.substring(1) : path;
    }
}

