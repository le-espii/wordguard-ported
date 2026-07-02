/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.jboss;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.UrlResolver;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

public class JBossVFSv2UrlResolver
implements UrlResolver {
    @Override
    public URL toStandardJavaUrl(URL url) throws IOException {
        try {
            Class<?> vfsClass = Class.forName("org.jboss.virtual.VFS");
            Class<?> vfsUtilsClass = Class.forName("org.jboss.virtual.VFSUtils");
            Class<?> virtualFileClass = Class.forName("org.jboss.virtual.VirtualFile");
            Method getRootMethod = vfsClass.getMethod("getRoot", URL.class);
            Method getRealURLMethod = vfsUtilsClass.getMethod("getRealURL", virtualFileClass);
            Object root = getRootMethod.invoke(null, url);
            return (URL)getRealURLMethod.invoke(null, root);
        }
        catch (Exception e) {
            throw new FlywayException("JBoss VFS v2 call failed", e);
        }
    }
}

