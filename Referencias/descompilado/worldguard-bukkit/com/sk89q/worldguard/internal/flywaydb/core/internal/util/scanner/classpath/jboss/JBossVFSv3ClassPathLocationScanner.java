/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.vfs.VFS
 *  org.jboss.vfs.VirtualFile
 *  org.jboss.vfs.VirtualFileFilter
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.jboss;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.UrlUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;

public class JBossVFSv3ClassPathLocationScanner
implements ClassPathLocationScanner {
    private static final Log LOG = LogFactory.getLog(JBossVFSv3ClassPathLocationScanner.class);

    @Override
    public Set<String> findResourceNames(String location, URL locationUrl) throws IOException {
        String filePath = UrlUtils.toFilePath(locationUrl);
        String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
        if (!classPathRootOnDisk.endsWith("/")) {
            classPathRootOnDisk = classPathRootOnDisk + "/";
        }
        LOG.debug("Scanning starting at classpath root on JBoss VFS: " + classPathRootOnDisk);
        TreeSet<String> resourceNames = new TreeSet<String>();
        List files = VFS.getChild((String)filePath).getChildrenRecursively(new VirtualFileFilter(){

            public boolean accepts(VirtualFile file) {
                return file.isFile();
            }
        });
        for (VirtualFile file : files) {
            resourceNames.add(file.getPathName().substring(classPathRootOnDisk.length()));
        }
        return resourceNames;
    }
}

