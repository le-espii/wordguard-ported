/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ClassUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FeatureDetector;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.UrlUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.DefaultUrlResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.FileSystemClassPathLocationScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.JarFileClassPathLocationScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.OsgiClassPathLocationScanner;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.UrlResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.jboss.JBossVFSv2UrlResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.jboss.JBossVFSv3ClassPathLocationScanner;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassPathScanner {
    private static final Log LOG = LogFactory.getLog(ClassPathScanner.class);
    private final ClassLoader classLoader;

    public ClassPathScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Resource[] scanForResources(String path, String prefix, String suffix) throws IOException {
        LOG.debug("Scanning for classpath resources at '" + path + "' (Prefix: '" + prefix + "', Suffix: '" + suffix + "')");
        TreeSet<ClassPathResource> resources = new TreeSet<ClassPathResource>();
        Set<String> resourceNames = this.findResourceNames(path, prefix, suffix);
        for (String resourceName : resourceNames) {
            resources.add(new ClassPathResource(resourceName, this.classLoader));
            LOG.debug("Found resource: " + resourceName);
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public Class<?>[] scanForClasses(String location, Class<?> implementedInterface) throws Exception {
        LOG.debug("Scanning for classes at '" + location + "' (Implementing: '" + implementedInterface.getName() + "')");
        ArrayList classes = new ArrayList();
        Set<String> resourceNames = this.findResourceNames(location, "", ".class");
        for (String resourceName : resourceNames) {
            String className = this.toClassName(resourceName);
            Class<?> clazz = this.classLoader.loadClass(className);
            if (Modifier.isAbstract(clazz.getModifiers())) {
                LOG.debug("Skipping abstract class: " + className);
                continue;
            }
            if (!implementedInterface.isAssignableFrom(clazz)) continue;
            try {
                ClassUtils.instantiate(className, this.classLoader);
            }
            catch (Exception e) {
                throw new FlywayException("Unable to instantiate class: " + className);
            }
            classes.add(clazz);
            LOG.debug("Found class: " + className);
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private String toClassName(String resourceName) {
        String nameWithDots = resourceName.replace("/", ".");
        return nameWithDots.substring(0, nameWithDots.length() - ".class".length());
    }

    private Set<String> findResourceNames(String path, String prefix, String suffix) throws IOException {
        TreeSet<String> resourceNames = new TreeSet<String>();
        List<URL> locationsUrls = this.getLocationUrlsForPath(path);
        for (URL locationUrl : locationsUrls) {
            LOG.debug("Scanning URL: " + locationUrl.toExternalForm());
            UrlResolver urlResolver = this.createUrlResolver(locationUrl.getProtocol());
            URL resolvedUrl = urlResolver.toStandardJavaUrl(locationUrl);
            String protocol = resolvedUrl.getProtocol();
            ClassPathLocationScanner classPathLocationScanner = this.createLocationScanner(protocol);
            if (classPathLocationScanner == null) {
                String scanRoot = UrlUtils.toFilePath(resolvedUrl);
                LOG.warn("Unable to scan location: " + scanRoot + " (unsupported protocol: " + protocol + ")");
                continue;
            }
            resourceNames.addAll(classPathLocationScanner.findResourceNames(path, resolvedUrl));
        }
        return this.filterResourceNames(resourceNames, prefix, suffix);
    }

    private List<URL> getLocationUrlsForPath(String path) throws IOException {
        ArrayList<URL> locationUrls = new ArrayList<URL>();
        if (this.classLoader.getClass().getName().startsWith("com.ibm")) {
            Enumeration<URL> urls = this.classLoader.getResources(path + "/flyway.location");
            if (!urls.hasMoreElements()) {
                throw new FlywayException("Unable to determine URL for classpath location: " + path + " (ClassLoader: " + this.classLoader + ")" + " On WebSphere an empty file named flyway.location must be present on the classpath location for WebSphere to find it!");
            }
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                locationUrls.add(new URL(URLDecoder.decode(url.toExternalForm(), "UTF-8").replace("/flyway.location", "")));
            }
        } else {
            Enumeration<URL> urls = this.classLoader.getResources(path);
            if (!urls.hasMoreElements()) {
                throw new FlywayException("Unable to determine URL for classpath location: " + path + " (ClassLoader: " + this.classLoader + ")");
            }
            while (urls.hasMoreElements()) {
                locationUrls.add(urls.nextElement());
            }
        }
        return locationUrls;
    }

    private UrlResolver createUrlResolver(String protocol) {
        if (new FeatureDetector(this.classLoader).isJBossVFSv2Available() && protocol.startsWith("vfs")) {
            return new JBossVFSv2UrlResolver();
        }
        return new DefaultUrlResolver();
    }

    private ClassPathLocationScanner createLocationScanner(String protocol) {
        if ("file".equals(protocol)) {
            return new FileSystemClassPathLocationScanner();
        }
        if ("jar".equals(protocol) || "zip".equals(protocol) || "wsjar".equals(protocol)) {
            return new JarFileClassPathLocationScanner();
        }
        FeatureDetector featureDetector = new FeatureDetector(this.classLoader);
        if (featureDetector.isJBossVFSv3Available() && "vfs".equals(protocol)) {
            return new JBossVFSv3ClassPathLocationScanner();
        }
        if (featureDetector.isOsgiFrameworkAvailable() && ("bundle".equals(protocol) || "bundleresource".equals(protocol))) {
            return new OsgiClassPathLocationScanner();
        }
        return null;
    }

    private Set<String> filterResourceNames(Set<String> resourceNames, String prefix, String suffix) {
        TreeSet<String> filteredResourceNames = new TreeSet<String>();
        for (String resourceName : resourceNames) {
            String fileName = resourceName.substring(resourceName.lastIndexOf("/") + 1);
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix) && fileName.length() > (prefix + suffix).length()) {
                filteredResourceNames.add(resourceName);
                continue;
            }
            LOG.debug("Filtering out resource: " + resourceName + " (filename: " + fileName + ")");
        }
        return filteredResourceNames;
    }
}

