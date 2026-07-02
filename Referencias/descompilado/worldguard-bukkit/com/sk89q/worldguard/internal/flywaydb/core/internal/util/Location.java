/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;

public final class Location
implements Comparable<Location> {
    private static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILESYSTEM_PREFIX = "filesystem:";
    private String prefix;
    private String path;

    public Location(String descriptor) {
        String normalizedDescriptor = descriptor.trim().replace("\\", "/");
        if (normalizedDescriptor.contains(":")) {
            this.prefix = normalizedDescriptor.substring(0, normalizedDescriptor.indexOf(":") + 1);
            this.path = normalizedDescriptor.substring(normalizedDescriptor.indexOf(":") + 1);
        } else {
            this.prefix = CLASSPATH_PREFIX;
            this.path = normalizedDescriptor;
        }
        if (this.isClassPath()) {
            this.path = this.path.replace(".", "/");
            if (this.path.startsWith("/")) {
                this.path = this.path.substring(1);
            }
        } else if (!this.isFileSystem()) {
            throw new FlywayException("Unknown prefix for location (should be either filesystem: or classpath:): " + normalizedDescriptor);
        }
        if (this.path.endsWith("/")) {
            this.path = this.path.substring(0, this.path.length() - 1);
        }
    }

    public boolean isClassPath() {
        return CLASSPATH_PREFIX.equals(this.prefix);
    }

    public boolean isFileSystem() {
        return FILESYSTEM_PREFIX.equals(this.prefix);
    }

    public boolean isParentOf(Location other) {
        return (other.getDescriptor() + "/").startsWith(this.getDescriptor() + "/");
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getPath() {
        return this.path;
    }

    public String getDescriptor() {
        return this.prefix + this.path;
    }

    @Override
    public int compareTo(Location o) {
        return this.getDescriptor().compareTo(o.getDescriptor());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location)o;
        return this.getDescriptor().equals(location.getDescriptor());
    }

    public int hashCode() {
        return this.getDescriptor().hashCode();
    }

    public String toString() {
        return this.getDescriptor();
    }
}

