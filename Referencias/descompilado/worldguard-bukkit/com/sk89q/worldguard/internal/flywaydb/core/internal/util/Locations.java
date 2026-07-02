/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Location;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Locations {
    private static final Log LOG = LogFactory.getLog(Locations.class);
    private final List<Location> locations = new ArrayList<Location>();

    public Locations(String ... rawLocations) {
        ArrayList<Location> normalizedLocations = new ArrayList<Location>();
        for (String rawLocation : rawLocations) {
            normalizedLocations.add(new Location(rawLocation));
        }
        Collections.sort(normalizedLocations);
        for (Location normalizedLocation : normalizedLocations) {
            if (this.locations.contains(normalizedLocation)) {
                LOG.warn("Discarding duplicate location '" + normalizedLocation + "'");
                continue;
            }
            Location parentLocation = this.getParentLocationIfExists(normalizedLocation, this.locations);
            if (parentLocation != null) {
                LOG.warn("Discarding location '" + normalizedLocation + "' as it is a sublocation of '" + parentLocation + "'");
                continue;
            }
            this.locations.add(normalizedLocation);
        }
    }

    public List<Location> getLocations() {
        return this.locations;
    }

    private Location getParentLocationIfExists(Location location, List<Location> finalLocations) {
        for (Location finalLocation : finalLocations) {
            if (!finalLocation.isParentOf(location)) continue;
            return finalLocation;
        }
        return null;
    }
}

