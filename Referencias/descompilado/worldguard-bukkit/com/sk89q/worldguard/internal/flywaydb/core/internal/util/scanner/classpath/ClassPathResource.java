/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FileCopyUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public class ClassPathResource
implements Comparable<ClassPathResource>,
Resource {
    private String location;
    private ClassLoader classLoader;

    public ClassPathResource(String location, ClassLoader classLoader) {
        this.location = location;
        this.classLoader = classLoader;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public String getLocationOnDisk() {
        URL url = this.getUrl();
        if (url == null) {
            throw new FlywayException("Unable to location resource on disk: " + this.location);
        }
        try {
            return URLDecoder.decode(url.getPath(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new FlywayException("Unknown encoding: UTF-8", e);
        }
    }

    private URL getUrl() {
        return this.classLoader.getResource(this.location);
    }

    @Override
    public String loadAsString(String encoding) {
        try {
            InputStream inputStream = this.classLoader.getResourceAsStream(this.location);
            if (inputStream == null) {
                throw new FlywayException("Unable to obtain inputstream for resource: " + this.location);
            }
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName(encoding));
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load resource: " + this.location + " (encoding: " + encoding + ")", e);
        }
    }

    @Override
    public byte[] loadAsBytes() {
        try {
            InputStream inputStream = this.classLoader.getResourceAsStream(this.location);
            if (inputStream == null) {
                throw new FlywayException("Unable to obtain inputstream for resource: " + this.location);
            }
            return FileCopyUtils.copyToByteArray(inputStream);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load resource: " + this.location, e);
        }
    }

    @Override
    public String getFilename() {
        return this.location.substring(this.location.lastIndexOf("/") + 1);
    }

    public boolean exists() {
        return this.getUrl() != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClassPathResource that = (ClassPathResource)o;
        return this.location.equals(that.location);
    }

    public int hashCode() {
        return this.location.hashCode();
    }

    @Override
    public int compareTo(ClassPathResource o) {
        return this.location.compareTo(o.location);
    }
}

