/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    private ClassUtils() {
    }

    public static synchronized <T> T instantiate(String className, ClassLoader classLoader) throws Exception {
        return (T)Class.forName(className, true, classLoader).newInstance();
    }

    public static <T> List<T> instantiateAll(String[] classes, ClassLoader classLoader) {
        ArrayList<T> clazzes = new ArrayList<T>();
        for (String clazz : classes) {
            if (!StringUtils.hasLength(clazz)) continue;
            try {
                clazzes.add(ClassUtils.instantiate(clazz, classLoader));
            }
            catch (Exception e) {
                throw new FlywayException("Unable to instantiate class: " + clazz);
            }
        }
        return clazzes;
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }

    public static String getShortName(Class<?> aClass) {
        String name = aClass.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String getLocationOnDisk(Class<?> aClass) {
        try {
            ProtectionDomain protectionDomain = aClass.getProtectionDomain();
            if (protectionDomain == null) {
                return null;
            }
            String url = protectionDomain.getCodeSource().getLocation().getPath();
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}

