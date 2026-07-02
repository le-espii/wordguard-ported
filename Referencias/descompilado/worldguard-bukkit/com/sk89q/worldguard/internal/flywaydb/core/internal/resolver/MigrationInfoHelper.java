/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Pair;

public class MigrationInfoHelper {
    private MigrationInfoHelper() {
    }

    public static Pair<MigrationVersion, String> extractVersionAndDescription(String migrationName, String prefix, String separator, String suffix) {
        String cleanMigrationName = migrationName.substring(prefix.length(), migrationName.length() - suffix.length());
        int descriptionPos = cleanMigrationName.indexOf(separator);
        if (descriptionPos < 0) {
            throw new FlywayException("Wrong migration name format: " + migrationName + "(It should look like this: " + prefix + "1_2" + separator + "Description" + suffix + ")");
        }
        String version = cleanMigrationName.substring(0, descriptionPos);
        String description = cleanMigrationName.substring(descriptionPos + separator.length()).replaceAll("_", " ");
        return Pair.of(MigrationVersion.fromVersion(version), description);
    }
}

